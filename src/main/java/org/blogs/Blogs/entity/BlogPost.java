package org.blogs.Blogs.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "blog_tbl")
public class BlogPost{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Size(max = 1000000, message = "Description to long {up to 1000 words}")
    @Column(length = 1000000, columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private boolean published;

    private String slug;

    @ElementCollection
    private List<String> tags = new ArrayList<>();

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<FeedBack> feedBacks = new ArrayList<>();

    private double averageRating;

}
