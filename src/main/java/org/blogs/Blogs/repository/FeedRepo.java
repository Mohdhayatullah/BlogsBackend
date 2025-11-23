package org.blogs.Blogs.repository;


import org.blogs.Blogs.entity.FeedBack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedRepo extends JpaRepository<FeedBack, Long> {
    List<FeedBack> findByBlogId(Long blogId);
    List<FeedBack> findByUserId(Long userId);
    Optional<FeedBack> findByUserIdAndBlogId(Long userId, Long blogId);
}
