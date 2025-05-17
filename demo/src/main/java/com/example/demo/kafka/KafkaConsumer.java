package com.example.demo.kafka;


import com.example.demo.dto.PersonDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;



@Service
public class KafkaConsumer {

    private BlockingQueue<ConsumerRecord<String, PersonDTO>> queue = new ArrayBlockingQueue<>(10);
    private BlockingQueue<ConsumerRecord<String, String>> queueValidate = new ArrayBlockingQueue<>(10);
    private BlockingQueue<ConsumerRecord<String, String>> queueGenerate = new ArrayBlockingQueue<>(10);

    @KafkaListener(
            topics = "db_response",
            groupId = "db_response_group",
            containerFactory = "DBListenerFactory"
    )
    public void listen(ConsumerRecord<String, PersonDTO> record, Acknowledgment ack) {
        queue.offer(record);
        ack.acknowledge();
    }

    @KafkaListener(
            topics = "jwtValidate_response",
            groupId = "jwt_response_validate",
            containerFactory = "ValidateListenerFactory"
    )
    public void listenValidate(ConsumerRecord<String, String> record, Acknowledgment ack) {
        queueValidate.offer(record);
        ack.acknowledge();
    }

    @KafkaListener(
            topics = "jwtGenerate_response",
            groupId = "jwt_response_generate",
            containerFactory = "GenerateListenerFactory"
    )
    public void listenGenerate(ConsumerRecord<String, String> record, Acknowledgment ack) {
        queueGenerate.offer(record);
        ack.acknowledge();
    }

    public ConsumerRecord<String, PersonDTO> getQueue() throws InterruptedException {
        return queue.take();
    }

    public ConsumerRecord<String, String> queueValidate() throws InterruptedException {
        return queueValidate.take();
    }

    public ConsumerRecord<String, String> queueGenerate() throws InterruptedException {
        return queueGenerate.take();
    }
}
