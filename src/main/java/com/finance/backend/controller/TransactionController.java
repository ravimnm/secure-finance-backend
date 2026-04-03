package com.finance.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finance.backend.dto.PagedResponse;
import com.finance.backend.entity.Transaction;
import com.finance.backend.service.TransactionService;
@RestController
@RequestMapping("/transactions")
public class TransactionController {
	private final TransactionService service;
	public TransactionController(TransactionService service) {
		this.service=service;
	}
	
	//Create
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public Transaction create(@RequestBody Transaction t, Authentication auth) {
		return service.createTransaction(t, auth.getName());
	}
	
	// Get All Transactions

	@GetMapping
	public PagedResponse<Transaction> getAll(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        Authentication auth) {

	    Page<Transaction> result = service.getUserTransactions(auth.getName(), page, size);

	    return new PagedResponse<>(
	            result.getContent(),
	            result.getNumber(),
	            result.getSize(),
	            result.getTotalElements(),
	            result.getTotalPages()
	    );
	}
	
	
	//Update Transaction
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public Transaction update(@PathVariable String id, @RequestBody Transaction t, Authentication auth) {
		return service.updateTransaction(id, t, auth.getName());
	}
	
	//Delete 
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public String delete(@PathVariable String id, Authentication auth) {
		service.deleteTransaction(id, auth.getName());
		return "Trasacntion deleted Successfully";
	}
	
	//Filter by Type
	@GetMapping("/filter")
	@PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
	public Page<Transaction> filterByType(
	        @RequestParam String type,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        Authentication auth) {

	    return service.getTransactionsByType(auth.getName(), type, page, size);
	}
	
	//Monthly Trends
	@GetMapping("/trends")
	@PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
	public Map<String, Map<String,Double>> trends(Authentication auth){
		return service.getMonthlyTrends(auth.getName());
	}
	
	//Monthly Trends
	@GetMapping("/trendsAgg")
	@PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
	public Map<String, Map<String,Double>> trendsAgg(Authentication auth){
		return service.getMonthlyTrends(auth.getName());
	}
	
}
