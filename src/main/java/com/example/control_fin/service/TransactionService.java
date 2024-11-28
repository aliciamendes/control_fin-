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

  @SuppressWarnings("rawtypes")
  public ResponseEntity findTransactionByAccount(CustomerModel account) {
    var isTransaction = transactionDAO.findByAccount(account);

    System.out.println("\n\n\n\n" + isTransaction + "\n\n\n\n");

    if (isTransaction.size() > 0) {
      return ResponseEntity.status(HttpStatus.ACCEPTED).body(isTransaction);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found");
  }

  public String saveTransaction(TransactionModel transaction) {

    CustomerModel customer = customerDAO
        .findByAccountNumber(transaction.getCustomerAccount().getAccountNumber().toString());
    if (customer == null) {
      throw new RuntimeException("Account not found");
    }

    switch (transaction.getTransactionType()) {
      case DEPOSIT:
        var rowsAffected = transactionDAO.saveDeposit(transaction.getCustomerAccount(), transaction.getAmount());
        if (rowsAffected == 0) {
          throw new RuntimeException("Failed to deposit");
        }
        return "Deposit successful";

      case TRANSFER:
        if (transaction.getCustomerAccount().getAccountNumber()
            .equals(transaction.getDestinationCustomer().getAccountNumber())) {
          return "Cannot transfer to the same account.";
        }
        if (customer.getBalance().compareTo(transaction.getAmount()) < 0) {
          throw new RuntimeException("Insufficient balance for transfer");
        }
        return transactionDAO.saveTransfer(transaction);

      case WITHDRAWAL:
        if (customer.getBalance().compareTo(transaction.getAmount()) < 0) {
          throw new RuntimeException("Insufficient balance for withdrawal");
        }
        var rowsAffected_Withdrawal = transactionDAO.saveWithdrawal(
            transaction.getCustomerAccount(),
            transaction.getAmount());
        if (rowsAffected_Withdrawal == 0) {
          throw new RuntimeException("Failed to withdrawal");
        }
        return "Withdrawal successful";

      default:
        return "Invalid transaction type";
    }
  }

}
