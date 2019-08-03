# SpringCloud学习笔记之分布式配置中心Config

## 前言

SpringCloudConfig 用来分布式系统中的基础设施和微服务应用提供集中化的外部配置支持，它分为服务端和客户端两部分。其中服务端也称为分布式配置中心，它是一个独立的微服务应用，用来连接配置仓库并为客户端提供获取配置信息、加密/解密信息等访问接口；而客户端则是微服务架构中的各个微服务应用或者基础设施，他们通过指定的配置中心来管理应用资源与业务相关的配置内容，并在启动的时候从配置中心获取和加载配置信息。SpringCloudConfig 实现了对服务端和客户端中环境变量和属性配置的抽象映射。默认 Git 来存储配置信息。

服务端依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

客户端依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

## 快速入门

### 服务端开发

#### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>top.simba1949</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.5.RELEASE</version>
        <relativePath /> <!-- lookup parent from repository -->
    </parent>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Greenwich.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <!--spring boot starter : Core starter, including auto-configuration support, logging and YAML-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <!--spring boot starter test : Starter for testing Spring Boot applications with libraries including JUnit, Hamcrest and Mockito-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!--spring boot starter actuator:
            Starter for using Spring Boot’s Actuator which provides production ready features to help you monitor and manage your application
        -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <!--spring boot starter web : Starter for building web, including RestFul, applications using Spring MVC. Uses Tomcat as the default embedded container-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- spring-cloud-config-server -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <!--编译插件-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <!-- 配置使用的 jdk 版本 -->
                    <target>1.8</target>
                    <source>1.8</source>
                </configuration>
            </plugin>
            <!--springboot-maven打包插件 和 热部署配置-->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <fork>true</fork> <!-- 如果没有该配置，devtools不会生效 -->
                    <executable>true</executable><!--将项目注册到linux服务上，可以通过命令开启、关闭以及伴随开机启动等功能-->
                </configuration>
            </plugin>
            <!--资源拷贝插件-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
                <configuration>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>
        </plugins>
        <!--IDEA是不会编译src的java目录的xml文件，如果需要读取，则需要手动指定哪些配置文件需要读取-->
        <resources>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.xml</include>
                </includes>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>**/*</include>
                </includes>
            </resource>
        </resources>
    </build>
</project>
```

#### 配置文件 properties

```properties
spring.application.name=spring-cloud-config-server
server.port=7000

# 配置 Git 仓库的用户名
spring.cloud.config.server.git.uri=https://github.com/simba1949/config-center.git
# 配置仓库路径下的相对搜索位置，可以配置多个
spring.cloud.config.server.git.search-paths=spring-cloud-learn-config/
# 访问 Git 仓库的用户名
spring.cloud.config.server.git.username=
# 访问 Git 仓库的用户名
spring.cloud.config.server.git.password=
```

#### 启动类 application.java

```java
package top.simba1949;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @EnableConfigServer 开启配置服务端功能
 *
 * @author SIMBA1949
 * @date 2019/7/28 11:23
 */
@EnableConfigServer
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

#### 访问配置信息的 URL 与配置文件的映射关系如下所示：

* /{application}/{profile}[/{lable}]
* /{application}-{profile}.yml
* /{lable}/{application}-{profile}.yml
* /{application}-{profile}.properties
* /{lable}/{application}-{profile}.properties

lable：表示版本分支

### 客户端开发

#### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>top.simba1949</groupId>
    <artifactId>spring-cloud-config-client</artifactId>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.5.RELEASE</version>
        <relativePath /> <!-- lookup parent from repository -->
    </parent>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Greenwich.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <!--spring boot starter : Core starter, including auto-configuration support, logging and YAML-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <!--spring boot starter test : Starter for testing Spring Boot applications with libraries including JUnit, Hamcrest and Mockito-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!--spring boot starter actuator:
            Starter for using Spring Boot’s Actuator which provides production ready features to help you monitor and manage your application
        -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <!--spring boot starter web : Starter for building web, including RestFul, applications using Spring MVC. Uses Tomcat as the default embedded container-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <!--编译插件-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <!-- 配置使用的 jdk 版本 -->
                    <target>1.8</target>
                    <source>1.8</source>
                </configuration>
            </plugin>
            <!--springboot-maven打包插件 和 热部署配置-->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <fork>true</fork> <!-- 如果没有该配置，devtools不会生效 -->
                    <executable>true</executable><!--将项目注册到linux服务上，可以通过命令开启、关闭以及伴随开机启动等功能-->
                </configuration>
            </plugin>
            <!--资源拷贝插件-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
                <configuration>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>
        </plugins>
        <!--IDEA是不会编译src的java目录的xml文件，如果需要读取，则需要手动指定哪些配置文件需要读取-->
        <resources>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.xml</include>
                </includes>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>**/*</include>
                </includes>
            </resource>
        </resources>
    </build>
</project>
```

#### 配置文件 bootstrap.properties

这些属性必须配置在 bootstrap.properties 中，这样 config-server 中的配置信息才能被正确加载。

```properties
# spring.application.name 要和配置文件名称 {application} 对应
spring.application.name=user-service
server.port=8081

# 指定分布式配置文件的环境
spring.cloud.config.profile=dev
# 指定分布式配置文件的分支
spring.cloud.config.label=master
# 指定分布式配置中心的uri
spring.cloud.config.uri=http://localhost:7000/
```

#### RestFul 风格获取配置信息数据

```java
package top.simba1949.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SIMBA1949
 * @date 2019/7/28 11:54
 */
@RestController
@RequestMapping("config")
public class ConfigController {

    @Value("${from}")
    private String from;

    @GetMapping
    public String getFromValue(){
        System.out.println("分布式配置文件客户端");
        return from;
    }
}
```

#### 启动类

```java
package top.simba1949;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author SIMBA1949
 * @date 2019/7/28 11:50
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## *配置多个仓库

## 健康检查

```properties
spring.cloud.config.server.health.repositories.check.name=check-repo
spring.cloud.config.server.health.repositories.check.label=master
spring.cloud.config.server.health.repositories.check.profiles=dev
```

* name：应用名
* label：分支名
* profiles：环境名

## 安全保护

由于配置中心存储的内容比较敏感，做一定的安全处理是必需的。为配置中心实现安全保护的方式有很多，比如物理网络限制、OAuth2 授权等。不过由于我们的微服务应用和配置中心基于 SpringBoot 构建，所以与 SpringSecurity 结合使用更方便。

添加依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

默认情况下，SpringSecurity 会为我们分配一个名为 user 的用户名，并且在配置中心启动的时候在日志中打印该用户随机密码。

在客户端的配置文件中需要配置

```properties
# 连接分布式配置中心的用户和密码
spring.cloud.config.username=user
spring.cloud.config.password=29494ad4-f9db-4c6e-9a2e-f8556a1253c2
```

## 失败快速响应与重试

添加下面的配置信息

```properties
# 失败快速响应与重试
spring.cloud.config.fail-fast=true
# 初始重试间隔时间
spring.cloud.config.retry.initial-interval=1000
# 最大间隔时间
spring.cloud.config.retry.max-interval=20000
# 下一间隔的乘数
spring.cloud.config.retry.multiplier=11
# 最大重试次数
spring.cloud.config.retry.max-attempts=6
```



## 高可用配置

将 config-server 作为一个普通的微服务应用，纳入 Eureka 的服务治理体系中。

### 高可用服务端

#### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>top.simba1949</groupId>
    <artifactId>spring-cloud-config-high-availability-server</artifactId>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.5.RELEASE</version>
        <relativePath /> <!-- lookup parent from repository -->
    </parent>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Greenwich.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <!--spring boot starter : Core starter, including auto-configuration support, logging and YAML-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <!--spring boot starter test : Starter for testing Spring Boot applications with libraries including JUnit, Hamcrest and Mockito-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!--spring boot starter actuator:
            Starter for using Spring Boot’s Actuator which provides production ready features to help you monitor and manage your application
        -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <!--spring boot starter web : Starter for building web, including RestFul, applications using Spring MVC. Uses Tomcat as the default embedded container-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- spring-cloud-config-server -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <!--编译插件-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <!-- 配置使用的 jdk 版本 -->
                    <target>1.8</target>
                    <source>1.8</source>
                </configuration>
            </plugin>
            <!--springboot-maven打包插件 和 热部署配置-->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <fork>true</fork> <!-- 如果没有该配置，devtools不会生效 -->
                    <executable>true</executable><!--将项目注册到linux服务上，可以通过命令开启、关闭以及伴随开机启动等功能-->
                </configuration>
            </plugin>
            <!--资源拷贝插件-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
                <configuration>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>
        </plugins>
        <!--IDEA是不会编译src的java目录的xml文件，如果需要读取，则需要手动指定哪些配置文件需要读取-->
        <resources>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.xml</include>
                </includes>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>**/*</include>
                </includes>
            </resource>
        </resources>
    </build>
</project>
```

#### 配置文件 properties

```properties
spring.application.name=spring-cloud-config-high-availability-server
server.port=7001

# eureka 地址
eureka.instance.hostname=localhost
eureka.client.service-url.defalutZone=http://${eureka.instance.hostname}:8761/eureka

# 配置 Git 仓库的用户名
spring.cloud.config.server.git.uri=https://github.com/simba1949/config-center.git
# 配置仓库路径下的相对搜索位置，可以配置多个
spring.cloud.config.server.git.search-paths=spring-cloud-learn-config/
# 访问 Git 仓库的用户名
spring.cloud.config.server.git.username=
# 访问 Git 仓库的用户名
spring.cloud.config.server.git.password=
```

#### 启动类

```java
package top.simba1949;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @author SIMBA1949
 * @date 2019/7/28 18:44
 */
@EnableConfigServer
@EnableDiscoveryClient
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 高可用客户端

#### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>top.simba1949</groupId>
    <artifactId>spring-cloud-config-high-availability-client</artifactId>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.5.RELEASE</version>
        <relativePath /> <!-- lookup parent from repository -->
    </parent>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Greenwich.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <!--spring boot starter : Core starter, including auto-configuration support, logging and YAML-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <!--spring boot starter test : Starter for testing Spring Boot applications with libraries including JUnit, Hamcrest and Mockito-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!--spring boot starter actuator:
            Starter for using Spring Boot’s Actuator which provides production ready features to help you monitor and manage your application
        -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <!--spring boot starter web : Starter for building web, including RestFul, applications using Spring MVC. Uses Tomcat as the default embedded container-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <!--编译插件-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <!-- 配置使用的 jdk 版本 -->
                    <target>1.8</target>
                    <source>1.8</source>
                </configuration>
            </plugin>
            <!--springboot-maven打包插件 和 热部署配置-->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <fork>true</fork> <!-- 如果没有该配置，devtools不会生效 -->
                    <executable>true</executable><!--将项目注册到linux服务上，可以通过命令开启、关闭以及伴随开机启动等功能-->
                </configuration>
            </plugin>
            <!--资源拷贝插件-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
                <configuration>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>
        </plugins>
        <!--IDEA是不会编译src的java目录的xml文件，如果需要读取，则需要手动指定哪些配置文件需要读取-->
        <resources>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.xml</include>
                </includes>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>**/*</include>
                </includes>
            </resource>
        </resources>
    </build>
</project>
```

#### 配置文件 bootstrap.properties

```properties
# spring.application.name 要和配置文件名称 {application} 对应
spring.application.name=user-service
server.port=8081

eureka.instance.hostname=localhost
eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:8761/eureka

# 指定分布式配置中心的uri
#spring.cloud.config.uri=http://localhost:7001/
# 指定分布式配置文件的环境
spring.cloud.config.profile=dev
# 指定分布式配置文件的分支
spring.cloud.config.label=master
# 开启分布式配置系统的注册与发现功能
spring.cloud.config.discovery.enabled=true
# 指定分布式配置中心的服务名
spring.cloud.config.discovery.service-id=SPRING-CLOUD-CONFIG-HIGH-AVAILABILITY-SERVER
# 失败快速响应与重试
spring.cloud.config.fail-fast=true
# 初始重试间隔时间
spring.cloud.config.retry.initial-interval=1000
# 最大间隔时间
spring.cloud.config.retry.max-interval=20000
# 下一间隔的乘数
spring.cloud.config.retry.multiplier=1.1
# 最大重试次数
spring.cloud.config.retry.max-attempts=6
```

#### Controller

```java
package top.simba1949.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SIMBA1949
 * @date 2019/7/28 11:54
 */
@RestController
@RequestMapping("config")
public class ConfigController {

    @Value("${from}")
    private String from;

