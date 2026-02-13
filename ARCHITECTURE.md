# 项目架构说明文档

## 项目概述

本项目是一个基于 Spring Boot 3.2.4 的企业级应用程序模板，集成了用户认证、OAuth2授权服务器、微信登录、用户管理和API密钥管理等功能。项目采用经典的三层架构设计，支持 Docker 容器化部署。

## 技术栈

### 核心框架
- **Java**: 21
- **Spring Boot**: 3.2.4
- **Maven**: 项目构建工具

### 主要依赖
- **Spring Boot Web**: RESTful API 支持
- **Spring Boot WebFlux**: 响应式编程支持
- **Spring Boot Data JPA**: 数据持久化
- **Spring Security**: 安全认证与授权
- **Spring OAuth2 Authorization Server**: OAuth2 授权服务器
- **Spring Boot Thymeleaf**: 服务端模板引擎
- **Spring Boot Actuator**: 应用监控与管理

### 数据库与数据访问
- **MySQL**: 8.0（主数据库）
- **Liquibase**: 数据库版本管理与迁移
- **Hibernate/JPA**: ORM 框架

### 其他重要库
- **Lombok**: 简化 Java 代码
- **Guava**: Google 核心库
- **SpringDoc OpenAPI**: API 文档（Swagger UI）
- **Jackson**: JSON 序列化/反序列化

### 部署与运维
- **Docker**: 容器化部署
- **Eclipse Temurin JDK 21**: Docker 基础镜像

## 项目结构

```
springboot-starter/
├── src/
│   ├── main/
│   │   ├── java/com/openai36/aggregation/
│   │   │   ├── app/                      # 应用层（Controller）
│   │   │   │   ├── admin/                # 管理员接口
│   │   │   │   │   └── UserAdminResource.java
│   │   │   │   ├── pub/                  # 公共接口（注册等）
│   │   │   │   │   └── RegisterResource.java
│   │   │   │   ├── rep/                  # 通用响应DTO
│   │   │   │   ├── wechat/               # 微信相关接口
│   │   │   │   ├── MeResource.java       # 当前用户信息接口
│   │   │   │   └── UserSecretKeyResource.java  # API密钥管理接口
│   │   │   ├── eao/                      # 数据访问层（Repository & Entity）
│   │   │   │   ├── UserEntity.java
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── UserRoleEntity.java
│   │   │   │   ├── UserRoleRepository.java
│   │   │   │   ├── UserSecretKeyEntity.java
│   │   │   │   ├── UserSecretKeyRepository.java
│   │   │   │   ├── WechatUserEntity.java
│   │   │   │   ├── WechatUserRepository.java
│   │   │   │   ├── SystemConfigEntity.java
│   │   │   │   └── SystemConfigRepository.java
│   │   │   ├── service/                  # 业务逻辑层
│   │   │   │   ├── dto/                  # 服务层DTO
│   │   │   │   ├── event/                # 事件定义
│   │   │   │   │   ├── UserCreatedEvent.java
│   │   │   │   │   └── UserDeleteEvent.java
│   │   │   │   ├── UserService.java
│   │   │   │   ├── UserSecretKeyService.java
│   │   │   │   ├── WechatService.java
│   │   │   │   ├── WechatLoginCache.java
│   │   │   │   └── InitService.java
│   │   │   ├── security/                 # 安全相关
│   │   │   │   ├── Roles.java
│   │   │   │   ├── Login.java
│   │   │   │   ├── MyUserDetails.java
│   │   │   │   ├── MyUserDetailsService.java
│   │   │   │   └── MyPasswordEncoder.java
│   │   │   ├── common/                   # 通用工具类
│   │   │   │   ├── Constants.java
│   │   │   │   ├── DateTimeUtil.java
│   │   │   │   ├── GeneralOperationResult.java
│   │   │   │   └── StringResult.java
│   │   │   ├── AggregationApplication.java  # 应用启动类
│   │   │   ├── SecurityConfig.java       # 安全配置
│   │   │   ├── WebConfig.java            # Web配置（CORS等）
│   │   │   └── WebClientConfig.java      # WebClient配置
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-prod.properties
│   │       ├── db/changelog/             # Liquibase数据库迁移脚本
│   │       │   ├── db.changelog-master.yaml
│   │       │   ├── user_entity.sql
│   │       │   ├── user_secret_key_entity.sql
│   │       │   ├── wechat_user_entity.sql
│   │       │   └── system_config_entity.sql
│   │       ├── static/                   # 静态资源
│   │       │   ├── css/
│   │       │   └── libs/
│   │       └── templates/                # Thymeleaf模板
│   │           ├── login.html
│   │           ├── register.html
│   │           ├── register_success.html
│   │           ├── wechat_login.html
│   │           └── wechat_login_result.html
│   └── test/                             # 测试代码
├── pom.xml                               # Maven配置文件
├── Dockerfile                            # Docker镜像构建文件
├── README.md                             # 项目说明文档
└── ARCHITECTURE.md                       # 本文档

```

