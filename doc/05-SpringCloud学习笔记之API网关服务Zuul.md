# SpringCloud学习笔记之API网关服务Zuul

## 传统路由方式

### pom.xml 依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>top.simba1949</groupId>
    <artifactId>spring-cloud-zuul-api-gateway</artifactId>
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

        <!-- zuul 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
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

### application.properties 配置信息

```properties
server.port=80
spring.application.name=spring-cloud-zuul-api-gateway

# 所有符合 /api-zuul-consumer/** 规则的访问都将被路由转发到 http://localhost:7000/ 地址上
#也就是说 当访问 http://localhost/api-zuul-consumer/user/list 的时候 api 网关服务会将请求路由到 http://localhost:7000/user/list 
zuul.routes.api-zuul-consumer.path=/api-zuul-consumer/**
zuul.routes.api-zuul-consumer.url=http://localhost:7000/
```

### 启动类

```java
package top.simba1949;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @author SIMBA1949
 * @date 2019/7/27 16:19
 */
@EnableZuulProxy
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## 面向服务的路由

### pom.xml 依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>top.simba1949</groupId>
    <artifactId>spring-cloud-zuul-api-gateway-with-eureka</artifactId>
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

        <!-- zuul 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
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

### application.properties 配置信息

```properties
spring.application.name=spring-cloud-zuul-api-gateway-with-eureka
server.port=80

# eureka 配置
eureka.instance.hostname=localhost
eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:8761/eureka

# zuul 配置
zuul.routes.api-zuul-consumer-eureka.path=/api-zuul-consumer-eureka/**
zuul.routes.api-zuul-consumer-eureka.service-id=spring-cloud-zuul-consumer

zuul.routes.spring-cloud-zuul-service.path=/spring-cloud-zuul-service/**
zuul.routes.spring-cloud-zuul-service.service-id=spring-cloud-zuul-service
```

### 启动类

```java
package top.simba1949;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @author SIMBA1949
 * @date 2019/7/27 21:22
 */
@EnableZuulProxy
@EnableDiscoveryClient
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## 请求过滤

为了实现对客户端请求的安全校验和权限控制，最简单和粗暴的方法就是为每个微服务应用都实现一套用于校验签名和鉴别权限的过滤器或者拦截器。不过这样做会增加日后系统的维护难度，因为同一个系统中的各种校验逻辑很多情况下大致相同或者类似，出现代码冗余。所以比较好的方式将这些校验逻辑剥离出来，构建出一个鉴权服务。

Zuul 允许开发者在 API 网关上通过定义过滤器来实现对请求的拦截与过滤，实现的方式很简单，只需要继承 ZuulFilter 抽象类并实现它定义的 4 个抽象方法即可。

```java
package top.simba1949.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author SIMBA1949
 * @date 2019/7/27 22:51
 */
public class AccessFilter extends ZuulFilter {
    /**
	 *  filterType ：过滤器的类型，它决定过滤器在请求的哪个生命周期中执行。
	 *  这里定义 pre ，代表会在请求路由之前执行
	 *
	 * @return
	 */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
	 * filterOrder ： 过滤器的执行顺序。
	 * 当请求在一个阶段中存在多个过滤器时，需要根据该方法返回的值来依次执行
	 *
	 * @return
	 */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
	 * shouldFilter ：判断该过滤器是否需要被执行。
	 *
	 * @return
	 */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
	 * run ： 过滤器的具体逻辑。
	 *
	 * 这里可以通过 currentContext.setSendZuulResponse(false);  令 zuul 过滤该请求，不对其进行路由
	 * 通过 currentContext.setResponseStatusCode(401); 设置返回的错误码
	 * 通过 currentContext.setResponseBody("错误请求"); 对返回的 body 内容进行编辑
	 *
	 * RequestContext currentContext = RequestContext.getCurrentContext();
	 * currentContext.setSendZuulResponse(false);
	 * currentContext.setResponseStatusCode(401);
	 * currentContext.setResponseBody("错误请求");
	 *
	 * @return
	 * @throws ZuulException
	 */
    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        System.out.println("请求方式：" + request.getMethod() + "；请求的 URL:" + request.getRequestURL().toString());
        String token = request.getParameter("token");
        if (null == token){
            currentContext.setSendZuulResponse(false);
            currentContext.setResponseStatusCode(401);
            currentContext.setResponseBody("错误请求");
        }

        return null;
    }
}
```

