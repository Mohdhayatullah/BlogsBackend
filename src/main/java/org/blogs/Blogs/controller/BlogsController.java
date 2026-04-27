package org.blogs.Blogs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.blogs.Blogs.dto.BlogDto;
import org.blogs.Blogs.dto.BlogResponseDTO;
import org.blogs.Blogs.entity.UserEntity;
import org.blogs.Blogs.service.BlogViewService;
import org.blogs.Blogs.service.BlogsService;
import org.blogs.Blogs.service.UserServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/blogs")
public class BlogsController {

    private final BlogsService blogsService;
    private final BlogViewService blogViewService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createBlog(
            @RequestPart("data") String data,
            @RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {
        System.out.println(data);
        ObjectMapper objectMapper = new ObjectMapper();
        BlogDto dto = objectMapper.readValue(data,BlogDto.class);
        blogsService.createBlogs(dto, file);
        return ResponseEntity.status(HttpStatus.CREATED).body("Blog Successfully created");
    }

    // ✅ GET ALL BLOGS
    @GetMapping
    public ResponseEntity<List<BlogResponseDTO>> getAllBlogs() {
        return ResponseEntity.ok(blogsService.getAllBlogs());
    }

    // ✅ GET MY BLOGS
    @GetMapping("/private")
    public ResponseEntity<List<BlogResponseDTO>> getMyBlogs() {
        return ResponseEntity.ok(blogsService.getUserBlogs());
    }

    // ✅ GET BLOG BY ID + VIEW TRACK
    @GetMapping("/{id}")
    public ResponseEntity<BlogResponseDTO> getBlogById(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId,
            HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();
        blogViewService.addView(id, userId, ipAddress);

        return ResponseEntity.ok(blogsService.getBlogsById(id));
    }

    // ✅ UPDATE BLOG
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<BlogResponseDTO> updateBlog(
            @PathVariable Long id,
            @RequestPart("data") String data,
            @RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        BlogDto dto = objectMapper.readValue(data,BlogDto.class);
        BlogResponseDTO response = blogsService.update(id, dto, file);
        return ResponseEntity.ok(response);
    }

    // ✅ DELETE BLOG
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long id) {
        blogsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}