# ControlFin-

<p align="center">
ControlFin+ é um aplicativo desenvolvido como parte de um teste técnico para uma posição de Java Jr. O objetivo é gerenciar efetivamente usuários e transações financeiras.
</p>

<p align="center">
<strong>Release:</strong> 0.0.1 <strong>Build:</strong> 001
</p>

###

---

### Ferramentas

- [x] **Java SDK** - Recomendado: JDKs v23
- [x] **Spring Boot** - Recomendado: v3.4
- [x] **H2 Database Project** (em memória)
- [x] **VSCode** (opcional)
- [x] **Postman** (opcional)

###

---

###

<style>
  .tech-logo {
    margin: 0 10px;
  }
</style>

<div align="center">
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg" height="40" alt="Java logo" class="tech-logo" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg" height="40" alt="Spring logo" class="tech-logo" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/vscode/vscode-original.svg" height="40" alt="VSCode logo" class="tech-logo" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/postman/postman-original.svg" height="40" alt="Postman logo" class="tech-logo" />
</div>

###

### Manual do usuário da API

###

### Configurações de Ambiente de Desenvolvimento

#### Configuração Java no VS Code

##### Para utilizar o Java no VSCode, é necessário instalar as extensões abaixo

- **vscjava.vscode-java-pack**
  - vscjava.vscode-java-debug
  - vscjava.vscode-java-test
  - vscjava.vscode-java-dependency
  - vscjava.vscode-maven
  - redhat.java
- **vscjava.vscode-spring-initializr**
- **vmware.vscode-spring-boot**
- **vscjava.vscode-spring-boot-dashboard**

###

#### Para começar, faça o clone do repositório

```bash
git clone https://github.com/aliciamendes/control_fin-.git
```

### Endpoints

### Endpoints do usuário

#### Buscar usuário pelo id

> GET: /customer/{id}

#### Buscar todos os usuário

> GET: /customer

#### Criar usuário

> POST: /customer

```json
{
  "username": "aliciamesmo",
  "fullname": "Alícia Mendes",
  "password": "my_p4ssw0rd",
  "age": 25,
  "individualRegistration": "27084849076" // Gerado no 4Devs
}
```

#### Atualizar usuário

> PATCH: /customer/{id}

```json
{
  "password": "new_my_p4ssw0rd"
}
```

#### Deletar usuário

> DELETE: /customer/{id}

### Endpoints da Transação

#### Buscar os todas as transações do usuário pelo número da conta

> GET: /transaction/{accountNumber}

#### Criar transação (depósito)

> POST: /transaction

```json
{
  "transactionType": "DEPOSIT",
  "amount": 100.5,
  "customerAccount": {
    "accountNumber": 549131
  },
  "destinationCustomer": null
}
```

#### Criar transação (saque)

> POST: /transaction

```json
{
  "transactionType": "WITHDRAWAL",
  "amount": 50,
  "customerAccount": {
    "accountNumber": 147464
  }
}
```

#### Criar transação (transferência)

> POST: /transaction

```json
{
  "transactionType": "TRANSFER",
  "amount": 100.5,
  "customerAccount": {
    "accountNumber": 552928
  },
  "destinationCustomer": {
    "accountNumber": 552928
  }
}
```

> [!TIP] > [!TIP]
> Transações no Projeto:
>
> O módulo Transactions possui apenas duas rotas principais porque segue princípios contábeis e legislações rigorosas sobre a imutabilidade de registros financeiros:
>
> > Princípio contábil: Não é permitido apagar ou alterar transações financeiras registradas. Caso ocorra um erro ou necessidade de reversão, é preciso criar novas transações que justifiquem e ajustem a operação anterior.
>
> > Lei Sarbanes-Oxley (SOX): Essa lei norte-americana exige que todas as transações financeiras sejam rastreáveis, garantindo a transparência e a conformidade em auditorias financeiras.
>
> > Banco Central do Brasil: Similarmente, no Brasil, as regulamentações determinam que todas as operações financeiras sejam registradas de forma permanente, sem possibilidade de edição direta, para assegurar a integridade dos dados.
>
> No projeto, essas regras são aplicadas para:
>
> > Manter um registro histórico confiável de todas as operações financeiras realizadas.
>
> > Garantir que cada transação seja imutável, protegendo os dados contra manipulações indevidas.
>
> > Implementar métodos para corrigir ou reverter operações de forma rastreável e transparente, sem comprometer a conformidade legal.
