# VDT 2023 - Thực hành Kafka

> Required: Máy cần cài đặt sẵn Docker. Cấu hình máy cần RAM >= 8GB

## 1. Chuẩn bị trước
Chạy lệnh dưới đây để khởi chạy docker compose project 
```sh
cd ${thư mục clone về}
docker compose -f docker-compose.zk-kafka.yml -p vdt-kafka-zk up -d
docker compose -f docker-compose.zkless-kafka.yml -p vdt-kafka-kraft up -d
```

## 2. Nội dung 
### 2.1. Dựng cụm Kafka cùng các thành phần liên quan
**Dựng cụm Kafka với Zookeeper**
*Mô hình triển khai*

![Kafka with Zookeeper deployment model](../images/)

B1: Chạy lệnh docker compose 
```sh
docker compose -f docker-compose.zk-kafka.yml -p vdt-kafka-zk up -d
```
Các cấu hình của Kafka Broker xem tại [đây](https://docs.confluent.io/platform/current/installation/configuration/broker-configs.html)

B2: Truy cập vào link [Kafka UI](http://localhost:8080)
![Result 1](../images/result-1.png)


**Dựng cụm Kafka với Kraft**
```sh
docker compose -f docker-compose.zkless-kafka.yml -p vdt-kafka-zkless up -d
```
Các cấu hình của Kafka Broker xem tại [đây](https://docs.confluent.io/platform/current/installation/configuration/broker-configs.html)

*Mô hình triển khai*

![Kafka with Zookeeper deployment model](../images/)

### 2.2. Code producer đọc dữ liệu từ csv đẩy vào Kakfa
### 2.3. Code consumer lấy dữ liệu từ Kafka ra
### 2.4. Tạo Kafka Connect đọc dữ liệu từ csv đẩy vào Kafka
- B1: Kiểm tra plugin của Kafka Connect

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
Lưu ý: Cần phải unzip 3 file trong thư mục connectors để Kafka connect load được connector plugin. 

- B2: Tạo Kafka Connect với cấu hình

```sh
curl -i -X PUT -H "Accept:application/json" \
    -H  "Content-Type:application/json" http://localhost:8083/connectors/source-csv-spooldir-00/config \
    -d '{
        "connector.class": "com.github.jcustenborder.kafka.connect.spooldir.SpoolDirCsvSourceConnector",
        "topic": "orders_spooldir_00",
        "input.path": "/data/unprocessed",
        "finished.path": "/data/processed",
        "error.path": "/data/error",
        "input.file.pattern": ".*\\.csv",
        "schema.generation.enabled":"true",
        "csv.first.row.as.header":"true"
        }'
```

Vào Kafka Connect
- B3: Bổ sung cấu hình nhiều task 

### 2.5. Streaming Pacman
