package com.w2e.ui;

import com.w2e.core.model.W2ESysProp;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class WD2EUIController {
    private static final Logger logger = LoggerFactory.getLogger(WD2EUIController.class);
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
        String pathToConfig = W2ESysProp.CONFIG_FILE_PATH.getPath();
        ConvertFileTask convertFilesTask = new ConvertFileTask(docFilesToConvert, excelFileToConvertTo, pathToConfig);
        fxConvertingDocsProgress.progressProperty().bind(convertFilesTask.progressProperty());

        // Optional: Bind a Label to show progress message
        // Label statusLabel = new Label();
        fxDocNameLbl.textProperty().bind(convertFilesTask.messageProperty());

        convertFilesTask.setOnSucceeded(event -> {
            logger.info("Converting finished successfully!");
            fileNames.removeAll(docFilesToConvert);
            // Unbind the progress property when done
            fxConvertingDocsProgress.progressProperty().unbind();
        });

        convertFilesTask.setOnFailed(event -> {
            Throwable ex = convertFilesTask.getException();
            logger.error("Converting failed: {}", ex.getMessage());

            // Display a critical error message to the user
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();

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
        fileChooser.getExtensionFilters().addAll(getExtensionFiltersForDoc());
        List<File> docFileList = fileChooser.showOpenMultipleDialog(getStage(event));
        updateListView(docFileList);
    }

    @FXML
    void selectExcelFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Excel File");
        fileChooser.getExtensionFilters().addAll(getExtensionFiltersForExcel());

        File selectedFile = fileChooser.showOpenDialog(getStage(event));
        if (selectedFile != null) {
            logger.info("Selected file: [{}]", selectedFile.getAbsolutePath());
            // Further processing of the selected file
            tgtDocTxt.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    void initialize() {
        assert browseDocsBtn != null : "fx:id=\"browseDocsBtn\" was not injected: check your FXML file 'w2e.fxml'.";
        assert cancelBtn != null : "fx:id=\"cancelBtn\" was not injected: check your FXML file 'w2e.fxml'.";
        assert selectExcelBtn != null : "fx:id=\"selectExcelBtn\" was not injected: check your FXML file 'w2e.fxml'.";
        assert sourceWrdDocListView != null : "fx:id=\"sourceWrdDocListView\" was not injected: check your FXML file 'w2e.fxml'.";
        assert tgtDocTxt != null : "fx:id=\"tgtDocTxt\" was not injected: check your FXML file 'w2e.fxml'.";

        sourceWrdDocListView.setCellFactory( lv -> new DocListCell());
        sourceWrdDocListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        sourceWrdDocListView.setItems(fileNames);

        tgtDocTxt.textProperty().bindBidirectional(pathToExcelProp);

        Tooltip styledTooltip = new Tooltip();
        styledTooltip.textProperty().bindBidirectional(pathToExcelProp);
        styledTooltip.setFont(new Font("Arial", 14));
        styledTooltip.setStyle("-fx-background-color: lightblue; -fx-text-fill: darkblue;");
        tgtDocTxt.setTooltip(styledTooltip);
    }

    private void updateListView(List<File> docFileList) {
        if(CollectionUtils.isNotEmpty(docFileList)) {
            // TODO: Validate if we already have this doc in list
            // Get intersection of documents from list view and selected documents
            List<File> docListToAdd = new ArrayList<>(docFileList);
            List<File> intersection = docFileList.stream()
                    .filter(fileNames::contains)
                    .distinct()
                    .toList();
            if(CollectionUtils.isNotEmpty(intersection)) {
                logger.info("The following documents is already in list: [{}].", intersection);
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
            boolean allFilesAreAccepted = isAllExcelFilesAccepted(db.getFiles());
            if (allFilesAreAccepted) {
                List<File> docFileLst = db.getFiles();
                logger.info("Dropped excel file(s): {}", docFileLst);
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
            boolean allFilesAreAccepted = isAllExcelFilesAccepted(db.getFiles());
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
            boolean allFilesAreAccepted = isAllDocFilesAccepted(db.getFiles());
            if (allFilesAreAccepted) {
                List<File> docFileLst = db.getFiles();
                logger.info("Dropped document files: {}", docFileLst);
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
            boolean allFilesAreAccepted = isAllDocFilesAccepted(db.getFiles());
            if (allFilesAreAccepted) {
                dragEvent.acceptTransferModes(TransferMode.COPY);
            }
        }
        dragEvent.consume();  // Consume the event to prevent propagation
    }

    private boolean isAllDocFilesAccepted(List<File> fileList) {
        return fileList.stream()
                .allMatch(file -> file.getName().endsWith(".docx") || file.getName().endsWith(".doc"));
    }

    private boolean isAllExcelFilesAccepted(List<File> fileList) {
        return fileList.stream()
                .allMatch(file -> file.getName().endsWith(".xlsx"));
    }

    private List<FileChooser.ExtensionFilter> getExtensionFiltersForDoc() {
        return List.of(new FileChooser.ExtensionFilter("Document Files", "*.doc", "*.docx")/*,
                new FileChooser.ExtensionFilter("All Files", "*.*")*/);
    }

    private Collection<FileChooser.ExtensionFilter> getExtensionFiltersForExcel() {
        return List.of(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
    }
}