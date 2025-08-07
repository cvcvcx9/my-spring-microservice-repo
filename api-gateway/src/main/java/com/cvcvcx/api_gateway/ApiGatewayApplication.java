package com.cvcvcx.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient; // TODO: EnableDiscoveryClient 임포트 추가

@SpringBootApplication
@EnableDiscoveryClient // TODO: EnableEurekaClient 대신 EnableDiscoveryClient 사용
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

}
