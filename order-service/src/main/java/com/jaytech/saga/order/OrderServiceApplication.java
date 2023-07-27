/**
 * OrderServiceApplication is the entry point of the Order Service application.
 * It bootstraps the Spring Boot application and starts the Order Service.
 */
package com.jaytech.saga.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {

    /**
     * The main method is the entry point of the Order Service application.
     * It bootstraps the Spring Boot application and starts the Order Service.
     *
     * @param args The command line arguments passed to the application (not used in this case).
     */
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
