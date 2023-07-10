package com.michael.blog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "profile_images")
public class ProfileImage {
    @Id
    @SequenceGenerator(
            name = "profileImage_sequence",
            sequenceName = "profileImage_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profileImage_sequence")
    private Long id;

    @Column(nullable = false, unique = true, name = "file_name")
    private String fileName;
    @Column(name = "file_type", nullable = false)
    private String fileType;
    @Lob
    @JdbcTypeCode(Types.BINARY)
//    @Column(columnDefinition = "longblob")
    private byte[] data;
    @Column(name = "profile_image_URL")
    private String profileImageURL;
}
