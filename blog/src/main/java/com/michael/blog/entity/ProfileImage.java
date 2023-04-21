package com.michael.blog.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "profile_image")
public class ProfileImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, name = "file_name")
    private String fileName;
    @Column(name = "file_type", nullable = false)
    private String fileType;
    @Lob
    @Column(columnDefinition = "longblob")
    private byte[] data;
    @Column(name = "profile_image_URL")
    private String profileImageURL;
}
