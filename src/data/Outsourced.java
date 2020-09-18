package data;

public class Outsourced extends Part {

    private String companyName;

    public Outsourced(Integer partID, String partName, Double partPrice, Integer partStock, Integer partMin, Integer partMax, String companyName) {
        super(partID, partName, partPrice, partStock, partMin, partMax);
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Override
    public String toString() {
        return getPartID() + "," +
                getPartName() + "," +
                getPartPrice() + "," +
                getPartStock() + "," +
                getPartMin() + "," +
                getPartMax() + "," +
                getCompanyName() + "," + "\n";
    }
}
