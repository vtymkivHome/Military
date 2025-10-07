package com.w2e.ui;

import com.w2e.ui.cell.DocListCell;
import com.w2e.ui.task.ConvertFileTask;
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
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

//@Slf4J
public class WD2EUIController {
    private final ObservableList<File> fileNames = FXCollections.observableArrayList();
    private final StringProperty pathToExcelProp = new SimpleStringProperty("");
    public ProgressBar fxConvertingDocsProgress;
    public Label fxDocNameLbl;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

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
    public void convertButtonPressed(MouseEvent mouseEvent) {

        boolean isPathToExcelBlank = StringUtils.isAllBlank(pathToExcelProp.getValue());
        boolean isListOfWDBlank = CollectionUtils.isEmpty(fileNames);

        if(isPathToExcelBlank || isListOfWDBlank) {
            Alert warningAlert = new Alert(Alert.AlertType.WARNING);

            String contentText = isPathToExcelBlank ? "The path to excel document is not defined." : "The list of documents is not populated.";
            if(isPathToExcelBlank && isListOfWDBlank) {
                contentText = "The path to excel document and the list of documents are not defined";
            }

            warningAlert.setContentText(contentText);
            warningAlert.show();
            mouseEvent.consume();
            return;
        }

        //Show popup window
        // Create an Alert object with AlertType.INFORMATION
        Alert confirmationDlg = new Alert(Alert.AlertType.CONFIRMATION);

        // Set the title of the confirmationDlg dialog
        confirmationDlg.setTitle("Confirm converting");

        // Set the header text (optional)
        confirmationDlg.setHeaderText("Are you sure you want to convert the following list of word documents ?");

        // Set the content text (the main message)
        String msgListOffDocs = fileNames.stream().map(File::getName).collect(Collectors.joining("\n"));
        String contentMsg = "The following documents:\n\n" + msgListOffDocs + "\n\nwill be converted into: " + pathToExcelProp.getValue();
        confirmationDlg.setContentText( contentMsg );

        // Display the confirmationDlg and wait for the user to close it
        Optional<ButtonType> buttonType = confirmationDlg.showAndWait();
        if(ButtonType.OK == buttonType.orElseThrow() ) {
            convertDocToExcel();
        }

    }

    private void convertDocToExcel() {
        List<File> docFilesToConvert = fileNames.stream().toList();
        String excelFileToConvertTo = pathToExcelProp.getValue();
        ConvertFileTask convertFilesTask = new ConvertFileTask(docFilesToConvert, excelFileToConvertTo);
        fxConvertingDocsProgress.progressProperty().bind(convertFilesTask.progressProperty());

        // Optional: Bind a Label to show progress message
        // Label statusLabel = new Label();
         fxDocNameLbl.textProperty().bind(convertFilesTask.messageProperty());
        //new DocDevCLI().convertDocToExcel(fileNames.stream().map(File::getAbsolutePath).toList(), pathToExcelProp.getValue());
        //fileNames.clear();
        convertFilesTask.setOnSucceeded(event -> {
            System.out.println("Converting finished successfully!");
            fileNames.removeAll(docFilesToConvert);
            // Unbind the progress property when done
            fxConvertingDocsProgress.progressProperty().unbind();
        });

        convertFilesTask.setOnFailed(event -> {
            System.err.println("Converting failed: " + convertFilesTask.getException().getMessage());
            fxConvertingDocsProgress.progressProperty().unbind();
        });

        Thread thread = new Thread(convertFilesTask);
        thread.setDaemon(true); // Allow the application to exit even if this thread is running
        thread.start();
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

        tgtDocTxt.textProperty().bindBidirectional(pathToExcelProp);

        Tooltip styledTooltip = new Tooltip();
        styledTooltip.textProperty().bindBidirectional(pathToExcelProp);
        styledTooltip.setFont(new Font("Arial", 14));
        styledTooltip.setStyle("-fx-background-color: lightblue; -fx-text-fill: darkblue;");
        tgtDocTxt.setTooltip(styledTooltip);
        //styledButton.setTooltip(styledTooltip);


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

    public void onDragExcelDropped(DragEvent dragEvent) {
        Dragboard db = dragEvent.getDragboard();
        boolean success = false;
        // Process the dropped content only if it meets the criteria
        if (db.hasFiles()) {
            boolean allFilesAreAccepted = db.getFiles().stream()
                    .allMatch(file -> file.getName().endsWith(".xlsx"));
            if (allFilesAreAccepted) {
                List<File> docFileLst = db.getFiles();
                System.out.println("Dropped files: " + docFileLst);
                pathToExcelProp.set(docFileLst.getFirst().getAbsolutePath());
                success = true;
            }
        }

        dragEvent.setDropCompleted(success);
        dragEvent.consume();
    }

    public void onDragExcelOver(DragEvent dragEvent) {
        Dragboard db = dragEvent.getDragboard();
        // Filter based on the content of the Dragboard
        if (db.hasFiles()) {
            // Filter based on file extensions
            boolean allFilesAreAccepted = db.getFiles().stream()
                    .allMatch(file -> file.getName().endsWith(".xlsx") );
            if (allFilesAreAccepted) {
                dragEvent.acceptTransferModes(TransferMode.COPY);
            }
        }
        dragEvent.consume();  // Consume the event to prevent propagation
    }

    @FXML
    public void onDragDocDropped(DragEvent dragEvent) {
        Dragboard db = dragEvent.getDragboard();
        boolean success = false;
        // Process the dropped content only if it meets the criteria
        if (db.hasFiles()) {
            boolean allFilesAreAccepted = db.getFiles().stream()
                    .allMatch(file -> file.getName().endsWith(".docx"));
            if (allFilesAreAccepted) {
                List<File> docFileLst = db.getFiles();
                System.out.println("Dropped files: " + docFileLst);
                updateListView(docFileLst);
                success = true;
            }
        }

        dragEvent.setDropCompleted(success);
        dragEvent.consume();
    }

    @FXML
    public void onDragDocOver(DragEvent dragEvent) {
        Dragboard db = dragEvent.getDragboard();
        // Filter based on the content of the Dragboard
        if (db.hasFiles()) {
            // Filter based on file extensions
            boolean allFilesAreAccepted = db.getFiles().stream()
                    .allMatch(file -> file.getName().endsWith(".docx") );
            if (allFilesAreAccepted) {
                dragEvent.acceptTransferModes(TransferMode.COPY);
            }
        }
        dragEvent.consume();  // Consume the event to prevent propagation
    }

    /*public void onDragExcelDetected(MouseEvent mouseEvent) {
        Dragboard db = sourceWrdDocListView.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();

        // Add data to the Dragboard based on what you want to allow to be dragged
        if (sourceWrdDocListView.getUserData() instanceof String userData) {
            if(userData.endsWith("xlsx")) {
                content.putString(userData);
            }
        } else if (sourceWrdDocListView.getUserData() instanceof File file) {
            // Example: Only allow certain file types to be dragged
            if (file.getName().endsWith(".xlsx")) {
                content.putFiles(java.util.Collections.singletonList(file));
            } else {
                // If the file type is not allowed, don't start the drag
                mouseEvent.consume();
                return;
            }
        }
        db.setContent(content);
        mouseEvent.consume();
    }

    public void onDragDocumentsDetected(MouseEvent mouseEvent) {

    }*/
}