package com.michael.blog.payload.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class JwtAuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
}
