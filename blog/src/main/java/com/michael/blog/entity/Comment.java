package com.michael.blog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @SequenceGenerator(
            name = "comment_sequence",
            sequenceName = "comment_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_sequence")
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String body;

    @Column(name = "number_of_likes")
    private Integer likes;
    @Column(name = "liked_users")
    @ElementCollection(targetClass = String.class)
    private Set<String> likedUsers = new HashSet<>();


    @ElementCollection(targetClass = String.class)
    private Set<String> imageUrlSet = new HashSet<>();


    @CreationTimestamp
    @Column(updatable = false, name = "create_date")
    private LocalDateTime createDate;
    @UpdateTimestamp
    @Column(updatable = true, name = "update_date")
    private LocalDateTime updateDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;


    public void addImageURL(String imageURL) {
        imageUrlSet.add(imageURL);
    }


//    if (imageUrlSet.size() < 2) {
//        imageUrlSet.add(imageURL);
//    } else {
//        throw new IllegalStateException("Can not add more than 2 images to a comment");
//    }


    public void removeImageURL(String imageURL) {
        imageUrlSet.remove(imageURL);
    }
}
