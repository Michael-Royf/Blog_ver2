package com.michael.blog.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "LoginRequest model Information")
public class LoginRequest {
    @Schema(description = "Username")
    @NotBlank(message = "Username should not be empty")
    private String username;
    @Schema(description = "password")
    @NotBlank(message = "Password should not be empty")
    private String password;
}
