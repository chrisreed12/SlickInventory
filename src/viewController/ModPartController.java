package viewController;

import data.InHouse;
import data.Outsourced;
import data.Part;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

import static utilities.DBHelper.updateIH;
import static utilities.DBHelper.updateOS;

public class ModPartController implements Initializable {

    @FXML
    private TextField modPartIDTF, modPartNameTF, modPartInvTF, modPartPriceTF, modPartMaxTF, modPartMinTF, modMachOut;
    @FXML
    private RadioButton modInHouseRB, modOutsourcedRB;
    @FXML
    private Button modPartSaveBtn, modPartCnclBtn;
    @FXML
    private Text modOutIHTxt;

    private Outsourced outsourced;
    private InHouse inHouse;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Disable access to part ID TextField and initialize
        modPartIDTF.setEditable(false);

        // Create a Toggle group for radio buttons and add a listener
        final ToggleGroup modPartRB = new ToggleGroup();
        modInHouseRB.setToggleGroup(modPartRB);
        modOutsourcedRB.setToggleGroup(modPartRB);
        modInHouseRB.setSelected(true);
        modPartRB.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle toggle, Toggle t1) {
                if (modPartRB.getSelectedToggle() == modInHouseRB) {
                    modMachOut.setPromptText("Machine ID");
                    modOutIHTxt.setText("Machine ID");
                } else {
                    modMachOut.setPromptText("Company");
                    modOutIHTxt.setText("Company");
                }
            }
        });

        // Create Buttons and onAction events
        modPartSaveBtn.setOnAction(event -> {
            save(event);
        });

        modPartCnclBtn.setOnAction(event -> {
            cancel(event);
        });
    }

    private void save(ActionEvent event) {
        String partName = "";
        Integer inventory = 0, max = 0, min = 0;
        Double price = 0.00;
        Boolean writeOk = true;
        StringBuilder errorText = new StringBuilder();

        // Check if fields are empty and if not associates them to variables that will create part, has validation.
        if (!modPartNameTF.getText().trim().isEmpty() && modPartNameTF.getText().trim() != null) {
            if (modPartNameTF.getText().contains(",")) {
                partName = modPartNameTF.getText().replace(",", "");
            } else {
                partName = modPartNameTF.getText();
            }
        } else {
            errorText.append("Part Name must be specified!\n");
            writeOk = false;
        }

        if (!modPartInvTF.getText().trim().isEmpty() && modPartInvTF.getText().trim() != null) {
            try {
                inventory = Integer.parseInt(modPartInvTF.getText());
            } catch (NumberFormatException e) {
                errorText.append("Inventory value must be a number, please change inventory value.\n");
                writeOk = false;
            }
        } else {
            errorText.append("Inventory level must be specified!\n");
            writeOk = false;
        }

        if (!modPartPriceTF.getText().trim().isEmpty() && modPartPriceTF.getText().trim() != null) {
            try {
                if (Double.parseDouble(modPartPriceTF.getText()) != 0.0) {
                    price = Double.parseDouble(modPartPriceTF.getText());
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

        if (!modPartMinTF.getText().trim().isEmpty() && modPartMinTF.getText().trim() != null) {
            try {
                min = Integer.parseInt(modPartMinTF.getText());
            } catch (NumberFormatException e) {
                errorText.append("Min must be a valid number, please change Min value.\n");
                writeOk = false;
            }
        } else {
            errorText.append("Inventory level must be specified!\n");
            writeOk = false;
        }

        if (!modPartMaxTF.getText().trim().isEmpty() && modPartMaxTF.getText().trim() != null) {
            try {
                max = Integer.parseInt(modPartMaxTF.getText());
            } catch (NumberFormatException e) {
                errorText.append("Max must be a valid number, please enter a number.\n");
                writeOk = false;
            }
        } else {
            errorText.append("Inventory level must be specified!\n");
            writeOk = false;
        }

        if (modOutsourcedRB.isSelected()) {
            if (!modMachOut.getText().trim().isEmpty() && modMachOut.getText() != null) {
                outsourced = (new Outsourced(Integer.parseInt(modPartIDTF.getText()), partName, price, inventory, min, max, modMachOut.getText()));
            } else {
                errorText.append("Vendor name is required.");
                writeOk = false;
            }
        } else {
            if (!modMachOut.getText().trim().isEmpty() && modMachOut.getText() != null) {
                try {
                    int id = Integer.parseInt(modMachOut.getText());
                    inHouse = new InHouse(Integer.parseInt(modPartIDTF.getText()), partName, price, inventory, min, max, id);
                } catch (NumberFormatException e) {
                    errorText.append("Machine must only contain numbers, please enter a number.");
                }
            }
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
                if (modOutsourcedRB.isSelected()) {
                    updateOS(outsourced);
                } else {
                    updateIH(inHouse);
                }
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

    public void initData(Part part) {
        modPartIDTF.setText(String.valueOf(part.getPartID()));
        modPartNameTF.setText(part.getPartName());
        modPartInvTF.setText(String.valueOf(part.getPartStock()));
        modPartPriceTF.setText(String.valueOf(part.getPartPrice()));
        modPartMaxTF.setText(String.valueOf(part.getPartMax()));
        modPartMinTF.setText(String.valueOf(part.getPartMin()));
        if (part.getClass() == Outsourced.class) {
            Outsourced osIn = (Outsourced) part;
            modOutsourcedRB.setSelected(true);
            modMachOut.setText(osIn.getCompanyName());
        } else {
            InHouse ih = (InHouse) part;
            modInHouseRB.setSelected(true);
            modMachOut.setText(String.valueOf(ih.getMachineID()));
        }
    }
}
