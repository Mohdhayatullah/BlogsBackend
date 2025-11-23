package org.blogs.Blogs.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpDto {
    private Long id;
    private String fullName;

    private String email;

    private String password;

    private String otp;

    private String phoneNumber;
}
