package com.pintertamas.userservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.pintertamas.userservice.exceptions.UserExistsException;
import com.pintertamas.userservice.exceptions.UserNotFoundException;
import com.pintertamas.userservice.exceptions.WeakPasswordException;
import com.pintertamas.userservice.exceptions.WrongFormatException;
import com.pintertamas.userservice.model.User;
import com.pintertamas.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Service
public class UserService {
    final UserRepository userRepository;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    private final PasswordEncoder bcryptEncoder;
    private final AmazonS3 s3;

    public UserService(UserRepository userRepository, PasswordEncoder bcryptEncoder, AmazonS3 s3) {
        this.userRepository = userRepository;
        this.bcryptEncoder = bcryptEncoder;
        this.s3 = s3;
    }

    static final String regex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$";

    public User register(User newUser) throws UserExistsException, WeakPasswordException {
        if (userRepository.findUserByUsername(newUser.getUsername()) != null || userRepository.findUserByEmail(newUser.getEmail()) != null) {
            throw new UserExistsException(newUser);
        }
        if (!newUser.getPassword().matches(regex)) throw new WeakPasswordException();
        newUser.setPassword(bcryptEncoder.encode(newUser.getPassword()));
        newUser.setRegistrationDate(new Timestamp(System.currentTimeMillis()));
        return userRepository.save(newUser);
    }

    public User getUserData(Long userId) throws UserNotFoundException {
        try {
            return userRepository.findUserById(userId);
        } catch (Exception e) {
            throw new UserNotFoundException(userId);
        }
    }

    public List<User> getListOfUsers() throws UserNotFoundException {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            throw new UserNotFoundException();
        }
    }

    public void updateProfilePicture(Long userId, MultipartFile profilePicture) throws UserNotFoundException, WrongFormatException {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        if (user.getProfilePicture() != null) deleteImage(user.getProfilePicture());
        String fileName = userId + "_profile_picture." + getExtensionOfFile(profilePicture);
        try {
            uploadImage(profilePicture, fileName);
            user.setProfilePicture(fileName);
            userRepository.save(user);
        } catch (WrongFormatException e) {
            deleteImage(fileName);
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getExtensionOfFile(MultipartFile file) {
        return StringUtils.getFilenameExtension(file.getOriginalFilename());
    }

    public User updateProfile(User editedUser) {
        User user = userRepository.findUserById(editedUser.getId());
        editedUser.setPassword(user.getPassword());
        editedUser.setProfilePicture(user.getProfilePicture());
        editedUser.setRegistrationDate(user.getRegistrationDate());
        user = editedUser;
        return userRepository.save(user);
    }

    public void editPassword(Long userId, String newPassword) throws WeakPasswordException {
        if (!newPassword.matches(regex)) throw new WeakPasswordException();
        User user = userRepository.findUserById(userId);
        String encryptedPassword = bcryptEncoder.encode(newPassword);
        user.setPassword(encryptedPassword);
        userRepository.save(user);
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

    public byte[] downloadProfilePictureByUsername(String username) {
        try {
            User user = userRepository.findUserByUsername(username);
            return downloadImage(user.getProfilePicture());
        } catch (Exception e) {
            log.error(e.getMessage());
            return new byte[]{};
        }
    }

    private byte[] downloadImage(String fileName) {
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

    private void deleteImage(String imageName) throws IllegalArgumentException {
        try {
            s3.deleteObject(bucketName, imageName);
        } catch (Exception e) {
            throw new IllegalArgumentException("Wrong fileName");
        }
    }

    public void delete(Long userId) throws UserNotFoundException {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }

        userRepository.delete(user);
    }

    public User findUserById(Long userId) {
        return userRepository.findUserById(userId);
    }

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }
}
