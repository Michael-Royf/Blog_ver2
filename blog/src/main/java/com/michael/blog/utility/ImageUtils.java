package com.michael.blog.utility;

import com.michael.blog.entity.*;
import com.michael.blog.exception.payload.ImageNotFoundException;
import com.michael.blog.repository.CommentRepository;
import com.michael.blog.repository.ImageDataRepository;
import com.michael.blog.repository.PostRepository;
import com.michael.blog.repository.ProfileImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import static com.michael.blog.constants.FileConstant.*;
import static org.springframework.http.MediaType.*;

@Component
@Slf4j
public class ImageUtils {

    @Autowired
    private ProfileImageRepository profileImageRepository;
    @Autowired
    private ImageDataRepository imageDataRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;

    public void saveTempProfileImage(User user) throws IOException {
        ProfileImage profileImage = ProfileImage.builder()
                .fileName(user.getUsername() + DOT + JPG_EXTENSION)
                .fileType(IMAGE_JPEG_VALUE)
                .data(compressImage(getTempImage(user.getUsername())))
                .profileImageURL(setProfileImageUrl(user.getUsername()))
                .build();
        profileImage = profileImageRepository.save(profileImage);
        user.setProfileImageURL(profileImage.getProfileImageURL());
        log.info("Saved Profile Image in database by name: {} " + profileImage.getFileName());
        saveProfileImageToLocalSystem(user, profileImage);
    }


