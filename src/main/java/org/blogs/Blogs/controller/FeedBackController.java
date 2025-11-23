package org.blogs.Blogs.controller;


import lombok.RequiredArgsConstructor;
import org.blogs.Blogs.entity.FeedBack;
import org.blogs.Blogs.service.FeedBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feedback")
public class FeedBackController {

    private final FeedBackService feedBackService;

    @PostMapping
    public ResponseEntity<FeedBack> createFeedback(
            @RequestParam Long blogId,
            @RequestParam double rating,
            @RequestParam String comment) {

        try {
            FeedBack feedback = feedBackService.createFeedback(blogId, rating, comment);
            return ResponseEntity.ok(feedback);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/blog/{blogId}")
    public ResponseEntity<List<FeedBack>> getFeedbackByBlog(@PathVariable Long blogId) {
        List<FeedBack> feedbacks = feedBackService.getFeedbackByBlogId(blogId);
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FeedBack>> getFeedbackByUser(@PathVariable Long userId) {
        List<FeedBack> feedbacks = feedBackService.getFeedbackByUserId(userId);
        return ResponseEntity.ok(feedbacks);
    }

    @PutMapping("/{feedbackId}")
    public ResponseEntity<FeedBack> updateFeedback(
            @PathVariable Long feedbackId,
            @RequestParam double rating,
            @RequestParam String comment) {

        try {
            FeedBack feedback = feedBackService.updateFeedback(feedbackId, rating, comment);
            return ResponseEntity.ok(feedback);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long feedbackId) {
        try {
            feedBackService.deleteFeedback(feedbackId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/blog/{blogId}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long blogId) {
        double averageRating = feedBackService.getAverageRatingForBlog(blogId);
        return ResponseEntity.ok(averageRating);
    }
}

