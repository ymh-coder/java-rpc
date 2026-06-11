# ymh-rpc

<p align="center">
  <strong>🚀 一个轻量级 Java RPC 框架</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/JDK-8+-blue.svg" alt="JDK">
  <img src="https://img.shields.io/badge/Maven-3.6+-blue.svg" alt="Maven">
  <img src="https://img.shields.io/badge/License-MIT-green.svg" alt="License">
</p>

---

## 📖 简介

**ymh-rpc** 是一个从零构建的轻量级 Java RPC（远程过程调用）框架，支持多种序列化方式、注册中心、负载均衡、容错机制和重试策略，并提供了 Spring Boot Starter 便于快速集成。

## 🏗️ 项目结构

```
ymh-rpc
├── ymh-rpc-easy                  # 简易版 RPC 核心（HTTP 服务器 + JDK 动态代理）
├── ymh-rpc-core                  # 完整版 RPC 核心（TCP/HTTP、SPI、注册中心等）
├── ymh_rpc_spring_boot_starter   # Spring Boot 自动配置 Starter
├── example-common                # 示例公共模块（接口 & 模型）
├── example-consumer              # 简易版消费者示例
├── example-provider              # 简易版提供者示例
├── example_springboot_consumer   # Spring Boot 消费者示例
└── example_springboot_provider   # Spring Boot 提供者示例
```

## ✨ 核心特性

- **多种序列化方式**：支持 JDK、JSON（Jackson）、Hessian、Kryo 四种序列化协议
- **注册中心**：支持 ZooKeeper 和 Etcd 作为服务注册与发现中心
- **负载均衡**：提供随机、轮询、一致性哈希三种负载均衡策略
- **容错机制**：支持快速失败（Fail-Fast）、故障转移（Fail-Over）、故障恢复（Fail-Back）、静默处理（Fail-Safe）
- **重试策略**：支持固定间隔重试和不重试策略
- **SPI 机制**：通过 SPI 加载器实现组件的可插拔扩展
- **传输协议**：同时支持 HTTP 和 TCP（基于 Vert.x）两种传输方式
- **Spring Boot 集成**：提供 `@EnableRpc` 注解和自动配置，无缝集成 Spring Boot
- **双检锁单例**：全局配置采用线程安全的双检锁单例模式

## 🚀 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+

### 1. 启动提供者

```java
// 简易版
public class EasyProviderExample {
    public static void main(String[] args) {
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);
    }
}
```

### 2. 启动消费者

```java
// 简易版 - 通过动态代理调用远程服务
public class EasyConsumerExample {
    public static void main(String[] args) {
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("ymh");
        User resultUser = userService.getUser(user);
        System.out.println(resultUser.getName());
    }
}
```

### 3. Spring Boot 集成

```java
// 提供者
@SpringBootApplication
@EnableRpc(needServer = true)
public class ExampleSpringbootProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleSpringbootProviderApplication.class, args);
    }
}

// 消费者 - 使用 @RpcReference 注入远程服务
@RestController
public class ExampleController {
    @RpcReference
    private UserService userService;
}
```

## ⚙️ 配置说明

在 `application.properties` 中配置 RPC 参数：

```properties
rpc.name=ymh-rpc
rpc.version=1.0
rpc.serverHost=localhost
rpc.serverPort=8080
rpc.mock=false
rpc.serializer=jdk
rpc.registryConfig.registry=zookeeper
rpc.registryConfig.address=localhost:2181
rpc.loadBalancer=roundRobin
rpc.retryStrategy=fixedInterval
rpc.tolerantStrategy=failFast
```

## 📦 模块说明

| 模块 | 说明 |
|------|------|
| `ymh-rpc-easy` | 简易版实现，包含 HTTP 服务器、JDK 序列化、本地注册中心、JDK 动态代理 |
| `ymh-rpc-core` | 完整版实现，包含 SPI 加载器、多种序列化/负载均衡/容错/重试策略、ZooKeeper 注册中心 |
| `ymh_rpc_spring_boot_starter` | Spring Boot Starter，提供 `@EnableRpc`、`@RpcReference`、`@RpcService` 注解 |

## 📄 License

MIT License

---

**作者**：ymh-coder
