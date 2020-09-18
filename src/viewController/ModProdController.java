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

public class ModProdController implements Initializable {
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
    private TableView<Part> modProdPartTable;
    @FXML
    private TableColumn<Part, Integer> modProdPartID;
    @FXML
    private TableColumn<Part, String> modProdPartName;
    @FXML
    private TableColumn<Part, Integer> modProdPartInv;
    @FXML
    private TableColumn<Part, Double> modProdPartPrice;

    // Setup mod Product TextFields
    @FXML
    private TextField modProdID, modProdName, modProdInv, modProdPrice, modProdMax, modProdMin, modProdSearchTxt;

    // Setup recommended price text
    @FXML
    private Text modProdRecPrice;

    // Set up buttons
    @FXML
    private Button modProdSearchBtn, modProdAddBtn, modProdDltBtn, modProdSaveBtn, modProdCnclBtn;

    // Setup parts list
    private ObservableList<Part> parts;
    private ObservableList<Part> associatedParts;
    private Product product;
    private double recPrice = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Populate parts list with values from DB
        try {
            parts = readPartsDB();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize Product ID TextField and assign it the last ID
        modProdID.setEditable(false);
        modProdID.setDisable(true);

        // Associates Values in Part class with columns in table
        lstPartID.setCellValueFactory(new PropertyValueFactory<>("partID"));
        lstPartName.setCellValueFactory(new PropertyValueFactory<>("partName"));
        lstPartInv.setCellValueFactory(new PropertyValueFactory<>("partStock"));
        lstPartPrice.setCellValueFactory(new PropertyValueFactory<>("partPrice"));
        modProdPartID.setCellValueFactory(new PropertyValueFactory<>("partID"));
        modProdPartName.setCellValueFactory(new PropertyValueFactory<>("partName"));
        modProdPartInv.setCellValueFactory(new PropertyValueFactory<>("partStock"));
        modProdPartPrice.setCellValueFactory(new PropertyValueFactory<>("partPrice"));

        // Populates the table with slickInventory data from Part DB
        lstPartTbl.getItems().setAll(parts);

        // Create Buttons and onAction events
        modProdSearchBtn.setOnAction(e -> partSearchAction(modProdSearchTxt.getText()));
        modProdAddBtn.setOnAction(e -> {
            associatedParts.add((lstPartTbl.getSelectionModel().getSelectedItem()));
            modProdPartTable.setItems(associatedParts);
            getRecPrice();
        });
        modProdDltBtn.setOnAction(e -> {
            Part selected = modProdPartTable.getSelectionModel().getSelectedItem();
            associatedParts.remove(selected);
            modProdPartTable.getItems().remove(selected);
            getRecPrice();
        });
        modProdSaveBtn.setOnAction(event -> {
            save(event);
        });
        modProdCnclBtn.setOnAction(event -> {
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
        if (!modProdName.getText().trim().isEmpty() && modProdName.getText().trim() != null) {
            prodName = modProdName.getText();
        } else {
            errorText.append("Product Name must be specified!\n");
            writeOk = false;
        }

        if (!modProdInv.getText().trim().isEmpty() && modProdInv.getText().trim() != null) {
            try {
                inventory = Integer.parseInt(modProdInv.getText());
            } catch (NumberFormatException e) {
                errorText.append("Inventory value must be a number, please change inventory value.\n");
                writeOk = false;
            }
        } else {
            errorText.append("Inventory level must be specified!\n");
            writeOk = false;
        }

        if (!modProdPrice.getText().trim().isEmpty() && modProdPrice.getText().trim() != null) {
            try {
                if (Double.parseDouble(modProdPrice.getText()) != 0.0 && Double.parseDouble((modProdPrice.getText())) > recPrice) {
                    price = Double.parseDouble(modProdPrice.getText());
                } else {
                    errorText.append("Price cannot be 0.0, or less than combined total of parts.\n");
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

        if (!modProdMin.getText().trim().isEmpty() && modProdMin.getText().trim() != null) {
            try {
                min = Integer.parseInt(modProdMin.getText());
            } catch (NumberFormatException e) {
                errorText.append("Min must be a valid number, please change Min value.\n");
                writeOk = false;
            }
        } else {
            errorText.append("Min level must be specified!\n");
            writeOk = false;
        }

        if (!modProdMax.getText().trim().isEmpty() && modProdMax.getText().trim() != null) {
            try {
                max = Integer.parseInt(modProdMax.getText());
            } catch (NumberFormatException e) {
                errorText.append("Max must be a valid number, please enter a number.\n");
                writeOk = false;
            }
        } else {
            errorText.append("Max level must be specified!\n");
            writeOk = false;
        }

        try {
            associatedParts = modProdPartTable.getItems();
        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No Parts have been moded to Product");
            alert.showAndWait();
        }
        try {
            product = new Product(associatedParts, Integer.parseInt(modProdID.getText()), prodName, price, inventory, min, max);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, product.toString());
            alert.showAndWait();
            e.printStackTrace();
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

        if (associatedParts.isEmpty()) {
            writeOk = false;
            errorText.append("A product must have parts associated to it.\n");
        }

        if (writeOk) {
            try {
                updateProd(product);
                updateAssociatedParts(product, product.getAssociatedParts());
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

    public void initData(Product product) throws IOException {
        modProdID.setText(String.valueOf(product.getProductID()));
        modProdName.setText(product.getProductName());
        modProdInv.setText(String.valueOf(product.getProductStock()));
        modProdPrice.setText(String.valueOf(product.getProductPrice()));
        modProdMax.setText(String.valueOf(product.getProductMax()));
        modProdMin.setText(String.valueOf(product.getProductMin()));
        associatedParts = getAssociatedParts(product);
        modProdPartTable.getItems().setAll(associatedParts);
        getRecPrice();
    }

    public void getRecPrice() {
        recPrice = 0;
        for (Part ap : associatedParts) {
            recPrice += ap.getPartPrice();
        }
        DecimalFormat df = new DecimalFormat("#.##");
        modProdRecPrice.setText("Suggested Price is $" + df.format(recPrice * 1.3));
    }
}
