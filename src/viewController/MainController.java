package viewController;

import data.Part;
import data.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static utilities.DBHelper.*;

public class MainController implements Initializable {

    // Create search text fields
    @FXML
    public TextField partSearchTxt;
    @FXML
    public Text partAlertText;
    @FXML
    public TextField prodSearchTxt;
    @FXML
    public Text prodAlertText;
    @FXML
    public Text addModPartDesc;
    // Create ObservableLists for Product and Part
    private ObservableList<Product> products;
    private ObservableList<Part> parts;
    // Sets up Product Table for controller
    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, Integer> prodIDColumn;
    @FXML
    private TableColumn<Product, String> prodNameColumn;
    @FXML
    private TableColumn<Product, Integer> prodStockColumn;
    @FXML
    private TableColumn<Product, Double> prodPriceColumn;

    // Sets up Part Table for controller
    @FXML
    private TableView<Part> partTable;
    @FXML
    private TableColumn<Part, Integer> partIDColumn;
    @FXML
    private TableColumn<Part, String> partNameColumn;
    @FXML
    private TableColumn<Part, Integer> partStockColumn;
    @FXML
    private TableColumn<Part, Double> partPriceColumn;

    // Setup Product Buttons
    @FXML
    private Button partSearchBtn, partAddBtn, partModBtn, partDelBtn;

    // Setup Part Buttons
    @FXML
    private Button prodSearchBtn, prodAddBtn, prodModBtn, prodDltBtn, exitBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Initialize Part and Product lists from DB
        try {
            parts = readPartsDB();
            products = readProductDB();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Associates Values in Product class with columns in table
        prodIDColumn.setCellValueFactory(new PropertyValueFactory<>("productID"));
        prodNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        prodStockColumn.setCellValueFactory(new PropertyValueFactory<>("productStock"));
        prodPriceColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));

        // Populates the table with slickInventory.data from Product DB
        productTable.getItems().setAll(products);

        // Associates Values in Part class with columns in table
        partIDColumn.setCellValueFactory(new PropertyValueFactory<>("partID"));
        partNameColumn.setCellValueFactory(new PropertyValueFactory<>("partName"));
        partStockColumn.setCellValueFactory(new PropertyValueFactory<>("partStock"));
        partPriceColumn.setCellValueFactory(new PropertyValueFactory<>("partPrice"));

        // Populates the table with slickInventory.data from Part DB
        partTable.getItems().setAll(parts);

        // Setup Part Search TextView and Button
        partSearchTxt.setFocusTraversable(false);
        partSearchBtn.setOnAction(e -> partSearchAction(partSearchTxt.getText()));
        partAlertText.setText("");

        // Setup Product Search TextView and Button
        prodSearchTxt.setFocusTraversable(false);
        prodSearchBtn.setOnAction(event -> prodSearchAction(prodSearchTxt.getText()));
        prodAlertText.setText("");

        // Product Interface Buttons
        prodDltBtn.setOnAction(event -> {
            try {
                prodDltAction(productTable.getSelectionModel().getSelectedItem());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        prodAddBtn.setOnAction(event -> {
            try {
                addProduct(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        prodDltBtn.setOnAction(event -> {
            try {
                prodDltAction(productTable.getSelectionModel().getSelectedItem());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        prodModBtn.setOnAction(event -> {
            if (productTable.getSelectionModel().getSelectedItem() != null) {
                try {
                    modProd(event, productTable.getSelectionModel().getSelectedItem());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Alert nothingSelected = new Alert(Alert.AlertType.ERROR);
                nothingSelected.setContentText("Nothing to modify, select product");
                nothingSelected.show();
            }
        });

        // Part Interface Buttons
        partDelBtn.setOnAction(event -> {
            try {
                partDltAction(partTable.getSelectionModel().getSelectedItem());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        partAddBtn.setOnAction(event -> {
            try {
                addPart(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        partModBtn.setOnAction(event -> {

            if (partTable.getSelectionModel().getSelectedItem() != null) {
                try {
                    modPart(event, partTable.getSelectionModel().getSelectedItem());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Alert nothingSelected = new Alert(Alert.AlertType.ERROR);
                nothingSelected.setContentText("Nothing to modify, select part");
                nothingSelected.show();
            }
        });
        exitBtn.setOnAction(event -> {
            exit();
        });
    }

    private void exit() {
        Alert exitAlert = new Alert(Alert.AlertType.CONFIRMATION);
        exitAlert.setContentText("Are you sure you want to close SlickInventory?");

        Optional<ButtonType> result = exitAlert.showAndWait();
        if (result.get() == ButtonType.OK) {
            System.exit(0);
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    private void modProd(ActionEvent event, Product product) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ModProductView.fxml"));
        Parent root = loader.load();
        ModProdController modProdController = loader.getController();
        modProdController.initData(product);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(new Scene(root));
        window.show();
    }

    private void modPart(ActionEvent event, Part part) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ModifyPartView.fxml"));
        Parent root = loader.load();
        ModPartController modController = loader.getController();
        modController.initData(part);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(new Scene(root));
        window.show();
    }

    private void addPart(ActionEvent event) throws IOException {
        AnchorPane addPartPane = FXMLLoader.load(getClass().getResource("/view/AddPartView.fxml"));
        Scene addPartScene = new Scene(addPartPane);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(addPartScene);
        window.show();
    }

    private void addProduct(ActionEvent event) throws IOException {
        AnchorPane addProductPane = FXMLLoader.load(getClass().getResource("/view/AddProductView.fxml"));
        Scene addProductScene = new Scene(addProductPane);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(addProductScene);
        window.show();
    }

    private void partDltAction(Part selectedItem) throws IOException {
        String resultString = selectedItem.getPartName() + " has been deleted";
        parts.remove(selectedItem);

        if (updatePartDB(parts)) {
            partAlertText.setFill(Color.DARKGREEN);
            partAlertText.setText(resultString);
        } else {
            partAlertText.setFill(Color.RED);
            partAlertText.setText("ERROR!");
        }
        partTable.getItems().remove(selectedItem);
    }

    private void prodDltAction(Product selectedItem) throws IOException {
        String resultString = selectedItem.getProductName() + " has been deleted!";
        products.remove(selectedItem);
        if (updateProdDB(products)) {
            prodAlertText.setFill(Color.DARKGREEN);
            prodAlertText.setText(resultString);
        } else {
            prodAlertText.setFill(Color.RED);
            prodAlertText.setText("ERROR!");
        }
        productTable.getItems().remove(selectedItem);
    }

    private void prodSearchAction(String input) {
        ObservableList<Product> returnProd = FXCollections.observableArrayList();
        if (isInteger(input) == true) {
            Integer id = Integer.parseInt(input);
            for (Product p : products)
                if (p.getProductID() == id)
                    returnProd.add(p);
        } else {
            for (Product p : products)
                if (p.getProductName().toLowerCase().contains(input.toLowerCase()))
                    returnProd.add(p);
        }
        productTable.getItems().clear();
        productTable.getItems().addAll(returnProd);
    }

    @FXML
    private void partSearchAction(String input) {
        ObservableList<Part> returnParts = FXCollections.observableArrayList();
        if (isInteger(input) == true) {
            Integer id = Integer.parseInt(input);
            for (Part p : parts)
                if (p.getPartID() == id)
                    returnParts.add(p);
        } else {
            for (Part p : parts)
                if (p.getPartName().toLowerCase().contains(input.toLowerCase()))
                    returnParts.add(p);
        }
        partTable.getItems().clear();
        partTable.getItems().addAll(returnParts);
    }

    private boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