* filterType：过滤器的类型，它决定过滤器在请求的哪个生命周期中执行。
  1. pre：在请求被路由之前调用
  2. routing：在路由请求时被调用
  3. post：在 routing 和 error 过滤器之后被调用
  4. error：处理请求时发生错误时被调用
* filterOrder：过滤器的执行顺序。当请求在一个阶段中存在多个过滤器时，需要根据该方法返回的值来依次执行，数值越小优先级越高。
* shouldFilter：判断该过滤器是否需要被执行。
* run：过滤器的具体逻辑。

## 路由详解

### 传统路由配置

#### 单实例配置

通过 zuul.routes.<route>.path 与 zuul.routes.<route>.url 参数对的方式配置，比如：

```properties
zuul.routes.user-consumer.path=/user-consumer/**
zuul.routes.user-consumer.url=http://localhost:8081/
```

该配置对于所有符合 /user-consumer/** 规则的请求路由都会转发到 http://localhost:8081/ 。比如，当一个请求 http://zuul-proxy-server:8080/user-consumer/hello 发送到 API 网关上，由于 /user-consumer/hello 能够被上述配置的 path 规则所匹配，所以 API 网关会被转发请求到 http://localhost:8081/hello 地址上。

#### 多实例配置

通过 zuul.routes.<route>.path 与 zuul.routes.<route>.serviceId 参数对的方式配置，比如：

```properties
zuul.routes.user-consumer.path=/user-consumer/**
zuul.routes.user-consumer.serviceId=user-service
ribbon.eureka.enabled=false
user-service.ribbon.listOfServers=http://localhost:8081/,http://localhost:8082/
```

* ribbon.eureka.enabled：由于 zuul.routes.<route>.serviceId 指定的是服务名称，默认情况下 Ribbon 会根据服务发现机制获取配置服务名对应的实例清单。但是，该实例没有整合类似 eurea 之类的服务治理框架，所以需要配置将该参数设置为 false，否则配置的 serviceId 获取不到对应的实例的清单。

### 服务路由配置

```properties
zuul.routes.api-zuul-consumer-eureka.path=/api-zuul-consumer-eureka/**
zuul.routes.api-zuul-consumer-eureka.service-id=spring-cloud-zuul-consumer
```

#### 服务路由的默认规则

由于默认情况下所有 Eureke 上的服务都会被 zuul 自动的创建映射关系进行路由，这会使得一些我们不希望对外开放的服务也可能被外部访问到。这时候，我们可以使用 zuul.ignored-services 参数设置一个服务名匹配表达式来定义不自动创建路由，那么 zuul 将会跳过该服务，不为其创建路由规则。

### 路径匹配

| 通配符 |               说明               |
| :----: | :------------------------------: |
|   ?    |         匹配任意单个字符         |
|   *    |        匹配任意数量的字符        |
|   **   | 匹配任意数量的字符，支持多级目录 |

路由示例

```properties
zuul.routes.api-zuul-consumer-eureka.path=/api-zuul-consumer-eureka/**
zuul.routes.api-zuul-consumer-eureka.service-id=spring-cloud-zuul-consumer

zuul.routes.api-zuul-consumer-eureka-ext.path=/api-zuul-consumer-eureka/ext/**
zuul.routes.api-zuul-consumer-eureka-ext.service-id=spring-cloud-zuul-consumer
```

调用 api-zuul-consumer-eureka-ext 服务的 URL 路径实际上会同时被 /api-zuul-consumer-eureka/** 和 api-zuul-consumer-eureka/ext/** 两个表达式所匹配。在逻辑上，API 网关服务需要优先选择 /api-zuul-consumer-eureka/ext/** 路由，然后再匹配 /api-zuul-consumer-eureka/** 路由才能实现上述需求。但是 properties 配置方式实际上无法保证这样的路由优先顺序的。为了保证优先顺序，需要使用 yaml 配置方式

```yaml
zuul:
  routes:
    api-zuul-consumer-eureka-ext:
      path: /api-zuul-consumer-eureka/ext/**
      service-id: spring-cloud-zuul-consumer
    api-zuul-consumer-eureka:
      path: /api-zuul-consumer-eureka/**
      service-id: spring-cloud-zuul-consumer
```

### 忽略表达式

```properties
zuul.ignored-patterns=/**/hello/*

zuul.routes.api-zuul-consumer-eureka.path=/api-zuul-consumer-eureka/**
zuul.routes.api-zuul-consumer-eureka.service-id=spring-cloud-zuul-consumer
```

通过网关访问 spring-cloud-zuul-consumer 的 /hello 接口 http://api-zuul-consumer-eureka/hello，虽然访问该路径完全符合 /api-zuul-consumer-eureka/** 路由规则，但是由于该路径符合 zuul.ignored-patterns 参数定义的规则，所以不会被正确路由。

### 本地跳转

```properties
zuul.routes.api-a.path=/api-a/**
zuul.routes.api-a.service-id=spring-cloud-zuul-consumer

zuul.routes.api-b.path=/api-b/**
zuul.routes.api-b.service-id=forward:/
```

当访问 /api-b/hello 时，跳转本地 /hello 接口上

### Cookie 与头信息

默认情况下，SpringCloudZuul 在请求路由时，会过滤掉 HTTP 请求头信息中的一些敏感信息，防止他们被传递到下游的外部服务器。默认的敏感头信息通过 zuul.sensitiveHeaders 参数定义，包括 Cookie、Set-conkie、Authorization 三个属性。所以，在我们开发 Web 项目时常用的 Cookie 在 SpringCloudZuul 网关中默认是不会传递的，这就会引发一个问题：如果我们要将使用了 SpringSecurity、Shiro等安全框架构建的 Web 应用通过 SpringCloudZuul 构建的网关来进行路由时，由于 Cookie 信息无法传递，我们的 Web 应用无法实现登录和鉴权。解决方法如下

1. 通过设置全局参数为空来覆盖默认值，具体如下(不推荐)

   ```properties
   zuul.senditiveHeaders=
   ```

2. 通过指定路由的参数来配置，方法有下面两种

   ```properties
   # 方法一：对指定路由开启自定义敏感头
   zuul.routes.<router>.customSensitiveHeaders=true
   # 方法二：将指定路由的敏感头设置为空
   zuul.routes.<router>.sensitiveHeader=
   ```

### 重定向问题

在解决了 Cookie 问题之后，我们已经能够通过网关来访问并登录到我们的 Web 应用了。但是这个时候又会发现另一个问题：虽然可以通过网关访问登录页面并发起登录请求，但是登录成功之后，我们跳转到的页面 URL 却是具体 Web 应用实例的地址，而不是通过网关路由的地址。这个问题非常严重，因为使用 API 网关的一个重要原因就是要将网关作为统一入口，从而不暴露所有的内部服务细节。

通过浏览器开发工具查看登录以及登录之后的请求详情，可以发现，引起问题的大概原因使由于 SpringSecurity 或者 Shiro 在登录完成之后，通过重定向的方式跳转到登录后的页面，此时登录后的请求结果是 302 ，请求响应头信息中的 Location 指向具体的服务实例地址，而请求头信息中的 Host 也指向具体的服务实例 IP 地址和端口。所以根本原因在于 SpringCloudZuul 在路由请求时，并没有将最初的 Host 信息设置正确

```properties
zuul.addHostHeader=true
```

### Hystrix 和 Ribbon支持

spring-cloud-starter-netflix-zuul 自身包含对 hystrix 和 Ribbon 模块的依赖，所以 Zuul 天生就拥有线程隔离和断路器的自我保护功能，以及对服务调用的客户端负载均衡功能。但是需要注意的是，当使用 path 与 url 的映射关系来配置路由规则的时候，对于路由转发的请求不会采用 HystrixCommand 来包装，所以这类路由请求没有线程隔离和断路器的保护，并且也不会有负载均衡的能力。因此，我们在使用 Zuul 的时候尽量使用 path 与 serviceId 的组合来配置，这样不仅可以保证 API 网关的健壮和稳定，也能用的 Ribbon 的客户端负载均衡功能。

```properties
# hystrix 可能会出现服务能够调用成功但是还会调用熔断回调方法，这里需要配置熔断时间
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=10000
# 该参数用来设置路由转发请求的时候，创建请求连接的超时时间
eureka.client.eureka-server-connect-timeout-seconds=20
# 该参数用来设置路由转发请求的超时时间
eureka.client.eureka-server-read-timeout-seconds=20
```

### 动态路由

创建用来启动 API 网关的应用主类。这里我们需要使用 @RefreshScope 注解来将 Zuul 的配置内容动态化具体实现如下

```java
package top.simba1949;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;

/**
 * @author SIMBA1949
 * @date 2019/7/27 21:22
 */
@EnableZuulProxy
@EnableDiscoveryClient
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
	 * @RefreshScope 动态刷新 api 
	 * 
	 * @return
	 */
    @RefreshScope
    @ConfigurationProperties("zuul")
    public ZuulProperties zuulProperties(){
        return new ZuulProperties();
    }
}
```

### 动态过滤器，需要借助 groovy