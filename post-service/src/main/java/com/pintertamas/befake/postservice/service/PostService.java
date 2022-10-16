package com.pintertamas.befake.postservice.service;

import com.amazonaws.services.mq.model.NotFoundException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.pintertamas.befake.postservice.model.Post;
import com.pintertamas.befake.postservice.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
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

    public Post createPost(Long userId, MultipartFile mainPhoto, MultipartFile selfiePhoto, String location) throws FileUploadException {
        Long now = System.currentTimeMillis();
        String mainName = "main_" + now;
        String selfieName = "selfie_" + now;
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
        } catch (Exception e) {
            deleteImage(mainName);
            deleteImage(selfieName);
            throw e;
        }
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

    private void uploadImage(MultipartFile image, String fileName) throws FileUploadException {
        File file = convertMultipartFileToFile(image);
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, file);
            s3.putObject(putObjectRequest);
            file.delete();
        } catch (Exception e) {
            log.error("Could not upload image", e);
            file.delete();
            throw new FileUploadException("Error uploading image");
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

    public String deleteImage(String fileName) throws IllegalArgumentException {
        try {
            s3.deleteObject(bucketName, fileName);
            return fileName + " has been deleted";
        } catch (Exception e) {
            throw new IllegalArgumentException("Wrong fileName");
        }
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) {
        File convertedFile = new File(multipartFile.getOriginalFilename());
        try (FileOutputStream fileOutputStream = new FileOutputStream(convertedFile)) {
            fileOutputStream.write(multipartFile.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipart file to file", e);
        }
        return convertedFile;
    }

    public List<Post> getPostsByUser(Long userId) {
        Optional<List<Post>> posts = postRepository.findAllByUserId(userId);
        if (posts.isEmpty()) throw new NotFoundException("No posts could be found");
        return posts.get();
    }
}
