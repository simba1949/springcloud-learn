# SpringCloud 学习笔记

## 前言

SpringCloud 官网：

SpringCLoud 官网文档：

## 什么是微服务架构

“微服务” 一词源于 Martin Fowler 的名为 Microservices 的博文，https://martinfowler.com/articles/microservices.html

简单说，微服务是系统架构上的一种风格，他的主旨是将一个原本独立的系统拆分成若干个小型服务，这些小型服务在各自独立的进程中运行，服务之间通过基于 HTTP 的 RESTful API 进行通信。被拆分的每一个服务都围绕着系统中的某一项或者一些耦合度较高的业务功能进行构建，并且每个服务都维护者自身的数据存储、业务开发、自动化测试案例以及独立部署机制。由于有了轻量级的通信协作基础，所以这些微服务可是使用不同的语言来编写。

## 微服务开源框架

服务治理：dubbo（阿里）、dubbox（当当）、eureka（Netflix）、consul（Apache）

分布式配置管理：disconf（百度）、archaius（Netflix）、qconf（360）、config（SprignCloud）、diamond（淘宝）

批量任务：elastic-job（当当）、azkaban（LinkedIn）、task（SpringCloud）

服务跟踪：hydra（京东）、sleuth（springcloud）、zipkin（twitter）

## SpringCloud 简介

Spring Cloud为开发人员提供了快速构建分布式系统中一些常见模式的工具（例如配置管理，服务发现，断路器，智能路由，微代理，控制总线）。分布式系统的协调导致了样板模式, 使用Spring Cloud开发人员可以快速地支持实现这些模式的服务和应用程序。

### 特性

Spring Cloud专注于提供良好的开箱即用经验的典型用例和可扩展性机制覆盖。

- 分布式/版本化配置
- 服务注册和发现
- 路由
- service - to - service调用
- 负载均衡
- 断路器
- 分布式消息传递

### 子项目

* Spring Cloud Config：配置管理工具，支持使用 Git 存储配置内容，可以使用它实现应用配置的外部化存储、并支持客户端配置信息刷新、加密/解密配置内容等
* Spring Cloud Netflix：核心组件，对多个 Netflix OSS 开源套件进行整合
  * eureka(server/client)：包括服务注册中心、服务注册与发现的实现
  * Hystrix：容错管理组件、实现断路器模式、帮助服务依赖中出现的延迟和为故障提供强大的容错能力
  * Ribbon：客户端负载均衡的服务调用组件
  * Feign：基于 Ribbon 和 Hystrix 的声明式服务调用组件
  * Zuul：网关组件，提供智能路由、访问guol等功能
  * Archaius：外部化配置组件
* Spring Cloud Bus：事件、消息总线，用于传播集群中的状态变化或者事件，以及触发后续的处理，比如用来动态刷新配置等
* Spring Cloud Cluster：针对 Zookeeper、Redis、Hazelcast、Consul 的选举算法和通用状态模式的实现
* Spring Cloud Cloufoundry：与 Pivotal Cloudfoundry 的整合支持
* Spring Cloud Consul：服务发现与配置管理工具
* Spring Cloud Stream：通过 Redis、Rabbit 或者 Kafka 实现的消费微服务，可以通过简单地声明式模型来发送和接收消息
* Spring Cloud AWS：用于简化孩子呢个 Amazon Web Service 的组件
* Spring Cloud Securiry：安全工具包，提供在 Zuul 代理中对 OAuth2 客户端请求的中继器
* Spring Cloud Sleuth：Spring Cloud 应用的分布式跟踪实现，可以完美整合 Zipkin
* Spring Cloud Starters：Spring Cloud 的基础组件，他是基于 Spring Boot 风格项目的基础依赖模块
* Spring Cloud CLI：用于在 Groovy 中快速创建 Spring Cloud 应用的 Spring Boot CLI 插件。



