module icu.BenEide.MusicPlayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens icu.BenEide.MusicPlayer to javafx.fxml;
    exports icu.BenEide.MusicPlayer;
}