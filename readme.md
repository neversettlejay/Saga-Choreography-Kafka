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
