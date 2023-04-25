package com.michael.blog.repository;

import com.michael.blog.entity.ImageData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageDataRepository extends JpaRepository<ImageData, Long> {
    Optional<ImageData> findById(String id);

    Optional<ImageData> findByFileNameAndPostId(String filename, Long postId);

    Optional<ImageData> findByImageURL(String imageURL);

    List<ImageData> findAllByPostId(Long postId);

    List<ImageData> findAllByPostIdAndIsPostImage(Long postId, Boolean bool);

    List<ImageData> findAllByPostIdAndCommentId(Long postId, Long commentId);

    Optional<ImageData> findByPostIdAndCommentIdAndFileName(Long postId, Long commentId, String filename);
}
