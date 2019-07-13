# SpringCloud学习笔记之客户端负载均衡Ribbon

## 前言

## Ribbon 初识

Spring Cloud Ribbon 是一个基于 HTTP 和 TCP 的客户端负载均衡工具，它基于 Netflix Ribbon 实现。

通过 Spring Cloud Ribbon 的封装，我们在微服务架构中使用客户端负载均衡调用非常简单，仅需两步：

1. 服务提供者只需启动多个服务实例不注册到一个注册中心或者是多个相关联的服务注册中心；
2. 服务消费者直接调用被 @LoadBalanced 注解修饰过的 RestTemplate 来实现面向服务的接口调用；

## 项目实战

### pom 依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>top.simba1949</groupId>
    <artifactId>spring-cloud-rabbion-consumer</artifactId>
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
        <!-- ribbon 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
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

### application.properties

```properties
server.port=7000
spring.application.name=spring-cloud-rabbion-consumer

eureka.instance.hostname=127.0.0.1
eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:8081/eureka
eureka.client.eureka-server-connect-timeout-seconds=20
eureka.client.eureka-server-read-timeout-seconds=20
```

### Rabbtion 配置类

```java
package top.simba1949.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author SIMBA1949
 * @date 2019/7/12 15:57
 */
@Configuration
public class RibbonConfig {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
```

### JavaBean 类

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

### 控制层

```java
package top.simba1949.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import top.simba1949.common.UserDTO;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author SIMBA1949
 * @date 2019/7/12 15:59
 */
@RestController
@RequestMapping("user")
public class UserController {

    private static final String SERVICE_NAME = "http://SPRING-CLOUD-RABBION-PROVIDER";
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("test")
    public String test(){
        return "SUCCESS";
    }

    @GetMapping("list")
    public List<UserDTO> userList(){
        ResponseEntity<List> resp = restTemplate.getForEntity(SERVICE_NAME + "/user/list", List.class);
        List body = resp.getBody();

        List list = restTemplate.getForObject(SERVICE_NAME + "/user/list", List.class);
        return body;
    }

    @GetMapping("get")
    public UserDTO get(UserDTO userDTO){
        Map map = new HashMap<>(16);
        map.put("id", userDTO.getId());
        map.put("username", userDTO.getUsername());
        // 通过可变参数传递参数
        ResponseEntity<UserDTO> resp = restTemplate.getForEntity(SERVICE_NAME + "/user/get?id={1}&username={2}", UserDTO.class, userDTO.getId(), userDTO.getUsername());
        // 通过 Map 传递参数
        ResponseEntity forEntity = restTemplate.getForEntity(SERVICE_NAME + "/user/get?id={id}&username={username}", UserDTO.class, map);

        UserDTO userDTO1 = restTemplate.getForObject(SERVICE_NAME + "/user/get?id={1}&username={2}", UserDTO.class, userDTO.getId(), userDTO.getUsername());
        UserDTO userDTO2 = restTemplate.getForObject(SERVICE_NAME + "/user/get?id={id}&username={username}", UserDTO.class, map);
        return null;
    }

    @PostMapping
    public String insert(@RequestBody UserDTO userDTO){
        ResponseEntity<UserDTO> resp = restTemplate.postForEntity(SERVICE_NAME + "/user/", userDTO, UserDTO.class);

        UserDTO dto = restTemplate.postForObject(SERVICE_NAME + "/user/", userDTO, UserDTO.class);

        URI uri = restTemplate.postForLocation(SERVICE_NAME + "/user/", userDTO, UserDTO.class);
        return "SUCCESS";
    }

    @PutMapping
    public String update(@RequestBody UserDTO userDTO){
        restTemplate.put(SERVICE_NAME + "/user/", userDTO, UserDTO.class);
        return "SUCCESS";
    }

    @DeleteMapping
    public String delete(Integer id){
        restTemplate.delete(SERVICE_NAME + "/user?id={1}", id);
        return "SUCCESS";
    }
}
```

### 启动类

