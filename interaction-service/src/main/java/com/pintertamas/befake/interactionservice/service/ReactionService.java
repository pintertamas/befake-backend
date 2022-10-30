package com.pintertamas.befake.interactionservice.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.mq.model.NotFoundException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.pintertamas.befake.interactionservice.exception.WrongFormatException;
import com.pintertamas.befake.interactionservice.model.Reaction;
import com.pintertamas.befake.interactionservice.repository.ReactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private final InteractionService interactionService;

    public ReactionService(AmazonS3 s3, ReactionRepository reactionRepository, InteractionService interactionService) {
        this.s3 = s3;
        this.reactionRepository = reactionRepository;
        this.interactionService = interactionService;
    }

    public Reaction react(Long userId, MultipartFile reactionPhoto, Long postId) throws IOException, WrongFormatException {
        long now = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd_HH:mm:ss");
        Date date = new Date(now);
        String fileName = sdf.format(date) + "." + getExtensionOfFile(reactionPhoto);
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

    public void deleteReactionOnPost(Long reactionId) throws NotFoundException {
        Optional<Reaction> reaction = reactionRepository.findById(reactionId);
        if (reaction.isEmpty()) throw new NotFoundException("Could not find reaction from this user on this post");
        String imageName;
        imageName = reaction.get().getImageName();
        deleteImage(imageName);
        reactionRepository.delete(reaction.get());
    }

    public void deleteEveryReactionOnPost(Long postId) throws NotFoundException {
        Optional<List<Reaction>> reactions = reactionRepository.findAllByPostId(postId);
        if (reactions.isEmpty()) throw new NotFoundException("Could not find reactions on this post");
        reactions.get().forEach(reaction -> {
            deleteImage(reaction.getImageName());
            reactionRepository.delete(reaction);
        });
    }

    public Reaction getReactionById(Long reactionId) {
        Optional<Reaction> reaction = reactionRepository.findById(reactionId);
        if (reaction.isEmpty()) throw new NotFoundException("Could not find reaction with this id: " + reactionId);
        return reaction.get();
    }

    public List<Long> getAffectedUserIdsByPost(Long postId) {
        List<Long> affectedIds = new ArrayList<>();
        getReactionsByPost(postId).forEach(post -> affectedIds.add(post.getUserId()));
        affectedIds.add(interactionService.getPostOwnerByPost(postId));
        return affectedIds;
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

    public String getReactionUrl(String filename) {
        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60;
        expiration.setTime(expTimeMillis);
        log.info("Generating pre-signed URL.");
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, filename)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);
        URL url = s3.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
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
            if (fileContentType == null || !fileContentType.startsWith("image/"))
                throw new WrongFormatException("Not an image");
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
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
        return new byte[]{};
    }

    private void deleteImage(String imageName) throws IllegalArgumentException {
        try {
            s3.deleteObject(bucketName, imageName);
        } catch (Exception e) {
            throw new IllegalArgumentException("Wrong fileName");
        }
    }

    public void deleteReactionsByUser(Long userId) throws NotFoundException {
        Optional<List<Reaction>> reactions = reactionRepository.findAllByUserId(userId);
        if (reactions.isEmpty()) throw new NotFoundException("Could not find reactions by this user");
        reactions.get().forEach(reaction -> {
            deleteImage(reaction.getImageName());
            reactionRepository.delete(reaction);
        });
    }

    public boolean alreadyReacted(Long userId, Long postId) {
        return reactionRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }
}
