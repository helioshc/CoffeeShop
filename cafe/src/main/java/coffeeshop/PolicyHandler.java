package coffeeshop;

import coffeeshop.config.kafka.KafkaProcessor;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @Autowired
    OrderRepository orderRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverProductCanceled_(@Payload ProductCanceled productCanceled){

        if(productCanceled.isMe()){
            System.out.println("##### listener  : " + productCanceled.toJson());

            // productCanceled�� Order Id�� ����� �ֹ��� �ֹ� ��Ͽ��� ã��
            Order order = orderRepository.findById(productCanceled.getOrderId()).get();

            // Status�� ���� ("ProductCanceled")
            order.setStatus(productCanceled.getStatus());

            // Order update
            orderRepository.save(order);
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverProduced_(@Payload Produced produced){

        if(produced.isMe()){
            System.out.println("##### listener  : " + produced.toJson());

            // Produced�� Order Id�� ���ۿϷ�� order�� ã��
            Order order = orderRepository.findById(produced.getOrderId()).get();

            // Order�� ProductId update
            order.setProductId(produced.getProductId());

            // Status �ݿ� ("Produced")
            order.setStatus(produced.getStatus());

            // Order update
            orderRepository.save(order);
        }
    }

}
