package com.sougata.auth_service.repository;

import com.sougata.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailOrUsername(String email, String userName);
    boolean existsByUsername(String username);
}
