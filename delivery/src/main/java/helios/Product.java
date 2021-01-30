package helios;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Product_table")
public class Product {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long orderId;
    private String status;
    private String productName;
    private Integer qty;

    @PostPersist
    public void onPostPersist(){
        Produced produced = new Produced();
        BeanUtils.copyProperties(this, produced);
        produced.publishAfterCommit();


    }

    @PostRemove
    public void onPostRemove(){
        ProductCanceled productCanceled = new ProductCanceled();
        BeanUtils.copyProperties(this, productCanceled);
        productCanceled.publishAfterCommit();


    }

    @PrePersist
    public void onPrePersist(){
        PreProduce preProduce = new PreProduce();
        BeanUtils.copyProperties(this, preProduce);
        preProduce.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        .external.Stock stock = new .external.Stock();
        // mappings goes here
        Application.applicationContext.getBean(.external.StockService.class)
            .reduce(stock);


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
