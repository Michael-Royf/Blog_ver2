package com.michael.blog.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "image_data")
public class ImageData {
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
}




//    @SequenceGenerator(
//            name = "image_sequence",
//            sequenceName = "image_sequence",
//            allocationSize = 1
//    )
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_sequence")