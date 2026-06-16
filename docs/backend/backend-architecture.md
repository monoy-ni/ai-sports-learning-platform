# 后端架构

## 技术栈

- Java 17
- Spring Boot 3
- Spring Web
- Spring Security
- Spring Data JPA
- Flyway
- MySQL

## 分层

- `controller`：HTTP 请求/响应边界。
- `service`：业务规则和事务编排。
- `repository`：数据库访问。
- `domain`：实体、枚举和领域类型。
- `dto`：请求和响应对象。
- `client`：外部服务调用，主要是 AI 服务。

Controller 统一返回 `ApiResponse<T>`。
