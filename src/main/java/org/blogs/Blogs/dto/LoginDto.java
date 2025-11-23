package org.blogs.Blogs.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    private String email;
    private String password;
    private String Token;
}
