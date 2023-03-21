package com.michael.blog.repository;

import com.michael.blog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> getPostByUserId(Long userId);

    List<Post> getAllByUserId(Long userId);

    List<Post> findPostsByCategoryId(Long categoryId);
}
