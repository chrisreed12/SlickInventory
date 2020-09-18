package data;

public abstract class Part {

    private Integer partID;
    private String partName;
    private Double partPrice;
    private Integer partStock;
    private Integer partMin;
    private Integer partMax;

    public Part(Integer partID, String partName, Double partPrice, Integer partStock, Integer partMin, Integer partMax) {
        this.partID = partID;
        this.partName = partName;
        this.partPrice = partPrice;
        this.partStock = partStock;
        this.partMin = partMin;
        this.partMax = partMax;
    }

    public Integer getPartID() {
        return partID;
    }

    public void setPartID(Integer partID) {
        this.partID = partID;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public Double getPartPrice() {
        return partPrice;
    }

    public void setPartPrice(Double partPrice) {
        this.partPrice = partPrice;
    }

    public Integer getPartStock() {
        return partStock;
    }

    public void setPartStock(Integer partStock) {
        this.partStock = partStock;
    }

    public Integer getPartMin() {
        return partMin;
    }

    public void setPartMin(Integer partMin) {
        this.partMin = partMin;
    }

    public Integer getPartMax() {
        return partMax;
    }

    public void setPartMax(Integer partMax) {
        this.partMax = partMax;
    }

}

