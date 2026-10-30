package com.klef.ms.sdp.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI biddingServiceOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Bidding Service API")
                        .description("REST API for Auction Bidding")
                        .version("1.0"));
    }
}