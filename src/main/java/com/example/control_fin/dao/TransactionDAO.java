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

  // Princípio contábil: Não se pode apagar ou alterar transações financeiras
  // registradas, mas sim criar novas transações para corrigir erros ou reverter
  // operações.
  // A Lei Sarbanes-Oxley (SOX) nos EUA exige que todas as transações financeiras
  // sejam rastreáveis.
  // Banco Central também demanda que operações financeiras sejam registradas de
  // forma imutável.

  @PersistenceContext
  private EntityManager entityManager;

  public List<TransactionModel> findByAccount(CustomerModel accountNumber) {
    try {
      return entityManager
          .createQuery("SELECT t FROM transaction t WHERE t.customerAccount = :accountNumber", TransactionModel.class)
          .setParameter("accountNumber", accountNumber)
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
  public String saveTransfer(CustomerModel customerAccount, BigDecimal amount, CustomerModel customerAccountDest) {
    try {
      String withdrawSql = "UPDATE customer SET balance = balance - :amount WHERE account_number = :accountNumber";
      Query withdrawQuery = entityManager.createNativeQuery(withdrawSql);
      withdrawQuery.setParameter("amount", amount);
      withdrawQuery.setParameter("accountNumber", customerAccount.getAccountNumber());
      int withdrawRowsAffected = withdrawQuery.executeUpdate();

      String depositSql = "UPDATE customer SET balance = balance + :amount WHERE account_number = :accountNumber";
      Query depositQuery = entityManager.createNativeQuery(depositSql);
      depositQuery.setParameter("amount", amount);
      depositQuery.setParameter("accountNumber", customerAccountDest.getAccountNumber());
      int depositRowsAffected = depositQuery.executeUpdate();

      if (withdrawRowsAffected > 0 && depositRowsAffected > 0) {
        String senderTransactionSql = "INSERT INTO transaction (transaction_type, amount, transaction_date, account_number) "
            + "VALUES ('WITHDRAWAL', :amount, :transactionDate, :accountNumber)";
        Query senderTransactionQuery = entityManager.createNativeQuery(senderTransactionSql);
        senderTransactionQuery.setParameter("amount", amount);
        senderTransactionQuery.setParameter("transactionDate", LocalDateTime.now());
        senderTransactionQuery.setParameter("accountNumber", customerAccount.getAccountNumber());
        senderTransactionQuery.executeUpdate();

        String receiverTransactionSql = "INSERT INTO transaction (transaction_type, amount, transaction_date, account_number) "
            + "VALUES ('DEPOSIT', :amount, :transactionDate, :accountNumber)";
        Query receiverTransactionQuery = entityManager.createNativeQuery(receiverTransactionSql);
        receiverTransactionQuery.setParameter("amount", amount);
        receiverTransactionQuery.setParameter("transactionDate", LocalDateTime.now());
        receiverTransactionQuery.setParameter("accountNumber", customerAccountDest.getAccountNumber());
        receiverTransactionQuery.executeUpdate();

        return "Transfer made successfully!";
      } else {
        return "Failure when making the transfer";
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Error when processing the transfer", e);
    }
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
        return 0;
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Error when processing the withdrawal", e);
    }
  }

}