# SpringCloud学习笔记之声明式服务调用Feign

## 前言

SpringCloudRibbo、SpringCloudHystrix 是开发微服务应用的重磅武器，学会了如何在微服务架构中实现客户端负载均衡的服务调用以及如何通过断路器来保护我们的微服务引用。在实践过程中，我们会发现这个两个框架的使用几乎是同时出现的。既然如此，那么是否有更高层次的封装来整合这两个基础工具以简化开发成本呢？SpringCloudFeign 就是这样的一个工具，它是基于 Netflix Feign 实现，整合了 SpringCloudRibbon 与 SpringCloudHystrix，除了提供两者的强大功能之外，它还提供了一种声明式的 Web 服务客户端定义方式。

## 快速入门

### 通用模块

#### common/UserDTO.java

```java
package top.simba1949.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author SIMBA1949
 * @date 2019/7/12 14:25
 */
@Data
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 5572396433234547850L;
    private Integer id;
    private String username;
    private String password;
    private Date birthday;
    private Boolean adultFlag;
    private List<String> stringList;
}
```

### 服务提供者（one & two）

#### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>top.simba1949</groupId>
    <artifactId>spring-cloud-feign-provider-one</artifactId>
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

        <!-- eureka client，用于注册与发现 -->
        <!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-netflix-eureka-client -->
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

#### controller

```java
package top.simba1949.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.simba1949.common.UserDTO;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author SIMBA1949
 * @date 2019/7/12 14:30
 */
@Slf4j
@RestController
@RequestMapping("user")
public class UserController {

    private List<UserDTO> list = new ArrayList<>();

    @PostConstruct
    public void init(){
        for (int i = 0; i < 10; i++){
            UserDTO userDTO = new UserDTO();
            userDTO.setId(i);
            userDTO.setBirthday(new Date(System.currentTimeMillis() + i * (24 * 60 * 60 *1000)));
            userDTO.setUsername("libai-" + i);

            list.add(userDTO);
        }
    }

    @GetMapping("list")
    public List<UserDTO> userList(){
        log.info("spring-cloud-feign-provider-one");
        return list;
    }

    @PostMapping("get")
    public UserDTO get(@RequestBody UserDTO userDTO){
        log.info("spring-cloud-feign-provider-one");
        return list.get(userDTO.getId());
    }

    @PostMapping
    public String insert(@RequestBody UserDTO userDTO){
        log.info("spring-cloud-feign-provider-one");
        userDTO.setId(list.size());
        list.add(userDTO);
        return "SUCCESS";
    }

    @PutMapping
    public String update(@RequestBody UserDTO userDTO){
        log.info("spring-cloud-feign-provider-one");
        list.remove(userDTO.getId());
        list.add(userDTO.getId(), userDTO);
        return "SUCCESS";
    }

    @DeleteMapping
    public String delete(Integer id){
        log.info("spring-cloud-feign-provider-one");
        list.remove(id);
        list.add(id, null);
        return "SUCCESS";
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
 * @date 2019/7/12 14:21
 */
@EnableDiscoveryClient
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

#### 配置文件

服务提供者1配置文件

```properties
server.port=8001
spring.application.name=spring-cloud-feign-provider

eureka.instance.hostname=127.0.0.1
eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:8761/eureka
```

服务提供者2配置文件

```properties
server.port=8002
spring.application.name=spring-cloud-feign-provider

eureka.instance.hostname=127.0.0.1
eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:8761/eureka
```

### 服务消费者

#### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>top.simba1949</groupId>
    <artifactId>spring-cloud-feign-consumer</artifactId>
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

        <!-- eureka client，用于注册与发现 -->
        <!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-netflix-eureka-client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <!-- feign 声明式调用依赖 spring-cloud-starter-openfeign -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
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

#### controller

```java
package top.simba1949.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.simba1949.common.UserDTO;
import top.simba1949.feign.service.UserService;

import java.util.List;

/**
 * @author SIMBA1949
 * @date 2019/7/21 13:57
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("list")
    public List<UserDTO> userList(){
        return userService.userList();
    }

    @GetMapping("get")
    public UserDTO get(UserDTO userDTO){
        return userService.get(userDTO);
    }

    @PostMapping
    public String insert(@RequestBody UserDTO userDTO){
        return userService.insert(userDTO);
    }

    @PutMapping
    public String update(@RequestBody UserDTO userDTO){
        return userService.update(userDTO);
    }

    @DeleteMapping
    public String delete(Integer id){
        return userService.delete(id);
    }
}
```

#### feign.service.UserService

```java
package top.simba1949.feign.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import top.simba1949.common.UserDTO;

import java.util.List;

/**
 * GetMapping/PostMapping/PutMapping/DeleteMapping 必须写写全
 *
 * @author SIMBA1949
 * @date 2019/7/21 13:58
 */
@FeignClient("SPRING-CLOUD-FEIGN-PROVIDER")
public interface UserService {

    @GetMapping("user/list")
    List<UserDTO> userList();

    /**
	 * 如果需要传输对象，必须使用 @RequestBody，服务提供者使用 Post 请求接收
	 * @param userDTO
	 * @return
	 */
    @GetMapping("user/get")
    UserDTO get(@RequestBody UserDTO userDTO);

