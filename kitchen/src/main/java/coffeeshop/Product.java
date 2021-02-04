package coffeeshop;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
//import java.util.List;

@Entity
@Table(name="Product_table")
public class Product {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;            //  ���� ��ȣ
    private Long orderId;       //  �ֹ� ��ȣ
    private String status;      //  ���� ����
    private Long productId;     //  ��ǰ ��ȣ
    private String productName; //  ��ǰ ��
    private Integer qty;        //  ����

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

        // Status ������Ʈ
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

        // Event ����
        Produced produced = new Produced();

        // Aggregate ���� Event �� ����
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

        // Event ����
        ProductCanceled productCanceled = new ProductCanceled();

        // Aggregate ���� Event �� ����
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
