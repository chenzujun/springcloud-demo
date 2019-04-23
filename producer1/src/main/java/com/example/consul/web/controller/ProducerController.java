package com.example.consul.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenjun
 * @date 2019/4/23
 * @since V1.0.0
 */
@RestController
public class ProducerController {

    @GetMapping("/producer")
    public String producer(){
        return "consul-producer first demo";
    }
}
