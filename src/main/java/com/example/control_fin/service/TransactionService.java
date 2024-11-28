package com.example.control_fin.service;

import org.springframework.beans.factory.annotation.Autowired;
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

    if (isTransaction != null) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.notFound().build();
  }

  public String saveTransaction(TransactionModel transaction) {

    CustomerModel customer = customerDAO.findByAccountNumber(transaction.getCustomerAccount().getAccountNumber());

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
        return transactionDAO.saveTransfer(
            transaction.getCustomerAccount(),
            transaction.getAmount(),
            transaction.getDestinationCustomer());

      case WITHDRAWAL:
        var rowsAffected_Withdrawal = transactionDAO.saveWithdrawal(transaction.getCustomerAccount(),
            transaction.getAmount());
        if (rowsAffected_Withdrawal == 0) {
          throw new RuntimeException("Failed to Withdrawal");
        }
        return "Withdrawal successful";

      default:
        return "INVALIDO PESTE";
    }
  }

}
