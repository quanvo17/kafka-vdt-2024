# VDT 2023 - Thực hành Kafka

> Required: Máy cần cài đặt sẵn Docker. Cấu hình máy cần RAM >= 8GB. Môi trường thử nghiệm: Window 11

## 1. Chuẩn bị trước

**B1:** Clone repo và truy cập vào thư mục

**B2:** Chạy câu lệnh để pull docker images cần thiết cho bài thực hành

```sh
docker compose -f docker-compose.zk-kafka.yml pull
```

## 2. Nội dung 
### 2.1. Triển khai Kafka Cluster
> **Yêu cầu** 
> - Triển khai Kafka Cluster theo 2 mode Zookeeper hoặc Kraft
> - Hiểu được mô hình hoạt động của cả 2 mô hình
> - Hiểu được các cấu hình cơ bản của Kafka Cluster

**Dựng cụm Kafka với Zookeeper**

*Mô hình triển khai*

![Kafka with Zookeeper deployment model](../master/assets/zk-architecture.png)

**B1:** Chạy lệnh docker compose 
```sh
docker compose -f docker-compose.zk-kafka.yml -p vdt-kafka-zk up -d
```
Các cấu hình của Kafka Broker xem tại [đây](https://docs.confluent.io/platform/current/installation/configuration/broker-configs.html)

**B2:** Truy cập vào link [Kafka UI](http://localhost:8080) sẽ thấy kết quả như hình

![Result 1](../master/assets/result-1.png)

**Dựng cụm Kafka với Kraft**

*Mô hình triển khai*

![ ](../master/assets/kraft-architecture.png)

```sh
docker compose -f docker-compose.zk-kafka.yml down

docker compose -f docker-compose.zkless-kafka.yml -p vdt-kafka-zkless up -d
```

Các cấu hình của Kafka Broker xem tại [đây](https://docs.confluent.io/platform/current/installation/configuration/broker-configs.html)

### 2.2. Code producer đọc dữ liệu từ csv đẩy vào Kakfa
> Mục đích: Thử nghiệm code producer đẩy dữ liệu order từ file csv vào topic orders 
> của cụm Kafka vừa dựng. Yêu cầu cần hiểu được cách tạo 1 producer cơ bản và luồng ghi dữ liệu vào Kafka

B1: Khởi tạo project ở thư mục csv2kafka

B2: Chạy hàm main ở file Producer.java. Kết quả được trả ra đúng sẽ như hình dưới:

![Result 2.1](../master/assets/result-2.1.png)

B3: Truy cập [Kafka UI](http://localhost:8080/ui/clusters/cls-queue/all-topics/orders/messages?keySerde=String&valueSerde=String&limit=100) để kiểm tra kết quả message

![Result 2.2](../master/assets/result-2.2.png)

### 2.3. Code consumer lấy dữ liệu từ Kafka ra
> Mục đích: Thử nghiệm code consumer lấy dữ liệu topic orders và in ra màn hình. 
> Yêu cầu cần hiểu được cách tạo 1 consumer cơ bản và luồng đọc dữ liệu vào Kafka

B1: Khởi tạo project ở thư mục demo

B2: Chạy hàm main ở file VdtConsumer.java. Kết quả được trả ra đúng sẽ như hình dưới

![Result 3](../master/assets/result-3.1.png)

### 2.4. Tạo Kafka Connect đọc dữ liệu từ csv đẩy vào Kafka
> Mục đích: Tạo được Connectors để lấy dữ liệu từ file orders và đẩy vào topics connect-orders.
> Yêu cầu cần hiểu được các cấu hình để tạo connector source và thử nghiệm chạy Kafka connect distributed mode.

- B1: Download Connector Plugin 
    - [JDBC Source Connector](https://www.confluent.io/hub/confluentinc/kafka-connect-jdbc)
    - [File Source Connector](https://www.confluent.io/hub/jcustenborder/kafka-connect-spooldir)

- B2: Giải nén các connector vào thư mục connectors

- B3: Kiểm tra plugin của Kafka Connect

```sh
curl -s localhost:8083/connector-plugins|jq '.[].class'|egrep 'SpoolDir'
```

Thông tin hiển thị đúng sẽ là:
```sh
"com.github.jcustenborder.kafka.connect.spooldir.SpoolDirCsvSourceConnector"
"com.github.jcustenborder.kafka.connect.spooldir.SpoolDirJsonSourceConnector"
"com.github.jcustenborder.kafka.connect.spooldir.SpoolDirLineDelimitedSourceConnector"
"com.github.jcustenborder.kafka.connect.spooldir.SpoolDirSchemaLessJsonSourceConnector"
"com.github.jcustenborder.kafka.connect.spooldir.elf.SpoolDirELFSourceConnector"
```

- B4: Tạo Kafka Connect với cấu hình

```sh
curl -i -X PUT -H "Accept:application/json" \
    -H  "Content-Type:application/json" http://localhost:8083/connectors/source-csv-spooldir-00/config \
    -d '{
        "connector.class": "com.github.jcustenborder.kafka.connect.spooldir.SpoolDirCsvSourceConnector",
        "topic": "connect-orders",
        "input.path": "/data/unprocessed",
        "finished.path": "/data/processed",
        "error.path": "/data/error",
        "input.file.pattern": ".*\\.csv",
        "schema.generation.enabled":"true",
        "csv.first.row.as.header":"true"
        }'
```
- B5: Bổ sung cấu hình nhiều task 

### 2.5. Streaming Pacman
> Mục đích: Thử nghiệm Kafka Streaming và Confluent Kafka Cloud 

**Hướng dẫn:** [Link](../master/streaming-pacman/README.adoc)
