package coffeeshop.external;

public class Product {

    private Long id;            //  ���� ��ȣ
    private Long orderId;       //  �ֹ� ��ȣ
    private String status;      //  ���� ����
    private Long productId;     //  ��ǰ ��ȣ
    private String productName; //  ��ǰ ��
    private Integer qty;        //  ����

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
    public Integer getQty() {
        return qty;
    }
    public void setQty(Integer qty) {
        this.qty = qty;
    }
/*
    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", price=" + price +
                ", process='" + process + '\'' +
                '}';    
*/
}
