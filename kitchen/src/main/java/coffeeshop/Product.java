package coffeeshop;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
//import java.util.List;

@Entity
@Table(name="Product_table")
public class Product {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;            //  제조 번호
    private Long orderId;       //  주문 번호
    private String status;      //  제조 상태
    private Long productId;     //  제품 번호
    private String productName; //  제품 명
    private Integer qty;        //  수량

    @PrePersist
    public void onPrePersist(){

        PreProduce preProduce = new PreProduce();
        BeanUtils.copyProperties(this, preProduce);
        preProduce.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        coffeeshop.external.Stock stock = new coffeeshop.external.Stock();
        // mappings goes here

        // req/res
        boolean stockResponse = KitchenApplication.applicationContext.getBean(coffeeshop.external.StockService.class)
                .reduce(stock);

        // Status 업데이트
        if (stockResponse) {
            this.setStatus("Completed");
        } else {
            this.setStatus("Out of stock");
        }
 
    }

    @PostPersist
    public void onPostPersist(){

        try {   // Delay 
            Thread.sleep(1000 * 5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Event 생성
        Produced produced = new Produced();

        // Aggregate 값을 Event 에 전달
        BeanUtils.copyProperties(this, produced);

        // pub/sub
        produced.publishAfterCommit();

    }

    @PreUpdate
    public void onPreUpdate(){

        // 
        if (!this.getStatus().equals("Completed")) {
            this.setStatus("Canceled");
        }

        // Event 생성
        ProductCanceled productCanceled = new ProductCanceled();

        // Aggregate 값을 Event 에 전달
        BeanUtils.copyProperties(this, productCanceled);

        // pub/sub
        productCanceled.publishAfterCommit();
    }


    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQty() {
        return qty;
    }
    public void setQty(Integer qty) {
        this.qty = qty;
    }

}
