package com.w2e.ui;

import com.w2e.ui.cell.DocListCell;
import com.w2e.core.DocDevCLI;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

//@Slf4J
public class WD2EUIController {
    private ObservableList<File> fileNames = FXCollections.observableArrayList();
    private StringProperty docPathTxtProp = new SimpleStringProperty("");


    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="sourceWrdDocListView"
    private ListView<File> sourceWrdDocListView; // Value injected by FXMLLoader

    @FXML // fx:id="tgtDocTxt"
    private TextField tgtDocTxt; // Value injected by FXMLLoader


    @FXML
    private Button cancelBtn;

    @FXML
    private Button browseDocsBtn;

    @FXML
    private Button selectExcelBtn;

    @FXML
    void onDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            List<File> files = db.getFiles();
            updateListView(files);
            success = true;
        }
        event.setDropCompleted(success);
        event.consume();
    }

    @FXML
    void onDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume(); // Consume the event to prevent propagation
    }

    @FXML
    public void convertButtonPressed(MouseEvent mouseEvent) {
        //Show popup window
        // Create an Alert object with AlertType.INFORMATION
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        // Set the title of the alert dialog
        alert.setTitle("Confirm converting");

        // Set the header text (optional)
        alert.setHeaderText("Are you sure you want to convert the following list of word documents ?");

        // Set the content text (the main message)
        alert.setContentText(fileNames.stream().map(File::getName).collect(Collectors.joining("\n")) + "\n The documents will e converted to: " + docPathTxtProp.getValue());

        boolean isPathToExcelBlank = StringUtils.isAllBlank(docPathTxtProp.getValue());
        boolean isListOfWDBlank = CollectionUtils.isEmpty(fileNames);

        if(isPathToExcelBlank || isListOfWDBlank) {
            Alert warningAlert = new Alert(Alert.AlertType.WARNING);

            String contentText = isPathToExcelBlank ? "The path to excel document is not define." : "The list of documents is not populated.";
            if(isPathToExcelBlank && isListOfWDBlank) {
                contentText = "The path to excel document and the list of documents are not defined";
            }

            warningAlert.setContentText( contentText);
            warningAlert.show();
            return;
        }

        // Display the alert and wait for the user to close it
        Optional<ButtonType> buttonType = alert.showAndWait();
        if(ButtonType.OK == buttonType.get() ) {

            //TODO: Add actual logic here
            //TODO: Validate if the list of documents is not empty
            //TODO: Validate if excel file has been selected
            new DocDevCLI().convertDocToExcel(fileNames.stream().map(File::getAbsolutePath).toList(), docPathTxtProp.getValue());
        }


    }

    public void cancelButtonPressed(ActionEvent actionEvent) {
        Stage stage = getStage(actionEvent);
        stage.close();
    }

    private  Stage getStage(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        return  (Stage) node.getScene().getWindow();
    }

    @FXML
    void selectDocuments(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Multiple Word Document Files");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"))); // Sets initial directory to user's home
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Document Files", "*.docx"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        List<File> docFileList = fileChooser.showOpenMultipleDialog(getStage(event));
        updateListView(docFileList);
    }

    @FXML
    void selectExcelFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(getStage(event));
        if (selectedFile != null) {
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            // Further processing of the selected file
            tgtDocTxt.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    void initialize() {
        assert browseDocsBtn != null : "fx:id=\"browseDocsBtn\" was not injected: check your FXML file 'wd2e.fxml'.";
        assert cancelBtn != null : "fx:id=\"cancelBtn\" was not injected: check your FXML file 'wd2e.fxml'.";
        assert selectExcelBtn != null : "fx:id=\"selectExcelBtn\" was not injected: check your FXML file 'wd2e.fxml'.";
        assert sourceWrdDocListView != null : "fx:id=\"sourceWrdDocListView\" was not injected: check your FXML file 'wd2e.fxml'.";
        assert tgtDocTxt != null : "fx:id=\"tgtDocTxt\" was not injected: check your FXML file 'wd2e.fxml'.";

        sourceWrdDocListView.setCellFactory( lv -> new DocListCell());
        sourceWrdDocListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        sourceWrdDocListView.setItems(fileNames);

        tgtDocTxt.textProperty().bindBidirectional(docPathTxtProp);

    }

    private void updateListView(List<File> docFileList) {
        if(CollectionUtils.isNotEmpty(docFileList)) {
            //TODO: Validate if we already have this doc in list
            // Get intersection of documents from list view and selected documents
            //fileNames.retainAll(docFileList);
            List<File> docListToAdd = new ArrayList<>(docFileList);
            List<File> intersection = docFileList.stream()
                    .filter(fileNames::contains)
                    .distinct()
                    .toList();
            if(CollectionUtils.isNotEmpty(intersection)) {
                System.out.println("The following documents is already in list: " + intersection);
                // Remove existing documents in view from list
                docListToAdd.removeAll(intersection);
                // Show info message(warning message)
            }
            fileNames.addAll(docListToAdd);
        }
    }
}