package org.blogs.Blogs.service;

import lombok.RequiredArgsConstructor;
import org.blogs.Blogs.dto.BlogDto;
import org.blogs.Blogs.dto.BlogResponseDTO;
import org.blogs.Blogs.dto.CloudinaryDto;
import org.blogs.Blogs.entity.BlogPost;
import org.blogs.Blogs.entity.FeedBack;
import org.blogs.Blogs.entity.UserEntity;
import org.blogs.Blogs.repository.BlogLikeRepository;
import org.blogs.Blogs.repository.BlogRepo;
import org.blogs.Blogs.repository.FeedRepo;
import org.blogs.Blogs.repository.FollowRepository;
import org.blogs.Blogs.util.CloudinaryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogsService {

    private final BlogRepo blogRepo;
    private final FeedRepo feedRepo;
    private final UserServices services;
    public final CloudinaryService cloudinaryService;
    private final EmailService emailService;
    private final BlogLikeRepository blogLikeRepository;
    private final FollowRepository followRepository;

    @Value("${frontend.url}")
    private String frontend;

    public BlogResponseDTO createBlogs(BlogDto blogDto, MultipartFile file) {
        UserEntity user = services.getCurrentProfile();
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        BlogPost blog = toBlogPost(blogDto);
        blog.setUser(user);

        if (file != null && !file.isEmpty()) {
            CloudinaryDto cloudinaryDto =
                    cloudinaryService.uploadOrReplaceImage(blog.getPublic_id(), file);
            blog.setPublic_id(cloudinaryDto.getPublicId());
            blog.setImagePath(cloudinaryDto.getSecureUrl());
        }

        blogRepo.save(blog);
        BlogResponseDTO blogResponseDTO = toBlogDto(blog, user);
        emailService.sendBlogCreatedEmail(
                user.getEmail(),
                user.getFullName(),
                blog.getTitle(),
                frontend + "/blogs/" + blogResponseDTO.getId()
        );
        return blogResponseDTO;
    }

    public List<BlogResponseDTO> getAllBlogs() {
        List<BlogPost> blogPost = blogRepo.findByPublishedTrue();
        UserEntity currentUser = getCurrentUserOrNull();
        return blogPost.stream().map(blog -> toBlogDto(blog, currentUser)).toList();
    }

    public List<BlogResponseDTO> getUserBlogs() {
        UserEntity user = services.getCurrentProfile();
        List<BlogPost> blog = blogRepo.findByUser(user);
        return blog.stream()
                .map(post -> toBlogDto(post, user))
                .toList();
    }

    public BlogResponseDTO getBlogsById(Long id) {
        BlogPost blog = blogRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Blog Not found"));
        BlogResponseDTO dto = toBlogDto(blog, getCurrentUserOrNull());
        dto.setAverageRating(feedRepo.findByBlogId(id)
                .stream()
                .mapToDouble(FeedBack::getRating)
                .average()
                .orElse(0.0));
        return dto;
    }

    public BlogResponseDTO update(Long id, BlogDto blogDto, MultipartFile file) {
        BlogPost blogs = blogRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Blog Not found"));
        UserEntity currentUser = services.getCurrentProfile();

        if (!blogs.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You cannot update someone else's blog");
        }

        if (blogDto.getTitle() != null) {
            blogs.setTitle(blogDto.getTitle());
        }

        if (blogDto.getDescription() != null) {
            blogs.setDescription(blogDto.getDescription());
        }

        if (blogDto.getTags() != null) {
            blogs.setTags(blogDto.getTags());
        }

        if (blogDto.getPublished() != null) {
            blogs.setPublished(blogDto.getPublished());
        }

        if (blogDto.getSlug() != null && !blogDto.getSlug().isBlank()) {
            blogs.setSlug(slugify(blogDto.getSlug()));
        } else if ((blogs.getSlug() == null || blogs.getSlug().isBlank()) && blogs.getTitle() != null) {
            blogs.setSlug(slugify(blogs.getTitle()));
        }

        if (file != null && !file.isEmpty()) {
            CloudinaryDto cloudinaryDto =
                    cloudinaryService.uploadOrReplaceImage(blogs.getPublic_id(), file);

            blogs.setPublic_id(cloudinaryDto.getPublicId());
            blogs.setImagePath(cloudinaryDto.getSecureUrl());
        }

        blogRepo.save(blogs);
        return toBlogDto(blogs, currentUser);
    }

    public void delete(Long id) {
        BlogPost blog = blogRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found"));
        UserEntity currentUser = services.getCurrentProfile();

        if (!blog.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You cannot delete someone else's blog");
        }
        blogRepo.delete(blog);
    }

    public List<BlogResponseDTO> toBlogDtos(List<BlogPost> blogs) {
        UserEntity currentUser = getCurrentUserOrNull();
        return blogs.stream()
                .map(blog -> toBlogDto(blog, currentUser))
                .toList();
    }

    private BlogResponseDTO toBlogDto(BlogPost entity, UserEntity currentUser) {
        boolean followingAuthor = currentUser != null
                && !currentUser.getId().equals(entity.getUser().getId())
                && followRepository.existsByFollowerAndFollowing(currentUser, entity.getUser());
        boolean likedByCurrentUser = currentUser != null
                && blogLikeRepository.findByBlogAndUser(entity, currentUser).isPresent();

        boolean isFollowing = currentUser != null
                && followRepository.existsByFollowerAndFollowing(
                currentUser,
                entity.getUser());

        boolean isFollower = currentUser != null
                && followRepository.existsByFollowerAndFollowing(
                entity.getUser(),
                currentUser);


        return BlogResponseDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .userId(entity.getUser().getId())
                .userName(entity.getUser().getFullName())
                .userImageUrl(entity.getUser().getImageUrl())
                .imageUrl(entity.getImagePath())
                .createdAt(entity.getCreatedAt())
                .viewCount(entity.getViewCount())
                .likeCount(entity.getLikeCount())
                .isPublished(entity.isPublished())
                .averageRating(entity.getAverageRating())
                .likedByCurrentUser(likedByCurrentUser)
                .followingAuthor(isFollowing)
                .isFollower(isFollower)        // replace with your actual logic
                .isFollowing(followingAuthor)
                .tags(entity.getTags())
                .build();
    }

    private BlogPost toBlogPost(BlogDto dto) {
        BlogPost post = BlogPost.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .published(dto.getPublished() != null ? dto.getPublished() : true)
                .slug(dto.getSlug() != null && !dto.getSlug().isBlank() ? slugify(dto.getSlug()) : slugify(dto.getTitle()))
                .tags(dto.getTags())
                .build();

        if (dto.getUserId() != null) {
            UserEntity user = new UserEntity();
            user.setId(dto.getUserId());
            post.setUser(user);
        }
        return post;
    }

    private UserEntity getCurrentUserOrNull() {
        try {
            return services.getCurrentProfile();
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private String slugify(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }
}
