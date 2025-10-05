package com.w2e.ui.cell;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;

import java.io.File;

public class DocListCell extends ListCell<File> { // Replace String with your item type
    private final ContextMenu contextMenu = new ContextMenu();
    private final MenuItem editItem = new MenuItem("Edit");
    private final MenuItem deleteItem = new MenuItem("Delete");

    public DocListCell() {
        contextMenu.getItems().addAll(deleteItem);

        editItem.setOnAction(event -> {
            // Access the item associated with this cell: getItem()
            File itemToEdit = getItem();
            System.out.println("Editing: " + itemToEdit);
            // Implement your edit logic here
        });

        deleteItem.setOnAction(event -> {
            ObservableList<File> selectedItems = getListView().getSelectionModel().getSelectedItems();
            System.out.println("Deleting: " + selectedItems);
            getListView().getItems().removeAll(selectedItems);

        });
    }

    @Override
    protected void updateItem(File item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            setContextMenu(null); // Clear context menu when cell is empty
        } else {
            setText(item.getAbsolutePath()); // Display the item's content
            setContextMenu(contextMenu); // Attach the context menu to this cell
        }
    }
}