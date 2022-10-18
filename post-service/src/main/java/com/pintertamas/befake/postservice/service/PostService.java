package com.pintertamas.befake.postservice.service;

import com.amazonaws.services.mq.model.NotFoundException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.pintertamas.befake.postservice.exception.WrongFormatException;
import com.pintertamas.befake.postservice.model.Post;
import com.pintertamas.befake.postservice.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
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

    public PostService(AmazonS3 s3, PostRepository postRepository) {
        this.s3 = s3;
        this.postRepository = postRepository;
    }

    public Post createPost(Long userId, MultipartFile mainPhoto, MultipartFile selfiePhoto, String location) throws IOException, WrongFormatException {
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
            post.setUserId(userId);
            return postRepository.save(post);
        } catch (WrongFormatException e) {
            deleteImage(mainName);
            deleteImage(selfieName);
            throw e;
        }
    }

    private String getExtensionOfFile(MultipartFile file) {
        return StringUtils.getFilenameExtension(file.getOriginalFilename());
    }

    public void addDescription(Long postId, String description) throws NotFoundException {
        Optional<Post> post = postRepository.findById(postId);
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
        postRepository.delete(post.get());
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
        return posts.get();
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
        if (posts.isEmpty()) throw new NotFoundException("Could not find posts belonging to this user");
        return posts.get();
    }

    public Post getLastPostBy(Long userId) {
        return getPostsFromLastXDays(userId, 1).get(0);
    }

    public Post findPostById(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        return post.orElse(null);
    }
}
