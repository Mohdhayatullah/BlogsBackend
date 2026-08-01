package org.blogs.Blogs.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlogLikeResponseDTO {
    private Long blogId;
    private long likeCount;
    private boolean liked;
    private String message;
}
