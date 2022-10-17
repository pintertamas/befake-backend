package com.pintertamas.befake.interactionservice.service;

import com.amazonaws.services.mq.model.NotFoundException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.pintertamas.befake.interactionservice.exception.WrongFormatException;
import com.pintertamas.befake.interactionservice.model.Post;
import com.pintertamas.befake.interactionservice.model.Reaction;
import com.pintertamas.befake.interactionservice.model.User;
import com.pintertamas.befake.interactionservice.repository.ReactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ReactionService {

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    private final AmazonS3 s3;
    private final ReactionRepository reactionRepository;

    public ReactionService(AmazonS3 s3, ReactionRepository reactionRepository) {
        this.s3 = s3;
        this.reactionRepository = reactionRepository;
    }

    public Reaction react(Long userId, MultipartFile reactionPhoto, Long postId) throws IOException, WrongFormatException {
        long now = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd_HH:mm:ss");
        Date date = new Date(now);
        String fileName = sdf.format(date) + getExtensionOfFile(reactionPhoto);
        try {
            uploadReaction(reactionPhoto, fileName);
            Reaction reaction = new Reaction();
            reaction.setPostId(postId);
            reaction.setUserId(userId);
            reaction.setImageName(fileName);
            reaction.setReactionTime(new Timestamp(now));
            return reactionRepository.save(reaction);
        } catch (WrongFormatException e) {
            deleteImage(fileName);
            throw e;
        }
    }

    private String getExtensionOfFile(MultipartFile file) {
        return StringUtils.getFilenameExtension(file.getOriginalFilename());
    }

    public void deleteReactionOnPost(Long postId, Long userId) throws NotFoundException {
        Optional<Reaction> reaction = reactionRepository.findByPostIdAndUserId(postId, userId);
        if (reaction.isEmpty()) throw new NotFoundException("Could not find reaction from this user on this post");
        String imageName;
        imageName = reaction.get().getImageName();
        deleteImage(imageName);
        reactionRepository.delete(reaction.get());
    }

    private void uploadReaction(MultipartFile image, String fileName) throws IOException, WrongFormatException {
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

    public List<Reaction> getReactionsByPost(Long postId) {
        Optional<List<Reaction>> reactions = reactionRepository.findAllByPostId(postId);
        if (reactions.isEmpty()) throw new NotFoundException("No reactions could be found");
        return reactions.get();
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
}
