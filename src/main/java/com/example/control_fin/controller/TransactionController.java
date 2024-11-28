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
import com.example.control_fin.service.TransactionService;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

  @Autowired
  private TransactionService transactionService;

  @GetMapping("/{account}")
  @SuppressWarnings("rawtypes")
  public ResponseEntity<ResponseEntity> getTransactionByAccount(@PathVariable CustomerModel account) {
    try {
      ResponseEntity transactions = transactionService.findTransactionByAccount(account);

      if (transactions == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
      }
      return ResponseEntity.ok(transactions);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @PostMapping()
  public ResponseEntity<String> createTransaction(@RequestBody TransactionModel transaction) {
    // try {
    String result = transactionService.saveTransaction(transaction);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);

    // } catch (IllegalArgumentException e) {
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

    // } catch (Exception e) {
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    // .body("An unexpected error occurred while creating the transaction");
    // }
  }

}
