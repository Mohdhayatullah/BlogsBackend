package org.blogs.Blogs.repository;

import org.blogs.Blogs.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
}
