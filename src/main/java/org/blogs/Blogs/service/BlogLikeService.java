package org.blogs.Blogs.service;

import lombok.RequiredArgsConstructor;
import org.blogs.Blogs.dto.BlogLikeResponseDTO;
import org.blogs.Blogs.entity.BlogLike;
import org.blogs.Blogs.entity.BlogPost;
import org.blogs.Blogs.entity.UserEntity;
import org.blogs.Blogs.repository.BlogLikeRepository;
import org.blogs.Blogs.repository.BlogRepo;
import org.blogs.Blogs.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlogLikeService {

    private final BlogLikeRepository blogLikeRepository;
    private final BlogRepo blogRepository;
    private final UserRepository userRepository;

    public BlogLikeResponseDTO likeBlog(Long blogId, Long userId) {
        BlogPost blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (blogLikeRepository.findByBlogAndUser(blog, user).isPresent()) {
            throw new RuntimeException("Already liked");
        }

        BlogLike like = BlogLike.builder()
                .blog(blog)
                .user(user)
                .build();

        blogLikeRepository.save(like);
        return buildLikeResponse(blog, true, "Liked successfully");
    }

    public BlogLikeResponseDTO unlikeBlog(Long blogId, Long userId) {
        BlogPost blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BlogLike like = blogLikeRepository.findByBlogAndUser(blog, user)
                .orElseThrow(() -> new RuntimeException("Like not found"));

        blogLikeRepository.delete(like);
        return buildLikeResponse(blog, false, "Unliked successfully");
    }

    public long getLikeCount(Long blogId) {
        BlogPost blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        long likeCount = blogLikeRepository.countByBlog(blog);
        if (blog.getLikeCount() != likeCount) {
            blog.setLikeCount(likeCount);
            blogRepository.save(blog);
        }
        return likeCount;
    }

    private BlogLikeResponseDTO buildLikeResponse(BlogPost blog, boolean liked, String message) {
        long likeCount = blogLikeRepository.countByBlog(blog);
        blog.setLikeCount(likeCount);
        blogRepository.save(blog);

        return BlogLikeResponseDTO.builder()
                .blogId(blog.getId())
                .likeCount(likeCount)
                .liked(liked)
                .message(message)
                .build();
    }
}
