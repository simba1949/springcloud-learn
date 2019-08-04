# SpringCloud学习笔记之分布式服务跟踪Sleuth

## 实战演练

### common 包

#### user

```java
package top.simba1949.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author SIMBA1949
 * @date 2019/8/3 11:11
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 2474529628017078524L;
    private Long id;
    private String username;
    private Date birthday;
}
```

### 服务端

#### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>top.simba1949</groupId>
    <artifactId>spring-cloud-sleuth-service</artifactId>
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
        <!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-sleuth -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-sleuth</artifactId>
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
import top.simba1949.common.User;

import java.util.Date;

/**
 * @author SIMBA1949
 * @date 2019/8/3 11:10
 */
@Slf4j
@RestController
@RequestMapping("user")
public class UserController {
    /**
	 * 需要打印日志，才能看到链路追踪
	 * @param user
	 * @return
	 */
    @PostMapping
    public User getUser(@RequestBody User user){
        log.warn("the request's data of UserController-getUser is {}", user);
        user.setId(1L);
        user.setUsername("李白");
        user.setBirthday(new Date());

        return user;
    }
}
```

#### 配置文件

```properties
spring.application.name=spring-cloud-sleuth-service
server.port=7000

eureka.instance.hostname=localhost
eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:8761/eureka
```

#### 启动类

```java
package top.simba1949;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author SIMBA1949
 * @date 2019/8/3 11:08
 */
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
    <artifactId>spring-cloud-sleuth-client</artifactId>
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
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-sleuth</artifactId>
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.simba1949.common.User;
import top.simba1949.service.UserService;

/**
 * @author SIMBA1949
 * @date 2019/8/3 11:20
 */
@Slf4j
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public User getUser(){
        log.info("the quest coming");
        User user = new User();
        User result = userService.getUser(user);
        return result;
    }
}
```

#### feign service 

```java
package top.simba1949.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.simba1949.common.User;
import top.simba1949.service.fallback.UserServiceImpl;

/**
 * @author SIMBA1949
 * @date 2019/8/3 11:21
 */
@FeignClient(value = "spring-cloud-sleuth-service", fallback = UserServiceImpl.class)
public interface UserService {
    /**
	 * 获取 user
	 *
	 * 传输对象需要使用 Post 请求
	 * @param user
	 * @return
	 */
    @PostMapping(value = "user")
    User getUser(@RequestBody User user);
}
```

#### fallback

```java
package top.simba1949.service.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.simba1949.common.User;
import top.simba1949.service.UserService;

/**
 * @author SIMBA1949
 * @date 2019/8/3 11:24
 */
@Slf4j
@Component
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        log.warn("the request's data of fallback is {}", user);
        return null;
    }
}
```

#### 配置文件

```properties
spring.application.name=spring-cloud-sleuth-client
server.port=8081

eureka.instance.hostname=localhost
eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:8761/eureka

# 需要配置超时时间，否则会请求多个服务，返回错误
ribbon.ConnectTimeout=5000

hystrix.command.default.execution.isolation.thread.tiemoutInMilliseconds=5000
# 打开熔断器
feign.hystrix.enabled=true
```

#### 启动类

```java
package top.simba1949;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author SIMBA1949
 * @date 2019/8/3 11:16
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

## 日志分析

```
# 客户端请求日志
2019-08-03 12:01:19.352  INFO [spring-cloud-sleuth-client,b5e10e3c4bf53798,b5e10e3c4bf53798,false] 31604 --- [nio-8081-exec-9] top.simba1949.controller.UserController  : the quest coming
# 服务端请求日志
2019-08-03 12:01:19.356  WARN [spring-cloud-sleuth-service,b5e10e3c4bf53798,c8c228cc3683ecdb,false] 8316 --- [nio-7000-exec-2] top.simba1949.controller.UserController  : the request's data of UserController-getUser is User(id=null, username=null, birthday=null)
```

