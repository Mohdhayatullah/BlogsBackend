package org.blogs.Blogs.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedBackService {

    private final FeedRepo feedBackRepository;
    private final BlogRepo blogPostRepository;
    private final UserRepository userRepository;

    public FeedBack createFeedback(Long blogId, double rating, String comment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        BlogPost blog = blogPostRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog post not found"));

        Optional<FeedBack> existingFeedback = feedBackRepository.findByUserIdAndBlogId(user.getId(), blogId);
        if (existingFeedback.isPresent()) {
            throw new RuntimeException("User has already provided feedback for this blog post");
        }

        FeedBack feedback = FeedBack.builder()
                .user(user)
                .rating(rating)
                .comment(comment)
                .blog(blog)
                .build();

        FeedBack savedFeedback = feedBackRepository.save(feedback);
        updateBlogAverageRating(blog);
        return savedFeedback;
    }

    public List<FeedBack> getFeedbackByBlogId(Long blogId) {
        return feedBackRepository.findByBlogId(blogId);
    }

    public List<FeedBack> getFeedbackByUserId(Long userId) {
        return feedBackRepository.findByUserId(userId);
    }

    public FeedBack updateFeedback(Long feedbackId, double rating, String comment) {
        FeedBack feedback = feedBackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        feedback.setRating(rating);
        feedback.setComment(comment);
        FeedBack updatedFeedback = feedBackRepository.save(feedback);
        updateBlogAverageRating(feedback.getBlog());
        return updatedFeedback;
    }

    public void deleteFeedback(Long feedbackId) {
        FeedBack feedback = feedBackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        BlogPost blog = feedback.getBlog();
        feedBackRepository.delete(feedback);
        updateBlogAverageRating(blog);
    }

    public double getAverageRatingForBlog(Long blogId) {
        List<FeedBack> feedbacks = feedBackRepository.findByBlogId(blogId);
        return feedbacks.stream()
                .mapToDouble(FeedBack::getRating)
                .average()
                .orElse(0.0);
    }

    private void updateBlogAverageRating(BlogPost blog) {
        double averageRating = getAverageRatingForBlog(blog.getId());
        blog.setAverageRating(averageRating);
        blogPostRepository.save(blog);
    }
}