```java
package top.simba1949;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author SIMBA1949
 * @date 2019/7/12 15:53
 */
@EnableDiscoveryClient
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## RestTemplate 详解

### ResponseEntity

getForEntity() 方法返回的是 ResponseEntity<T> ，该对象是 Spring 对 HTTP 请求响应的封装，其中主要存储了 HTTP 的几个重要元素，比如 HTTP 请求状态码的枚举对象 HttpStatus，在它的父类 HttpEntity 中还存储 HTTP 请求头信息对象 HttpHeaders 以及泛型类型的请求体对象。

### GET 请求

#### getForEntity

* url：为请求地址，http:// 服务名 / 具体请求路径
* responseType：为请求响应体 body 的包装类型
* uriVariables：url 中绑定的参数

1. 通过可变参数，使用数值占位符

   ```java
   // 格式
   public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Object... uriVariables)
   // 示例
   ResponseEntity<UserDTO> resp = restTemplate.getForEntity("http://SPRING-CLOUD-RABBION-PROVIDER/user/get?id={1}&username={2}", UserDTO.class, userDTO.getId(), userDTO.getUsername());
   ```

2. 通过 Map， 使用 key 值占位符

   ```java
   // 格式
   public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Map<String, ?> uriVariables)
   // 示例
   Map map = new HashMap<>(16);
   map.put("id", userDTO.getId());
   map.put("username", userDTO.getUsername());
   ResponseEntity forEntity = restTemplate.getForEntity("http://SPRING-CLOUD-RABBION-PROVIDER/user/get?id={id}&username={username}", UserDTO.class, map);
   ```

3. 无参数

   ```java
   // 格式
   public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Object... uriVariables)
   // 示例
   ResponseEntity<List> resp = restTemplate.getForEntity("http://SPRING-CLOUD-RABBION-PROVIDER/user/list", List.class);
   ```

#### getObject

1. 通过可变参数，使用数值占位符

   ```java
   // 格式
   public <T> T getForObject(String url, Class<T> responseType, Object... uriVariables)
   // 示例
   ResponseEntity<UserDTO> resp = restTemplate.getForEntity("http://SPRING-CLOUD-RABBION-PROVIDER/user/get?id={1}&username={2}", UserDTO.class, userDTO.getId(), userDTO.getUsername());
   ```

2. 通过 Map， 使用 key 值占位符

   ```java
   // 格式
   public <T> T getForObject(String url, Class<T> responseType, Map<String, ?> uriVariables)
   // 示例
   Map map = new HashMap<>(16);
   map.put("id", userDTO.getId());
   map.put("username", userDTO.getUsername());
   ResponseEntity forEntity = restTemplate.getForEntity("http://SPRING-CLOUD-RABBION-PROVIDER/user/get?id={id}&username={username}", UserDTO.class, map);
   ```

3. 无参数

   ```java
   // 格式
   public <T> T getForObject(URI url, Class<T> responseType)
   // 示例
   List list = restTemplate.getForObject("http://SPRING-CLOUD-RABBION-PROVIDER/user/list/user/list", List.class);
   ```

### POST 请求

- url：为请求地址，http:// 服务名 / 具体请求路径
- request：为请求体的数据
- responseType：为请求响应体 body 的包装类型
- uriVariables：url 中绑定的参数

#### postForEntity

```java
// 1. 数值占位符（示例参考 get 请求）
public <T> ResponseEntity<T> postForEntity(String url, @Nullable Object request,
			Class<T> responseType, Object... uriVariables)
// 2. key 值占位符（示例参考 get 请求）
public <T> ResponseEntity<T> postForEntity(String url, @Nullable Object request,
			Class<T> responseType, Map<String, ?> uriVariables)
// 3. 无 url 绑定参数（示例参考 get 请求）
public <T> ResponseEntity<T> postForEntity(URI url, @Nullable Object request, Class<T> responseType)
```

#### postForObject

```java
// 1. 数值占位符（示例参考 get 请求）
public <T> T postForObject(String url, @Nullable Object request, Class<T> responseType,
			Object... uriVariables) 
// 2. key 值占位符（示例参考 get 请求）
public <T> T postForObject(String url, @Nullable Object request, Class<T> responseType,
			Map<String, ?> uriVariables)
// 3. 无 url 绑定参数（示例参考 get 请求）
public <T> T postForObject(URI url, @Nullable Object request, Class<T> responseType)
```

#### postForLocation

该方法实现以 POST 请求提交资源，并返回新资源的 URI

```java
// 1. 数值占位符（示例参考 get 请求）
public URI postForLocation(String url, @Nullable Object request, Object... uriVariables)
// 2. key 值占位符（示例参考 get 请求）
public URI postForLocation(String url, @Nullable Object request, Map<String, ?> uriVariables)
// 3. 无 url 绑定参数（示例参考 get 请求）
public URI postForLocation(URI url, @Nullable Object request)
```

### PUT 请求

```java
// 1. 数值占位符（示例参考 get 请求）
public void put(String url, @Nullable Object request, Object... uriVariables)
// 2. key 值占位符（示例参考 get 请求）
public void put(String url, @Nullable Object request, Map<String, ?> uriVariables)
// 3. 无 url 绑定参数（示例参考 get 请求）
public void put(URI url, @Nullable Object request)
```

### DELETE 请求

```java
// 1. 数值占位符（示例参考 get 请求）
public void delete(String url, Object... uriVariables)
// 2. key 值占位符（示例参考 get 请求）
public void delete(String url, Map<String, ?> uriVariables)
// 3. 无 url 绑定参数（示例参考 get 请求）
public void delete(URI url)
```







