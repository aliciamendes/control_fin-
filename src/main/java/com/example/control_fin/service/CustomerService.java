package com.example.control_fin.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.control_fin.dao.CustomerDAO;
import com.example.control_fin.model.CustomerModel;

@Service
@Transactional
public class CustomerService {

  @Autowired
  private CustomerDAO customerDAO;
  private static final int MINIMUM_AGE = 18;

  public ResponseEntity<List<CustomerModel>> getAllCustomers() {
    List<CustomerModel> customers = customerDAO.findAll();
    if (customers.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
    }
    return ResponseEntity.status(HttpStatus.OK).body(customers);
  }

  public CustomerModel getCustomerById(Long userId) {
    return customerDAO.findById(userId);
  }

  public CustomerModel findByAccountNumber(String accountNumber) {
    return customerDAO.findByAccountNumber(accountNumber);
  }

  @SuppressWarnings("rawtypes")
  public ResponseEntity createCustomer(CustomerModel customer) {
    if (customer.getAccountNumber() == null) {
      customer.setAccountNumber(100_000 + (long) (Math.random() * 900_000));
    }

    if (customer.getAge() < MINIMUM_AGE) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Under 18s cannot create an account");
    }

    var isUserIR = customerDAO.findByIR(customer.getIndividualRegistration());

    if (isUserIR != null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Individual Registration already exists");
    }

    if (!isValidIndividualRegistration(customer.getIndividualRegistration())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Individual Registration");
    }

    var isUser = customerDAO.findByUsername(customer.getUsername());

    if (isUser != null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
    }

    customerDAO.create(customer);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body("User created successfully");

  }

  @SuppressWarnings("rawtypes")
  public ResponseEntity deleteCustomer(Long id) {
    var deleted = customerDAO.deleteById(id);
    if (deleted == 0) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }
    return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
  }

  @SuppressWarnings("rawtypes")
  public ResponseEntity updateCustomer(Long id, CustomerModel customer) {
    CustomerModel existingCustomer = customerDAO.findById(id);
    if (existingCustomer == null) {
      throw new RuntimeException("Customer not found");
    }

    if (customer.getFullname() != null) {
      existingCustomer.setFullname(customer.getFullname());
    }
    if (customer.getUsername() != null) {
      existingCustomer.setUsername(customer.getUsername());
    }
    if (customer.getPassword() != null) {
      existingCustomer.setPassword(customer.getPassword());
    }
    if (customer.getAge() != null) {
      existingCustomer.setAge(customer.getAge());
    }
    if (customer.getIndividualRegistration() != null) {
      existingCustomer.setIndividualRegistration(customer.getIndividualRegistration());
    }

    customerDAO.update(existingCustomer);
    return ResponseEntity.status(HttpStatus.OK).body("User updated successfully");
  }

  private boolean isValidIndividualRegistration(String IR) {
    if (IR == null || IR.isEmpty()) {
      return false;
    }

    if (!IR.matches("[0-9\\.\\-]+")) {
      return false;
    }

    IR = IR.replaceAll("\\D", "");

    if (!IR.matches("\\d{11}")) {
      return false;
    }

    return isValidCPF(IR);
  }

  private boolean isValidCPF(String IR) {
    IR = IR.replaceAll("\\D", "");

    if (!IR.matches("\\d{11}") || IR.matches("(\\d)\\1{10}")) {
      return false;
    }

    try {
      int sum = 0;
      for (int i = 0; i < 9; i++) {
        sum += Character.getNumericValue(IR.charAt(i)) * (10 - i);
      }
      int firstVerifier = 11 - (sum % 11);
      if (firstVerifier >= 10)
        firstVerifier = 0;

      sum = 0;
      for (int i = 0; i < 10; i++) {
        sum += Character.getNumericValue(IR.charAt(i)) * (11 - i);
      }
      int secondVerifier = 11 - (sum % 11);
      if (secondVerifier >= 10)
        secondVerifier = 0;

      return IR.charAt(9) == Character.forDigit(firstVerifier, 10) &&
          IR.charAt(10) == Character.forDigit(secondVerifier, 10);
    } catch (Exception e) {
      return false;
    }

  }

}