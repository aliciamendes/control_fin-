package com.example.control_fin.dto;

import java.math.BigDecimal;

import com.example.control_fin.model.CustomerModel;

import lombok.Data;

@Data
public class CustomerDTO {

  private String fullname;
  private Integer age;
  private Long accountNumber;
  private BigDecimal balance;
  private String individualRegistration;

  public CustomerDTO(CustomerModel customer) {
    this.fullname = customer.getFullname();
    this.age = customer.getAge();
    this.accountNumber = customer.getAccountNumber();
    this.balance = customer.getBalance();
    this.individualRegistration = customer.getIndividualRegistration();
  }

}
