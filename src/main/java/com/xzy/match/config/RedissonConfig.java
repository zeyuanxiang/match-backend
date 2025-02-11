package com.xzy.match.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

    private String host;

    private int port;

    @Bean
    public RedissonClient getRedissonClient() {
        //1.创建配置
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%s", host, port);
        //使用单个Redis,没有开集群useClusterServers() 设置地址和使用库
        config.useSingleServer().setAddress(redisAddress).setDatabase(0);
        //创建实例
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
