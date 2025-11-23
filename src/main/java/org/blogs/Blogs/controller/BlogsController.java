package org.blogs.Blogs.controller;



import lombok.RequiredArgsConstructor;
import org.blogs.Blogs.dto.BlogDto;
import org.blogs.Blogs.entity.UserEntity;
import org.blogs.Blogs.service.BlogsService;
import org.blogs.Blogs.service.UserServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/blogs")
public class BlogsController {

    private final BlogsService blogsService;
    private final UserServices services;

    @GetMapping("/test")
    public String getData(){
        return "I am running";
    }

//    @PostMapping
//    public ResponseEntity<BlogDto> createBlogs(@RequestBody BlogDto blogDto){
//        try{
//            return ResponseEntity.status(HttpStatus.CREATED).body(blogsService.createBlogs(blogDto));
//        }catch (Exception e){
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    @PostMapping
    public ResponseEntity<BlogDto> createBlog(@RequestBody BlogDto dto) {
        try{
        BlogDto created = blogsService.createBlogs(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);}
        catch (Exception e){
            System.out.println("During appears error CREATE BLOGS "+ e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping
    public ResponseEntity<List<BlogDto>> getAllBlogs(){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(blogsService.getAllBlogs());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/private")
    public ResponseEntity<List<BlogDto>> getMyBlogs(){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(blogsService.getUserBlogs());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<BlogDto> getBlogsById(@PathVariable Long id){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(blogsService.getBlogsById(id));
        }catch (Exception e){
            System.out.println("internal server error+"+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping("/{put}")
    public ResponseEntity<BlogDto> update(@PathVariable(name ="put") Long id ,@RequestBody BlogDto blogDto){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(blogsService.update(id,blogDto));
        }catch (Exception e){
            System.out.println("internal server error+"+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{put}")
    public ResponseEntity<Void> update(@PathVariable(name ="put") Long id){
        try{
            blogsService.delete(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PatchMapping
    public ResponseEntity<UserEntity> forgetPassword(@RequestParam String pass){
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(services.forgetPassword(pass));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
