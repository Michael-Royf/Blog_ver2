package com.michael.blog.payload.request;

import com.michael.blog.validation.PasswordMatches;
import com.michael.blog.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@PasswordMatches
public class UserRequest {
    @NotBlank(message = "First name should not be empty")
    private String firstName;
    @NotBlank(message = "Last name should not be empty")
    private String lastName;
    @NotBlank(message = "Username should not be empty")
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
