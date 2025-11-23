package org.blogs.Blogs.repository;


import org.blogs.Blogs.entity.BlogPost;
import org.blogs.Blogs.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
