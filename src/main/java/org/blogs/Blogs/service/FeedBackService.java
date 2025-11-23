package org.blogs.Blogs.service;

import jakarta.transaction.Transactional;

import org.blogs.Blogs.entity.BlogPost;
import org.blogs.Blogs.entity.FeedBack;
import org.blogs.Blogs.entity.UserEntity;
import org.blogs.Blogs.repository.BlogRepo;
import org.blogs.Blogs.repository.FeedRepo;
import org.blogs.Blogs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FeedBackService {

    @Autowired
    private FeedRepo feedBackRepository;

    @Autowired
    private BlogRepo blogPostRepository;

    @Autowired
    private UserRepository userRepository;

    public FeedBack createFeedback(Long blogId, double rating, String comment) {


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        BlogPost blog = blogPostRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog post not found"));

        // Check if user already gave feedback for this blog
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
        return feedBackRepository.save(feedback);
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
        return feedBackRepository.save(feedback);
    }

    public void deleteFeedback(Long feedbackId) {
        if (!feedBackRepository.existsById(feedbackId)) {
            throw new RuntimeException("Feedback not found");
        }
        feedBackRepository.deleteById(feedbackId);
    }

    public double getAverageRatingForBlog(Long blogId) {
        List<FeedBack> feedbacks = feedBackRepository.findByBlogId(blogId);
        return feedbacks.stream()
                .mapToDouble(FeedBack::getRating)
                .average()
                .orElse(0.0);
    }
}
