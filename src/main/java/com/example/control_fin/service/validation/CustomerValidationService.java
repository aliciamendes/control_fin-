package com.example.control_fin.service.validation;

import org.springframework.stereotype.Service;

// service for customer validation (CPF)
@Service
public class CustomerValidationService {

  // valida seta no formato certo
  public boolean isValidCPF(String cpf) {
    if (cpf == null || cpf.isEmpty()) {
      return false;
    }

    cpf = cpf.replaceAll("\\D", "");

    if (!cpf.matches("\\d{11}") || cpf.matches("(\\d)\\1{10}")) {
      return false;
    }

    try {
      return validateCPFChecksum(cpf);
    } catch (Exception e) {
      return false;
    }
  }

  // valida o COF
  public boolean isValidIndividualRegistration(String registration) {
    if (registration == null || registration.isEmpty()) {
      return false;
    }

    if (!registration.matches("[0-9\\.\\-]+")) {
      return false;
    }

    registration = registration.replaceAll("\\D", "");

    if (!registration.matches("\\d{11}")) {
      return false;
    }

    return isValidCPF(registration);
  }

  // calcula e verifica os dgitos verificadores
  private boolean validateCPFChecksum(String cpf) {
    int sum = 0;
    for (int i = 0; i < 9; i++) {
      sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
    }
    int firstVerifier = 11 - (sum % 11);
    if (firstVerifier >= 10) {
      firstVerifier = 0;
    }

    sum = 0;
    for (int i = 0; i < 10; i++) {
      sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
    }
    int secondVerifier = 11 - (sum % 11);
    if (secondVerifier >= 10) {
      secondVerifier = 0;
    }

    return cpf.charAt(9) == Character.forDigit(firstVerifier, 10)
        && cpf.charAt(10) == Character.forDigit(secondVerifier, 10);
  }

}
