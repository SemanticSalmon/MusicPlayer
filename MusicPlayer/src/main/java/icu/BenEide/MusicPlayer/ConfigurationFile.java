package icu.BenEide.MusicPlayer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static java.lang.Double.parseDouble;

public class ConfigurationFile {
    // Singleton instance, we only want one instance of this class
    private static ConfigurationFile INSTANCE = null;
    // Config file location, where we store our settings
    private static final File CONFIG_FILE = new File("etc", "config.txt");

    private double volume;
    private Mode mode;

    // Get the singleton instance
    public static ConfigurationFile getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConfigurationFile();
        }
        return INSTANCE;
    }

    // Update the config file with the current properties
    private void updateConfigFile() {
        try (var bufferedWriter = new BufferedWriter(new FileWriter(CONFIG_FILE))) {
            // Write the current volume to the config file
            bufferedWriter.write("volume=" + volume);
            bufferedWriter.newLine();
            // Write the current mode to the config file
            bufferedWriter.write("mode=" + mode);
            bufferedWriter.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Set a new volume and update the config file
    public void setVolume(double volume) {
        this.volume = volume;
        updateConfigFile();
    }

    // Get the current volume setting
    public double getVolume() {
        return volume;
    }

    // Private constructor to prevent external instantiation
    private ConfigurationFile() {
        var properties = new Properties();
        try {
            // Ensure the etc directory and config file exist
            var etcDirectory = new File("etc");
            if (!CONFIG_FILE.exists()) {
                // If the config file doesn't exist, create it and set default values
                if (!etcDirectory.mkdir() && !CONFIG_FILE.createNewFile()) {
                    throw new IOException("Cannot create config file");
                }
                // We save these values because we don't want to blast the end user with 100% volume if the last time they
                // used the app it was at 20% volume. I added this because that end user was me and I have loud headphones
                volume = 1.0;
                mode = Mode.DEFAULT;
                updateConfigFile();
                return;
            }
            // Load properties from the config file
            var bufferedReader = new BufferedReader(new FileReader(CONFIG_FILE, StandardCharsets.UTF_8));
            properties.load(bufferedReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Parse volume property from the config file
        volume = parseDouble(properties.getProperty("volume"));
        // Parse mode property from the config file
        switch (properties.getProperty("mode")) {
            case "DEFAULT":
                mode = Mode.DEFAULT;
                break;
            case "REPEAT_ALL":
                mode = Mode.REPEAT_ALL;
                break;
            case "REPEAT_ONE":
                mode = Mode.REPEAT_ONE;
                break;
            case "SHUFFLE":
                mode = Mode.SHUFFLE;
                break;
            default:
                // If no valid mode is found, set to default mode
                mode = Mode.DEFAULT;
                break;
        }
    }
}
