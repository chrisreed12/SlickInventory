package utilities;

import data.InHouse;
import data.Outsourced;
import data.Part;
import data.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DBHelper {
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String ABSOLUTE_FILE_PATH = System.getProperty("user.home") + FILE_SEPARATOR + "DB" + FILE_SEPARATOR;
    private static final String PARTS_PATH = ABSOLUTE_FILE_PATH + "parts.si";
    private static final String PRODUCTS_PATH = ABSOLUTE_FILE_PATH + "products.si";
    private static final String ASSOCIATED_PARTS_PATH = ABSOLUTE_FILE_PATH + "associated_parts.si";
    private static boolean result = false;

    public static void createFiles() throws IOException {
        File partFile = new File(PARTS_PATH);
        partFile.getParentFile().mkdir();
        FileWriter partWriter = new FileWriter(PARTS_PATH, true);
        partWriter.close();
        File prodFile = new File(PRODUCTS_PATH);
        prodFile.getParentFile().mkdir();
        FileWriter prodWriter = new FileWriter(PRODUCTS_PATH, true);
        prodWriter.close();
        File assocPartFile = new File(ASSOCIATED_PARTS_PATH);
        assocPartFile.getParentFile().mkdir();
        FileWriter assocPartWriter = new FileWriter(ASSOCIATED_PARTS_PATH, true);
        assocPartWriter.close();
    }

    public static boolean addProdDB(Product product) throws IOException {

        FileWriter writer = new FileWriter(PRODUCTS_PATH, true);
        List<Product> currentProducts = readProductDB();
        if (currentProducts.isEmpty() == false) {
            if (!currentProducts.contains(product)) {
                writer.append(product.toString());
                result = true;
            } else {
                result = false;
            }
        } else {
            writer.write(product.toString());
        }
        writer.close();
        return result;
    }

    public static boolean updateProdDB(ObservableList<Product> products) throws IOException {

        FileWriter writer = new FileWriter(PRODUCTS_PATH, false);
        for (Product p : products)
            writer.write(p.toString());

        writer.close();
        return result = true;
    }

    public static boolean updateProd(Product product) throws IOException {
        ObservableList<Product> cp = readProductDB();
        cp.removeIf(p -> product.getProductID() == p.getProductID());
        cp.add(product);
        updateProdDB(cp);
        return true;
    }

    public static ObservableList<Product> readProductDB() throws IOException {
        ObservableList<Product> products = FXCollections.observableArrayList();
        BufferedReader reader = Files.newBufferedReader(Paths.get(PRODUCTS_PATH));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] names = line.split(",");
                products.add(new Product(
                        Integer.parseInt(names[0]),     // Product ID
                        names[1],                       // Product Name
                        Double.parseDouble(names[2]),   // Product Price
                        Integer.parseInt(names[3]),     // Product Stock
                        Integer.parseInt(names[4]),     // Product Min
                        Integer.parseInt(names[5])));   // Product Max
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return products;
    }

    public static boolean updatePartDB(ObservableList<Part> parts) throws IOException {
        FileWriter writer = new FileWriter(PARTS_PATH, false);
        for (Part p : parts)
            writer.write(p.toString().replace("[", "").replace("]", ""));

        writer.close();
        return result = true;
    }

    public static boolean updateOS(Outsourced osIn) throws IOException {
        ObservableList<Part> cp = readPartsDB();
        cp.removeIf(p -> osIn.getPartID() == p.getPartID());
        cp.add(osIn);
        updatePartDB(cp);
        return true;
    }

    public static boolean updateIH(InHouse ihIn) throws IOException {
        ObservableList<Part> cp = readPartsDB();
        cp.removeIf(p -> ihIn.getPartID() == p.getPartID());
        cp.add(ihIn);
        updatePartDB(cp);
        return true;
    }

    public static ObservableList<Part> readPartsDB() throws IOException {
        ObservableList<Part> parts = FXCollections.observableArrayList();
        Outsourced outsourced = null;
        InHouse inHouse = null;
        BufferedReader reader = Files.newBufferedReader(Paths.get(PARTS_PATH));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] names = line.split(",");

            try {
                Integer.parseInt(names[6]);
                inHouse = new InHouse(
                        Integer.parseInt(names[0]),     // Part ID
                        names[1],                       // Part Name
                        Double.parseDouble(names[2]),   // Part Price
                        Integer.parseInt(names[3]),     // Part Stock
                        Integer.parseInt(names[4]),     // Part Min
                        Integer.parseInt(names[5]),     // Part Max
                        Integer.parseInt(names[6]));   // Company name
                parts.add(inHouse);
            } catch (NumberFormatException e) {
                outsourced = new Outsourced(
                        Integer.parseInt(names[0]),     // Part ID
                        names[1],                       // Part Name
                        Double.parseDouble(names[2]),   // Part Price
                        Integer.parseInt(names[3]),     // Part Stock
                        Integer.parseInt(names[4]),     // Part Min
                        Integer.parseInt(names[5]),     // Part Max
                        names[6]);                     // Company name
                parts.add(outsourced);
            }
        }
        return parts;
    }

    public static ObservableList<Part> getAssociatedParts(Product product) throws IOException {
        ObservableList<Part> parts = readPartsDB();
        ObservableList<Part> associatedParts = FXCollections.observableArrayList();
        List<IntPair> intPairs = readAssociatedPartsDB();
        for (IntPair ip : intPairs) {
            if (ip.getProdID() == product.getProductID()) {
                for (Part p : parts) {
                    if (p.getPartID() == ip.getPartID()) {
                        associatedParts.add(p);
                    }
                }
            }
        }
        return associatedParts;
    }

    public static boolean addAssociatedParts(Product product, ObservableList<Part> associatedParts) throws IOException {
        FileWriter writer = new FileWriter(ASSOCIATED_PARTS_PATH, true);
        for (Part ap : associatedParts) {
            writer.write(product.getProductID() + "," + ap.getPartID() + "\n");
        }
        writer.close();
        return true;
    }

    public static boolean updateAssociatedParts(Product product, ObservableList<Part> parts) throws IOException {
        List<IntPair> intPairs = readAssociatedPartsDB();
        intPairs.removeIf(ip -> ip.getProdID() == product.getProductID());
        for (Part p : parts) {
            intPairs.add(new IntPair(product.getProductID(), p.getPartID()));
        }
        updateAPDB(intPairs);
        return true;
    }

    private static void updateAPDB(List<IntPair> intPairs) throws IOException {
        FileWriter writer = new FileWriter(ASSOCIATED_PARTS_PATH, false);
        for (IntPair ip : intPairs) {
            writer.write(ip.getProdID() + "," + ip.getPartID() + "\n");
        }
        writer.close();
    }

    private static List<IntPair> readAssociatedPartsDB() throws IOException {
        List<IntPair> readAssociatedPartsDB = new ArrayList<>();
        IntPair intPair;
        BufferedReader reader = Files.newBufferedReader(Paths.get(ASSOCIATED_PARTS_PATH));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] id = line.split(",");

            try {
                intPair = new IntPair(
                        Integer.parseInt(id[0]),     // Product ID
                        Integer.parseInt(id[1]));    // Associated Part ID Name
                readAssociatedPartsDB.add(intPair);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return readAssociatedPartsDB;
    }

    private static class IntPair {
        int prodID;
        int partID;

        private IntPair(int p, int a) {
            this.prodID = p;
            this.partID = a;
        }

        public int getProdID() {
            return prodID;
        }

        public void setProdID(int prodID) {
            this.prodID = prodID;
        }

        public int getPartID() {
            return partID;
        }

        public void setPartID(int partID) {
            this.partID = partID;
        }
    }
}