package com.michael.blog.payload.request;

import com.michael.blog.validation.PasswordMatches;
import com.michael.blog.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@PasswordMatches
public class PasswordChangeRequest {
    @NotBlank(message = "Old password should not be empty")
    private String oldPassword;

    @NotBlank(message = "New password should not be empty")
    @ValidPassword
    private String newPassword;

    @NotBlank(message = "New password should not be empty")
    @ValidPassword
    private String matchingPassword;
}
