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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.ResourceBundle;

import static utilities.DBHelper.*;

public class AddProdController implements Initializable {

    // Sets up global parts list table
    @FXML
    private TableView<Part> lstPartTbl;
    @FXML
    private TableColumn<Product, Integer> lstPartID;
    @FXML
    private TableColumn<Product, String> lstPartName;
    @FXML
    private TableColumn<Product, Integer> lstPartInv;
    @FXML
    private TableColumn<Product, Double> lstPartPrice;

    // Sets up associated parts list table
    @FXML
    private TableView<Part> addProdPartTable;
    @FXML
    private TableColumn<Part, Integer> addProdPartID;
    @FXML
    private TableColumn<Part, String> addProdPartName;
    @FXML
    private TableColumn<Part, Integer> addProdPartInv;
    @FXML
    private TableColumn<Part, Double> addProdPartPrice;

    // Setup Add Product TextFields
    @FXML
    private TextField addProdID, addProdName, addProdInv, addProdPrice, addProdMax, addProdMin, addProdSearchTxt;

    // Setup Recommended price text
    @FXML
    private Text addProdRecPrice;

    // Set up buttons
    @FXML
    private Button addProdSearchBtn, addProdAddBtn, addProdDltBtn, addProdSaveBtn, addProdCnclBtn;

    // Setup parts list
    private ObservableList<Part> parts;
    private ObservableList<Part> associatedParts;
    private ObservableList<Product> products;
    private Product product;
    private Part part;
    private double recPrice = 0;

    public AddProdController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Populate parts list with values from DB
        try {
            parts = readPartsDB();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            products = readProductDB();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize Product ID TextField and assign it the last ID
        addProdID.setText(String.valueOf(getNextID(products)));
        addProdID.setEditable(false);
        addProdID.setDisable(true);

        // Associates Values in Part class with columns in table
        lstPartID.setCellValueFactory(new PropertyValueFactory<>("partID"));
        lstPartName.setCellValueFactory(new PropertyValueFactory<>("partName"));
        lstPartInv.setCellValueFactory(new PropertyValueFactory<>("partStock"));
        lstPartPrice.setCellValueFactory(new PropertyValueFactory<>("partPrice"));
        addProdPartID.setCellValueFactory(new PropertyValueFactory<>("partID"));
        addProdPartName.setCellValueFactory(new PropertyValueFactory<>("partName"));
        addProdPartInv.setCellValueFactory(new PropertyValueFactory<>("partStock"));
        addProdPartPrice.setCellValueFactory(new PropertyValueFactory<>("partPrice"));

        // Populates the table with slickInventory data from Part DB
        lstPartTbl.getItems().setAll(parts);

        // Create Buttons and onAction events
        associatedParts = FXCollections.observableArrayList();
        addProdSearchBtn.setOnAction(e -> partSearchAction(addProdSearchTxt.getText()));
        addProdAddBtn.setOnAction(e -> {
            part = lstPartTbl.getSelectionModel().getSelectedItem();
            associatedParts.add(part);
            addProdPartTable.setItems(associatedParts);
            getReqPrice();
        });
        addProdDltBtn.setOnAction(e -> {
            Part selected = addProdPartTable.getSelectionModel().getSelectedItem();
            associatedParts.remove(selected);
            addProdPartTable.getItems().remove(selected);
            getReqPrice();
        });
        addProdSaveBtn.setOnAction(event -> {
            save(event);
        });
        addProdCnclBtn.setOnAction(event -> {
            cancel(event);
        });
    }

