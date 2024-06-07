package icu.BenEide.MusicPlayer;

import javafx.collections.MapChangeListener;
import javafx.scene.image.Image;
import javafx.scene.media.Media;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

public class Song implements Serializable {
    private String title;
    private String artist;
    private Image image;
    private final URI uri;

    Song(String s) {
        // Initialize the URI for the song
        try {
            uri = new URI(s);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        var path = uri.getPath();

        // Default title is the filename, default artist is unknown
        title = path.substring(path.lastIndexOf('/') + 1);
        artist = "<Unknown Artist>";

        // Try to load cover image in various formats
        label: try {
            var url = uri.toURL().toString();
            var partialPath = url.substring(0, url.lastIndexOf('/') + 1) + "cover";

            image = new Image(partialPath + ".jpg");
            if (!image.isError())
                break label;
            image = new Image(partialPath + ".png");
            if (!image.isError())
                break label;
            image = new Image(partialPath + ".bmp");
            if (!image.isError())
                break label;

            image = null;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        // If file exists, get its metadata
        var file = new File(path);
        if (file.exists()) {
            getMetadata(uri.toString());
        }
    }

    public URI getUri() {
        return uri;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public Image getImage() {
        return image;
    }



    private void getMetadata(String s) {
        var metadata = new Media(s).getMetadata();
        metadata.addListener((MapChangeListener<String, Object>) c -> {
            if (c.wasAdded()) {
                var key = c.getKey();
                var value = c.getValueAdded();

                // Update metadata fields based on keys
                switch (key) {
                    case "title":
                        title = (String) value;
                        break;
                    case "artist":
                        artist = (String) value;
                        break;
                    case "image":
                        image = (Image) value;
                }
            }
        });
    }
}
