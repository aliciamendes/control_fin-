package com.example.control_fin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.control_fin.model.CustomerModel;
import com.example.control_fin.service.CustomerService;

@RestController
@RequestMapping("/customer")
public class CustomerController {

  @Autowired
  private CustomerService customerService;

  @GetMapping()
  @SuppressWarnings("rawtypes")
  public ResponseEntity getCustomers() {
    return customerService.getAllCustomers();
  }

  @GetMapping("/{id}")
  @SuppressWarnings("rawtypes")
  public ResponseEntity getCustomerById(@PathVariable Long id) {
    return customerService.getCustomerById(id);
  }

  @GetMapping("/{id}/transactions")
  @SuppressWarnings("rawtypes")
  public ResponseEntity getTransactions(@PathVariable Long id) {
    return customerService.getTransactionCustomerById(id);
  }

  @PostMapping()
  @SuppressWarnings("rawtypes")
  public ResponseEntity createCustomer(@RequestBody CustomerModel customer) {
    return customerService.createCustomer(customer);
  }

  @DeleteMapping("/{id}")
  @SuppressWarnings("rawtypes")
  public ResponseEntity deleteCustomer(@PathVariable Long id) {
    return customerService.deleteCustomer(id);
  }

  @PatchMapping("/{id}")
  @SuppressWarnings("rawtypes")
  public ResponseEntity updateCustomer(@PathVariable Long id, @RequestBody CustomerModel customer) {
    return customerService.updateCustomer(id, customer);
  }

}