1. 第一个值 spring-cloud-sleuth-service：记录应用的名称
2. 第二个值 b5e10e3c4bf53798：SpringCloudSleuth 生成的一个 ID，成为 Trece ID，用来表示一条请求链路。一个请求链路包含一个 Trace ID，多个 Span ID
3. 第三个值 c8c228cc3683ecdb：SpringCloudSleuth 生成的另外一个 ID，成为 Span ID，表示一个基本的工作单元，比如发送一个 HTTP 请求
4. 第四个值 false：表示是否要将该信息输出到 Zipkin 等服务中来收集和展示。

上面的四个值得 Trace ID 和 Span ID 是 SpringCloudSleuth 实现分布式服务跟踪的核心。在一次服务请求链路的调用过程中，会保持并传递同一个 Trace ID，从而将整个分布于不同微服务进程中的请求跟踪信息串联起来。在上面输出的内容为例，spring-cloud-sleuth-client 和 spring-cloud-sleuth-service 同属于一个前端服务请求来源，所以他们的 Trace ID 是相同的，处于同一条请求链路中。

## 跟踪原理

分布式系统中的服务跟踪在理论上并不复杂，它主要包括下面两个关键点。

1. 为了实现请求跟踪，当请求发送到分布式系统的入口端点时，只需要服务跟踪框架为该请求创建一个唯一的跟踪标识，同时在分布式系统流转的时候，框架始终保持传递这个唯一标识，直到返回给请求方为止，这个唯一标识就是 Trace ID。通过 Trace ID 的记录，就能将所有请求过程的日志关联起来。
2. 为了统计各个处理单元的时间延迟，当请求到达各个组件时，或是处理逻辑到达某个状态时，也通过一个唯一标识来标记它的开始、具体过程以及结束，该表示就是 Span ID。对于每个 Span ID来说，他必须有两个开始和结束两个节点，通过记录开始 Span 和结束 Span 的时间戳，就能统计出该 Span 的时间延迟，除了时间戳记录之外，他还可以包含一些其他元数据，比如事件名称、请求信息等。

在实战演练中，在 spring-cloud-sleuth-client 发送到 spring-cloud-sleuth-service 之前，Sleuth 会在该请求的 Header 中增加实现跟踪需要的重要信息，主要有下面几个

* X-B3-TraceId：一个请求链路（Trace）的唯一标识，必需的值
* X-B3-SpanId：一个工作单元（Span）的唯一标识，必需的值
* X-B3-ParentSpanId：标识当前工作单元所属的上一个工作单元，Root Span（请求链路的第一个单元）的该值为空
* X-B3-Sampled：是否被抽样输出的标识，1 表示需要被输出，0 表示不需要被输出
* X-Span-Name：工作单元的名称

```java
log.info("X-B3-TraceId is {}, X-B3-SpanId is {}, X-B3-ParentSpanId is {}, X-B3-Sampled  is {}, X-Span-Name is {}",
         request.getHeader("X-B3-TraceId"),
         request.getHeader("X-B3-SpanId"),
         request.getHeader("X-B3-ParentSpanId"),
         request.getHeader("X-B3-Sampled"),
         request.getHeader("X-Span-Name")
        );
```

## 抽样收集

```properties
# 抽样比例, 0.1 代表 10% 的请求跟踪信息
spring.sleuth.sampler.probability=0.1
```

## 与 Logstash 整合

由于日志文件都离散地存储在各个服务实例的文件系统之上，仅仅通过查看日志文件来分析请求链路依然是件相当麻烦的事情，所以需要引入一些工具帮忙集中收集、存储和搜索这些跟踪信息。引入基于日志的分析系统是一个不错的选择，ELK。

ELK 平台主要有 ElasticSearch、Logstash 和 Kibana 三个开源工具组成

