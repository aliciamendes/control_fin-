package com.example.control_fin.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.control_fin.dao.CustomerDAO;
import com.example.control_fin.model.CustomerModel;

import jakarta.transaction.Transactional;

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

  // VALIDAR CPF, IDADE NÃO TA FUNFANDO, CPF > 11 < CPF
  @SuppressWarnings("rawtypes")
  public ResponseEntity createCustomer(CustomerModel customer) {

    var isUser = customerDAO.findByUsername(customer.getUsername());

    if (isUser == null) {
      customerDAO.create(customer);
      return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
    }

    if (isUser.getAge() < MINIMUM_AGE) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Under 18s cannot create an account");
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
  }
}
