package com.michael.blog.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.michael.blog.entity.Post;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CommentResponse {
    private Long id;
    private String username;
    private String body;
    private Integer likes;
    private Set<String> likedUsers;
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss", timezone = "Israel")
    private LocalDateTime createDate;
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss", timezone = "Israel")
    private LocalDateTime updateDate;
    private Set<String> imageUrlSet;
}
