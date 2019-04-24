package com.example.consul.remote.fallback;

import com.example.consul.remote.IHelloRemoteClient;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author chenjun
 * @date 2019/4/24
 * @since V1.0.0
 */
@Component
public class HelloRemoteFallbackFactory implements FallbackFactory<IHelloRemoteClient> {

    private final static Logger logger = LoggerFactory.getLogger(HelloRemoteFallbackFactory.class);

    @Override
    public IHelloRemoteClient create(Throwable throwable) {
        logger.info("fallback reason was :{}", throwable.getMessage());
        return new IHelloRemoteClient() {
            @Override
            public String hello() {
                return "服务暂停";
            }
        };
    }
}
