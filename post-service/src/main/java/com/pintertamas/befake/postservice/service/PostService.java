package com.pintertamas.befake.postservice.service;

import com.amazonaws.services.mq.model.BadRequestException;
import com.amazonaws.services.mq.model.NotFoundException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.pintertamas.befake.postservice.exception.PostNotFoundException;
import com.pintertamas.befake.postservice.exception.WrongFormatException;
import com.pintertamas.befake.postservice.model.Post;
import com.pintertamas.befake.postservice.model.User;
import com.pintertamas.befake.postservice.proxy.FriendProxy;
import com.pintertamas.befake.postservice.proxy.TimeServiceProxy;
import com.pintertamas.befake.postservice.proxy.UserProxy;
import com.pintertamas.befake.postservice.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PostService {

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    private final AmazonS3 s3;
    private final PostRepository postRepository;
    private final TimeServiceProxy timeServiceProxy;
    private final FriendProxy friendProxy;

    public PostService(AmazonS3 s3, PostRepository postRepository, FriendProxy friendProxy, TimeServiceProxy timeServiceProxy) {
        this.s3 = s3;
        this.postRepository = postRepository;
        this.friendProxy = friendProxy;
        this.timeServiceProxy = timeServiceProxy;
    }

    public Post createPost(User user, MultipartFile mainPhoto, MultipartFile selfiePhoto, String location) throws IOException, WrongFormatException, BadRequestException {
        ResponseEntity<Timestamp> lastBeFakeTimeResponse = timeServiceProxy.getLastBeFakeTime();
        if (lastBeFakeTimeResponse == null || !lastBeFakeTimeResponse.getStatusCode().equals(HttpStatus.OK)) {
            throw new BadRequestException("Could not reach time service");
        }
        if (userAlreadyPostedToday(user, lastBeFakeTimeResponse.getBody()))
            throw new BadRequestException("You already posted today");

        long now = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd_HH:mm:ss");
        Date date = new Date(now);
        String mainName = sdf.format(date) + "_main." + getExtensionOfFile(mainPhoto);
        String selfieName = sdf.format(date) + "_selfie." + getExtensionOfFile(selfiePhoto);

        try {
            uploadImage(mainPhoto, mainName);
            uploadImage(selfiePhoto, selfieName);
            Post post = new Post();
            post.setMainPhoto(mainName);
            post.setSelfiePhoto(selfieName);
            post.setLocation(location);
            post.setPostingTime(new Timestamp(now));
            post.setBeFakeTime(lastBeFakeTimeResponse.getBody());
            post.setUserId(user.getId());
            post.setDeleted(false);
            return postRepository.save(post);
        } catch (WrongFormatException e) {
            deleteImage(mainName);
            deleteImage(selfieName);
            throw e;
        }
    }

    private boolean userAlreadyPostedToday(User user, Timestamp beFakeTime) {
        Timestamp postBeFakeTime;
        try {
            Post lastPost = getLastPostBy(user.getId());
            if (lastPost == null) return false;
            postBeFakeTime = lastPost.getBeFakeTime();
        } catch (PostNotFoundException e) {
            postBeFakeTime = new Timestamp(0);
        }
        return postBeFakeTime.compareTo(beFakeTime) >= 0;
    }

    private String getExtensionOfFile(MultipartFile file) {
        return StringUtils.getFilenameExtension(file.getOriginalFilename());
    }

    public void addDescription(Long postId, String description) throws NotFoundException {
        Optional<Post> post = postRepository.findById(postId).filter(existingPost -> !existingPost.isDeleted());
        if (post.isPresent()) {
            post.get().setDescription(description);
            postRepository.save(post.get());
        } else {
            throw new NotFoundException("Could not find post with id: " + postId);
        }
    }

    private void uploadImage(MultipartFile image, String fileName) throws IOException, WrongFormatException {
        File file = convertMultipartFileToFile(image);
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, file);
            s3.putObject(putObjectRequest);
            file.delete();
        } catch (Exception e) {
            log.error("Could not upload image", e);
            file.delete();
            throw new FileUploadException("MultipartFile conversion to File was successful, but an error happened during the upload");
        }
    }

    public byte[] downloadImage(String fileName) {
        try {
            S3Object s3Object = s3.getObject(bucketName, fileName);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            log.error("IOException - Image download error", e);
        } catch (AmazonS3Exception e) {
            log.error("AmazonS3Exception - Image download error", e);
        } catch (Exception e) {
            log.error("Exception - Wrong API call", e);
        }
        return null;
    }

    private void deleteImage(String imageName) throws IllegalArgumentException {
        try {
            s3.deleteObject(bucketName, imageName);
        } catch (Exception e) {
            throw new IllegalArgumentException("Wrong fileName");
        }
    }

    public void deletePost(Long postId) throws NotFoundException {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) throw new NotFoundException("Could not find post by id: " + postId);
        String imageName;
        imageName = post.get().getMainPhoto();
        deleteImage(imageName);
        imageName = post.get().getSelfiePhoto();
        deleteImage(imageName);
        post.get().setMainPhoto(null);
        post.get().setSelfiePhoto(null);
        post.get().setDescription(null);
        post.get().setLocation(null);
        post.get().setDeleted(true);
        postRepository.save(post.get()); // soft delete
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException, WrongFormatException {
        if (!isAnImage(multipartFile)) throw new WrongFormatException("Wrong image format");
        String originalFileName = multipartFile.getOriginalFilename();
        if (originalFileName == null) throw new IOException("Original filename is null");
        File convertedFile = new File(originalFileName);
        FileOutputStream fileOutputStream = new FileOutputStream(convertedFile);
        fileOutputStream.write(multipartFile.getBytes());
        fileOutputStream.close();
        return convertedFile;
    }

    public List<Post> getPostsByUser(Long userId) {
        Optional<List<Post>> posts = postRepository.findAllByUserId(userId);
        if (posts.isEmpty()) throw new NotFoundException("No posts could be found");
        return posts.get()
                .stream()
                .filter(existingPost -> !existingPost.isDeleted())
                .toList();
    }

    public List<Post> getPosts() {
        List<Post> posts = postRepository.findAll();
        if (posts.isEmpty()) throw new NotFoundException("No posts could be found");
        return posts;
    }

    public List<Post> getPostsFromFriends(HttpHeaders headers) {
        ResponseEntity<List<Long>> friends = friendProxy.getListOfFriends(headers);
        if (friends.getBody() == null || !friends.getStatusCode().equals(HttpStatus.OK)) throw new NotFoundException("Could not establish connection with friend-service");
        List<Post> postsFromFriends = new ArrayList<>();
        friends.getBody().forEach(friend -> postsFromFriends.add(getTodaysPostBy(friend)));
        return postsFromFriends;
    }

    private boolean isAnImage(MultipartFile file) {
        try {
            String fileContentType = file.getContentType();
            System.out.println(fileContentType);
            if (fileContentType == null || !fileContentType.startsWith("image/")) throw new Exception();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Post> getPostsFromLastXDays(Long userId, int days) {
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate xDaysAgo = LocalDate.now().minusDays(days - 1);
        LocalDateTime xDaysAgoMidnight = LocalDateTime.of(xDaysAgo, midnight);
        log.info("x days ago midnight was: " + xDaysAgoMidnight);
        Optional<List<Post>> posts = postRepository.findAllByUserIdAndPostingTimeAfter(userId, Timestamp.valueOf(xDaysAgoMidnight));
        if (posts.isEmpty()) throw new NotFoundException("Could not query posts belonging to this user");
        return posts.get()
                .stream()
                .filter(post -> post.getBeFakeTime()
                        .after(Timestamp.valueOf(xDaysAgoMidnight))
                ).toList();
    }

    public Post getTodaysPostBy(Long userId) {
        return getPostsFromLastXDays(userId, 1).get(0);
    }

    public Post getLastPostBy(Long userId) throws PostNotFoundException {
        Optional<List<Post>> posts = postRepository.findAllByUserId(userId);
        if (posts.isEmpty()) throw new PostNotFoundException(userId);
        if (posts.get().size() == 0) return null;
        posts.get().sort((p1, p2) -> {
            if (p1.getPostingTime().equals(p2.getPostingTime())) return 0;
            else return p1.getPostingTime().after(p2.getPostingTime()) ? 1 : -1;
        });
        log.info(posts.get().toString());
        posts.get().forEach((post) -> log.info(post.getPostingTime().toString()));
        Post lastPost = posts.get().get(posts.get().size() - 1);
        log.info(lastPost.getPostingTime().toString());
        return lastPost;
    }

    public Post findPostById(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        return post.orElse(null);
    }

    public void removePostsByUser(Long userId) throws PostNotFoundException {
        Optional<List<Post>> posts = postRepository.findAllByUserId(userId);
        if (posts.isEmpty()) throw new PostNotFoundException(userId);
        posts.get().forEach((post) -> {
            deleteImage(post.getMainPhoto());
            deleteImage(post.getSelfiePhoto());
            postRepository.delete(post);
        });
    }
}
