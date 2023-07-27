package com.jaytech.saga.payment.config;

import com.jaytech.saga.commons.event.OrderEvent;
import com.jaytech.saga.commons.event.OrderStatus;
import com.jaytech.saga.commons.event.PaymentEvent;
import com.jaytech.saga.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * The PaymentConsumerConfig class provides the configuration for the paymentProcessor function,
 * which consumes order events, processes payments, and produces payment events.
 * It utilizes reactive programming concepts such as Flux and Mono to handle events in a non-blocking and asynchronous manner.
 */

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PaymentConsumerConfig {

    /**
     * The PaymentService instance is autowired into the PaymentConsumerConfig class.
     * The PaymentService is responsible for handling payment-related operations, such as processing new orders and canceling orders.
     * By autowiring the PaymentService, the paymentProcessor function can interact with the PaymentService to handle payment events.
     */
    private final PaymentService paymentService;

    /**
     * The paymentProcessor function is a reactive function that consumes a Flux of OrderEvent and produces a Flux of PaymentEvent.
     * It is annotated with @Bean, indicating that it is a Spring bean and can be accessed by other components.
     * The paymentProcessor function processes order events and generates corresponding payment events using reactive streams.
     * It returns a Flux of PaymentEvent, representing the stream of payment events that will be produced.
     *
     * The Function interface defines a single abstract method apply, which is implemented here to process order events.
     * The input to the function is a Flux of OrderEvent (orderEventFlux), and the output is a Flux of PaymentEvent.
     * The flatMap operator is used to apply the processPayment function to each OrderEvent in the input Flux and produce a new Flux of PaymentEvent.
     * The flatMap operation allows processing each order event independently and asynchronously, enabling non-blocking event handling.
     *
     * Use: 'paymentProcessor' method is annotated with @Bean. This annotation tells Spring to register this method as a bean in the Spring application context. When the Spring application context is created and the PaymentService is instantiated, Spring will automatically call the paymentProcessor method and create a bean of type Function<Flux<OrderEvent>, Flux<PaymentEvent>> using the returned value of the method.
     * The paymentProcessor bean can then be used by other parts of the application that require a Function<Flux<OrderEvent>, Flux<PaymentEvent>> bean. For example, if you have a reactive pipeline or a stream of OrderEvent objects in your application, you can use the paymentProcessor bean to process the events and obtain a stream of PaymentEvent objects.
     * To use the paymentProcessor bean, you can inject it into other components or services that require payment processing functionality.
     * When Flux of OrderEvent objects are consumed from the queue (configured in application.yaml), representing the incoming stream of order events. It then uses the paymentProcessor function to process these events and obtain a Flux of PaymentEvent objects, representing the processed payment events. This allows the application to process incoming order events and handle payments using the PaymentService's paymentProcessor function.
     */


    @Bean
    public Function<Flux<OrderEvent>, Flux<PaymentEvent>> paymentProcessor() {
        return orderEventFlux -> orderEventFlux.flatMap(this::processPayment);
    }

    /**
     * Provides an alternative implementation for the paymentProcessor function.
     * This function processes a stream of OrderEvent objects and returns a stream of PaymentEvent objects.
     *
     * @return A Function that takes a Flux of OrderEvent as input and produces a Flux of PaymentEvent as output.
     */
   /*@Bean
    public Function<Flux<OrderEvent>, Flux<PaymentEvent>> paymentProcessor() {
        return new Function<Flux<OrderEvent>, Flux<PaymentEvent>>() {
            @Override
            public Flux<PaymentEvent> apply(Flux<OrderEvent> orderEventFlux) {
                // For each OrderEvent in the input stream, we process it and get a Mono of PaymentEvent.
                Flux<PaymentEvent> paymentEventFlux = orderEventFlux.flatMap(orderEvent -> {
                    // For each OrderEvent, we call the processPayment method to get a Mono of PaymentEvent.
                    log.info("Published order event fetched by payment service" + orderEvent);
                    Mono<PaymentEvent> paymentEventMono = processPayment(orderEvent);

                    // Return the Mono of PaymentEvent.
                    return paymentEventMono;
                });

                // Return the resulting Flux of PaymentEvent objects.
                return paymentEventFlux;
            }
        };
    }*/

    /**
     * Provides an alternative implementation for the paymentProcessor function.
     * This function processes a stream of OrderEvent objects and returns a stream of PaymentEvent objects.
     *
     * @return A Function that takes a Flux of OrderEvent as input and produces a Flux of PaymentEvent as output.
     */

/*    @Bean
    public Function<Flux<OrderEvent>, Flux<PaymentEvent>> paymentProcessor() {
        return orderEventFlux -> {
            // For each OrderEvent in the input stream, we process it and get a Mono of PaymentEvent.
            Flux<PaymentEvent> paymentEventFlux = orderEventFlux.flatMap(orderEvent -> {
                // For each OrderEvent, we call the processPayment method to get a Mono of PaymentEvent.
                Mono<PaymentEvent> paymentEventMono = processPayment(orderEvent);

                // Return the Mono of PaymentEvent.
                return paymentEventMono;
            });

            // Return the resulting Flux of PaymentEvent objects.
            return paymentEventFlux;
        };
    }*/

    /**
     * The processPayment function processes an individual OrderEvent and generates the corresponding PaymentEvent.
     * It takes an OrderEvent as input and returns a Mono of PaymentEvent, representing the payment event that will be
     * produced. The processPayment function handles different scenarios based on the OrderStatus of the input OrderEvent.
     *
     * If the OrderStatus is ORDER_CREATED, it indicates a new order has been created and needs payment processing.
     * In this case, the function calls the newOrderEvent method of the PaymentService to handle the payment processing.
     * The newOrderEvent method processes the payment, checks the balance availability, and deducts the amount from the
     * database. The result of this processing is wrapped in a Mono using Mono.fromSupplier.
     * The fromSupplier operator is used when the payment processing is based on a supplier function that produces the result.
     * Using Mono.fromSupplier ensures that the payment processing is deferred and executed only when a subscriber
     * subscribes to the returned Mono. It allows for lazy evaluation of the payment processing.
     *
     * Lazy Evaluation:
     * Reactive Monos, such as Mono.fromSupplier and Mono.fromRunnable, employ lazy evaluation. This means that the payment
     * processing logic (i.e., newOrderEvent or cancelOrderEvent) is not executed right away when the processPayment function
     * is called. Instead, the actual payment processing is deferred until there is a subscriber interested in the payment
     * event. This enables the application to optimize resource usage and computation, executing the logic only when needed.
     *
     * If the OrderStatus is not ORDER_CREATED, it indicates that the order has been canceled.
     * In this case, the function calls the cancelOrderEvent method of the PaymentService to handle the payment cancellation.
     * The cancelOrderEvent method updates the amount in the database for the canceled order. The result of this processing
     * is wrapped in a Mono using Mono.fromRunnable.
     * The fromRunnable operator is used when the payment processing is based on a Runnable function with no return value.
     * Using Mono.fromRunnable allows for executing the payment cancellation logic without producing any specific result.
     * It is suitable for scenarios where you need to perform an action that doesn't produce a value, such as updating the
     * database in response to an event.
     *
     * By using Monos for payment processing, the function can handle different scenarios in a reactive and non-blocking manner.
     * Depending on the OrderStatus, either the newOrderEvent or cancelOrderEvent method of the PaymentService is invoked,
     * and the result is wrapped in a Mono. This reactive approach allows for asynchronous and non-blocking processing of
     * order events and payments, ensuring high performance and responsiveness in handling concurrent requests.
     *
     * @param orderEvent The OrderEvent object representing the order request or cancellation request.
     * @return A Mono of PaymentEvent representing the payment event for the processed order.
     */
    private Mono<PaymentEvent> processPayment(OrderEvent orderEvent) {
        // Depending on the OrderStatus of the orderEvent, we handle different scenarios using reactive Monos.
        // If the OrderStatus is ORDER_CREATED, we process the payment for the new order using the newOrderEvent method.
        if (OrderStatus.ORDER_CREATED.equals(orderEvent.getOrderStatus())) {
            // Mono.fromSupplier allows for lazy evaluation of the payment processing logic.
            // The newOrderEvent method will be executed only when a subscriber subscribes to the returned Mono.
            return Mono.fromSupplier(() -> this.paymentService.newOrderEvent(orderEvent));
        } else {
            // If the OrderStatus is not ORDER_CREATED, it means the order has been canceled.
            // We perform the payment cancellation logic using the cancelOrderEvent method.
            // Mono.fromRunnable allows for executing the cancellation logic without producing a specific result.
            return Mono.fromRunnable(() -> this.paymentService.cancelOrderEvent(orderEvent));
        }
    }

}
