//package org.blogs.Blogs.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//@Entity
//@Table(name = "profile_tbl")
//public class Profile {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
//
//    @OneToOne
//    @JoinColumn(name = "user_id", referencedColumnName = "id")
//    private UserEntity user;
//
//    private String PhotoUrl;
//
////    public Profile(UserEntity user, String photoUrl) {
////        this.user = user;
////        this.id = id;
////        PhotoUrl = photoUrl;
////    }
//}
