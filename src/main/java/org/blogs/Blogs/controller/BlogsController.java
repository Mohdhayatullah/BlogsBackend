package org.blogs.Blogs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.blogs.Blogs.dto.BlogDto;
import org.blogs.Blogs.dto.BlogResponseDTO;
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
    private final UserServices userServices;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<BlogResponseDTO> createBlog(
            @RequestPart("data") String data,
            @RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        BlogDto dto = objectMapper.readValue(data, BlogDto.class);
        BlogResponseDTO response = blogsService.createBlogs(dto, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BlogResponseDTO>> getAllBlogs() {
        return ResponseEntity.ok(blogsService.getAllBlogs());
    }

    @GetMapping("/private")
    public ResponseEntity<List<BlogResponseDTO>> getMyBlogs() {
        return ResponseEntity.ok(blogsService.getUserBlogs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogResponseDTO> getBlogById(
            @PathVariable Long id,
            HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();
        Long viewerId = null;
        try {
            viewerId = userServices.getCurrentProfile().getId();
        } catch (RuntimeException ignored) {
        }

        blogViewService.addView(id, viewerId, ipAddress);
        return ResponseEntity.ok(blogsService.getBlogsById(id));
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<BlogResponseDTO> updateBlog(
            @PathVariable Long id,
            @RequestPart("data") String data,
            @RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        BlogDto dto = objectMapper.readValue(data, BlogDto.class);
        BlogResponseDTO response = blogsService.update(id, dto, file);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long id) {
        blogsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
