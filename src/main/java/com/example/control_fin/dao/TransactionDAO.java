package com.example.control_fin.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.control_fin.model.CustomerModel;
import com.example.control_fin.model.TransactionModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Repository
public class TransactionDAO {

  @PersistenceContext
  private EntityManager entityManager;

  public List<TransactionModel> findById(Long transactionId) {
    try {
      return entityManager
          .createQuery("SELECT t FROM transaction t WHERE t.id = :transactionId",
              TransactionModel.class)
          .setParameter("transactionId", transactionId)
          .getResultList();

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Transactional
  public Integer saveDeposit(CustomerModel customerAccount, BigDecimal amount) {
    try {
      String sql = "UPDATE customer SET balance = balance + :amount WHERE account_number = :accountNumber";
      Query query = entityManager.createNativeQuery(sql);
      query.setParameter("amount", amount);
      query.setParameter("accountNumber", customerAccount.getAccountNumber());
      int rowsAffected = query.executeUpdate();

      if (rowsAffected > 0) {
        String transactionSql = "INSERT INTO transaction (transaction_type, amount, transaction_date, account_number) "
            +
            "VALUES ('DEPOSIT', :amount, :transactionDate, :accountNumber)";
        Query transactionQuery = entityManager.createNativeQuery(transactionSql);
        transactionQuery.setParameter("amount", amount);
        transactionQuery.setParameter("transactionDate", LocalDateTime.now());
        transactionQuery.setParameter("accountNumber", customerAccount.getAccountNumber());
        transactionQuery.executeUpdate();

        return 1;
      } else {
        throw new RuntimeException("Account not found or balance update failed.");
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Unexpected error occurred while processing the deposit", e);
    }

  }

  @Transactional
  public String saveTransfer(TransactionModel transaction) {

    String updateSourceQuery = "UPDATE customer c SET c.balance = c.balance - :amount WHERE c.accountNumber = :accountNumber";
    Query sourceQuery = entityManager.createQuery(updateSourceQuery);
    sourceQuery.setParameter("amount", transaction.getAmount());
    sourceQuery.setParameter("accountNumber", transaction.getCustomerAccount().getAccountNumber());
    int updatedSource = sourceQuery.executeUpdate();

    String updateDestQuery = "UPDATE customer c SET c.balance = c.balance + :amount WHERE c.accountNumber = :accountNumber";
    Query destQuery = entityManager.createQuery(updateDestQuery);
    destQuery.setParameter("amount", transaction.getAmount());
    destQuery.setParameter("accountNumber", transaction.getDestinationCustomer().getAccountNumber());
    int updatedDest = destQuery.executeUpdate();

    if (updatedSource == 0 || updatedDest == 0) {
      return "Transfer failed.";
    }

    String insertTransactionQuery = "INSERT INTO transaction (transaction_type, amount, transaction_date, account_number, destination_account_number) "
        +
        "VALUES (:transactionType, :amount, :transactionDate, :accountNumber, :destinationAccountNumber)";
    Query insertQuery = entityManager.createNativeQuery(insertTransactionQuery);
    insertQuery.setParameter("transactionType", transaction.getTransactionType().toString());
    insertQuery.setParameter("amount", transaction.getAmount());
    insertQuery.setParameter("transactionDate", transaction.getTransactionDate());
    insertQuery.setParameter("accountNumber", transaction.getCustomerAccount().getAccountNumber());
    insertQuery.setParameter("destinationAccountNumber", transaction.getDestinationCustomer().getAccountNumber());
    insertQuery.executeUpdate();

    return "Transfer successful";
  }

  @Transactional
  public int saveWithdrawal(CustomerModel customerAccount, BigDecimal amount) {
    try {
      String sql = "UPDATE customer SET balance = balance - :amount WHERE account_number = :accountNumber";
      Query query = entityManager.createNativeQuery(sql);
      query.setParameter("amount", amount);
      query.setParameter("accountNumber", customerAccount.getAccountNumber());
      int rowsAffected = query.executeUpdate();

      if (rowsAffected > 0) {
        String withdrawalTransactionSql = "INSERT INTO transaction (transaction_type, amount, transaction_date, account_number) "
            + "VALUES ('WITHDRAWAL', :amount, :transactionDate, :accountNumber)";
        Query withdrawalTransactionQuery = entityManager.createNativeQuery(withdrawalTransactionSql);
        withdrawalTransactionQuery.setParameter("amount", amount);
        withdrawalTransactionQuery.setParameter("transactionDate", LocalDateTime.now());
        withdrawalTransactionQuery.setParameter("accountNumber", customerAccount.getAccountNumber());
        withdrawalTransactionQuery.executeUpdate();

        return 1;
      } else {
        return 0;
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Error when processing the withdrawal", e);
    }
  }

}