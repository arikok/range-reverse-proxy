package com.arikok.rangereverseproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class RangeReverseProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(RangeReverseProxyApplication.class, args);
    }

}
