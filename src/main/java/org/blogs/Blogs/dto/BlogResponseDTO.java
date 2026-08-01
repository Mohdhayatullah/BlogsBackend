package org.blogs.Blogs.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BlogResponseDTO {

    private Long id;
    private String title;
    private String description;

    private String imageUrl;      // Blog image
    private String userImageUrl;  // User profile image

    private String userName;
    private Long userId;

    private LocalDateTime createdAt;

    private boolean isPublished;

    private double averageRating;
    private long likeCount;
    private long viewCount;

    private boolean isFollower;
    private boolean isFollowing;

    private boolean likedByCurrentUser;
    private boolean followingAuthor;

    private List<String> tags;
}