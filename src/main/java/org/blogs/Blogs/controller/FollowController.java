package org.blogs.Blogs.controller;

import lombok.RequiredArgsConstructor;
import org.blogs.Blogs.dto.FollowResponseDTO;
import org.blogs.Blogs.service.FollowService;
import org.blogs.Blogs.service.UserServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final UserServices userServices;

    @PostMapping("/{id}/follow")
    public ResponseEntity<FollowResponseDTO> followUser(@PathVariable Long id) {
        Long followerId = userServices.getCurrentProfile().getId();
        return ResponseEntity.ok(followService.followUser(followerId, id));
    }

    @DeleteMapping("/{id}/follow")
    public ResponseEntity<FollowResponseDTO> unfollowUser(@PathVariable Long id) {
        Long followerId = userServices.getCurrentProfile().getId();
        return ResponseEntity.ok(followService.unfollowUser(followerId, id));
    }

    @GetMapping("/{id}/followers/count")
    public ResponseEntity<Long> getFollowers(@PathVariable Long id) {
        return ResponseEntity.ok(followService.getFollowersCount(id));
    }

    @GetMapping("/{id}/following/count")
    public ResponseEntity<Long> getFollowing(@PathVariable Long id) {
        return ResponseEntity.ok(followService.getFollowingCount(id));
    }
}