* ElasticSearch 是一个开源分布式搜索引擎，他的特点是：分布式、零配置，自动发现，索引自动分片，索引副本机制，RESTful 风格接口，多数据源，自动搜索负载等。
* Logstash 是一个完全开源的工具，他可以对日志进行收集、过滤，并将其存储供以后使用。
* Kibana 也是一个开源和免费的工具，它可以为 Logstash 和 ElasticSearch 提供日志分析友好的 Web 界面，可以帮助汇总、分析和搜索重要数据日志。

### pom 依赖

```xml
<!-- https://mvnrepository.com/artifact/net.logstash.logback/logstash-logback-encoder -->
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>6.1</version>
</dependency>
```

### 日志配置文件

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<configuration scan="true" scanPeriod="60 seconds" debug="false">
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    ​
    <springProperty scope="context" name="springAppName" source="spring.application.name"/>
    <!-- Example for logging into the build folder of your project -->
    <property name="LOG_FILE" value="${BUILD_FOLDER:-build}/${springAppName}"/>​

    <property name="CONSOLE_LOG_PATTERN"
              value="%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr([${springAppName:-},%X{X-B3-TraceId:-},%X{X-B3-SpanId:-},%X{X-Span-Export:-}]){yellow} %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}"/>

    <!-- Appender to log to console -->
    <appender name="console" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <!-- Minimum logging level to be presented in the console logs-->
            <level>INFO</level>
        </filter>
        <encoder>
            <pattern>${CONSOLE_LOG_PATTERN}</pattern>
            <charset>utf8</charset>
        </encoder>
    </appender>

    <!-- Appender to log to file -->​
    <!--<appender name="flatfile" class="ch.qos.logback.core.rolling.RollingFileAppender">-->
    <!--<file>${LOG_FILE}</file>-->
    <!--<rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">-->
    <!--<fileNamePattern>${LOG_FILE}.%d{yyyy-MM-dd}.gz</fileNamePattern>-->
    <!--<maxHistory>7</maxHistory>-->
    <!--</rollingPolicy>-->
    <!--<encoder>-->
    <!--<pattern>${CONSOLE_LOG_PATTERN}</pattern>-->
    <!--<charset>utf8</charset>-->
    <!--</encoder>-->
    <!--</appender>-->
    ​
    <!-- Appender to log to file in a JSON format -->
    <appender name="logstash" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_FILE}.json</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE}.json.%d{yyyy-MM-dd}.gz</fileNamePattern>
            <maxHistory>7</maxHistory>
        </rollingPolicy>
        <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
            <providers>
                <timestamp>
                    <timeZone>UTC</timeZone>
                </timestamp>
                <pattern>
                    <pattern>
                        {
                        "severity": "%level",
                        "service": "${springAppName:-}",
                        "trace": "%X{X-B3-TraceId:-}",
                        "span": "%X{X-B3-SpanId:-}",
                        "exportable": "%X{X-Span-Export:-}",
                        "pid": "${PID:-}",
                        "thread": "%thread",
                        "class": "%logger{40}",
                        "rest": "%message"
                        }
                    </pattern>
                </pattern>
            </providers>
        </encoder>
    </appender>
    <!--使用 LogstashTcpSocketAppender 将日志内容直接通过 Tcp Socket 输出到 Logstash 服务端-->
<!--    <appender name="logstash" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <destination>127.0.0.1:9250</destination>
    </appender>-->
    ​
    <root level="INFO">
        <appender-ref ref="console"/>
        <appender-ref ref="logstash"/>
        <!--<appender-ref ref="flatfile"/>-->
    </root>
