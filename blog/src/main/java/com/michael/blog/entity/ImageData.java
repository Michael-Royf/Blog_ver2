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
@Table(name = "image_data")
public class ImageData {
    @Id
    @SequenceGenerator(
            name = "image_sequence",
            sequenceName = "image_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_sequence")
    private Long id;
    @Column(nullable = false, unique = true, name = "file_name")
    private String fileName;
    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Lob
    @JdbcTypeCode(Types.BINARY)
    //   @Column(columnDefinition = "longblob")
    private byte[] data;
    @Column(nullable = false, name = "image_URL", unique = true)
    private String imageURL;
    private Boolean isPostImage;
    @Column(name = "post_id")
    private Long postId;

    @Column(name = "comment_id")
    private Long commentId;
}


