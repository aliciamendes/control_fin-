<h1 align="center">ControlFin-</h1>

<p align="justify">
ControlFin- é um aplicativo de gerenciamento financeiro, desenvolvido como parte de um teste técnico para a posição de Java Jr. Ele oferece funcionalidades como criação de usuários, gerenciamento de transações financeiras (depósitos, saques, transferências) e rastreabilidade conforme padrões regulatórios.
</p>

<p align="center">
<strong>Release:</strong> 0.0.1 <strong>Build:</strong> 001
</p>

###

---

### Ferramentas

- [x] **Java SDK** _- Recomendado: JDK 17 ou superior_
- [x] **Spring Boot** _- Recomendado: 3.4.0_
- [x] **H2 Database Project** _- Banco de dados em memória_
- [x] **VSCode** _(opcional)_
- [x] **Postman** _(opcional)_

###

---

###

<div align="center">
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg" height="40" alt="Java logo" style="margin: 0 10px;" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg" height="40" alt="Spring logo"style="margin: 0 10px;" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/vscode/vscode-original.svg" height="40" alt="VSCode logo"style="margin: 0 10px;" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/postman/postman-original.svg" height="40" alt="Postman logo"style="margin: 0 10px;" />
</div>

###

### Manual do usuário da API

A API permite gerenciar usuários e transações financeiras com endpoints para operações básicas. Os dados são armazenados em um banco H2 em memória, o que significa que os dados serão apagados sempre que a aplicação for reiniciada. As transações seguem padrões contábeis e legais de imutabilidade.

###

### Configurações de Ambiente de Desenvolvimento

#### Configuração Java no VS Code

##### Para utilizar o Java no VSCode, é necessário instalar as extensões abaixo

- **vscjava.vscode-java-pack**
  - Debug, testes e dependências para projetos Java
- **vscjava.vscode-spring-initializr**
- **vmware.vscode-spring-boot**
- **vscjava.vscode-spring-boot-dashboard**

###

#### Como rodar o projeto

1. Clone do projeto

   ```bash
   git clone https://github.com/aliciamendes/control_fin-.git
   ```

2. Abra o projeto no VSCode (ou por linha de comando)

   ```bash
    cd control_fin-

   ```

3. Inicie a aplicação

   ```bash
    ./mvnw spring-boot:run

   ```

### Endpoints

### Endpoints do usuário

#### Buscar usuário pelo ID

Retorna as informações de um usuário específico pelo seu identificador único.

> GET: /customer/{id}

**Exemplo de resposta**

```json
{
  "id": 1,
  "username": "user_example",
  "fullname": "Exemplo Nome",
  "age": 30,
  "individualRegistration": "12345678901"
}
```

#### Buscar todos os usuários

Retorna uma lista com as informações de todos os usuário.

> GET: /customer

#### Buscar todos as transações do usuário pelo id

> GET: /customer/{id}/transactions

#### Criar usuário

> POST: /customer

```json
{
  "username": "user_example",
  "fullname": "Exemplo Nome",
  "password": "my_p4ssw0rd",
  "age": 25,
  "individualRegistration": "27084849076" // Gerado no 4Devs
}
```

#### Atualizar usuário

> PATCH: /customer/{id}

```json
{
  "password": "my_n3w_pa5sw0rd"
}
```

#### Deletar usuário

> DELETE: /customer/{id}

### Endpoints da Transação

#### Buscar os todas as transações do usuário pelo id do usuário

> GET: /transaction/{id}

#### Criar transação de depósito

> POST: /transaction/deposit

```json
{
  "transactionType": "DEPOSIT",
  "amount": 100.5,
  "customerAccount": {
    "accountNumber": 549131
  }
}
```

#### Criar transação de saque

> POST: /transaction/withdraw

```json
{
  "transactionType": "WITHDRAWAL",
  "amount": 50,
  "customerAccount": {
    "accountNumber": 147464
  }
}
```

#### Criar transação de transferência

> POST: /transaction/transfer

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

### Sobre Transações no Projeto

> Transações no Projeto:
>
> O módulo Transactions possui apenas duas rotas principais porque segue princípios contábeis e legislações rigorosas sobre a imutabilidade de registros financeiros:
>
> > Princípio contábil: Não é permitido apagar ou alterar transações financeiras registradas. Caso ocorra um erro ou necessidade de reversão, é necessário criar novas transações que ajustem ou justifiquem a operação anterior.
>
> > Lei Sarbanes-Oxley (SOX): Essa legislação norte-americana exige que todas as transações financeiras sejam rastreáveis, garantindo a transparência e a conformidade durante auditorias financeiras.
>
> > Banco Central do Brasil: Regulamentações brasileiras determinam que todas as operações financeiras sejam registradas de forma permanente, sem possibilidade de edição direta, assegurando a integridade dos dados.
>
> No projeto, essas regras foram aplicadas para:
>
> > Manter um histórico confiável: Garantindo que todas as operações sejam registradas de forma permanente.
>
> > Proteger os dados contra manipulação: Garantindo que cada transação seja imutável.
>
> > Facilitar auditorias financeiras: Implementando métodos para corrigir ou reverter operações de forma rastreável e transparente, sem comprometer a conformidade legal.

> PS: Na minha máquina roda 😂