    @GetMapping
    public String getFromValue(){
        System.out.println("分布式配置文件客户端");
        return from;
    }
}
```

#### 启动类

```java
package top.simba1949;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author SIMBA1949
 * @date 2019/7/28 11:50
 */
@EnableDiscoveryClient
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## 手动刷新配置

### 服务端

#### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>top.simba1949</groupId>
    <artifactId>spring-cloud-config-manual-refresh-server</artifactId>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.5.RELEASE</version>
        <relativePath /> <!-- lookup parent from repository -->
    </parent>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Greenwich.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <!--spring boot starter : Core starter, including auto-configuration support, logging and YAML-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <!--spring boot starter test : Starter for testing Spring Boot applications with libraries including JUnit, Hamcrest and Mockito-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!--spring boot starter actuator:
            Starter for using Spring Boot’s Actuator which provides production ready features to help you monitor and manage your application
        -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <!--spring boot starter web : Starter for building web, including RestFul, applications using Spring MVC. Uses Tomcat as the default embedded container-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- spring-cloud-config-server -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <!--编译插件-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <!-- 配置使用的 jdk 版本 -->
                    <target>1.8</target>
                    <source>1.8</source>
                </configuration>
            </plugin>
            <!--springboot-maven打包插件 和 热部署配置-->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <fork>true</fork> <!-- 如果没有该配置，devtools不会生效 -->
                    <executable>true</executable><!--将项目注册到linux服务上，可以通过命令开启、关闭以及伴随开机启动等功能-->
                </configuration>
            </plugin>
            <!--资源拷贝插件-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
                <configuration>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>
        </plugins>
        <!--IDEA是不会编译src的java目录的xml文件，如果需要读取，则需要手动指定哪些配置文件需要读取-->
        <resources>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.xml</include>
                </includes>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>**/*</include>
                </includes>
            </resource>
        </resources>
    </build>
</project>
```

#### 配置文件

```properties
spring.application.name=spring-cloud-config-manual-refresh-server
server.port=7001

eureka.instance.hostname=localhost
eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:8761/eureka

spring.cloud.config.server.git.uri=https://github.com/simba1949/config-center.git
spring.cloud.config.server.git.search-paths=spring-cloud-config-manual-refresh-client
spring.cloud.config.server.git.username=
spring.cloud.config.server.git.password=
```

#### 启动类

```java
package top.simba1949;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @author SIMBA1949
 * @date 2019/8/2 21:23
 */
@EnableConfigServer
@EnableDiscoveryClient
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 客户端

#### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>top.simba1949</groupId>
    <artifactId>spring-cloud-config-manual-refresh-client</artifactId>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.5.RELEASE</version>
        <relativePath /> <!-- lookup parent from repository -->
    </parent>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Greenwich.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <!--spring boot starter : Core starter, including auto-configuration support, logging and YAML-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <!--spring boot starter test : Starter for testing Spring Boot applications with libraries including JUnit, Hamcrest and Mockito-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!--spring boot starter actuator:
            Starter for using Spring Boot’s Actuator which provides production ready features to help you monitor and manage your application
        -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <!--spring boot starter web : Starter for building web, including RestFul, applications using Spring MVC. Uses Tomcat as the default embedded container-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.springframework.retry/spring-retry -->
        <dependency>
            <groupId>org.springframework.retry</groupId>
            <artifactId>spring-retry</artifactId>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-aop -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <!--编译插件-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <!-- 配置使用的 jdk 版本 -->
                    <target>1.8</target>
                    <source>1.8</source>
                </configuration>
            </plugin>
            <!--springboot-maven打包插件 和 热部署配置-->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <fork>true</fork> <!-- 如果没有该配置，devtools不会生效 -->
                    <executable>true</executable><!--将项目注册到linux服务上，可以通过命令开启、关闭以及伴随开机启动等功能-->
                </configuration>
            </plugin>
            <!--资源拷贝插件-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
                <configuration>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>
        </plugins>
        <!--IDEA是不会编译src的java目录的xml文件，如果需要读取，则需要手动指定哪些配置文件需要读取-->
        <resources>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.xml</include>
                </includes>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>**/*</include>
                </includes>
            </resource>
        </resources>
    </build>
</project>
```

#### 配置文件

bootstrap.properties

```properties
# spring.application.name 要和配置文件名称 {application} 对应
spring.application.name=spring-cloud-config-manual-refresh-client
server.port=8081

eureka.instance.hostname=localhost
eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:8761/eureka

# 指定分布式配置中心的uri
#spring.cloud.config.uri=http://localhost:7001/
# 指定分布式配置文件的环境
spring.cloud.config.profile=dev
# 指定分布式配置文件的分支
spring.cloud.config.label=master
# 开启分布式配置系统的注册与发现功能
spring.cloud.config.discovery.enabled=true
# 指定分布式配置中心的服务名
spring.cloud.config.discovery.service-id=spring-cloud-config-manual-refresh-server
# 失败快速响应与重试
spring.cloud.config.fail-fast=true
# 初始重试间隔时间
spring.cloud.config.retry.initial-interval=1000
# 最大间隔时间
spring.cloud.config.retry.max-interval=20000
# 下一间隔的乘数
spring.cloud.config.retry.multiplier=11
# 最大重试次数
spring.cloud.config.retry.max-attempts=6
```

application.properties

```properties
# 端点配置需要在 application.yml(properties)配置，否则服务暴露端点
management.endpoints.web.exposure.include=*
```

#### 获取配置信息类

```java
package top.simba1949.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @RefreshScope 刷新该类的配置信息数据
 * 
 * @author SIMBA1949
 * @date 2019/7/28 11:54
 */
