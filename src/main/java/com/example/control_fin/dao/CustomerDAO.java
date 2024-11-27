package com.example.control_fin.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.control_fin.model.CustomerModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

@Repository
public class CustomerDAO {

  @PersistenceContext
  private EntityManager entityManager;

  public void create(CustomerModel customer) {
    entityManager.persist(customer);
  }

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

  public CustomerModel findById(Long userId) {
    try {
      return entityManager
          .createQuery("SELECT c FROM customer c WHERE c.id = :userId", CustomerModel.class)
          .setParameter("userId", userId)
          .getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

}
