package com.viettel.vdt.kafka;

import org.apache.kafka.clients.producer.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class Producer {
    private static final String TOPIC_NAME = "orders";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9094";
    private static final String dataPath = "data\\orders.csv";

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        try (BufferedReader reader = new BufferedReader(new FileReader(dataPath))) {
            String line;
            int recordCount = 0;

            while ((line = reader.readLine()) != null) {
                // Process the CSV line and create a ProducerRecord
                String[] fields = line.split(",");
                String key = fields[0];  // Assuming the key is in the first column
                ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, key, line);

                // Send the record and handle the result with callback
                producer.send(record, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception exception) {
                        if (exception == null) {
                            System.out.println("Record id " + record.key() + " sent successfully - topic: " + metadata.topic() +
                                    ", partition: " + metadata.partition() +
                                    ", offset: " + metadata.offset());
                        } else {
                            System.err.println("Error while sending record " + record.key() + ": "+ exception.getMessage());
                        }
                    }
                });

                recordCount++;
            }

            System.out.println(recordCount + " records sent to Kafka successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }

    private static void sendBatch(KafkaProducer<String, String> producer, List<ProducerRecord<String, String>> batchRecords) {
        for (ProducerRecord<String, String> record : batchRecords) {
            producer.send(record);
        }
        producer.flush();
    }
}
