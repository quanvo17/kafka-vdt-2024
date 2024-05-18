package com.viettel.vdt.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import java.io.StringReader;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class VdtConsumer {

	private static final String TOPIC_NAME = "orders";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9094";
    private static final String GROUP_ID = "vdt-orders";

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (Consumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(TOPIC_NAME));
            boolean stopFlags = true;

            while (stopFlags) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                // Process each record
                records.forEach(record -> {
                    String csvData = record.value();
                    try (CSVReader csvReader = new CSVReader(new StringReader(csvData))) {
                        String[] fields = csvReader.readNext();
                        // Process the fields or perform operations on the CSV data
                        for (String field : fields) {
                            System.out.print(field + " ");
                        }
                        System.out.println();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
