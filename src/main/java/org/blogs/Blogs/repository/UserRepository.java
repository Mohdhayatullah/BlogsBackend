package org.blogs.Blogs.repository;


import org.blogs.Blogs.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findByEmail(String email);    // to fetch the user by email
    boolean existsByEmail(String email);

//    @Query("SELECT u.id FROM UserEntity u WHERE u.email = :email")
//    Long findIdByEmail(@Param("email") String email);
}
