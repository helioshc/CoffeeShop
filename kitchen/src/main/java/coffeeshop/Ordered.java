package coffeeshop;

public class Ordered extends AbstractEvent {

    private Long id;                // �ֹ� ��ȣ
    private Integer qty;            // �ֹ� ����
    private Long productId;         // ��ǰ ��ȣ (Initial Data : Null )
    private String productName;     // ��ǰ ��
    private String status;          // �ֹ� ���� ����    

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

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}