## 架构设计

### 1. 分层架构

本项目采用经典的三层架构模式：

#### 表现层（app）
- **职责**: 处理 HTTP 请求和响应，参数验证，调用业务逻辑层
- **命名规范**: `*Resource.java`
- **子包组织**:
  - `admin/`: 管理员权限的接口
  - `pub/`: 公共访问接口（如注册、登录）
  - `wechat/`: 微信相关接口
  - 根目录: 认证用户的通用接口（如 `MeResource`, `UserSecretKeyResource`）

#### 业务逻辑层（service）
- **职责**: 核心业务逻辑，事务管理，业务规则验证
- **命名规范**: `*Service.java`
- **包含**:
  - `dto/`: 服务层数据传输对象
  - `event/`: 业务事件定义

#### 数据访问层（eao - Entity and Access Object）
- **职责**: 数据持久化，数据库交互
- **命名规范**:
  - Entity: `*Entity.java`
  - Repository: `*Repository.java`
- **使用**: Spring Data JPA

### 2. 核心模块

#### 2.1 用户认证与授权
- **Spring Security**: 提供基础的认证和授权功能
- **OAuth2 Authorization Server**: 实现 OAuth2 和 OpenID Connect 协议
- **JWT**: 使用 RSA 密钥对签名的 JWT 令牌
- **Remember Me**: 支持"记住我"功能
- **自定义认证**:
  - `MyUserDetailsService`: 自定义用户详情服务
  - `MyPasswordEncoder`: 自定义密码编码器
  - `MyUserDetails`: 自定义用户详情

#### 2.2 微信登录集成
- **微信 OAuth2**: 支持微信公众号网页授权登录
- **用户绑定**: 微信用户与系统用户的关联
- **浏览器检测**: 自动识别微信浏览器并重定向

#### 2.3 用户管理
- **用户注册**: 页面注册和API注册
- **用户角色**: 基于角色的访问控制（RBAC）
- **用户配置**: 系统级用户配置管理

#### 2.4 API密钥管理
- **密钥生成**: 为用户生成API访问密钥
- **密钥管理**: 增删查改API密钥
- **安全性**: 密钥与用户绑定

#### 2.5 系统配置
- **动态配置**: 通过数据库管理系统配置
- **功能开关**:
  - 页面登录启用/禁用
  - 页面注册启用/禁用
  - 微信登录启用/禁用

### 3. 数据库设计

使用 Liquibase 进行数据库版本控制，主要数据表包括：

- **user_entity**: 用户基础信息表
- **user_role_entity**: 用户角色关联表
- **user_secret_key_entity**: 用户API密钥表
- **wechat_user_entity**: 微信用户信息表
- **system_config_entity**: 系统配置表

### 4. 安全配置