</configuration>
```

## 与 Zipkin 整合

ELK 平台提供的收集、存储、搜索等强大功能，我们对跟踪信息的管理和使用已经变得非常便利。但是，在 ELK 平台中的数据分析维度缺少对请求链路中各个阶段时间延迟的关注，很多时候我们追溯请求链路的一个原因是为了找到调用链路中出现延迟过高的瓶颈源，或为实现对分布式系统做延迟监控等与时间消耗相关的需求。需要引入 Zipkin 得以轻松整合。

Zipkin 的基础架构，有四个核心组件构成：

* Collector：收集器组件，主要处理从外部系统发送过来的跟踪信息，将这些信息转换为 Zipkin 内部处理的 Span 格式，以支持后续的存储、分析、展示等功能。
* Storage：存储组件，它主要处理收集器收到的跟踪信息，默认会将这些信息存储在内存中。也可以修改存储策略，通过使用其他存储组件将跟踪信息存储到数据库中。
* RESTful API：API 组件，主要用来提供外部访问接口。比如给客户端展示跟踪信息，或是外接系统访问以实现监控。
* Web UI：基于 API 组件实现的上层应用。通过 UI 组件，用户可以方便而又直观地查询和分析跟踪信息。

### Zipkin Server

SpringCloud 不推荐通过 SpringCloud & SpringBoot 构建 Zipkin Server 服务。

官网推荐的构建方式：https://zipkin.io/pages/quickstart

docker 方式构建

```
docker run -d -p 9411:9411 openzipkin/zipkin
```

java 方式构建，至少java8

```
curl -sSL https://zipkin.io/quickstart.sh | bash -s
java -jar zipkin.jar
```

从源码构建

````
# get the latest source
git clone https://github.com/openzipkin/zipkin
cd zipkin
# Build the server and also make its dependencies
./mvnw -DskipTests --also-make -pl zipkin-server clean install
# Run the server
java -jar ./zipkin-server/target/zipkin-server-*exec.jar
````

### Zipkin 客户端

#### common

user

```java
package top.simba1949.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author SIMBA1949
 * @date 2019/8/3 11:11
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 2474529628017078524L;
    private Long id;
    private String username;
    private Date birthday;
}
```

#### 服务提供者

pom

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>top.simba1949</groupId>
    <artifactId>spring-cloud-sleuth-zipkin-service</artifactId>
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
        <!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-sleuth -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-sleuth</artifactId>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-sleuth-zipkin -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-sleuth-zipkin</artifactId>
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

配置文件

application.properties

```properties
spring.application.name=spring-cloud-sleuth-zipkin-service
server.port=7000

eureka.instance.hostname=localhost
eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:8761/eureka

# 抽样比例, 0.1 代表 10% 的请求跟踪信息
spring.sleuth.sampler.probability=1.0

# 配置 zipkin 地址
spring.zipkin.base-url=http://192.168.128.5:9411
```

logback-spring.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<configuration scan="true" scanPeriod="60 seconds" debug="false">
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    ​
    <springProperty scope="context" name="springAppName" source="spring.application.name"/>

    <property name="CONSOLE_LOG_PATTERN"
              value="%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr([${springAppName:-},%X{X-B3-TraceId:-},%X{X-B3-SpanId:-},%X{X-Span-Export:-}]){yellow} %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}"/>

    <!-- Appender to log to console -->
    <appender name="console" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <!-- Minimum logging level to be presented in the console logs-->
            <level>INFO</level>
        </filter>
        <encoder>
            <pattern>${CONSOLE_LOG_PATTERN}</pattern>
            <charset>utf8</charset>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="console"/>
    </root>
</configuration>
```

controller

```java
package top.simba1949.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.simba1949.common.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author SIMBA1949
 * @date 2019/8/3 11:10
 */
@Slf4j
@RestController
@RequestMapping("user")
public class UserController {
    /**
	 * 需要打印日志，才能看到链路追踪
	 * @param user
	 * @return
	 */
    @PostMapping
    public User getUser(@RequestBody User user, HttpServletRequest request){
        log.warn("the request's data of UserController-getUser is {}", user);
        user.setId(1L);
        user.setUsername("李白");
        user.setBirthday(new Date());
        log.info("X-B3-TraceId is {}, X-B3-SpanId is {}, X-B3-ParentSpanId is {}, X-B3-Sampled  is {}, X-Span-Name is {}",
                 request.getHeader("X-B3-TraceId"),
                 request.getHeader("X-B3-SpanId"),
                 request.getHeader("X-B3-ParentSpanId"),
                 request.getHeader("X-B3-Sampled"),
                 request.getHeader("X-Span-Name")
                );

        return user;
    }
}
```

