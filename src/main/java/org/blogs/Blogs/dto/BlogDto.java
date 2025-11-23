package org.blogs.Blogs.dto;

import lombok.*;
import org.blogs.Blogs.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogDto {

    private Long id;
    private String title;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

//    private UserEntity user; instead of
    private Long userId;
    private String userName;

    private boolean published;

    private String slug;

    private double averageRating;

    private List<String> tags;

}
