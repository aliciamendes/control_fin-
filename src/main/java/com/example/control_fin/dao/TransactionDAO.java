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

  private static final String TRANSACTION_INSERT = "INSERT INTO transaction (transaction_type, amount, transaction_date, account_number, destination_account_number) "
      +
      "VALUES (:transactionType, :amount, :transactionDate, :accountNumber, :destinationAccountNumber)";

  private static final String UPDATE_BALANCE = "UPDATE customer SET balance = balance + :amount WHERE account_number = :accountNumber";

  public List<TransactionModel> findById(Long transactionId) {
    try {
      return entityManager.createQuery(
          "SELECT t FROM TransactionModel t WHERE t.id = :transactionId",
          TransactionModel.class)
          .setParameter("transactionId", transactionId)
          .getResultList();
    } catch (Exception e) {
      throw new RuntimeException("Error retrieving transaction by ID: " + transactionId, e);
    }
  }

  @Transactional
  public int saveDeposit(CustomerModel customerAccount, BigDecimal amount) {
    try {
      int rowsUpdated = updateCustomerBalance(customerAccount.getAccountNumber(), amount);
      if (rowsUpdated > 0) {
        saveTransaction("DEPOSIT", amount, customerAccount.getAccountNumber(), null);
        return rowsUpdated;
      }
      throw new RuntimeException("Failed to update balance for deposit.");
    } catch (Exception e) {
      throw new RuntimeException("Error processing deposit for account: " + customerAccount.getAccountNumber(), e);
    }
  }

  @Transactional
  public String saveTransfer(TransactionModel transaction) {
    try {
      int rowsSource = updateCustomerBalance(
          transaction.getCustomerAccount().getAccountNumber(),
          transaction.getAmount().negate());
      int rowsDest = updateCustomerBalance(
          transaction.getDestinationCustomer().getAccountNumber(),
          transaction.getAmount());

      if (rowsSource > 0 && rowsDest > 0) {
        saveTransaction(
            "TRANSFER",
            transaction.getAmount(),
            transaction.getCustomerAccount().getAccountNumber(),
            transaction.getDestinationCustomer().getAccountNumber());
        return "Transfer successful.";
      }
      throw new RuntimeException("Failed to complete the transfer.");
    } catch (Exception e) {
      throw new RuntimeException("Error processing transfer from account: " +
          transaction.getCustomerAccount().getAccountNumber(), e);
    }
  }

  @Transactional
  public int saveWithdrawal(CustomerModel customerAccount, BigDecimal amount) {
    try {
      int rowsUpdated = updateCustomerBalance(
          customerAccount.getAccountNumber(),
          amount.negate());
      if (rowsUpdated > 0) {
        saveTransaction("WITHDRAWAL", amount, customerAccount.getAccountNumber(), null);
        return rowsUpdated;
      }
      throw new RuntimeException("Failed to update balance for withdrawal.");
    } catch (Exception e) {
      throw new RuntimeException("Error processing withdrawal for account: " + customerAccount.getAccountNumber(), e);
    }
  }

  private int updateCustomerBalance(Long accountNumber, BigDecimal amount) {
    Query query = entityManager.createNativeQuery(UPDATE_BALANCE);
    query.setParameter("amount", amount);
    query.setParameter("accountNumber", accountNumber);
    return query.executeUpdate();
  }

  private void saveTransaction(String type, BigDecimal amount, Long accountNumber, Long destinationAccountNumber) {
    Query query = entityManager.createNativeQuery(TRANSACTION_INSERT);
    query.setParameter("transactionType", type);
    query.setParameter("amount", amount);
    query.setParameter("transactionDate", LocalDateTime.now());
    query.setParameter("accountNumber", accountNumber);
    query.setParameter("destinationAccountNumber", destinationAccountNumber);
    query.executeUpdate();
  }
}
