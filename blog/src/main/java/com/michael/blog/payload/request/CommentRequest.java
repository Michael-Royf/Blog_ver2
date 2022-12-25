package com.michael.blog.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CommentRequest {
    @NotBlank(message = "Name should not be null or empty")
    @Size(min = 2, message = "Name should have at least 2 characters")
    private String name;
    @NotBlank(message = "Email should not be null or empty")
    @Email
    private String email;
    @NotBlank(message = "Body should not be null or empty")
    private String body;
}
