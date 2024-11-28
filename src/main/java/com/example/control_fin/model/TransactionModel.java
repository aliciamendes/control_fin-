package com.example.control_fin.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity(name = "transaction")
public class TransactionModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private TransactionType transactionType;

  @NotNull
  private BigDecimal amount;

  @NotNull
  private LocalDateTime transactionDate = LocalDateTime.now();

  @NotNull
  @ManyToOne
  @JoinColumn(name = "account_number", referencedColumnName = "accountNumber")
  private CustomerModel customerAccount;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "destination_account_number", referencedColumnName = "accountNumber", nullable = true)
  private CustomerModel destinationCustomer;

  @Transient
  private BigDecimal balanceAfterTransaction;

}
