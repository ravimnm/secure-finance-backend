package com.finance.backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.finance.backend.entity.Transaction;

public interface TransactionRepository extends MongoRepository<Transaction,String>{
	Page<Transaction> findByUserId(String userId,Pageable pageable);
	Page<Transaction> findByUserIdAndType(String userId, String type,Pageable pageable);
}
