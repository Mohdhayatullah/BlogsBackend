package org.blogs.Blogs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "title must be required")
    private String title;
    @NotBlank(message = "title must be required")
    private String description;
    private Long userId;
    private String userName;
    private Boolean published;
    private String slug;
    private List<String> tags;
}
