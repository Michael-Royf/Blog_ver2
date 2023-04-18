package com.michael.blog.entity;

import com.michael.blog.entity.enumeration.TokenType;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Token {
    @Id
//    @SequenceGenerator(
//            name = "token_sequence",
//            sequenceName = "token_sequence",
//            allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "token_sequence")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private boolean expired;

    private boolean revoked;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
