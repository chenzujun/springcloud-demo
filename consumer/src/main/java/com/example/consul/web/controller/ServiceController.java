package com.example.consul.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceController {
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     * 获取所有服务名为service-producer的服务信息并返回到页面
     * @return
     */
    @RequestMapping("/services")
    public Object services(){
        return discoveryClient.getInstances("service-consul-producer");
    }

    /**
     * 随机从服务名为service-producer的服务中获取一个并返回到页面(轮询)
     * @return
     */
    @RequestMapping("/discover")
    public Object discover(){
        return loadBalancerClient.choose("service-consul-producer").getUri().toString();
    }
}
