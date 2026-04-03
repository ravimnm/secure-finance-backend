package com.finance.backend.service;

import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.bson.Document;

import com.finance.backend.entity.Transaction;
import com.finance.backend.entity.User;
import com.finance.backend.repository.TransactionRepository;
import com.finance.backend.repository.UserRepository;

@Service
public class TransactionService {

    private final TransactionRepository repo;
    private final UserRepository userRepo;
    private final MongoTemplate mongoTemplate;

    public TransactionService(TransactionRepository repo,
                              UserRepository userRepo,
                              MongoTemplate mongoTemplate) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.mongoTemplate = mongoTemplate;
    }

    //  Create
    public Transaction createTransaction(Transaction t, String username) {

        User user = getUser(username);

        if (t.getAmount() <= 0) {
            throw new RuntimeException("Amount must be positive");
        }

        if (!List.of("income", "expense").contains(t.getType().toLowerCase())) {
            throw new RuntimeException("Type must be 'income' or 'expense'");
        }

        t.setUserId(user.getId());

        return repo.save(t);
    }

    //  Get All (Paginated + Sorted)
    public Page<Transaction> getUserTransactions(String username, int page, int size) {
        User user = getUser(username);

        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

        return repo.findByUserId(user.getId(), pageable);
    }

    //  Update
    public Transaction updateTransaction(String id, Transaction updated, String username) {

        User user = getUser(username);

        Transaction existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!existing.getUserId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        existing.setAmount(updated.getAmount());
        existing.setType(updated.getType());
        existing.setCategory(updated.getCategory());
        existing.setDate(updated.getDate());
        existing.setDescription(updated.getDescription());

        return repo.save(existing);
    }

    //  Delete
    public void deleteTransaction(String id, String username) {

        User user = getUser(username);

        Transaction existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!existing.getUserId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        repo.deleteById(id);
    }

    // Filter by type (Paginated)
    public Page<Transaction> getTransactionsByType(String username, String type, int page, int size) {

        User user = getUser(username);

        if (!List.of("income", "expense").contains(type.toLowerCase())) {
            throw new RuntimeException("Invalid type");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

        return repo.findByUserIdAndType(user.getId(), type.toLowerCase(), pageable);
    }

    // Summary (Aggregation )
    public Map<String, Double> getSummary(String username) {

        User user = getUser(username);

        MatchOperation match = Aggregation.match(
                Criteria.where("userId").is(user.getId())
        );

        GroupOperation group = Aggregation.group("type")
                .sum("amount").as("total");

        Aggregation aggregation = Aggregation.newAggregation(match, group);

        AggregationResults<Document> results =
                mongoTemplate.aggregate(aggregation, "transactions", Document.class);

        double income = 0;
        double expense = 0;

        for (Document doc : results.getMappedResults()) {
            String type = doc.getString("_id");
            double total = doc.getDouble("total");

            if ("income".equalsIgnoreCase(type)) {
                income = total;
            } else {
                expense = total;
            }
        }

        return Map.of(
                "totalIncome", income,
                "totalExpense", expense,
                "netBalance", income - expense
        );
    }

    //  Category Summary (Aggregation)
    public Map<String, Double> getCategorySummary(String username) {

        User user = getUser(username);

        MatchOperation match = Aggregation.match(
                Criteria.where("userId").is(user.getId())
        );

        GroupOperation group = Aggregation.group("category")
                .sum("amount").as("total");

        Aggregation aggregation = Aggregation.newAggregation(match, group);

        AggregationResults<Document> results =
                mongoTemplate.aggregate(aggregation, "transactions", Document.class);

        Map<String, Double> categoryMap = new HashMap<>();

        for (Document doc : results.getMappedResults()) {
            categoryMap.put(doc.getString("_id"), doc.getDouble("total"));
        }

        return categoryMap;
    }

    // Monthly Trends 
    public Map<String, Map<String, Double>> getMonthlyTrends(String username) {

        User user = getUser(username);

        MatchOperation match = Aggregation.match(
                Criteria.where("userId").is(user.getId())
        );

        ProjectionOperation project = Aggregation.project()
                .andExpression("year(date)").as("year")
                .andExpression("month(date)").as("month")
                .and("amount").as("amount")
                .and("type").as("type");

        GroupOperation group = Aggregation.group("year", "month", "type")
                .sum("amount").as("total");

        Aggregation aggregation = Aggregation.newAggregation(match, project, group);

        AggregationResults<Document> results =
                mongoTemplate.aggregate(aggregation, "transactions", Document.class);

        Map<String, Map<String, Double>> response = new TreeMap<>();

        for (Document doc : results.getMappedResults()) {

            Document id = (Document) doc.get("_id");

            int year = id.getInteger("year");
            int month = id.getInteger("month");
            String type = id.getString("type");

            double total = doc.getDouble("total");

            String key = year + "-" + String.format("%02d", month);

            response.putIfAbsent(key, new HashMap<>());
            response.get(key).put(type, total);
        }

        return response;
    }

    // 🔹 Helper
    private User getUser(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}