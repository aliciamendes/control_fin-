package com.example.control_fin.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Data
@Entity(name = "customer")
@Table(name = "customer", uniqueConstraints = {
    @UniqueConstraint(columnNames = "username"),
    @UniqueConstraint(columnNames = "accountNumber"),
    @UniqueConstraint(columnNames = "individualRegistration")
})
public class CustomerModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(min = 11, max = 11)
  private String individualRegistration; // CPF

  @NotBlank
  @Size(max = 30)
  private String username;

  @NotBlank
  @Size(max = 120)
  private String fullname;

  private Long accountNumber;

  @NotBlank
  @Size(min = 6, max = 20)
  private String password;

  @NotBlank
  private Integer age;

  private BigDecimal balance = BigDecimal.ZERO;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @PrePersist
  public void generateAccountNumber() {
    if (this.accountNumber == null) {
      this.accountNumber = 100_000 + (long) (Math.random() * 900_000);
    }
  }

}
