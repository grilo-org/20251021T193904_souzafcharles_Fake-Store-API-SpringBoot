package com.github.souzafcharles.api.endpoint.user.repository;

import com.github.souzafcharles.api.endpoint.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByUsernameContainingIgnoreCase(String keyword);
    long countByUsernameIsNotNull();
}
