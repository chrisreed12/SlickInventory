package data;

import javafx.collections.ObservableList;

import java.io.IOException;

import static utilities.DBHelper.readPartsDB;
import static utilities.DBHelper.readProductDB;

public class Inventory {

    ObservableList<Part> allParts;
    ObservableList<Product> allProducts;

    public Inventory() throws IOException {
        allParts = readPartsDB();
        allProducts = readProductDB();
    }

    void addPart(Part newPart) {
        allParts.add(newPart);
    }

    Part lookupPart(int partID) {
        for (Part p : allParts)
            if (p.getPartID() == partID)
                return p;
        return null;
    }

    Part lookupPart(String partName) {
        for (Part p : allParts)
            if (p.getPartName().contains(partName))
                return p;
        return null;
    }

    void updatePart(int id, Part selectedPart) {
        for (Part p : allParts)
            if (p.getPartID() == selectedPart.getPartID())
                allParts.remove(p);
        allParts.add(selectedPart);

    }

    boolean deletePart(Part selectedPart) {
        allParts.remove(selectedPart);
        return true;
    }

    ObservableList<Part> getAllParts() {
        return allParts;
    }

    void addProduct(Product newProduct) {
        allProducts.add(newProduct);
    }

    Product lookupProduct(int productID) {
        if (allProducts.contains(productID))
            for (Product p : allProducts)
                if (p.productID == (productID))
                    return p;
        return null;
    }

    Product lookupProduct(String productName) {

        if (allProducts.contains(productName)) {
            for (Product p : allProducts)
                if (p.productName.contains(productName))
                    return p;
        }
        return null;
    }

    void updateProduct(Product product) {
        allProducts.add(product);
    }

    boolean deleteProduct(Product product) {
        allProducts.remove(product);
        return true;
    }

    ObservableList<Product> getAllProducts() {
        return allProducts;
    }
}
