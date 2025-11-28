package org.blogs.Blogs.repository;


import jakarta.transaction.Transactional;
import org.blogs.Blogs.entity.BlogPost;
import org.blogs.Blogs.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepo extends JpaRepository<BlogPost, Long> {

    // 1️⃣ Fetch all public blogs
    List<BlogPost> findByPublishedTrue();

    // 2️⃣ Fetch private blogs of a specific user
    List<BlogPost> findByUserAndPublishedFalse(UserEntity user);

    // 3️⃣ Optional: Fetch all blogs by a specific user (both public & private)
    List<BlogPost> findByUser(UserEntity user);

    @Modifying
    @Transactional
    @Query("UPDATE BlogPost b SET b.viewCount = b.viewCount + 1 WHERE b.id = :id")
    void incrementViewCount(@Param("id") Long id);

    List<BlogPost> findByUserInOrderByCreatedAtDesc(List<UserEntity> followingUsers);
}
