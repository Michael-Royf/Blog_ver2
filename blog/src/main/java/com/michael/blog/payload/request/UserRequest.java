package com.michael.blog.payload.request;

import com.michael.blog.validation.PasswordMatches;
import com.michael.blog.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@PasswordMatches
public class UserRequest {
    @NotBlank(message = "First name should not be empty")
    @Pattern(regexp = "^(?!\\s)(.*\\S)$", message = "The firstName should not start or end with a space")
    private String firstName;
    @NotBlank(message = "Last name should not be empty")
    @Pattern(regexp = "^(?!\\s)(.*\\S)$", message = "The lastName should not start or end with a space")
    private String lastName;
    @NotBlank(message = "Username should not be empty")
    @Pattern(regexp = "^[^\\s]+$", message = "Username should not contain a space")
    private String username;
    @Email
    @NotBlank(message = "Email should not be empty")
    private String email;

    @NotBlank(message = "Password should not be empty")
    @ValidPassword
    private String password;
    @NotBlank(message = "Password should not be empty")
    @ValidPassword
    private String matchingPassword;
}
