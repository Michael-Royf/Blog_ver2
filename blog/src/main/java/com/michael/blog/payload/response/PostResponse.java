package com.michael.blog.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.michael.blog.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PostResponse {
    private Long id;
    private String username;
    private String title;
    private String description;
    private String content;
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss", timezone = "Israel")
    private LocalDateTime createDate;
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss", timezone = "Israel")
    private LocalDateTime updateDate;
    private Set<Comment> comments = new HashSet<>();
    private Long categoryId;
}