@RefreshScope
@RestController
@RequestMapping("config")
public class ConfigController {

    @Value("${name}")
    private String name;

    @GetMapping
    public String getFromValue(){
        System.out.println("分布式配置文件客户端");
        return name;
    }
}
```

#### 启动类

```java
package top.simba1949;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author SIMBA1949
 * @date 2019/8/3 6:42
 */
@EnableDiscoveryClient
@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
```

### 测试

需要访问 http://IP:PORT/actuator/refresh 才能刷新到最新配置数据

## 自动刷新配置

### 服务端

#### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>top.simba1949</groupId>
    <artifactId>spring-cloud-bus-and-config-auto-refresh-server</artifactId>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.5.RELEASE</version>
        <relativePath /> <!-- lookup parent from repository -->
    </parent>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Greenwich.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <!--spring boot starter : Core starter, including auto-configuration support, logging and YAML-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <!--spring boot starter test : Starter for testing Spring Boot applications with libraries including JUnit, Hamcrest and Mockito-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!--spring boot starter actuator:
            Starter for using Spring Boot’s Actuator which provides production ready features to help you monitor and manage your application
        -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <!--spring boot starter web : Starter for building web, including RestFul, applications using Spring MVC. Uses Tomcat as the default embedded container-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- spring-cloud-config-server -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <!--编译插件-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <!-- 配置使用的 jdk 版本 -->
                    <target>1.8</target>
                    <source>1.8</source>
                </configuration>
            </plugin>
            <!--springboot-maven打包插件 和 热部署配置-->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <fork>true</fork> <!-- 如果没有该配置，devtools不会生效 -->
                    <executable>true</executable><!--将项目注册到linux服务上，可以通过命令开启、关闭以及伴随开机启动等功能-->
                </configuration>
            </plugin>
            <!--资源拷贝插件-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
                <configuration>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>
        </plugins>
        <!--IDEA是不会编译src的java目录的xml文件，如果需要读取，则需要手动指定哪些配置文件需要读取-->
        <resources>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.xml</include>
                </includes>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>**/*</include>
                </includes>
            </resource>
        </resources>
    </build>
</project>
```

#### 配置文件

```properties
spring.application.name=spring-cloud-bus-and-config-auto-refresh-server
server.port=7001

eureka.instance.hostname=localhost
eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:8761/eureka

spring.cloud.config.server.git.uri=https://github.com/simba1949/config-center.git
spring.cloud.config.server.git.search-paths=spring-cloud-bus-and-config-auto-refresh-client
spring.cloud.config.server.git.username=
spring.cloud.config.server.git.password=
```

#### 启动类

```java
package top.simba1949;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @author SIMBA1949
 * @date 2019/8/2 21:23
 */
@EnableConfigServer
@EnableDiscoveryClient
@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
```

### 客户端 1

#### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>top.simba1949</groupId>
    <artifactId>spring-cloud-bus-and-config-auto-refresh-client</artifactId>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.5.RELEASE</version>
        <relativePath /> <!-- lookup parent from repository -->
    </parent>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Greenwich.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <!--spring boot starter : Core starter, including auto-configuration support, logging and YAML-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <!--spring boot starter test : Starter for testing Spring Boot applications with libraries including JUnit, Hamcrest and Mockito-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!--spring boot starter actuator:
            Starter for using Spring Boot’s Actuator which provides production ready features to help you monitor and manage your application
        -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <!--spring boot starter web : Starter for building web, including RestFul, applications using Spring MVC. Uses Tomcat as the default embedded container-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.springframework.retry/spring-retry -->
        <dependency>
            <groupId>org.springframework.retry</groupId>
            <artifactId>spring-retry</artifactId>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-aop -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-bus-amqp -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-bus-amqp</artifactId>
        </dependency>


    </dependencies>

    <build>
        <plugins>
            <!--编译插件-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <!-- 配置使用的 jdk 版本 -->
                    <target>1.8</target>
                    <source>1.8</source>
                </configuration>
            </plugin>
            <!--springboot-maven打包插件 和 热部署配置-->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <fork>true</fork> <!-- 如果没有该配置，devtools不会生效 -->
                    <executable>true</executable><!--将项目注册到linux服务上，可以通过命令开启、关闭以及伴随开机启动等功能-->
                </configuration>
            </plugin>
            <!--资源拷贝插件-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
                <configuration>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>
        </plugins>
        <!--IDEA是不会编译src的java目录的xml文件，如果需要读取，则需要手动指定哪些配置文件需要读取-->
        <resources>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.xml</include>
                </includes>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>**/*</include>
                </includes>
            </resource>
        </resources>
    </build>
</project>
```

#### 配置文件

**bootsrtap.properties**

```properties
# 指定分布式配置中心的uri
#spring.cloud.config.uri=http://localhost:7001/
# 指定分布式配置文件的环境
spring.cloud.config.profile=dev
# 指定分布式配置文件的分支
spring.cloud.config.label=master
# 开启分布式配置系统的注册与发现功能
spring.cloud.config.discovery.enabled=true
# 指定分布式配置中心的服务名
spring.cloud.config.discovery.service-id=spring-cloud-bus-and-config-auto-refresh-server
# 失败快速响应与重试
spring.cloud.config.fail-fast=true
# 初始重试间隔时间
spring.cloud.config.retry.initial-interval=1000
# 最大间隔时间
spring.cloud.config.retry.max-interval=20000
# 下一间隔的乘数
spring.cloud.config.retry.multiplier=11
# 最大重试次数
spring.cloud.config.retry.max-attempts=6

# rabbitmq 配置信息
spring.rabbitmq.host=192.168.128.4
spring.rabbitmq.port=5672
spring.rabbitmq.username=rabbitmq
spring.rabbitmq.password=rabbitmq
```

**application.properties**

```properties
# spring.application.name 要和配置文件名称 {application} 对应
spring.application.name=spring-cloud-bus-and-config-auto-refresh-client
server.port=8082

eureka.instance.hostname=localhost
eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:8761/eureka

# 端点配置需要在 application.yml(properties)配置，否则服务暴露端点
management.endpoints.web.exposure.include=*
```

#### 获取配置信息类

```java
package top.simba1949.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @RefreshScope 刷新该类的配置信息数据
 *
 * @author SIMBA1949
 * @date 2019/7/28 11:54
 */
@RefreshScope
@RestController
@RequestMapping("config")
public class ConfigController {

	@Value("${name}")
	private String name;

	@GetMapping
	public String getFromValue(){
		System.out.println("分布式配置文件客户端");
		return name;
	}
}
```

#### 启动类

```java
package top.simba1949;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author SIMBA1949
 * @date 2019/8/3 8:41
 */
@EnableDiscoveryClient
@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
```

### 客户端 2

将客户端 1 端口改动，生成客户端2 ，使用 POST 请求方式访问 http://IP2:PORT2/actuator/bus-refresh ，（理论上应该有 Git 的 Webhook 来触发，需要 Webhook 能够访问到的外网才可，这里使用手动触发）

### 访问测试

分别访问客户端1,2获取配置信息的类，获取到最新配置信息



