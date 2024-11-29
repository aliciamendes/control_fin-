package com.example.control_fin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.control_fin.model.TransactionModel;
import com.example.control_fin.service.TransactionService;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

  @Autowired
  private TransactionService transactionService;

  @GetMapping("/{id}")
  @SuppressWarnings("rawtypes")
  public ResponseEntity getTransactionById(@PathVariable Long accountNumber) {
    return transactionService.findTransactionById(accountNumber);
  }

  @SuppressWarnings("rawtypes")
  @PostMapping("/deposit")
  public ResponseEntity deposit(@RequestBody TransactionModel transaction) {
    try {
      ResponseEntity<String> result = transactionService.createTransactionDeposit(transaction);
      return ResponseEntity.ok(result);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @PostMapping("/withdraw")
  public ResponseEntity<?> withdraw(@RequestBody TransactionModel transaction) {
    try {
      ResponseEntity<String> result = transactionService.createTransactionWithdraw(transaction);
      return ResponseEntity.ok(result);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @PostMapping("/transfer")
  public ResponseEntity<?> transfer(@RequestBody TransactionModel transaction) {
    try {
      ResponseEntity<String> result = transactionService.createTransactionTransfer(transaction);
      return ResponseEntity.ok(result);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

}
