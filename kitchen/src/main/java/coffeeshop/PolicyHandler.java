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
    ProductRepository productRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrdered_(@Payload Ordered ordered){

        if(ordered.isMe()){
            System.out.println("##### listener  : " + ordered.toJson());

            // 
            Product product = new Product();

            // Order 정보를 Product 에 반영
            product.setOrderId(ordered.getId());
            product.setProductName(ordered.getProductName());
            product.setQty(ordered.getQty());

            // Status 변경
            product.setStatus("Ready to Produce");

            // Product insert
            productRepository.save(product);
        }
    }
}
