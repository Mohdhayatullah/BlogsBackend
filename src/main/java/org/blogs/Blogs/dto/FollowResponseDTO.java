package org.blogs.Blogs.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FollowResponseDTO {
    private Long userId;
    private long followersCount;
    private long followingCount;
    private boolean following;
    private boolean follower;
    private String message;
}
