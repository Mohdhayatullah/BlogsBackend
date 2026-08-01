package org.blogs.Blogs.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.blogs.Blogs.dto.FollowResponseDTO;
import org.blogs.Blogs.entity.Follow;
import org.blogs.Blogs.entity.UserEntity;
import org.blogs.Blogs.repository.FollowRepository;
import org.blogs.Blogs.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowResponseDTO followUser(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new RuntimeException("You cannot follow yourself");
        }

        UserEntity follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));

        UserEntity following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new RuntimeException("Already following");
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);
        return buildFollowResponse(following, true, "Followed successfully");
    }

    public FollowResponseDTO unfollowUser(Long followerId, Long followingId) {
        UserEntity follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));

        UserEntity following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new RuntimeException("You are not following this user"));

        followRepository.delete(follow);
        return buildFollowResponse(following, false, "Unfollowed successfully");
    }

    public long getFollowersCount(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followRepository.countByFollowing(user);
    }

    public long getFollowingCount(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followRepository.countByFollower(user);
    }

    private FollowResponseDTO buildFollowResponse(UserEntity followingUser, boolean following, String message) {
        return FollowResponseDTO.builder()
                .userId(followingUser.getId())
                .followersCount(followRepository.countByFollowing(followingUser))
                .followingCount(followRepository.countByFollower(followingUser))
                .following(following)
                .message(message)
                .build();
    }
}