#### SecurityConfig
- **CSRF**: 禁用（适用于API服务）
- **认证方式**:
  - 表单登录（/login）
  - Bearer Token（JWT）
  - Remember Me Cookie
- **授权服务器**: 集成 OAuth2 授权服务器配置
- **资源服务器**: 同时作为资源服务器验证 JWT 令牌
- **公开端点**: `/openai/v1/**` 不需要认证

#### WebConfig
- **CORS**: 配置跨域访问白名单
- **请求日志**: CommonsRequestLoggingFilter 记录请求详情
- **HTTP交换追踪**: Spring Actuator HTTP Exchange 追踪

### 5. 关键特性

#### 5.1 JWT 密钥管理
- 密钥持久化到 `/var/hc/springboot-starter/aggregation_jwkset`
- 首次启动自动生成 RSA 2048位密钥对
- 后续启动从文件加载，确保令牌验证一致性

#### 5.2 事件驱动
- `UserCreatedEvent`: 用户创建事件
- `UserDeleteEvent`: 用户删除事件
- 支持后续扩展事件监听器处理

#### 5.3 API文档
- SpringDoc OpenAPI 自动生成 API 文档
- Swagger UI 界面访问: `/swagger-ui.html`

#### 5.4 监控与管理
- Spring Boot Actuator 端点全部开放
- 端点访问路径: `/actuator/*`
- 可监控应用健康状态、指标、配置等

## 配置说明

### 应用配置（application.properties）

#### 数据库配置
```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/springboot-starter
spring.datasource.username=root
spring.datasource.password=root
```

#### Jackson JSON配置
- 属性命名策略: SNAKE_CASE
- 排除 null 值
- 属性和Map键按字母顺序排序

#### 日志配置
- Spring Security: INFO
- WebClient: DEBUG
- Request Logging: INFO

#### Session配置
- Cookie最大存活时间: 30天
- 静态资源缓存: 365天

#### OAuth2配置
- Client ID: `public-client`
- 授权类型: authorization_code
- 需要 PKCE（Proof Key for Code Exchange）
- Access Token 有效期: 7天

#### 文件上传
- 最大文件大小: 20MB
- 最大请求大小: 20MB

### 环境配置
- **开发环境**: 使用 `application.properties`
- **生产环境**: 使用 `application-prod.properties`
- Docker 容器默认激活 `prod` profile

## 部署说明

### 本地开发

1. **环境要求**:
   - JDK 21
   - Maven 3.6+
   - MySQL 8.0

2. **数据库准备**:
   ```bash
   docker run --name mysql --restart always -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -d mysql:8.0
   ```

3. **启动应用**:
   - 使用 IDE（推荐 IntelliJ IDEA）直接运行 `AggregationApplication`
   - 或使用 Maven: `mvn spring-boot:run`

4. **访问应用**:
   - 应用地址: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Actuator: http://localhost:8080/actuator

### Docker 部署

1. **构建镜像**:
   ```bash
   docker build -t openai36/springboot-starter .
   ```

2. **运行容器**:
   ```bash
   docker run -it --rm -p 8888:8080 openai36/springboot-starter
   ```

3. **生产环境部署**:
   ```bash
   docker run --name springboot-starter \
     --restart always \
     -p 8888:8080 \
     -v /var/hc/springboot-starter:/var/hc/springboot-starter \
     -d registry.cn-shenzhen.aliyuncs.com/openai36/springboot-starter:latest
   ```

### Maven 自动化

项目配置了 Maven 插件自动化流程：

- **package 阶段**: 自动构建 Docker 镜像
- **install 阶段**: 自动标记镜像并推送到阿里云容器镜像服务

## 快速开始（创建新项目）

1. 修改 `pom.xml` 中的 `artifactId`
2. 使用 IntelliJ IDEA 打开项目
3. 全局替换 `springboot-starter` 为新项目名
4. 更新 Git 远程仓库地址:
   ```bash
   git remote remove origin
   git remote add origin git@github.com:你的用户名/新项目名.git
   ```
