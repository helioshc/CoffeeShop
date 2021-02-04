package coffeeshop;

public class ProductCanceled extends AbstractEvent {

    private Long id;                // 제작 번호
    private Long orderId;           // 주문 번호
    private String status;          // 진행 상태
    private Long productId;         // 제품 번호
    private String productName;     // 제품 명

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(final Long orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(final Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(final String productName) {
        this.productName = productName;
    }
}