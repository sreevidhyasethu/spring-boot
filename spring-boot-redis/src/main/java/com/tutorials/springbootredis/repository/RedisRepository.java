package com.tutorials.springbootredis.repository;

import com.tutorials.springbootredis.bean.Product;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class RedisRepository {

    /**
     * Spring Boot Property Redis Host - localhost(default)
     */
    @Value("${spring.redis.host}")
    private String redisHost;

    /**
     * Spring Boot Property Redis Host - localhost(8379)
     */
    @Value("${spring.redis.port}")
    private int redisPort;

    /**
     * Custom Timeout property
     */
    @Value ("${redis.timeOut}")
    private int timeout;

    @Autowired
    @Qualifier("productRedisTemplate")
    RedisTemplate redisTemplate;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory (){
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisHost);
        redisConfig.setPort(redisPort);


        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder clientConfigBuilder = LettucePoolingClientConfiguration.builder ();

        clientConfigBuilder.commandTimeout (Duration.ofSeconds (timeout));
        /**
         * Connection Pooling Configuration - GenericObjectPoolConfig needs commons-pool JAR dependency
         */
        clientConfigBuilder.poolConfig (new GenericObjectPoolConfig ());


        LettucePoolingClientConfiguration clientConfig = clientConfigBuilder.build();

        return new LettuceConnectionFactory(redisConfig, clientConfig);
    }
    @Bean()
    @Qualifier("productRedisTemplate")
    public RedisTemplate<String, Product> redisTemplate (LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, Product> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        // Key is String and value is JSON
        redisTemplate.setKeySerializer (new StringRedisSerializer ());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Product.class));
        return redisTemplate;
    }

    /**
     * This method fetches the value of the Key
     * @param key
     * @return
     */
    public Object getValue(String key) {

        return redisTemplate.opsForValue().get(key);
    }

    /**
     * This method sets the key and value and sets the expiryTime.
     * Key will be evicted after the expiryTime by Redis
     * @param key
     * @param value
     * @param expiryTime
     */
    public void setValue(String key, Object value,int expiryTime) {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire (key, expiryTime, TimeUnit.MINUTES);
    }
}
