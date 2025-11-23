package org.blogs.Blogs.service;

import lombok.RequiredArgsConstructor;

import org.blogs.Blogs.dto.BlogDto;
import org.blogs.Blogs.entity.BlogPost;
import org.blogs.Blogs.entity.FeedBack;
import org.blogs.Blogs.entity.UserEntity;
import org.blogs.Blogs.repository.BlogRepo;
import org.blogs.Blogs.repository.FeedRepo;
import org.blogs.Blogs.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogsService {

    private final BlogRepo blogRepo;
    private final UserRepository userRepository;
    private final FeedRepo feedRepo;
    private final UserServices services;

    public BlogDto createBlogs(BlogDto blogDto) {
        UserEntity user = services.getCurrentProfile();
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        BlogPost blog = toBlogPost(blogDto);
        blog.setUser(user); // link blog to logged-in user

        blogRepo.save(blog);
        return toBlogDto(blog);
    }


//    private UserEntity getCurrentUser(SignUpDto sign) {
//        UserEntity newUser = userRepository.findByEmail(s)
//    }


    public List<BlogDto> getAllBlogs() {
        List<BlogPost> blogPost = blogRepo.findByPublishedTrue();
        return blogPost.stream().map(this::toBlogDto).toList();
    }


    // Get blogs of currently logged-in user (private + public)
    public List<BlogDto> getUserBlogs() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<BlogPost> blog = blogRepo.findByUser(user);
        return blog.stream().map(this::toBlogDto).toList();
    }



//    public BlogDto getBlogsById(Long id) {
//        BlogPost blog = blogRepo.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Blog Not Found"));
//        return toBlogDto(blog);
//    }

    public BlogDto getBlogsById(Long id) {
        BlogPost blog = blogRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Blog Not found"));
        BlogDto dto = toBlogDto(blog);
        dto.setAverageRating(feedRepo.findByBlogId(id)
                .stream()
                .mapToDouble(FeedBack::getRating)
                .average()
                .orElse(0.0));
        return dto;
    }



    public BlogDto update(Long id, BlogDto blogDto) {
        BlogPost blogs = blogRepo.findById(id).orElseThrow(()-> new IllegalArgumentException("Blog Not found"));
        blogs.setTitle(blogDto.getTitle() != null ? blogDto.getTitle():blogs.getTitle());
        blogs.setDescription(blogDto.getDescription() != null ? blogDto.getDescription():blogs.getDescription());
        blogs.setUpdatedAt(blogDto.getUpdatedAt() != null ? blogDto.getUpdatedAt() : blogs.getUpdatedAt());
        blogs.setSlug(blogDto.getSlug() != null ? blogDto.getSlug() : blogs.getSlug());
        blogs.setTags(blogDto.getTags() !=null ? blogDto.getTags():blogs.getTags());
        blogs.setPublished(blogDto.isPublished());
        blogRepo.save(blogs);
        return toBlogDto(blogs);
    }

    public void delete(Long id) throws AccessDeniedException {
        BlogPost blog = blogRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found"));
        UserEntity currentUser = services.getCurrentProfile();

        if (!blog.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You cannot delete someone else's blog");
        }
        blogRepo.delete(blog);
    }


//    public List<BlogDto> getMyBlogs(String user) {
//        List<BlogPost> blogs = blogRepo.findByUser(user);
//        return blogs.stream().map(this::toBlogDto).toList();
//    }

//    public List<FeedBack> feedBackList(){
//
//    }


    // helping method
    private BlogDto toBlogDto(BlogPost entity) {
        return BlogDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .userId(entity.getUser().getId())
                .userName(entity.getUser().getFullName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .published(entity.isPublished())
                .slug(entity.getSlug())
                .averageRating(entity.getAverageRating())
                .tags(entity.getTags())
                .build();
    }


    private BlogPost toBlogPost(BlogDto dto) {
        BlogPost post = BlogPost.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .published(dto.isPublished())
                .slug(dto.getSlug())
                .averageRating(dto.getAverageRating())
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