    public void updateProfileImage(User user, MultipartFile profileImage) throws IOException {
        if (profileImage != null) {
            if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(profileImage.getContentType())) {
                throw new RuntimeException(profileImage.getOriginalFilename() + NOT_AN_IMAGE_FILE);
            }
            if (user.getProfileImageURL() != null) {
                ProfileImage imageDataDb = findProfileImageInDB(user.getProfileImageURL());
                profileImageRepository.delete(imageDataDb);
                user.setProfileImageURL(null);
            }
            ProfileImage profileImageDB = ProfileImage.builder()
                    .fileName(user.getUsername() + DOT + JPG_EXTENSION)
                    .fileType(profileImage.getContentType())
                    .data(compressImage(profileImage.getBytes()))
                    .profileImageURL(setProfileImageUrl(user.getUsername()))
                    .build();
            profileImageDB = profileImageRepository.save(profileImageDB);
            user.setProfileImageURL(profileImageDB.getProfileImageURL());
            log.info("Saved file in database by name: {}" + profileImage.getOriginalFilename());
            saveProfileImageToLocalSystem(user, profileImageDB);
        } else {
            throw new ImageNotFoundException(IMAGE_NOT_FOUND);
        }
    }

    public Comment saveImagesToComment(User user, Comment comment, List<MultipartFile> images) throws IOException {
        if (!images.isEmpty()) {
            for (MultipartFile m : images) {
                if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(m.getContentType())) {
                    throw new RuntimeException(m.getOriginalFilename() + NOT_AN_IMAGE_FILE);
                }
            }
            for (MultipartFile file : images) {
                String filename = "post_" + comment.getPost().getId() + "comment_" + comment.getId() + "_image_" + RandomStringUtils.randomAlphanumeric(10) + DOT + JPG_EXTENSION;
                ImageData imageData = ImageData.builder()
                        .fileType(file.getContentType())
                        .fileName(filename)
                        .data(compressImage(file.getBytes()))
                        .imageURL(setImageUrlToComment(user.getUsername(), comment, filename))
                        .commentId(comment.getId())
                        .postId(comment.getPost().getId())
                        .build();

                if ((long) imageDataRepository.findAllByPostIdAndCommentId(comment.getPost().getId(), comment.getId()).size() < 2) {
                    imageDataRepository.save(imageData);
                    comment.addImageURL(imageData.getImageURL());
                }
            }
            return commentRepository.save(comment);
        } else {
            throw new ImageNotFoundException(IMAGE_NOT_FOUND);
        }
    }


    public void saveImagesToPost(User user, Post post, List<MultipartFile> images) throws IOException {
        if (!images.isEmpty()) {
            for (MultipartFile m : images) {
                if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(m.getContentType())) {
                    throw new RuntimeException(m.getOriginalFilename() + NOT_AN_IMAGE_FILE);
                }
            }

            for (MultipartFile file : images) {
                String filename = "post_" + post.getId() + "_image_" + RandomStringUtils.randomAlphanumeric(10) + DOT + JPG_EXTENSION;
                ImageData imageData = ImageData.builder()
                        .fileType(file.getContentType())
                        .fileName(filename)
                        .data(compressImage(file.getBytes()))
                        .imageURL(setImageUrlToPost(user.getUsername(), post, filename))
                        .postId(post.getId())
                        .isPostImage(true)
                        .build();

                if ((long) imageDataRepository.findAllByPostIdAndIsPostImage(post.getId(), true).size() < 5) {
                    imageDataRepository.save(imageData);
                    post.addImageURL(imageData.getImageURL());
                }
                postRepository.save(post);
            }
        } else {
            throw new ImageNotFoundException(IMAGE_NOT_FOUND);
        }
    }


    private String setImageUrlToPost(String username, Post post, String filename) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(PATH_PREFIX + POST_IMAGE_PATH + username + FORWARD_SLASH + post.getId() + FORWARD_SLASH
                        + filename).toUriString();
    }


    private String setImageUrlToComment(String username, Comment comment, String filename) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(PATH_PREFIX + "post" + FORWARD_SLASH + comment.getPost().getId() + FORWARD_SLASH
                        + username + FORWARD_SLASH + "comment" + FORWARD_SLASH + comment.getId() + FORWARD_SLASH + "image" + FORWARD_SLASH
                        + filename).toUriString();
    }


    private void saveProfileImageToLocalSystem(User user, ProfileImage profileImage) {
        if (profileImage != null) {
            if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(profileImage.getFileType())) {
                throw new RuntimeException(profileImage.getFileName() + NOT_AN_IMAGE_FILE);
            }
            Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
            if (!Files.exists(userFolder)) {
                try {
                    Files.createDirectories(userFolder);
                    log.info(DIRECTORY_CREATED + userFolder);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to create a directory for the avatar on the local computer");
                }
                log.info(DIRECTORY_CREATED + userFolder);
            }
            try {
                Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + DOT + JPG_EXTENSION));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Path path = Paths.get(String.valueOf(userFolder.resolve(user.getUsername() + DOT + JPG_EXTENSION)));
            copyFile(path, profileImage);
        } else {
            throw new ImageNotFoundException(IMAGE_NOT_FOUND);
        }
    }


    public void copyImageToLocalSystemFromDB(User user) {
        ProfileImage imageDataDb = findProfileImageInDB(user.getProfileImageURL());
        createDirectoryAndCopyFileToLocalSystem(user, imageDataDb);
    }

    private void createDirectoryAndCopyFileToLocalSystem(User user, ProfileImage profileImage) {
        Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
        Path path = Paths.get(String.valueOf(userFolder.resolve(user.getUsername() + DOT + JPG_EXTENSION)));
        if (!Files.exists(userFolder)) {
            try {
                Files.createDirectories(userFolder);
                log.info(DIRECTORY_CREATED + userFolder);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create a directory for the application on the local computer");
            }
            copyFile(path, profileImage);
        } else if (Files.exists(userFolder) && !Files.exists(path)) {
            copyFile(path, profileImage);
        }
    }

    private void copyFile(Path path, ProfileImage profileImage) {
        try {
            Files.write(path, decompressImage(profileImage.getData()));
            log.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getFileName());
        } catch (IOException e) {
            throw new RuntimeException("The profile image could not be copied to the local computer");
        }
    }


    private byte[] getTempImage(String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()) {
            int bytesRead;
            byte[] chunk = new byte[1024];
            while ((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }


    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(PATH_PREFIX + USER_IMAGE_PATH + username + FORWARD_SLASH
                        + username + DOT + JPG_EXTENSION).toUriString();
    }

    public void deleteAllInDirectory(Path path) {
        Path directory = Paths.get(String.valueOf(path));
        try {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
            log.info("Directory was deleted.");
        } catch (IOException e) {
            log.error("Failed to delete directory: " + e.getMessage());
        }
    }


    public byte[] compressImage(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4 * 1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        try {
            outputStream.close();
        } catch (Exception ignored) {
        }
        return outputStream.toByteArray();
    }


    public byte[] decompressImage(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4 * 1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        } catch (Exception ignored) {
        }
        return outputStream.toByteArray();
    }

    public ProfileImage findProfileImageInDB(String ProfileImageUrl) {
        return profileImageRepository
                .findProfileImageByProfileImageURL(ProfileImageUrl)
                .orElseThrow(() -> new ImageNotFoundException(IMAGE_NOT_FOUND));
    }


}
