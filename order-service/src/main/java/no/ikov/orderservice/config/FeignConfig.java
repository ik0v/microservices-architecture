package no.ikov.orderservice.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "no.ikov.orderservice.integration")
public class FeignConfig {
}
