package com.michael.blog.payload.request;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PostRequest {
    @NotBlank(message = "Title should not be null or empty")
    @Size(min = 2, message = "Post title should have at least 2 characters")
    private String title;
    @NotBlank(message = "Description should not be null or empty")
    @Size(min = 10, message = "Post description should have at least 10 characters")
    private String description;
    @NotBlank(message = "Content should not be null or empty")
    private String content;
}
