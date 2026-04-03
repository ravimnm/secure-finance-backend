package com.finance.backend.entity;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Document(collection="transactions")
public class Transaction {
	@Id
	private String id;
	@NotNull
	private double amount;
	private String category;
	private LocalDate date;
	private String description;
	private String userId;
	@NotBlank
	private String type;
	
	public Transaction(String id, double amount, String category, LocalDate date, String description, String userId,
			String type) {
		super();
		this.id = id;
		this.amount = amount;
		this.category = category;
		this.date = date;
		this.description = description;
		this.userId = userId;
		this.type = type;
	}



	@Override
	public String toString() {
		return "Transaction [id=" + id + ", amount=" + amount + ", category=" + category + ", date=" + date
				+ ", description=" + description + ", userId=" + userId + ", type=" + type + "]";
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public double getAmount() {
		return amount;
	}



	public void setAmount(double amount) {
		this.amount = amount;
	}



	public String getCategory() {
		return category;
	}



	public void setCategory(String category) {
		this.category = category;
	}



	public LocalDate getDate() {
		return date;
	}



	public void setDate(LocalDate date) {
		this.date = date;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public String getUserId() {
		return userId;
	}



	public void setUserId(String userId) {
		this.userId = userId;
	}



	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}



	public Transaction() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
