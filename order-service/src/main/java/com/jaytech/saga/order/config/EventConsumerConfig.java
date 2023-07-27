package com.jaytech.saga.order.config;

import com.jaytech.genericevent.CustomEvent;
import com.jaytech.saga.commons.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class EventConsumerConfig {

    // Inject the OrderStatusUpdateHandler dependency into the configuration class.
    private final OrderStatusUpdateHandler orderStatusUpdateHandler;
    private final ApplicationEventPublisher publisher;


    /**
     * Bean Definition for Payment Event Consumer
     *
     * This method defines a Spring Bean that serves as a consumer for PaymentEvent messages.
     * The consumer listens to the "payment-event-topic" and processes PaymentEvent objects received from Kafka.
     *
     * The Consumer interface represents an operation that accepts a single input argument and returns no result.
     * In this case, the Consumer<PaymentEvent> receives PaymentEvent objects and processes them accordingly.
     *
     * When a PaymentEvent is received:
     * 1. The handler.updateOrder() method is called with the orderId and a lambda function to update the order status.
     * 2. The lambda function updates the PaymentStatus of the order based on the PaymentEvent's PaymentStatus.
     *    If the payment is completed, the order status will be set to "ORDER_COMPLETED".
     *    If the payment fails, the order status will be set to "ORDER_CANCELLED".
     *
     * The Consumer lambda function allows us to handle the PaymentEvent asynchronously and non-blocking.
     *
     * Note: paymentEventConsumer gets PaymentEvent from the kafka topic (configured in application.yaml)
     *
     * @return A Consumer<PaymentEvent> that processes PaymentEvent messages received from Kafka.
     */
    @Bean
    public Consumer<PaymentEvent> paymentEventConsumer() {
        return (paymentEvent) -> {
            // Extract the orderId from the PaymentEvent.
            Integer orderId = paymentEvent.getPaymentRequestDto().getOrderId();
            publisher.publishEvent( new CustomEvent(Map.of("PaymentStatus", paymentEvent.getPaymentStatus().name())));
            publisher.publishEvent( new CustomEvent(Map.of("OrderId", paymentEvent.getPaymentRequestDto().getOrderId().toString())));

            // Call the updateOrder() method of the OrderStatusUpdateHandler to update the order status.
            // The lambda function updates the PaymentStatus of the order based on the PaymentEvent's PaymentStatus.
            orderStatusUpdateHandler.updateOrder(orderId, order -> {
                // Set the order status based on the PaymentStatus in the PaymentEvent.
                order.setPaymentStatus(paymentEvent.getPaymentStatus());
            });
        };
    }
}
