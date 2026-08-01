package org.blogs.Blogs.controller;

import lombok.RequiredArgsConstructor;
import org.blogs.Blogs.dto.BlogLikeResponseDTO;
import org.blogs.Blogs.service.BlogLikeService;
import org.blogs.Blogs.service.UserServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogLikeController {

    private final BlogLikeService blogLikeService;
    private final UserServices userServices;

    @PostMapping("/{blogId}/like")
    public ResponseEntity<BlogLikeResponseDTO> likeBlog(@PathVariable Long blogId) {
        Long userId = userServices.getCurrentProfile().getId();
        return ResponseEntity.ok(blogLikeService.likeBlog(blogId, userId));
    }

    @DeleteMapping("/{blogId}/like")
    public ResponseEntity<BlogLikeResponseDTO> unlikeBlog(@PathVariable Long blogId) {
        Long userId = userServices.getCurrentProfile().getId();
        return ResponseEntity.ok(blogLikeService.unlikeBlog(blogId, userId));
    }

    @GetMapping("/{blogId}/likes/count")
    public ResponseEntity<Long> getLikes(@PathVariable Long blogId) {
        return ResponseEntity.ok(blogLikeService.getLikeCount(blogId));
    }
}
