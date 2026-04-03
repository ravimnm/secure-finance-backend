package com.finance.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.finance.backend.entity.Role;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(String name);
}