package com.viettel.vdt.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Producer {
    private static final String TOPIC_NAME = "your_topic_name";
    private static final String BOOTSTRAP_SERVERS = "your_bootstrap_servers";

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", BOOTSTRAP_SERVERS);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        String csvFile = "path_to_your_csv_file";
        String line;
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);
                String key = data[0]; // Assuming the first column as the key
                String value = line; // Entire line as the value

                ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, key, value);
                producer.send(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }
}
