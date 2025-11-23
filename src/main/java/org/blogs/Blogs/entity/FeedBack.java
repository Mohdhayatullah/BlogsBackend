package org.blogs.Blogs.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "feedback_tbl")
public class FeedBack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private double rating;

    @Size(max = 15000)
    @Column(length = 15000, columnDefinition = "TEXT")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "blog_id")
    private BlogPost blog;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
