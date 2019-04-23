package com.example.consul.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * feign调用实现
 * name属性:远程服务名，即在consul中注册的（producer）服务名
 * 此接口中方法的方法名和参数要与远程服务中contoller中需保持一致。
 */
@FeignClient(name="service-consul-producer")
public interface HelloRemote {
    @RequestMapping("/hello")
    String hello();
}
