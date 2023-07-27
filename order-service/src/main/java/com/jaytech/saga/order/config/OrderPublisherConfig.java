/**
 * The OrderPublisherConfig class provides the configuration for creating the OrderEvent publisher using Reactor Sinks.Many.
 * It sets up the Sinks.Many instance as a publisher of OrderEvent objects and creates a supplier to obtain a Flux of OrderEvent.
 * This class utilizes reactive programming concepts such as Flux and Mono to handle order-related events in a non-blocking and asynchronous manner.
 * In the context of messaging systems like Kafka, the terms used are described below:
 *
 * Publisher (Producer): A publisher is a component responsible for producing and publishing events/messages to a message queue or topic.
 * In this case, the OrderPublisherConfig sets up the Sinks.Many instance as a publisher of OrderEvent objects.
 * The orderSinks bean serves as a message topic for order-related events and allows multiple subscribers (consumers) to receive events.
 * By using .multicast() mode, it enables parallel processing and scalability when handling multiple subscribers efficiently.
 *
 * Subscriber (Consumer): A subscriber is a component responsible for consuming events/messages from a message queue or topic.
 * In this scenario, the consumers are the services that subscribe to the Flux of OrderEvent generated by the supplier orderSupplier.
 * Each subscriber independently consumes order-related events in a reactive manner from the Flux obtained from the supplier.
 *
 * Message Queue (Topic): A message queue or topic is a communication channel that facilitates the exchange of events/messages
 * between publishers and subscribers in a decoupled and asynchronous manner.
 * In messaging systems like Kafka, a topic represents a category or feed to which messages are published by producers (publishers).
 * Similarly, multiple consumers (subscribers) can subscribe to a topic to receive and process the published events/messages.
 *
 * Flux: Flux is a reactive stream in Reactor, representing a sequence of 0 to N events.
 * It supports asynchronous and non-blocking processing, allowing subscribers to handle events as they arrive, without waiting for each event to complete.
 * This enables efficient and parallel event processing, making it suitable for handling large volumes of events in real-time applications.
 *
 * Mono: Mono is another reactive type in Reactor, representing a stream that can emit 0 or 1 event.
 * Unlike Flux, which can emit multiple events, Mono is useful when dealing with scenarios that return a single result or no result at all.
 * For example, Mono can be used when fetching a single order by its ID or when handling requests with no valid results.
 * By leveraging Mono in combination with Flux, the application can handle both singular and multiple events in a reactive and non-blocking way.
 */

package com.jaytech.saga.order.config;

import com.jaytech.saga.commons.event.OrderEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Supplier;

@Configuration
public class OrderPublisherConfig {

    /**
     * Creates and configures the Sinks.Many instance as a publisher of OrderEvent objects.
     * Sinks.many() creates a new Sinks.Many instance, which serves as a message topic within the application.
     * A message topic in messaging systems like Kafka is a queue where events or messages are published by producers (publishers).
     * Similarly, Sinks.Many provides a way to send events to multiple subscribers (consumers) in a reactive manner within the application.
     *
     * In this case, the orderSinks bean represents a message topic for order-related events.
     * It is configured with .multicast(), which specifies that the Sinks.Many should be a multicast sink, allowing multiple subscribers to receive events.
     * The .onBackpressureBuffer() configuration ensures that the sink handles backpressure when subscribers cannot keep up with the event flow.
     * Backpressure occurs when the sink receives more events than the subscribers can process, and this buffer allows the sink to store excess events temporarily.
     * The buffer prevents the loss of events and ensures that the application can handle bursts of events without overwhelming the subscribers.
     *
     * The use of .multicast() ensures that each subscriber receives its copy of the events, enabling parallel processing.
     * This is different from the .unicast() mode where only one subscriber receives events, limiting parallel processing capabilities.
     * By choosing .multicast() mode, the application can handle multiple subscribers efficiently, improving scalability and performance.
     */

    @Bean
    public Sinks.Many<OrderEvent> orderSinks() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }

    /**
     * Creates a supplier that provides a Flux of OrderEvent from the orderSinks (message topic).
     * The Flux represents a stream of order-related events that will be consumed by subscribers (consumers) in a reactive manner.
     * In the context of messaging systems like Kafka, Flux can be compared to a Kafka consumer, which consumes events from a Kafka topic.
     * Similarly, the Flux obtained from the supplier allows subscribers to consume order-related events in a reactive manner.
     *
     * The Supplier interface provides a functional method get() that is implemented to return the Flux of OrderEvent.
     * When the supplier is invoked, it retrieves the Flux from the orderSinks, allowing subscribers to consume events.
     * This enables loose coupling between the publisher and subscribers, as each subscriber can independently subscribe to the Flux.
     *
     * The Flux is a reactive stream that represents a sequence of 0 to N events.
     * It supports asynchronous and non-blocking processing, allowing subscribers to handle events as they arrive, without waiting for each event to complete.
     * This enables efficient and parallel event processing, making it suitable for handling large volumes of events in real-time applications.
     *
     * The use of Flux ensures that the application can handle backpressure efficiently.
     * Backpressure occurs when a subscriber cannot keep up with the event flow, leading to a build-up of unprocessed events.
     * In this case, the .onBackpressureBuffer() configuration in the orderSinks ensures that the sink handles backpressure by buffering excess events.
     * The buffer allows the application to store unprocessed events temporarily until the subscribers can catch up and process them.
     * By handling backpressure gracefully, the application prevents event loss and maintains stability during high load conditions.
     *
     * Additionally, Reactive Streams API provides support for a second reactive type called Mono, which represents a single event or no event.
     * Unlike Flux that can emit multiple events, Mono is useful when dealing with scenarios that return a single result or no result at all.
     * For example, Mono can be used when fetching a single order by its ID or when handling requests with no valid results.
     * By leveraging Mono in combination with Flux, the application can handle both singular and multiple events in a reactive and non-blocking way.
     */

    @Bean
    public Supplier<Flux<OrderEvent>> orderSupplier(Sinks.Many<OrderEvent> sinks) {
        return sinks::asFlux;
    }
}
