package com.michael.blog.repository;

import com.michael.blog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByUserId(Long userId);

    List<Post> findPostsByCategoryId(Long categoryId);

    Optional<Post> findPostByTitle(String title);

    @Query("SELECT p from Post p WHERE p.title LIKE CONCAT('%', :query, '%') OR p.description LIKE CONCAT('%', :query, '%')")
    List<Post> searchPost(String query);
}