启动类

```java
package top.simba1949;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author SIMBA1949
 * @date 2019/8/3 11:08
 */
@EnableDiscoveryClient
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

#### 服务消费者

pom

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>top.simba1949</groupId>
    <artifactId>spring-cloud-sleuth-zipkin-client</artifactId>
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
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-sleuth</artifactId>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-sleuth-zipkin -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-sleuth-zipkin</artifactId>
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

配置文件

application.properties

```properties
server.port=8081

eureka.instance.hostname=localhost
eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:8761/eureka

# 需要配置超时时间，否则会请求多个服务，返回错误
ribbon.ConnectTimeout=5000

hystrix.command.default.execution.isolation.thread.tiemoutInMilliseconds=5000
# 打开熔断器
feign.hystrix.enabled=true

# 抽样比例, 0.1 代表 10% 的请求跟踪信息
spring.sleuth.sampler.probability=1.0

# 配置 zipkin 地址 
spring.zipkin.base-url=http://192.168.128.5:9411
```

bootstrap.properties

```properties
spring.application.name=spring-cloud-sleuth-zipkin-client
```

logback-spring.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<configuration scan="true" scanPeriod="60 seconds" debug="false">
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    ​
    <springProperty scope="context" name="springAppName" source="spring.application.name"/>

    <property name="CONSOLE_LOG_PATTERN"
              value="%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr([${springAppName:-},%X{X-B3-TraceId:-},%X{X-B3-SpanId:-},%X{X-Span-Export:-}]){yellow} %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}"/>

    <!-- Appender to log to console -->
    <appender name="console" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <!-- Minimum logging level to be presented in the console logs-->
            <level>INFO</level>
        </filter>
        <encoder>
            <pattern>${CONSOLE_LOG_PATTERN}</pattern>
            <charset>utf8</charset>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="console"/>
    </root>
</configuration>
```

service 接口

```java
package top.simba1949.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.simba1949.common.User;
import top.simba1949.service.fallback.UserServiceImpl;

/**
 * @author SIMBA1949
 * @date 2019/8/3 11:21
 */
@FeignClient(value = "spring-cloud-sleuth-zipkin-service", fallback = UserServiceImpl.class)
public interface UserService {
    /**
	 * 获取 user
	 *
	 * 传输对象需要使用 Post 请求
	 * @param user
	 * @return
	 */
    @PostMapping(value = "user")
    User getUser(@RequestBody User user);
}
```

fallback 服务降级

```java
package top.simba1949.service.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.simba1949.common.User;
import top.simba1949.service.UserService;

/**
 * @author SIMBA1949
 * @date 2019/8/3 11:24
 */
@Slf4j
@Component
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        log.warn("the request's data of fallback is {}", user);
        return null;
    }
}
```

controller

```java
package top.simba1949.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.simba1949.common.User;
import top.simba1949.service.UserService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author SIMBA1949
 * @date 2019/8/3 11:20
 */
@Slf4j
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public User getUser(){
        log.info("the quest coming");
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        log.info("X-B3-TraceId is {}, X-B3-SpanId is {}, X-B3-ParentSpanId is {}, X-B3-Sampled  is {}, X-Span-Name is {}",
                 request.getHeader("X-B3-TraceId"),
                 request.getHeader("X-B3-SpanId"),
                 request.getHeader("X-B3-ParentSpanId"),
                 request.getHeader("X-B3-Sampled"),
                 request.getHeader("X-Span-Name")
                );
        User user = new User();
        User result = userService.getUser(user);
        return result;
    }
}
```

启动类

```java
package top.simba1949;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author SIMBA1949
 * @date 2019/8/3 11:16
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

