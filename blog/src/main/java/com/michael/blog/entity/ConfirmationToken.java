package com.michael.blog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table
public class ConfirmationToken {
    @Id
    @SequenceGenerator(
            name = "confirmationToken_sequence",
            sequenceName = "confirmationToken_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "confirmationToken_sequence")
   // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String token;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime expiredAt;
    private LocalDateTime confirmedAt;


    @ManyToOne
    @JoinColumn(
            nullable = false,
            name = "user_id"
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;
}
