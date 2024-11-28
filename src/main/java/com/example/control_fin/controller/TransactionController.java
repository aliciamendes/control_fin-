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

import com.example.control_fin.model.CustomerModel;
import com.example.control_fin.model.TransactionModel;
import com.example.control_fin.service.CustomerService;
import com.example.control_fin.service.TransactionService;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

  @Autowired
  private TransactionService transactionService;

  @Autowired
  private CustomerService customerService;

  @GetMapping("/{accountNumber}")
  public ResponseEntity<?> getTransactionByAccount(@PathVariable String accountNumber) {
    CustomerModel account = customerService.findByAccountNumber(accountNumber);

    if (account == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
    }

    ResponseEntity transactions = transactionService.findTransactionByAccount(account);

    return ResponseEntity.ok(transactions);

  }

  @PostMapping()
  public String createTransaction(@RequestBody TransactionModel transaction) {
    return transactionService.saveTransaction(transaction);

  }

}
