package helios;

public class Produced extends AbstractEvent {

    private Long id;
    private String status;
    private String productId;

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
    public String getOrderId() {
        return productId;
    }

    public void setOrderId(String productId) {
        this.productId = productId;
    }
}