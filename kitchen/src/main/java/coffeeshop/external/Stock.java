package coffeeshop.external;

public class Stock {

    private Long id;            // ��ǰ ��ȣ
    private String productName; // ��ǰ ��
    private Integer qty;        // ��� ����

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

}
