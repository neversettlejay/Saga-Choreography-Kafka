# Event-Driven Microservices vs. REST Microservices
## Introduction
 In modern software architecture, the choice between event-driven microservices and traditional REST-based microservices can significantly impact the scalability, responsiveness, and maintainability of an application. This document outlines the advantages of adopting event-driven microservices and presents an overview of how our application leverages this architectural paradigm.

### Event-Driven Microservices
#### Advantages
Loose Coupling: Event-driven microservices promote loose coupling between services. Services interact through events, allowing them to evolve independently without impacting each other.

Scalability: Event-driven systems can handle bursts of traffic more effectively. Services can process events in parallel, providing better scalability under varying workloads.

Responsiveness: Asynchronous communication in event-driven architectures enables better responsiveness. Services can process events in the background, ensuring timely user feedback.

Decoupled Services: Event-driven microservices allow services to be developed and deployed independently, reducing the risk of deployment-related issues.

Event Sourcing: Event-driven architectures often employ event sourcing, which provides a complete audit trail of data changes and supports features like temporal querying and rebuilding state.

Our Event-Driven Microservices Application
### Overview
Our application employs an event-driven microservices architecture to handle orders and payments efficiently. Here's a high-level summary of how the app works:

#### Order Creation:

When a client initiates an order through the /create REST API, the Order microservice processes the request and publishes an OrderCreated event to the Order topic.
#### Payment Processing:

The Payment microservice subscribes to the Order topic and consumes the OrderCreated event.
It processes the payment request and publishes a PaymentCompleted event if the payment is successful, or a PaymentCancelled event if the customer cannot afford the item.
#### Order Status Update:

The Order microservice subscribes to the Payment topic and consumes the payment-related events.
Based on the received payment event, the Order microservice updates the order status to reflect successful payment or cancellation.
#### Client Communication:

The /create API responds to the client with an acknowledgment that the order has been received and is awaiting payment confirmation.
#### Client Polling:

The client can poll the order status through another API, checking if the payment was completed or cancelled. This asynchronous approach allows the client to track order progress without blocking.
#### Conclusion
Event-driven microservices offer numerous benefits over REST microservices, including enhanced scalability, responsiveness, and loose coupling. Our application showcases the power of this architecture, efficiently managing orders and payments through asynchronous event processing.

### Note
It might seem that createOrder method in your OrderController is not waiting for the payment event response and thus leaves the order and payment statuses unknown. To address this, we need to implement a mechanism to wait for the payment event and update the order status accordingly.

Here's a high-level approach we can consider:

Synchronous Request-Response (Blocking): One way to achieve this is by making the createOrder method synchronous, meaning it will wait for the payment event response before proceeding. However, this might not be the most efficient approach for a microservices architecture since it could lead to increased latency.

Asynchronous Approach (Non-Blocking): A more suitable approach for microservices is to design your application to be event-driven and asynchronous. Here's how you could modify your approach to handle this:

When the createOrder API is called, the Order microservice creates an order with a status like "Pending Payment" and publishes an OrderCreated event to the Order topic.
The Payment microservice consumes the OrderCreated event, performs the payment processing, and publishes a PaymentCompleted or PaymentCancelled event to the Payment topic.
The Order microservice consumes the PaymentCompleted or PaymentCancelled event and updates the order status accordingly.
To handle this asynchronous flow effectively, you would need to implement proper event listeners, event publishing, and possibly a mechanism to correlate orders with payments, like a unique order ID.

In this approach, the createOrder method would return a response indicating that the order has been received and is awaiting payment confirmation. **The client making the request can then periodically query the order status until it receives a confirmation of successful payment or cancellation.**

Remember that event-driven microservices should focus on loose coupling, scalability, and responsiveness. Synchronous behavior can undermine these principles. Therefore, it's often a better practice to design for asynchronicity and provide mechanisms for clients to track the progress of their orders.


# Kafka Configuration Steps

Follow these steps to configure Kafka for your project:

1. **Download Kafka**:
   Download the Kafka distribution tar.gz file from [Kafka Download](https://kafka.apache.org/downloads). Choose the appropriate version for your system.

2. **Extract Kafka**:
   Extract the downloaded tar.gz file using the following command:
    `tar xvf kafka_2.12-3.5.0.tgz`

3. **Configure Logging Directory**:
- Open the `server.properties` and `zookeeper.properties` files in the `config` folder of the extracted Kafka distribution.
- Change the directory for logging to your preferred location.
- Save the changes.

4. **Start ZooKeeper**:
   Run the following command to start ZooKeeper:
`bin/zookeeper-server-start.sh config/zookeeper.properties`

5. **Start Kafka Server**:
   Run the following command to start the Kafka server:

`bin/kafka-server-start.sh config/server.properties`


6. **Verify Topics**:
   To check if the topics "order-event" and "payment-event" exist, run the following command:
`./bin/kafka-topics.sh --bootstrap-server=localhost:9092 --list`

7. **See data inside Topics**:
`kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic order-event --from-beginning` and `kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic payment-event --from-beginning
   `
8. **Start Order service , Payment service, Use the API to Send Requests**:
   Use the API to send requests to create orders:
- API Endpoint: `localhost:8081/order/create` (HTTP POST)
- Request Body:
  ```json
  {
    "userId": 101,
    "productId": 10,
    "amount": 100
  }
  ```

**Note**: The provided steps assume that you have the necessary dependencies and configurations already set up for your application. Ensure that your application is correctly configured to interact with Kafka using the provided Kafka server and topics.

Please follow the steps carefully to set up and use Kafka for your project. For more details and visual assistance, refer to the [Java Techie YouTube video](https://www.youtube.com/watch?v=6O5iJ7PKUhs&t=851s). 
