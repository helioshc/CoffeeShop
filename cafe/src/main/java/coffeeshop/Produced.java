package coffeeshop;

public class Produced extends AbstractEvent {

    private Long id;                // 제작 번호
    private String status;          // 제작 진행 상태
    private Long productId;         // 제품 번호 (Initial Data : Null )
    private String productName;     // 제품 명
    private Long orderId;           // 주문 번호

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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

    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}