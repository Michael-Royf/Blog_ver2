package com.michael.blog.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CategoryRequest {
    @NotBlank(message = "Name should not be null or empty")
    private String name;
   @NotBlank(message = "Description should not be null or empty")
    private String description;
}
