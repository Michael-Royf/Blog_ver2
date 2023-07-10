package com.michael.blog.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CategoryRequest {
    @NotBlank(message = "Name should not be null or empty")
    @Pattern(regexp = "^(?!\\s)(.*\\S)$", message = "The name should not start or end with a space")
    private String name;
    @NotBlank(message = "Description should not be null or empty")
    private String description;
}
