# SpringCloud学习笔记之客户端负载均衡Ribbon

## 前言

## Ribbon 初识

Spring Cloud Ribbon 是一个基于 HTTP 和 TCP 的客户端负载均衡工具，它基于 Netflix Ribbon 实现。

通过 Spring Cloud Ribbon 的封装，我们在微服务架构中使用客户端负载均衡调用非常简单，仅需两步：

1. 服务提供者只需启动多个服务实例不注册到一个注册中心或者是多个相关联的服务注册中心；
2. 服务消费者直接调用被 @LoadBalanced 注解修饰过的 RestTemplate 来实现面向服务的接口调用；

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







