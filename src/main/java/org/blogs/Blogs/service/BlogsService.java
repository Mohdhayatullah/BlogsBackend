package org.blogs.Blogs.service;

import lombok.RequiredArgsConstructor;

import org.blogs.Blogs.dto.BlogDto;
import org.blogs.Blogs.dto.BlogResponseDTO;
import org.blogs.Blogs.dto.CloudinaryDto;
import org.blogs.Blogs.entity.BlogPost;
import org.blogs.Blogs.entity.FeedBack;
import org.blogs.Blogs.entity.UserEntity;
import org.blogs.Blogs.repository.BlogRepo;
import org.blogs.Blogs.repository.FeedRepo;
import org.blogs.Blogs.repository.UserRepository;
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
//    private final UserRepository userRepository;

    private final FeedRepo feedRepo;
    private final UserServices services;
    public final CloudinaryService cloudinaryService;
    private final EmailService emailService;

    @Value("${frontend.url}")
    private String frontend;

//    public void createBlogs(BlogDto blogDto, MultipartFile file) {
//        UserEntity user = services.getCurrentProfile();
//        if (user == null) {
//            throw new UsernameNotFoundException("User not found");
//        }
//        BlogPost blog = toBlogPost(blogDto);
//        blog.setUser(user); // link blog to logged-in user
//
//        CloudinaryDto cloudinaryDto = cloudinaryService.uploadOrReplaceImage(blog.getPublic_id(),file);
//        blog.setPublic_id(cloudinaryDto.getPublicId());
//        blog.setImagePath(cloudinaryDto.getSecureUrl());
//        blogRepo.save(blog);
//    }
public BlogResponseDTO createBlogs(BlogDto blogDto, MultipartFile file) {

    UserEntity user = services.getCurrentProfile();
    if (user == null) {
        throw new UsernameNotFoundException("User not found");
    }

    BlogPost blog = toBlogPost(blogDto);
    blog.setUser(user);

    if (file != null && !file.isEmpty()) {
        CloudinaryDto cloudinaryDto =
                cloudinaryService.uploadOrReplaceImage(blog.getPublic_id(),file);


        blog.setPublic_id(cloudinaryDto.getPublicId());
        blog.setImagePath(cloudinaryDto.getSecureUrl());
    }

    blogRepo.save(blog);
    BlogResponseDTO blogResponseDTO = toBlogDto(blog);
    this.emailService.sendBlogCreatedEmail(user.getEmail(),user.getFullName(),
            blog.getTitle(), frontend+"/blogs/"+blogResponseDTO.getId());
    return blogResponseDTO;
}


    public List<BlogResponseDTO> getAllBlogs() {
        List<BlogPost> blogPost = blogRepo.findByPublishedTrue();
        return blogPost.stream().map(this::toBlogDto).toList();
    }


    // Get blogs of currently logged-in user (private + public)
    public List<BlogResponseDTO> getUserBlogs() {
        UserEntity user = services.getCurrentProfile();
        List<BlogPost> blog = blogRepo.findByUser(user);
        return blog.stream()
                .map(this::toBlogDto)
                .toList();
    }


    public BlogResponseDTO getBlogsById(Long id) {
        BlogPost blog = blogRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Blog Not found"));
        BlogResponseDTO dto = toBlogDto(blog);
        dto.setAverageRating(feedRepo.findByBlogId(id)
                .stream()
                .mapToDouble(FeedBack::getRating)
                .average()
                .orElse(0.0));
        return dto;
    }



//    public BlogResponseDTO update(Long id, BlogDto blogDto, MultipartFile file) {
//        BlogPost blogs = blogRepo.findById(id)
//                .orElseThrow(()-> new IllegalArgumentException("Blog Not found"));
//        blogs.setTitle(blogDto.getTitle() != null ? blogDto.getTitle():blogs.getTitle());
//        blogs.setDescription(blogDto.getDescription() != null ? blogDto.getDescription():blogs.getDescription());
//        blogs.setTags(blogDto.getTags() !=null ? blogDto.getTags():blogs.getTags());
//        blogs.setPublished(blogDto.isPublished());
//
//        CloudinaryDto cloudinaryDto = cloudinaryService.uploadOrReplaceImage(blogs.getPublic_id(),file);
//        blogs.setPublic_id(cloudinaryDto.getPublicId());
//        blogs.setImagePath(cloudinaryDto.getSecureUrl());
//        blogRepo.save(blogs);
//        return toBlogDto(blogs);
//    }

    public BlogResponseDTO update(Long id, BlogDto blogDto, MultipartFile file) {

        BlogPost blogs = blogRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Blog Not found"));

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

        if (file != null && !file.isEmpty()) {
            CloudinaryDto cloudinaryDto =
                    cloudinaryService.uploadOrReplaceImage(blogs.getPublic_id(), file);

            blogs.setPublic_id(cloudinaryDto.getPublicId());
            blogs.setImagePath(cloudinaryDto.getSecureUrl());
        }

        blogRepo.save(blogs);
        return toBlogDto(blogs);
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

    // helping method
    private BlogResponseDTO toBlogDto(BlogPost entity) {
        return BlogResponseDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .userId(entity.getUser().getId())
                .userName(entity.getUser().getFullName())
                .createdAt(entity.getCreatedAt())
                .imageURl(entity.getImagePath())
                .viewCount(entity.getViewCount())
                .likeCount(entity.getLikeCount())
//                .published(entity.isPublished())
//                .slug(entity.getSlug())
                .averageRating(entity.getAverageRating())
                .tags(entity.getTags())
                .build();
    }


    private BlogPost toBlogPost(BlogDto dto) {
        BlogPost post = BlogPost.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .published(dto.getPublished())
                .slug(dto.getTitle().toLowerCase().replace(" ","-"))
                .tags(dto.getTags())
                .build();

        if (dto.getUserId() != null) {
            UserEntity user = new UserEntity();
            user.setId(dto.getUserId());
            post.setUser(user);
        }
        return post;
    }
}


