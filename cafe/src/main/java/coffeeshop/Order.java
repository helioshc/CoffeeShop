package coffeeshop;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
//import java.util.List;

@Entity
@Table(name="Order_table")
public class Order {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;                // 주문 번호
    private Long productId;         // 제품 번호 (Initial Data : Null )
    private String productName;     // 제품 명
    private Integer qty;            // 주문 수량
    private String status;          // 주문 진행 상태

    @PrePersist
    public void onPrePersist() {

        // Status 생성 Order insert
        this.setStatus("Requested");
    }

    @PostPersist
    public void onPostPersist(){
        Ordered ordered = new Ordered();
        // 필요한건지 -->
        //ordered.setId(this.getId());
        //ordered.setProductName(this.getProductName());
        //ordered.setQty(this.getQty());
        //ordered.setStatus(this.getStatus());
        // <-- 여기까지

        // Aggregate Event 
        BeanUtils.copyProperties(this, ordered);

        // pub/sub
        ordered.publishAfterCommit();


    }


    @PreUpdate
    public void onPreUpdate(){

        // 
        if (this.getStatus().equals("OrderCanceled")) {

            // Event 
            OrderCanceled orderCanceled = new OrderCanceled();

            // Aggregate 媛믪쓣 Event 媛앹껜濡� 蹂듭궗
            BeanUtils.copyProperties(this, orderCanceled);

            coffeeshop.external.Product product = new coffeeshop.external.Product();
            product.setId(orderCanceled.getProductId());
            product.setOrderId(orderCanceled.getId());
            product.setProductName(orderCanceled.getProductName());
            product.setStatus(orderCanceled.getStatus());
            product.setQty(orderCanceled.getQty());

            // req/res
            CafeApplication.applicationContext.getBean(coffeeshop.external.ProductService.class)
                .cancel( product );
        }
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

}
