package data;

import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.List;

public class Product {

    ObservableList<Part> associatedParts;
    Integer productID;
    String productName;
    Double productPrice;
    Integer productStock;
    Integer productMin;
    Integer productMax;

    public Product(ObservableList<Part> associatedParts, Integer productID, String productName, Double productPrice, Integer productStock, Integer productMin, Integer productMax) throws IOException {
        this.associatedParts = associatedParts;
        this.productID = productID;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productStock = productStock;
        this.productMin = productMin;
        this.productMax = productMax;
    }

    public Product(Integer productID, String productName, Double productPrice, Integer productStock, Integer productMin, Integer productMax) throws IOException {
        this.productID = productID;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productStock = productStock;
        this.productMin = productMin;
        this.productMax = productMax;
    }

    @Override
    public String toString() {
        return productID + "," +
                productName + "," +
                productPrice + "," +
                productStock + "," +
                productMin + "," +
                productMax + "\n";
    }

    public ObservableList<Part> getAssociatedParts() {
        return associatedParts;
    }

    public void setAssociatedParts(ObservableList<Part> associatedParts) {
        this.associatedParts = associatedParts;
    }

    public Integer getProductID() {
        return productID;
    }

    public void setProductID(Integer productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getProductStock() {
        return productStock;
    }

    public void setProductStock(Integer productStock) {
        this.productStock = productStock;
    }

    public Integer getProductMin() {
        return productMin;
    }

    public void setProductMin(Integer productMin) {
        this.productMin = productMin;
    }

    public Integer getProductMax() {
        return productMax;
    }

    public void setProductMax(Integer productMax) {
        this.productMax = productMax;
    }

    void addAssociatedPart(Part part) {
        associatedParts.add(part);
    }

    void deleteAssociatedPart(List<Part> parts) {
        associatedParts.removeAll(parts);
    }

    ObservableList<Part> getAllAssociatedParts() {
        return associatedParts;
    }
}
