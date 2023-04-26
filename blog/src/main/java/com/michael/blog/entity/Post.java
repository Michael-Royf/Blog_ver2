package com.michael.blog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "posts")
public class Post implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(
            name = "post_sequence",
            sequenceName = "post_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_sequence")
    @Column(name = "post_id", updatable = false)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(name = "title", nullable = false, unique = true)
    private String title;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "content", nullable = false)
    @Lob
    private String content;
    @Column(name = "number_of_likes")
    private Integer likes;
    @Column(name = "liked_users")
    @ElementCollection(targetClass = String.class)
    private Set<String> likedUsers = new HashSet<>();

    @CreationTimestamp
    @Column(updatable = false, name = "create_date")
    private LocalDateTime createDate;
    @UpdateTimestamp
    @Column(updatable = true, name = "update_date")
    private LocalDateTime updateDate;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    @ElementCollection(targetClass = String.class)
    private Set<String> imageUrlSet = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;


    public void addImageURL(String imageURL) {
        imageUrlSet.add(imageURL);
    }

    public void removeImageURL(String imageURL) {
        imageUrlSet.remove(imageURL);
    }
}
