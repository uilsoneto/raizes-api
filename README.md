# Raízes do Nordeste — API Back-End

API REST da rede de lanchonetes **Raízes do Nordeste**, desenvolvida com Spring Boot 4 + Java 21 + SQLite.

---

## Requisitos

| Ferramenta | Versão mínima |
|---|---|
| Java | 21 |
| Maven | 3.9+ |
| Docker (opcional) | 24+ |
| SQLite | (embutido via JDBC) |

---

## Configuração

Copie o arquivo de exemplo e ajuste se necessário:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Variáveis principais em `application.properties`:

```properties
spring.datasource.url=jdbc:sqlite:raizes.db
jwt.secret=raizes-nordeste-secret-key-2026-muito-segura
jwt.expiration-ms=3600000
```

---

## Como executar (local)

```bash
# 1. Instalar dependências e compilar
mvn clean compile

# 2. Iniciar a API (tabelas e seed são criados automaticamente)
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

---

## Como executar (Docker)

```bash
# Build da imagem
docker build -t raizes-api .

# Executar o container
docker run -d -p 8080:8080 --name raizes-api raizes-api
```

Para persistir o banco entre reinícios:

```bash
docker run -d -p 8080:8080 -v raizes-data:/app/data --name raizes-api raizes-api
```

Parar e remover:

```bash
docker stop raizes-api && docker rm raizes-api
```

---

## Deploy em K3s (Produção)

**Pré-requisitos:** K3s com Traefik (padrão) e cert-manager instalado com ClusterIssuer `letsencrypt-production`.

**URL de produção:** https://raizes-api.uilson.com

```bash
# Aplicar todos os manifests
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/pvc.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress.yaml
```

Ou de uma vez:

```bash
kubectl apply -f k8s/
```

**Verificar status:**

```bash
kubectl get pods -n raizes-api
kubectl get ingress -n raizes-api
kubectl get certificate -n raizes-api
```

**Swagger em produção:**
```
https://raizes-api.uilson.com/swagger-ui.html
```

**Atualizar imagem:**

```bash
kubectl set image deployment/raizes-api raizes-api=uilsoneto/raizes-api:NEW_TAG -n raizes-api
```

---

## Swagger / OpenAPI

Acesse a documentação interativa:

**Local:**
```
http://localhost:8080/swagger-ui.html
```

**Produção (K3s):**
```
https://raizes-api.uilson.com/swagger-ui.html
```

JSON da spec:
```
http://localhost:8080/v3/api-docs
```

---

## Usuário admin (seed)

| Campo | Valor |
|---|---|
| E-mail | admin@raizes.com |
| Senha | admin123 |
| Role | ADMIN |

---

## Como rodar os testes

```bash
mvn test
```

---

## Fluxo principal (Pedido → Pagamento → Status)

```
POST /auth/login                          → obtém token JWT
POST /pedidos                             → cria pedido (canalPedido obrigatório)
POST /pagamentos/pedidos/{id}?forma=MOCK  → processa pagamento mock
PATCH /pedidos/{id}/status                → atualiza status (EM_PREPARO → PRONTO → ENTREGUE)
```

---

## Endpoints resumidos

| Método | Rota | Permissão |
|---|---|---|
| POST | /auth/register | público |
| POST | /auth/login | público |
| GET | /unidades | público |
| POST | /unidades | ADMIN, GERENTE |
| GET | /produtos | público |
| POST | /produtos | ADMIN, GERENTE |
| GET | /estoque/unidades/{id} | ADMIN, GERENTE, ATENDENTE |
| POST | /estoque/unidades/{id}/movimentar | ADMIN, GERENTE |
| POST | /pedidos | CLIENTE, ATENDENTE, ADMIN |
| GET | /pedidos | ADMIN, GERENTE, COZINHA, ATENDENTE |
| GET | /pedidos?canalPedido=TOTEM&status=PAGO | ADMIN, GERENTE, COZINHA, ATENDENTE |
| PATCH | /pedidos/{id}/status | ADMIN, GERENTE, COZINHA, ATENDENTE |
| DELETE | /pedidos/{id} | ADMIN, GERENTE, CLIENTE |
| POST | /pagamentos/pedidos/{id} | CLIENTE, ATENDENTE, ADMIN |
| GET | /fidelidade/usuarios/{id} | ADMIN, GERENTE, CLIENTE |
| POST | /fidelidade/usuarios/{id}/resgatar | ADMIN, CLIENTE |

---

## Coleção Postman

Importe o arquivo `raizes-postman-collection.json` na raiz do repositório.

Ordem sugerida de execução:
1. Auth / Login Admin
2. Unidades / Listar
3. Produtos / Listar
4. Auth / Registrar Cliente
5. Auth / Login Cliente
6. Pedidos / Criar Pedido
7. Pagamentos / Processar Mock
8. Pedidos / Atualizar Status
9. Fidelidade / Consultar Pontos
10. Erros / Estoque Insuficiente

---

## Pagamento Mock

O gateway mock aprova todos os pagamentos **exceto** quando o valor total é múltiplo de 13.

Para testar pagamento **recusado**: crie um pedido com total = R$ 26,00 (ex: 2x Pamonha Doce a R$ 13,00 — ajuste o preço via SQL).

---

## Estrutura do Projeto

```
src/main/java/com/raizes/
├── RaizesApplication.java
├── api/
│   ├── controller/          (7 controllers REST)
│   └── exception/           (GlobalExceptionHandler)
├── application/
│   ├── dto/
│   │   ├── request/         (8 DTOs de entrada)
│   │   └── response/        (7 DTOs de saída)
│   └── service/             (8 services)
├── config/
│   ├── SecurityConfig.java
│   └── SwaggerConfig.java
├── domain/
│   ├── entity/              (8 entidades JPA)
│   └── enums/               (5 enums)
└── infrastructure/
    ├── mock/                (PagamentoGatewayMock)
    ├── repository/          (8 interfaces JPA)
    └── security/            (JwtUtil, JwtFilter)
```

---

## LGPD

- Senhas armazenadas com BCrypt (nunca em texto plano)
- Consentimento LGPD registrado com timestamp no cadastro
- Dados sensíveis não expostos em responses (senha nunca retornada)
- Todas as ações sensíveis (criar pedido, pagamento, cancelamento, mudança de status) são registradas na tabela `audit_log`
- Roles controlam acesso a dados por perfil