    private void save(ActionEvent event) {
        String prodName = "";
        Integer inventory = 0, max = 0, min = 0;
        Double price = 0.0;
        Boolean writeOk = true;
        StringBuilder errorText = new StringBuilder();

        // Check if fields are empty and if not associates them to variables that will create part, has validation.
        if (!addProdName.getText().trim().isEmpty() && addProdName.getText().trim() != null) {
            prodName = addProdName.getText();
        } else {
            errorText.append("Product Name must be specified!\n");
            writeOk = false;
        }

        if (!addProdInv.getText().trim().isEmpty() && addProdInv.getText().trim() != null) {
            try {
                inventory = Integer.parseInt(addProdInv.getText());
            } catch (NumberFormatException e) {
                errorText.append("Inventory value must be a number, please change inventory value.\n");
                writeOk = false;
            }
        } else {
            errorText.append("Inventory level must be specified!\n");
            writeOk = false;
        }

        if (!addProdPrice.getText().trim().isEmpty() && addProdPrice.getText().trim() != null) {
            try {
                if (Double.parseDouble(addProdPrice.getText()) != 0.0 && Double.parseDouble(addProdPrice.getText()) > recPrice) {
                    price = Double.parseDouble(addProdPrice.getText());
                } else {
                    errorText.append("Price cannot be 0.0, or lower than combined part price.\n");
                    writeOk = false;
                }

            } catch (NumberFormatException e) {
                errorText.append("Price must be a valid price (ex. .79, 1.00), please change price value.\n");
                writeOk = false;
            }
        } else {
            errorText.append("Inventory level must be specified!\n");
            writeOk = false;
        }

        if (!addProdMin.getText().trim().isEmpty() && addProdMin.getText().trim() != null) {
            try {
                min = Integer.parseInt(addProdMin.getText());
            } catch (NumberFormatException e) {
                errorText.append("Min must be a valid number, please change Min value.\n");
                writeOk = false;
            }
        } else {
            errorText.append("Min level must be specified!\n");
            writeOk = false;
        }

        if (!addProdMax.getText().trim().isEmpty() && addProdMax.getText().trim() != null) {
            try {
                max = Integer.parseInt(addProdMax.getText());
            } catch (NumberFormatException e) {
                errorText.append("Max must be a valid number, please enter a number.\n");
                writeOk = false;
            }
        } else {
            errorText.append("Max level must be specified!\n");
            writeOk = false;
        }

        try {
            associatedParts = addProdPartTable.getItems();
        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No Parts have been added to Product\n");
            alert.showAndWait();
        }
        try {
            product = new Product(associatedParts, Integer.parseInt(addProdID.getText()), prodName, price, inventory, min, max);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, product.toString());
            alert.showAndWait();
            e.printStackTrace();
        }

        if (associatedParts.isEmpty()) {
            writeOk = false;
            errorText.append("A product must have parts associated to it.\n");
        }

        if (inventory > max || inventory < min) {
            writeOk = false;
            errorText.append("Inventory value must be between Min and Max value.\n");
        }

        if (max < min + 5) {
            writeOk = false;
            errorText.append("Max value must be more than Min by at least 5.\n");
        }

        if (min >= max) {
            writeOk = false;
            errorText.append("Min must be less than Max.\n");
        }

        if (writeOk) {
            try {
                addProdDB(product);
                addAssociatedParts(product, product.getAssociatedParts());
            } catch (IOException e) {
                e.printStackTrace();
            }
            goToMain(event);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, errorText.toString());
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        }
    }

    private void getReqPrice() {
        recPrice = 0;
        for (Part ap : associatedParts) {
            recPrice += ap.getPartPrice();
        }
        DecimalFormat df = new DecimalFormat("#.##");
        addProdRecPrice.setText("Suggested Price is $" + df.format(recPrice * 1.3));
    }

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
        lstPartTbl.getItems().clear();
        lstPartTbl.getItems().addAll(returnParts);
    }

    private boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private int getNextID(ObservableList<Product> products) {
        int last = 0;
        try {
            for (Product p : products)
                if (p.getProductID() > last)
                    last = p.getProductID();
        } catch (NullPointerException e) {
            last = 0;
        }
        return last + 1;
    }

    private void cancel(ActionEvent event) {
        Alert cancelAlert = new Alert(Alert.AlertType.CONFIRMATION);
        cancelAlert.setContentText("You will loose all unsaved information.");

        Optional<ButtonType> result = cancelAlert.showAndWait();
        if (result.get() == ButtonType.OK) {
            goToMain(event);
        }
    }

    private void goToMain(ActionEvent event) {
        try {
            AnchorPane mainPane = FXMLLoader.load(getClass().getResource("/view/MainView.fxml"));
            Scene mainScene = new Scene(mainPane);

            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(mainScene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
