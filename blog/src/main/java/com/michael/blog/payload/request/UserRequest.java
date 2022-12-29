package com.michael.blog.payload.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
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
    @NotBlank(message = "Email should not be empty")
    @Size(min = 6, message = "The password must be at least 6 characters long")
    private String password;
}
