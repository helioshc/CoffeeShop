package helios;

import helios.config.kafka.KafkaProcessor;
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
                  = new ();
                // view 객체에 이벤트의 Value 를 set 함
                .setQty(.getQty());
                .setStatus(.getStatus());
                .setProductName(.getProductName());
                .setOrderId(.getId());
                // view 레파지 토리에 save
                Repository.save();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenProductReceived_then_UPDATE_1(@Payload ProductReceived productReceived) {
        try {
            if (productReceived.isMe()) {
                // view 객체 조회
                List<> List = Repository.findByOrderId(.getId());
                for(  : List){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    // view 레파지 토리에 save
                    Repository.save();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenProduced_then_UPDATE_2(@Payload Produced produced) {
        try {
            if (produced.isMe()) {
                // view 객체 조회
                List<> List = Repository.findByProductId(.getOrderId());
                for(  : List){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    // view 레파지 토리에 save
                    Repository.save();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenProductCanceled_then_UPDATE_3(@Payload ProductCanceled productCanceled) {
        try {
            if (productCanceled.isMe()) {
                // view 객체 조회
                List<> List = Repository.findByOrderId(.getOrderId());
                for(  : List){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    // view 레파지 토리에 save
                    Repository.save();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrderCanceled_then_DELETE_1(@Payload OrderCanceled orderCanceled) {
        try {
            if (orderCanceled.isMe()) {
                // view 레파지 토리에 삭제 쿼리
                Repository.deleteByOrderId(.getId());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}