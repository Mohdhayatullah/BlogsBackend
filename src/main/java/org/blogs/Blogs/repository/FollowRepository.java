package org.blogs.Blogs.repository;

import org.blogs.Blogs.entity.Follow;
import org.blogs.Blogs.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowing(UserEntity follower, UserEntity following);

    void deleteByFollowerAndFollowing(UserEntity follower, UserEntity following);

    Optional<Follow> findByFollowerAndFollowing(UserEntity follower, UserEntity following);

    List<Follow> findByFollower(UserEntity follower);

    List<Follow> findByFollowing(UserEntity following);

    long countByFollowing(UserEntity following);

    long countByFollower(UserEntity follower);
}
