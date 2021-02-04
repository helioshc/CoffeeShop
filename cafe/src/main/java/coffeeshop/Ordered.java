package coffeeshop;

public class Ordered extends AbstractEvent {

    private Long id;                // 주문 번호
    private Long productId;         // 제품 번호 (Initial Data : Null )
    private String productName;     // 제품 명
    private Integer qty;            // 주문 수량
    private String status;          // 주문 진행 상태    

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQty() {
        return qty;
    }
    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
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