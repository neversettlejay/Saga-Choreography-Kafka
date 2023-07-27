/**
 * OrderStatusPublisher is a service class responsible for publishing order-related events using Reactor Sinks.Many
 * or KafkaTemplate.
 */
package com.jaytech.saga.order.service;

import com.jaytech.saga.commons.dto.OrderRequestDto;
import com.jaytech.saga.commons.event.OrderEvent;
import com.jaytech.saga.commons.event.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

/**
 * The OrderStatusPublisher class handles the publishing of order-related events using Reactor Sinks.Many or KafkaTemplate.
 * It is annotated with @Service to indicate that it is a Spring service bean.
 * The class uses @RequiredArgsConstructor from Lombok to automatically generate a constructor that initializes the final Sinks.Many field and KafkaTemplate field.
 */
@Service
@RequiredArgsConstructor
public class OrderStatusPublisher {

    /**
     * The Sinks.Many instance is used to publish multiple events to multiple consumers using Reactor Sinks.Many.
     * Sinks.Many provides a way to send events to multiple subscribers in a reactive manner.
     * It is initialized through the constructor using the @Autowired annotation.
     *
     * In the context of messaging systems like Kafka, Reactor Sinks.Many can be seen as a representation of a message topic.
     * A message topic in Kafka is a queue where events or messages are published by producers (publishers) and consumed by consumers (subscribers).
     * Similarly, Reactor Sinks.Many provides a mechanism for publishing events (messages) to multiple subscribers (consumers).
     *
     * The OrderStatusPublisher acts as a producer that creates and sends OrderEvent objects to the Sinks.Many (message topic).
     * The consumers of the OrderEvent messages can be other components or services within the application that subscribe to the Sinks.Many.
     *
     * When the publishOrderEvent method is called, it creates an OrderEvent representing an order-related event with a specific order status.
     * The orderSinks (message topic) handles the event and notifies all its subscribers (consumers) about the newly created OrderEvent.
     *
     * Note that the actual implementation of messaging topics and Sinks.Many may differ in technical details, but conceptually, they both represent a message queue.
     * Sinks.Many leverages the reactive programming model to handle event publishing in a non-blocking and asynchronous manner, similar to how Kafka operates.
     * Both messaging topics and Sinks.Many provide a way to decouple producers and consumers, allowing multiple consumers to process events independently.
     * Depending on the use case and requirements, either Reactor Sinks.Many or KafkaTemplate can be chosen as the event publishing mechanism.
     *
     * The choice between using Reactor Sinks.Many and KafkaTemplate depends on the application's requirements and use case.
     * If the application requires a more low-level and fine-grained interaction with Kafka, KafkaTemplate is the preferred choice.
     * However, if the application leverages reactive programming and prefers a more high-level abstraction, Reactor Sinks.Many can be a better fit.
     *
     * In this implementation, both Reactor Sinks.Many and KafkaTemplate are used for illustration purposes.
     * The publishOrderEvent method uses Reactor Sinks.Many to publish the OrderEvent to the orderSinks (message topic) in a non-blocking manner.
     * But if desired, it could be modified to use KafkaTemplate to publish events to Kafka directly.
     */

    private final Sinks.Many<OrderEvent> orderSinks;

    /**
     * The KafkaTemplate instance is used to publish messages to Kafka topics.
     * KafkaTemplate provides a high-level abstraction for sending messages to Kafka brokers.
     * It is initialized through the constructor using the @Autowired annotation.
     *
     * Unlike Reactor Sinks.Many, KafkaTemplate directly interfaces with Kafka brokers and provides more advanced features such as batching, retries, and error handling.
     * It allows for more fine-grained control over message delivery and serialization/deserialization.
     *
     * The choice between using Reactor Sinks.Many and KafkaTemplate depends on the application's requirements and use case.
     * If the application requires a more low-level and fine-grained interaction with Kafka, KafkaTemplate is the preferred choice.
     * However, if the application leverages reactive programming and prefers a more high-level abstraction, Reactor Sinks.Many can be a better fit.
     *
     * In this implementation, both Reactor Sinks.Many and KafkaTemplate are used for illustration purposes.
     * The publishOrderEvent method uses Reactor Sinks.Many to publish the OrderEvent to the orderSinks (message topic) in a non-blocking manner.
     * But if desired, it could be modified to use KafkaTemplate to publish events to Kafka directly.
     */
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    /**
     * Publish an order-related event with the specified order status using Reactor Sinks.Many.
     *
     * @param orderRequestDto The OrderRequestDto containing the details of the order.
     * @param orderStatus     The OrderStatus representing the status of the order event.
     *
     * This method is responsible for creating an OrderEvent with the given OrderRequestDto and OrderStatus.
     * The new OrderEvent object represents an order-related event that needs to be published to the subscribers (consumers).
     *
     * The method then tries to emit the OrderEvent to the Sinks.Many, which will notify all subscribers.
     * This method does not block and returns false if the sink has been cancelled or has no subscribers.
     *
     * The publishing of the OrderEvent is done asynchronously, and the consumers (subscribers) will process the event independently.
     * By using Sinks.Many and the reactive programming model, the OrderStatusPublisher decouples the event producer from the consumers,
     * ensuring a scalable and efficient event-driven architecture.
     *
     * If using KafkaTemplate, the method would instead use the kafkaTemplate's send method to send the OrderEvent to a specific Kafka topic directly.
     */
    public void publishOrderEvent(OrderRequestDto orderRequestDto, OrderStatus orderStatus) {
        // Create a new OrderEvent with the given OrderRequestDto and OrderStatus
        OrderEvent orderEvent = new OrderEvent(orderRequestDto, orderStatus);

        // Try to emit the OrderEvent to the Sinks.Many, which will notify all subscribers.
        // This method does not block and returns false if the sink has been cancelled or has no subscribers.
        orderSinks.tryEmitNext(orderEvent);

        // If using KafkaTemplate:
        // kafkaTemplate.send("order-event", orderEvent);
    }
}
