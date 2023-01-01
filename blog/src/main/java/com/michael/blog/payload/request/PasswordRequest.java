package com.michael.blog.payload.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PasswordRequest {
    private String oldPassword;
    private String newPassword;
}
