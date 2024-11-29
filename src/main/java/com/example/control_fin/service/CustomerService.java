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
import com.example.control_fin.model.TransactionModel;
import com.example.control_fin.service.validation.CustomerValidationService;

import at.favre.lib.crypto.bcrypt.BCrypt;

@Service
@Transactional
public class CustomerService {

  @Autowired
  private CustomerDAO customerDAO;

  @Autowired
  private CustomerValidationService customerValidationService;

  private static final int MINIMUM_AGE = 18;

  public ResponseEntity<List<CustomerModel>> getAllCustomers() {
    List<CustomerModel> customers = customerDAO.findAll();
    if (customers.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
    }
    return ResponseEntity.status(HttpStatus.OK).body(customers);
  }

  public ResponseEntity<CustomerModel> getCustomerById(Long userId) {
    CustomerModel customer = customerDAO.findById(userId);
    if (customer == null) {
      return null;
    }
    customerDAO.findById(userId);
    return ResponseEntity.status(HttpStatus.OK).body(customer);
  }

  @SuppressWarnings("rawtypes")
  public ResponseEntity getTransactionCustomerById(Long userId) {
    List<TransactionModel> transaction = customerDAO.findTransactionById(userId);

    if (transaction != null) {
      return ResponseEntity.status(HttpStatus.OK).body(transaction);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
    }
  }

  @SuppressWarnings("rawtypes")
  public ResponseEntity findByAccountNumber(String accountNumber) {
    CustomerModel customer = customerDAO.findByAccountNumber(accountNumber);

    if (customer != null) {
      return ResponseEntity.status(HttpStatus.OK).body(customer);
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
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

    if (!customerValidationService.isValidIndividualRegistration(customer.getIndividualRegistration())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Individual Registration");
    }

    var isUser = customerDAO.findByUsername(customer.getUsername());

    if (isUser != null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
    }

    var passwordEncrypted = BCrypt.withDefaults().hashToString(12, customer.getPassword().toCharArray());
    customer.setPassword(passwordEncrypted);

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
      var passwordEncrypted = BCrypt.withDefaults().hashToString(12, customer.getPassword().toCharArray());
      existingCustomer.setPassword(passwordEncrypted);
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

}