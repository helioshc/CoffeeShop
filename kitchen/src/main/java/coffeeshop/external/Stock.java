package coffeeshop.external;

public class Stock {

    private Long id;            // 제품 번호
    private String productName; // 제품 명
    private Integer qty;        // 재고 수량

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
