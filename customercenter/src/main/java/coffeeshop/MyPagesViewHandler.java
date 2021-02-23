package coffeeshop;

import coffeeshop.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MyPagesViewHandler {


    @Autowired
    private MyPagesRepository myPagesRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrdered_then_CREATE_1 (@Payload Ordered ordered) {
        try {
            if (ordered.isMe()) {
                // view object create
                MyPages myPages  = new MyPages();
                // set Value of event Object to view object
                myPages.setQty(ordered.getQty());
                myPages.setStatus(ordered.getStatus());
                myPages.setProductName(ordered.getProductName());
                myPages.setOrderId(ordered.getId());
                myPages.setId(ordered.getId());
                
                // view object Save
                myPagesRepository.save(myPages);
            }
        }catch (final Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenProduced_then_UPDATE_1(@Payload Produced produced ) {
        try {
            if (produced.isMe()) {
                // view 
                // List<MyPages> list = myPagesRepository.findByOrderId(produced.getOrderId());
                   MyPages myPages = myPagesRepository.findByOrderId(produced.getOrderId());
                //for( MyPages myPages : list){
                    // event object's DirectValue to view
                    // myPages.setProductId(produced.getProductId());
                    myPages.setStatus(produced.getStatus());
                    //myPages.setStatus("Produced");
                    // view save
                    myPagesRepository.save(myPages);
                // }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenProductCanceled_then_UPDATE_2(@Payload ProductCanceled productCanceled) {
        try {
            if (productCanceled.isMe()) {
                // view
                // List<> List = myPagesRepository.findByOrderId(productCanceled.getOrderId());
                MyPages myPages = myPagesRepository.findByOrderId(productCanceled.getOrderId());
                //for( MyPages myPages : List){
                    // view eventDirectValue 
                    // myPages.setProductId(productCanceled.getProductId());
                    myPages.setStatus(productCanceled.getStatus());
                    // view save
                    myPagesRepository.save(myPages);
                //}
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrderCanceled_then_DELETE_1(@Payload OrderCanceled orderCanceled) {
        try {
            if (orderCanceled.isMe()) {
                // delete view list by ID
                myPagesRepository.deleteByOrderId(orderCanceled.getId());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