    @PostMapping("user")
    String insert(@RequestBody UserDTO userDTO);

    @PutMapping("user")
    String update(@RequestBody UserDTO userDTO);

    @DeleteMapping("user")
    String delete(@RequestParam("id") Integer id);
}
```

#### 启动类

```java
package top.simba1949;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @EnableFeignClients 开启 Feign
 *
 * @author SIMBA1949
 * @date 2019/7/21 13:50
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

#### 配置文件

```properties
server.port=7000
spring.application.name=spring-cloud-feign-consumer

eureka.instance.hostname=localhost
eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:8761/eureka

# 需要配置超时时间，否则会请求多个服务，返回错误
ribbon.ConnectTimeout=2000
```

## Feign 实现服务降级

**在配置文件中开启熔断配合**

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>top.simba1949</groupId>
    <artifactId>spring-cloud-feign-consumer</artifactId>
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

        <!-- eureka client，用于注册与发现 -->
        <!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-netflix-eureka-client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <!-- feign 声明式调用依赖 spring-cloud-starter-openfeign -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
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

### common.UserDTO

```java
package top.simba1949.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author SIMBA1949
 * @date 2019/7/12 14:25
 */
@Data
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 5572396433234547850L;
    private Integer id;
    private String username;
    private String password;
    private Date birthday;
    private Boolean adultFlag;
    private List<String> stringList;
}
```

### feign 服务调用feign.service.UserService

```java
package top.simba1949.feign.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import top.simba1949.common.UserDTO;
import top.simba1949.feign.service.fallback.UserServiceFallBackImpl;

import java.util.List;

/**
 * GetMapping/PostMapping/PutMapping/DeleteMapping 必须写写全
 * @FeignClient(name = "SPRING-CLOUD-FEIGN-PROVIDER", fallback = UserServiceFallBackImpl.class)
 * 				name 表示服务名
 * 				fallback 表示服务降级逻辑的实现类
 *
 * @author SIMBA1949
 * @date 2019/7/21 13:58
 */
@FeignClient(name = "SPRING-CLOUD-FEIGN-PROVIDER", fallback = UserServiceFallBackImpl.class)
public interface UserService {

    @GetMapping("user/list")
    List<UserDTO> userList();

    /**
	 * 如果需要传输对象，必须使用 @RequestBody，服务提供者使用 Post 请求接收
	 * @param userDTO
	 * @return
	 */
    @GetMapping("user/get")
    UserDTO get(@RequestBody UserDTO userDTO);

    @PostMapping("user")
    String insert(@RequestBody UserDTO userDTO);

    @PutMapping("user")
    String update(@RequestBody UserDTO userDTO);

    @DeleteMapping("user")
    String delete(@RequestParam("id") Integer id);
}
```

### 服务降级逻辑，需要实现 feign 服务调用接口

```java
package top.simba1949.feign.service.fallback;

import org.springframework.stereotype.Component;
import top.simba1949.common.UserDTO;
import top.simba1949.feign.service.UserService;

import java.util.List;

/**
 * 服务降级逻辑，需要实现对应的 Feign 接口
 *
 * @author SIMBA1949
 * @date 2019/7/21 15:19
 */
@Component
public class UserServiceFallBackImpl implements UserService {
    @Override
    public List<UserDTO> userList() {
        System.err.println("君不见黄河之水天上来");
        return null;
    }

    @Override
    public UserDTO get(UserDTO userDTO) {
        return null;
    }

    @Override
    public String insert(UserDTO userDTO) {
        return null;
    }

    @Override
    public String update(UserDTO userDTO) {
        return null;
    }

    @Override
    public String delete(Integer id) {
        return null;
    }
}
```

### controller

```java
package top.simba1949.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.simba1949.common.UserDTO;
import top.simba1949.feign.service.UserService;

import java.util.List;

/**
 * @author SIMBA1949
 * @date 2019/7/21 13:57
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("list")
    public List<UserDTO> userList(){
        return userService.userList();
    }

    @GetMapping("get")
    public UserDTO get(UserDTO userDTO){
        return userService.get(userDTO);
    }

    @PostMapping
    public String insert(@RequestBody UserDTO userDTO){
        return userService.insert(userDTO);
    }

    @PutMapping
    public String update(@RequestBody UserDTO userDTO){
        return userService.update(userDTO);
    }

    @DeleteMapping
    public String delete(Integer id){
        return userService.delete(id);
    }
}
```

### 启动类

```java
package top.simba1949;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @EnableFeignClients 开启 Feign
 *
 * @author SIMBA1949
 * @date 2019/7/21 13:50
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 配置文件 application.properties

```properties
server.port=7000
spring.application.name=spring-cloud-feign-consumer

eureka.instance.hostname=localhost
eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:8761/eureka

# 需要配置超时时间，否则会请求多个服务，返回错误
ribbon.ConnectTimeout=2000

hystrix.command.default.execution.isolation.thread.tiemoutInMilliseconds=5000
# 打开熔断器
feign.hystrix.enabled=true
```







