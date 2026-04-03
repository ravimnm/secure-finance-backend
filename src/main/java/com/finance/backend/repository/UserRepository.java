package com.finance.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.finance.backend.entity.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
}