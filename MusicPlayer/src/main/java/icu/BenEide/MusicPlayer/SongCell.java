package icu.BenEide.MusicPlayer;

import javafx.scene.Cursor;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class SongCell extends ListCell<Song> {
    private static final DataFormat DATA_FORMAT = new DataFormat("application/player-song");

    @Override
    protected void updateItem(Song song, boolean empty) {
        super.updateItem(song, empty);

        if (empty || song == null) {
            setGraphic(null);
            return;
        }

        // Create text nodes for song title and artist
        var textTitle = new Text(song.getTitle());
        textTitle.setFill(Color.BLACK);
        var textNewLine = new Text("\n");
        var textArtist = new Text(song.getArtist());
        textArtist.setFill(Color.BLACK);
        textArtist.setOpacity(0.5);

        // Combine text nodes into a TextFlow
        var textFlow = new TextFlow(textTitle, textNewLine, textArtist);
        HBox.setHgrow(textFlow, Priority.ALWAYS);

        // Create a pane for drag indicator
        var dragIndicator = new Pane();
        dragIndicator.setPrefSize(24, 24);
        dragIndicator.setId("drag-indicator");
        dragIndicator.setOpacity(0.5);
        dragIndicator.setCursor(Cursor.MOVE);

        // Combine TextFlow and drag indicator into an HBox
        var hBox = new HBox(textFlow, dragIndicator);

        // Set content display mode and graphic for the cell
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setGraphic(hBox);
        setCursor(Cursor.HAND);

        // Handle drag detected event
        dragIndicator.setOnDragDetected(e -> {
            if (getItem() == null)
                return;

            var db = startDragAndDrop(TransferMode.MOVE);
            var content = new ClipboardContent();
            content.put(DATA_FORMAT, getIndex());
            db.setDragView(getGraphic().snapshot(null, null));
            db.setContent(content);

            e.consume();
        });

        // Handle drag over event
        dragIndicator.setOnDragOver(e -> {
            if (e.getGestureSource() != this && e.getDragboard().hasContent(DATA_FORMAT))
                e.acceptTransferModes(TransferMode.MOVE);

            e.consume();
        });

        // Handle drag dropped event
        dragIndicator.setOnDragDropped(e -> {
            if (getItem() == null)
                return;

            var db = e.getDragboard();
            boolean completed = false;
            if (db.hasContent(DATA_FORMAT)) {
                var listView = getListView();
                var items = listView.getItems();

                int dragIdx = (Integer) db.getContent(DATA_FORMAT);
                var dragSong = items.remove(dragIdx);
                int dropIdx = getIndex();
                items.add(dropIdx, dragSong);
                listView.getSelectionModel().select(dropIdx);

                completed = true;
            }

            e.setDropCompleted(completed);
            e.consume();
        });
    }
}
