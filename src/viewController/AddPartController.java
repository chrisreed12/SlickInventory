package viewController;

import data.InHouse;
import data.Outsourced;
import data.Part;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static utilities.DBHelper.readPartsDB;
import static utilities.DBHelper.updatePartDB;

public class AddPartController implements Initializable {

    // Create Lists to add items.
    ObservableList<Part> parts;
    @FXML
    private RadioButton inHouseRB, outsourcedRB;
    @FXML
    private Text outIHTxt;
    @FXML
    private Button partSaveBtn, partCnclBtn;
    @FXML
    private TextField partIDTF, partNameTF, partInvTF, partPriceTF, partMaxTF, partMinTF, machOut;
    private Outsourced outsourced;
    private InHouse inHouse;

    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            parts = readPartsDB();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize Part ID TextField and assign it the last ID
        partIDTF.setText(String.valueOf(getNextID(parts)));
        partIDTF.setEditable(false);
        partIDTF.setDisable(true);

        // Create a Toggle group for radio buttons and add a listener
        final ToggleGroup addPartRB = new ToggleGroup();
        inHouseRB.setToggleGroup(addPartRB);
        outsourcedRB.setToggleGroup(addPartRB);
        inHouseRB.setSelected(true);
        machOut.setPromptText("Machine ID");
        addPartRB.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle toggle, Toggle t1) {
                if (addPartRB.getSelectedToggle() == inHouseRB) {
                    outIHTxt.setText("Machine ID");
                    machOut.setPromptText("Machine ID");
                } else {
                    outIHTxt.setText("Company");
                    machOut.setPromptText("Company");
                }
            }
        });

        // Create Buttons and onAction events
        partSaveBtn.setOnAction(event -> {
            save(event);
        });
        partCnclBtn.setOnAction(event -> {
            cancel(event);
        });
    }

    private void save(ActionEvent event) {
        String partName = "";
        Integer inventory = 0, max = 0, min = 0;
        Double price = 0.0;
        Boolean writeOk = true;
        StringBuilder errorText = new StringBuilder();

        // Check if fields are empty and if not associates them to variables that will create part, has validation.
        if (!partNameTF.getText().trim().isEmpty() && partNameTF.getText().trim() != null) {
            if (partName.contains(",")) {
                partName = partNameTF.getText().replace(",", "");
            } else {
                partName = partNameTF.getText();
            }
        } else {
            errorText.append("Part Name must be specified!\n");
            writeOk = false;
        }

        if (!partInvTF.getText().trim().isEmpty() && partInvTF.getText().trim() != null) {
            try {
                inventory = Integer.parseInt(partInvTF.getText());
            } catch (NumberFormatException e) {
                errorText.append("Inventory value must be a number, please change inventory value.\n");
                writeOk = false;
            }
        } else {
            errorText.append("Inventory level must be specified!\n");
            writeOk = false;
        }

        if (!partPriceTF.getText().trim().isEmpty() && partPriceTF.getText().trim() != null) {
            try {
                if (Double.parseDouble(partPriceTF.getText()) != 0.0) {
                    price = Double.parseDouble(partPriceTF.getText());
                } else {
                    errorText.append("Price cannot be 0.0, please add a price.\n");
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

        if (!partMinTF.getText().trim().isEmpty() && partMinTF.getText().trim() != null) {
            try {
                min = Integer.parseInt(partMinTF.getText());
            } catch (NumberFormatException e) {
                errorText.append("Min must be a valid number, please change Min value.\n");
                writeOk = false;
            }
        } else {
            errorText.append("Min level must be specified!\n");
            writeOk = false;
        }

        if (!partMaxTF.getText().trim().isEmpty() && partMaxTF.getText().trim() != null) {
            try {
                max = Integer.parseInt(partMaxTF.getText());
            } catch (NumberFormatException e) {
                errorText.append("Max must be a valid number, please enter a number.\n");
                writeOk = false;
            }
        } else {
            errorText.append("Max level must be specified.\n");
            writeOk = false;
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

        if (outsourcedRB.isSelected()) {
            if (!machOut.getText().trim().isEmpty() && machOut.getText() != null) {
                if (partIDTF.getText().contains(",")) {
                    outsourced = (new Outsourced(Integer.parseInt(partIDTF.getText().replace(",", "")), partName, price, inventory, min, max, machOut.getText()));
                    parts.add(outsourced);
                } else {
                    outsourced = (new Outsourced(Integer.parseInt(partIDTF.getText()), partName, price, inventory, min, max, machOut.getText()));
                    parts.add(outsourced);
                }
            } else {
                errorText.append("Vendor name is required.\n");
                writeOk = false;
            }
        } else {
            if (!machOut.getText().trim().isEmpty() && machOut.getText() != null) {
                try {
                    int id = Integer.parseInt(machOut.getText());
                    inHouse = new InHouse(Integer.parseInt(partIDTF.getText()), partName, price, inventory, min, max, id);
                    parts.add(inHouse);
                } catch (NumberFormatException e) {
                    errorText.append("Machine must only contain numbers, please enter a number.\n");
                    writeOk = false;
                }
            }
        }

        if (writeOk) {
            try {
                updatePartDB(parts);
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

    // Iterates part listing to find the largest id value.
    public int getNextID(ObservableList<Part> parts) {
        int last = 0;
        for (Part p : parts)
            if (p.getPartID() > last)
                last = p.getPartID();
        return last + 1;
    }
}
