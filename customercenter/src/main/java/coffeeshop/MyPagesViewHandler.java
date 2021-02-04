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
                // view ��ü ����
                MyPages myPages  = new MyPages();
                // view ��ü�� �̺�Ʈ�� Value �� set ��
                myPages.setQty(ordered.getQty());
                myPages.setStatus(ordered.getStatus());
                myPages.setProductName(ordered.getProductName());
                myPages.setOrderId(ordered.getId());
                
                // view ������ �丮�� save
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
                // view ��ü ��ȸ
//                List<MyPages> list = myPagesRepository.findByOrderId(produced.getOrderId());
                   MyPages myPages = myPagesRepository.findByOrderId(produced.getOrderId()).get();
            //    for( MyPages myPages : list){
                    // view ��ü�� �̺�Ʈ�� eventDirectValue �� set ��
                    myPages.setProductId(produced.getProductId());
                    myPages.setStatus(produced.getStatus());
                    //myPages.setStatus("Produced");
                    // view �������丮�� save
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
                // view ��ü ��ȸ
//                List<> List = myPagesRepository.findByOrderId(productCanceled.getOrderId());
                MyPages myPages = myPagesRepository.findByOrderId(productCanceled.getOrderId()).get();
            //    for( MyPages myPages : List){
                    // view ��ü�� �̺�Ʈ�� eventDirectValue �� set ��
                    myPages.setProductId(productCanceled.getProductId());
                    myPages.setStatus(productCanceled.getStatus());
                    // view �������丮�� save
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
                // view ������ �丮�� ���� ����
                MyPagesRepository.deleteByOrderId(orderCanceled.getId());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
   */ 