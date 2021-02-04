package coffeeshop;

import coffeeshop.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

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
                // view 객체 생성
                MyPages myPages  = new MyPages();
                // view 객체에 이벤트의 Value 를 set 함
                myPages.setQty(ordered.getQty());
                myPages.setStatus(ordered.getStatus());
                myPages.setProductName(ordered.getProductName());
                myPages.setOrderId(ordered.getId());
                
                // view 레파지 토리에 save
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
                // view 객체 조회
//                List<MyPages> list = myPagesRepository.findByOrderId(produced.getOrderId());
                   MyPages myPages = myPagesRepository.findByOrderId(produced.getOrderId()).get();
            //    for( MyPages myPages : list){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPages.setProductId(produced.getProductId());
                    myPages.setStatus(produced.getStatus());
                    //myPages.setStatus("Produced");
                    // view 레파지토리에 save
                    myPagesRepository.save(myPages);
            //    }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenProductCanceled_then_UPDATE_2(@Payload ProductCanceled productCanceled) {
        try {
            if (productCanceled.isMe()) {
                // view 객체 조회
//                List<> List = myPagesRepository.findByOrderId(productCanceled.getOrderId());
                MyPages myPages = myPagesRepository.findByOrderId(productCanceled.getOrderId()).get();
            //    for( MyPages myPages : List){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPages.setProductId(productCanceled.getProductId());
                    myPages.setStatus(productCanceled.getStatus());
                    // view 레파지토리에 save
                    myPagesRepository.save(myPages);
            //    }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

/*
    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrderCanceled_then_DELETE_1(@Payload OrderCanceled orderCanceled) {
        try {
            if (orderCanceled.isMe()) {
                // view 레파지 토리에 삭제 쿼리
                MyPagesRepository.deleteByOrderId(orderCanceled.getId());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
   */ 