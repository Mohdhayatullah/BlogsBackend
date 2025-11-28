package org.blogs.Blogs.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDto {


    private Long id;
    private String fullName;

    private String email;

    private String phoneNumber;

    private String photoUrl;
}
