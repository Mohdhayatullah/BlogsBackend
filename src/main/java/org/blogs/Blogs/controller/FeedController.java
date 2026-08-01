package org.blogs.Blogs.controller;

import lombok.RequiredArgsConstructor;
import org.blogs.Blogs.dto.BlogResponseDTO;
import org.blogs.Blogs.service.BlogsService;
import org.blogs.Blogs.service.FeedService;
import org.blogs.Blogs.service.UserServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final UserServices userServices;
    private final BlogsService blogsService;

    @GetMapping
    public ResponseEntity<List<BlogResponseDTO>> getFeed() {
        Long userId = userServices.getCurrentProfile().getId();
        return ResponseEntity.ok(blogsService.toBlogDtos(feedService.getFeed(userId)));
    }
}
