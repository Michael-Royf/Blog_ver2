package com.michael.blog.repository;

import com.michael.blog.entity.ImageData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageDataRepository  extends JpaRepository<ImageData, Long> {
    Optional<ImageData> findById(String id);

    Optional<ImageData> findByFileName(String filename);
}
