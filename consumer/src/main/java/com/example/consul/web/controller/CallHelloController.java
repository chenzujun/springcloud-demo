package com.example.consul.web.controller;

import com.example.consul.remote.HelloRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CallHelloController {
    @Autowired
    private LoadBalancerClient loadBalancer;

    @Autowired
    private HelloRemote helloRemote;

    @RequestMapping("/call")
    public String call() {
        //使用均衡负载的形式去获取服务端提供的服务
        ServiceInstance serviceInstance = loadBalancer.choose("service-consul-producer");
        System.out.println("服务地址：" + serviceInstance.getUri());
        System.out.println("服务名称：" + serviceInstance.getServiceId());

        //获取调用结果
        String callServiceResult = new RestTemplate().getForObject(serviceInstance.getUri().toString() + "/hello", String.class);
        System.out.println(callServiceResult);
        return callServiceResult;
    }

    /**
     * 通过feign调用远程接口
     * @return
     */
    @RequestMapping("/hello")
    public String test(){
        return helloRemote.hello()+"  feign测试";
    }
}
