package coffeeshop;

public class Produced extends AbstractEvent {

    private Long id;                // ���� ��ȣ
    private Long orderId;           // �ֹ� ��ȣ
    private String status;          // ���� ����
    private Long productId;         // ��ǰ ��ȣ
    private String productName;     // ��ǰ ��

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
}