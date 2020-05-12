package com.tutorials.springbootredis.controller;

import com.tutorials.springbootredis.bean.Product;
import com.tutorials.springbootredis.repository.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RestRedisController {

    @Autowired
    RedisRepository redisRepository;

    @PostMapping( value = "/products")
    public boolean saveProduct (@RequestBody Product product) {
        redisRepository.setValue (product.getId (), product, 10);


        return true;

    }

    @GetMapping(value = "/products/{productId}")
    public Product fetchProduct (@PathVariable String productId) {
        return(Product)redisRepository.getValue (productId);
    }
}
