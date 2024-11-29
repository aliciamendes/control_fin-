package com.example.control_fin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.control_fin.dao.CustomerDAO;
import com.example.control_fin.dao.TransactionDAO;
import com.example.control_fin.model.CustomerModel;
import com.example.control_fin.model.TransactionModel;

@Service
@Transactional
public class TransactionService {

  @Autowired
  private TransactionDAO transactionDAO;
  @Autowired
  private CustomerDAO customerDAO;

  private CustomerModel verifyCustomerAccount(TransactionModel transaction) {
    CustomerModel customer = customerDAO
        .findByAccountNumber(transaction.getCustomerAccount().getAccountNumber().toString());

    if (customer == null) {
      throw new AccountNotFoundException("Account not found");
    }

    if (customer.getBalance().compareTo(transaction.getAmount()) < 0) {
      throw new InsufficientBalanceException("Insufficient balance for transaction");
    }

    return customer;
  }

  @SuppressWarnings("rawtypes")
  public ResponseEntity findTransactionById(Long transactionId) {
    var transaction = transactionDAO.findById(transactionId);

    if (!transaction.isEmpty()) {
      return ResponseEntity.status(HttpStatus.OK).body(transaction);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found");
  }

  public ResponseEntity<String> createTransactionTransfer(TransactionModel transaction) {

    if (transaction.getCustomerAccount().getAccountNumber()
        .equals(transaction.getDestinationCustomer().getAccountNumber())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot transfer to the same account.");
    }

    transactionDAO.saveTransfer(transaction);
    return ResponseEntity.status(HttpStatus.CREATED).body("Transfer successful");
  }

  public ResponseEntity<String> createTransactionWithdraw(TransactionModel transaction) {
    verifyCustomerAccount(transaction);

    var rowsAffected_Withdrawal = transactionDAO.saveWithdrawal(
        transaction.getCustomerAccount(),
        transaction.getAmount());

    if (rowsAffected_Withdrawal == 0) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to withdraw");
    }

    return ResponseEntity.status(HttpStatus.OK).body("Withdrawal successful");
  }

  public ResponseEntity<String> createTransactionDeposit(TransactionModel transaction) {
    var rowsAffected = transactionDAO.saveDeposit(transaction.getCustomerAccount(), transaction.getAmount());

    if (rowsAffected == 0) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to deposit");
    }

    return ResponseEntity.status(HttpStatus.OK).body("Deposit successful");
  }

  public static class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
      super(message);
    }
  }

  public static class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
      super(message);
    }
  }
}