5. 修改 `README.md` 中的端口号（如果需要）
6. 修改 `WebConfig.java` 中的 CORS 配置
7. 创建数据库 `springboot-starter`（或修改配置文件中的数据库名）
8. 运行 `AggregationApplication`

## 开发规范

### 命名规范
- **Controller**: `*Resource.java`
- **Service**: `*Service.java`
- **Repository**: `*Repository.java`
- **Entity**: `*Entity.java`
- **DTO**: `*Dto.java` 或 放在 `dto/` 包中
- **Event**: `*Event.java` 或 放在 `event/` 包中

### 包组织规范
- `app/`: 按业务模块或访问权限分包（admin, pub, wechat等）
- `service/`: 按业务领域分包或直接放在根目录
- `eao/`: 所有 Entity 和 Repository 放在同一包
- `security/`: 安全相关的所有类
- `common/`: 通用工具类和常量

### 代码规范
- 使用 Lombok 简化代码（`@Slf4j`, `@Inject`, `@SneakyThrows` 等）
- 使用 JSR-250 注解（`@Inject`, `@RolesAllowed` 等）
- 使用 Jakarta EE 规范（`jakarta.*`）
- Controller 使用 `@Controller` 或 `@RestController`
- Service 使用 `@Service` 或 `@Named`
- 事务使用 `@Transactional`
- 参数验证使用 `@Valid` 和 Jakarta Validation 注解

## 扩展指南

### 添加新的业务模块

1. **创建 Entity**: 在 `eao/` 包中创建实体类
2. **创建 Repository**: 在 `eao/` 包中创建继承 `JpaRepository` 的接口
3. **创建 Liquibase 变更集**: 在 `resources/db/changelog/` 中添加SQL脚本
4. **创建 Service**: 在 `service/` 包中实现业务逻辑
5. **创建 Controller**: 在 `app/` 的相应子包中创建控制器
6. **添加测试**: 在 `src/test/` 中添加单元测试和集成测试

### 添加新的认证方式

1. 实现 `UserDetailsService` 接口
2. 在 `SecurityConfig` 中配置新的认证提供者
3. 如需自定义登录页面，在 `templates/` 中添加模板

### 集成第三方服务

1. 在 `service/` 中创建集成服务类
2. 使用 `WebClient`（响应式）或 `RestTemplate`（传统）
3. 配置相关参数到 `application.properties`
4. 在 `WebClientConfig` 中配置 WebClient Bean

## 注意事项

1. **JPA open-in-view**: 已禁用 (`spring.jpa.open-in-view=false`)，避免数据库连接耗尽
2. **Logbook**: 默认不启用，如需调试可在 `pom.xml` 中取消注释
3. **JWT密钥**: 生产环境应备份 `/var/hc/springboot-starter/aggregation_jwkset` 文件
4. **CORS配置**: 根据实际部署域名修改 `WebConfig.java`
5. **安全性**: 生产环境应修改数据库密码、密钥配置等敏感信息
6. **端口**: 应用默认 8080 端口，Docker容器内部也是 8080

## 常见问题

### Q: 如何修改默认端口？
A: 在 `application.properties` 中添加 `server.port=新端口号`

### Q: 如何禁用某个功能（如微信登录）？
A: 在数据库 `system_config_entity` 表中修改对应的开关字段，或通过管理接口修改

### Q: 如何添加新的角色？
A: 在 `security/Roles.java` 中定义新角色常量，然后在 `UserService` 中分配角色

### Q: 数据库迁移失败怎么办？
A: 检查 `liquibase_changelog` 表，确认失败的变更集，手动修复后更新状态或删除重试

### Q: 如何查看应用日志？
A: Docker部署使用 `docker logs -f springboot-starter`，本地开发查看控制台输出

## 维护与支持

如需修改本架构文档或项目有重大更新，请及时更新此文档以保持同步。

文档版本: 1.0
最后更新: 2026-02-13
