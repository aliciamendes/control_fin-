package com.example.control_fin.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.control_fin.dao.CustomerDAO;
import com.example.control_fin.dto.CustomerDTO;
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
  private static final long ACCOUNT_NUMBER_MIN = 100_000;
  private static final long ACCOUNT_NUMBER_MAX = 999_999;

  @SuppressWarnings("unchecked")
  public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
    List<CustomerModel> customers = customerDAO.findAll();
    if (customers.isEmpty()) {
      return createResponse(Collections.emptyList(), HttpStatus.NOT_FOUND);
    }

    List<CustomerDTO> customerDTO = customers.stream()
        .map(CustomerDTO::new)
        .collect(Collectors.toList());

    return createResponse(customerDTO, HttpStatus.OK);
  }

  @SuppressWarnings("unchecked")
  public ResponseEntity<CustomerDTO> getCustomerById(Long userId) {
    CustomerModel customer = findCustomerById(userId);
    CustomerDTO customerDTO = new CustomerDTO(customer);
    return createResponse(customerDTO, HttpStatus.OK);
  }

  @SuppressWarnings("unchecked")
  public ResponseEntity<List<TransactionModel>> getTransactionCustomerById(Long userId) {
    List<TransactionModel> transactions = customerDAO.findTransactionById(userId);
    if (transactions.isEmpty()) {
      return createResponse("Transactions not found for this customer", HttpStatus.NOT_FOUND);
    }
    return createResponse(transactions, HttpStatus.OK);
  }

  @SuppressWarnings("unchecked")
  public ResponseEntity<CustomerDTO> createCustomer(CustomerModel customer) {
    validateCustomerForCreation(customer);

    encryptPassword(customer);
    customerDAO.create(customer);

    return createResponse(new CustomerDTO(customer), HttpStatus.CREATED);
  }

  @SuppressWarnings("unchecked")
  public ResponseEntity<String> deleteCustomer(Long id) {
    if (customerDAO.deleteById(id) < 1) {
      return createResponse("User not found", HttpStatus.NOT_FOUND);
    }
    return createResponse("User deleted successfully", HttpStatus.OK);
  }

  @SuppressWarnings("unchecked")
  public ResponseEntity<String> updateCustomer(Long id, CustomerModel customer) {
    CustomerModel existingCustomer = findCustomerById(id);
    updateCustomerData(existingCustomer, customer);

    customerDAO.update(existingCustomer);

    return createResponse("User updated successfully", HttpStatus.OK);
  }

  private void validateCustomerForCreation(CustomerModel customer) {
    generateAccountNumberIfNeeded(customer);

    if (customer.getAge() < MINIMUM_AGE) {
      throw new IllegalArgumentException("Under 18s cannot create an account");
    }
    if (isIndividualRegistrationExists(customer)) {
      throw new IllegalArgumentException("Individual Registration already exists");
    }
    if (!customerValidationService.isValidIndividualRegistration(customer.getIndividualRegistration())) {
      throw new IllegalArgumentException("Invalid Individual Registration");
    }
    if (isUsernameTaken(customer)) {
      throw new IllegalArgumentException("User already exists");
    }
  }

  private CustomerModel findCustomerById(Long id) {
    return Optional.ofNullable(customerDAO.findById(id))
        .orElseThrow(() -> new RuntimeException("Customer not found"));
  }

  private void updateCustomerData(CustomerModel existingCustomer, CustomerModel customer) {
    if (customer.getFullname() != null)
      existingCustomer.setFullname(customer.getFullname());
    if (customer.getUsername() != null)
      existingCustomer.setUsername(customer.getUsername());
    if (customer.getPassword() != null)
      updateCustomerPassword(existingCustomer, customer.getPassword());
    if (customer.getAge() != null)
      existingCustomer.setAge(customer.getAge());
    if (customer.getIndividualRegistration() != null)
      existingCustomer.setIndividualRegistration(customer.getIndividualRegistration());
  }

  private void updateCustomerPassword(CustomerModel existingCustomer, String password) {
    String encryptedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
    existingCustomer.setPassword(encryptedPassword);
  }

  private void encryptPassword(CustomerModel customer) {
    String encryptedPassword = BCrypt.withDefaults().hashToString(12, customer.getPassword().toCharArray());
    customer.setPassword(encryptedPassword);
  }

  private void generateAccountNumberIfNeeded(CustomerModel customer) {
    if (customer.getAccountNumber() == null) {
      customer.setAccountNumber(ACCOUNT_NUMBER_MIN + (long) (Math.random() * ACCOUNT_NUMBER_MAX));
    }
  }

  private boolean isIndividualRegistrationExists(CustomerModel customer) {
    return customerDAO.findByIR(customer.getIndividualRegistration()) != null;
  }

  private boolean isUsernameTaken(CustomerModel customer) {
    return customerDAO.findByUsername(customer.getUsername()) != null;
  }

  @SuppressWarnings("rawtypes")
  private ResponseEntity createResponse(Object body, HttpStatus status) {
    return ResponseEntity.status(status).body(body);
  }
}
