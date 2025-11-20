package com.vericv.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class VericvApplication {

    public static void main(String[] args) {
        SpringApplication.run(VericvApplication.class, args);
        System.out.println("🚀 VeriCV Application Started Successfully!");
        System.out.println("📚 Swagger UI: http://localhost:8080/swagger-ui.html");
        System.out.println("📡 API Docs: http://localhost:8080/api-docs");
    }
}