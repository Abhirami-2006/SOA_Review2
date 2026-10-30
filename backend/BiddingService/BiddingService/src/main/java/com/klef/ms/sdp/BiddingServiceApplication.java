package com.klef.ms.sdp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BiddingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BiddingServiceApplication.class, args);
    }
}