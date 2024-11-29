package com.example.control_fin.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.control_fin.model.CustomerModel;
import com.example.control_fin.model.TransactionModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class CustomerDAO {

  @PersistenceContext
  private EntityManager entityManager;

  public List<CustomerModel> findAll() {
    try {
      return entityManager.createQuery("SELECT c FROM customer c", CustomerModel.class).getResultList();
    } catch (NoResultException e) {
      return null;
    }
  }

  public CustomerModel findByUsername(String username) {
    try {
      return entityManager
          .createQuery("SELECT c FROM customer c WHERE c.username = :username", CustomerModel.class)
          .setParameter("username", username)
          .getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public CustomerModel findByIR(String customerIR) {
    try {
      return entityManager
          .createQuery("SELECT c FROM customer c WHERE c.individualRegistration = :customerIR", CustomerModel.class)
          .setParameter("customerIR", customerIR)
          .getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public CustomerModel findById(Long customerId) {
    try {
      return entityManager
          .createQuery("SELECT c FROM customer c WHERE c.id = :customerId", CustomerModel.class)
          .setParameter("customerId", customerId)
          .getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public List<TransactionModel> findTransactionById(Long customerId) {
    try {
      return entityManager
          .createQuery(
              "SELECT t FROM transaction t JOIN t.customerAccount c WHERE c.id = :customerId",
              TransactionModel.class)
          .setParameter("customerId", customerId)
          .getResultList();
    } catch (NoResultException e) {
      return null;
    }
  }

  public CustomerModel findByAccountNumber(String customerAccountNumber) {
    return entityManager.createQuery(
        "SELECT c FROM customer c WHERE c.accountNumber = :customerAccountNumber",
        CustomerModel.class)
        .setParameter("customerAccountNumber", customerAccountNumber)
        .getSingleResult();
  }

  public void create(CustomerModel customer) {
    try {
      String sql = "INSERT INTO customer (username, individual_registration, account_number, fullname, password, age, balance, created_at) "
          +
          "VALUES (:username, :individualRegistration, :accountNumber, :fullname, :password, :age, :balance, :createdAt)";

      Query query = entityManager.createNativeQuery(sql);

      query.setParameter("username", customer.getUsername());
      query.setParameter("individualRegistration", customer.getIndividualRegistration());
      query.setParameter("accountNumber", customer.getAccountNumber());
      query.setParameter("fullname", customer.getFullname());
      query.setParameter("password", customer.getPassword());
      query.setParameter("age", customer.getAge());
      query.setParameter("balance", customer.getBalance());
      query.setParameter("createdAt", customer.getCreatedAt());

      query.executeUpdate();

    } catch (Exception e) {
      throw new RuntimeException("Failed to create customer", e);
    }
  }

  public int deleteById(Long customerId) {
    int rowsDeleted = entityManager
        .createQuery("DELETE FROM customer c WHERE c.id = :customerId")
        .setParameter("customerId", customerId)
        .executeUpdate();

    return rowsDeleted;
  }

  public void update(CustomerModel customer) {
    StringBuilder queryBuilder = new StringBuilder("UPDATE customer c SET ");
    boolean hasPrevious = false;

    if (customer.getUsername() != null) {
      queryBuilder.append("c.username = :username");
      hasPrevious = true;
    }
    if (customer.getPassword() != null) {
      if (hasPrevious)
        queryBuilder.append(", ");
      queryBuilder.append("c.password = :password");
      hasPrevious = true;
    }
    if (customer.getFullname() != null) {
      if (hasPrevious)
        queryBuilder.append(", ");
      queryBuilder.append("c.fullname = :fullname");
      hasPrevious = true;
    }
    if (customer.getAge() != null) {
      if (hasPrevious)
        queryBuilder.append(", ");
      queryBuilder.append("c.age = :age");
      hasPrevious = true;
    }
    if (customer.getIndividualRegistration() != null) {
      if (hasPrevious)
        queryBuilder.append(", ");
      queryBuilder.append("c.individualRegistration = :individualRegistration");
    }

    queryBuilder.append(" WHERE c.id = :id");

    Query query = entityManager.createQuery(queryBuilder.toString());

    if (customer.getUsername() != null) {
      query.setParameter("username", customer.getUsername());
    }
    if (customer.getPassword() != null) {
      query.setParameter("password", customer.getPassword());
    }
    if (customer.getFullname() != null) {
      query.setParameter("fullname", customer.getFullname());
    }
    if (customer.getAge() != null) {
      query.setParameter("age", customer.getAge());
    }
    if (customer.getIndividualRegistration() != null) {
      query.setParameter("individualRegistration", customer.getIndividualRegistration());
    }

    query.setParameter("id", customer.getId());
    query.executeUpdate();
  }
}
