package data;

public class InHouse extends Part {

    private int machineID;

    public InHouse(Integer partID, String partName, Double partPrice, Integer partStock, Integer partMin, Integer partMax, int machineID) {
        super(partID, partName, partPrice, partStock, partMin, partMax);
        this.machineID = machineID;
    }

    public int getMachineID() {
        return machineID;
    }

    public void setMachineID(int machineID) {
        this.machineID = machineID;
    }

    @Override
    public String toString() {
        return getPartID() + "," +
                getPartName() + "," +
                getPartPrice() + "," +
                getPartStock() + "," +
                getPartMin() + "," +
                getPartMax() + "," +
                getMachineID() + "," + "\n";
    }
}
