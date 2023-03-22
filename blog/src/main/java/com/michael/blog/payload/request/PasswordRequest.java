package com.michael.blog.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PasswordRequest {
    @NotBlank(message = "Old password should not be empty")
    private String oldPassword;
    @NotBlank(message = "New password should not be empty")
    @Size(min = 6, message = "New password should have at least 6 characters")
    private String newPassword;
}
