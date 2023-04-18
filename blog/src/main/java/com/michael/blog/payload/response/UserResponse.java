package com.michael.blog.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.michael.blog.entity.enumeration.UserRole;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserResponse {
    private String id;
    // private String generateId;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss", timezone = "Israel")
    private Date displayLastLoginDate;
    private UserRole role;
    private String profileImageFileName;
}
