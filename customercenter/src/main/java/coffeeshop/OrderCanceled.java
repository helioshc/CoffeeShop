package coffeeshop;

public class OrderCanceled extends AbstractEvent {

    private Long id;
    private Integer qty;
    private String productName;
    private String status;

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
}