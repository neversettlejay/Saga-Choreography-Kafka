/**
 * PaymentServiceApplication is the entry point of the Payment Service application.
 * It bootstraps the Spring Boot application and starts the Payment Service.
 */
package com.jaytech.saga.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentServiceApplication {

    /**
     * The main method is the entry point of the Payment Service application.
     * It bootstraps the Spring Boot application and starts the Payment Service.
     *
     * @param args The command line arguments passed to the application (not used in this case).
     */
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
