package com.example.pong;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Sound {
    private final Clip clip;

    public Sound(String resourcePath) {
        Clip loadedClip = null;
        try (AudioInputStream audioIn = openAudioStream(resourcePath)) {
            if (audioIn != null) {
                loadedClip = AudioSystem.getClip();
                loadedClip.open(audioIn);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Sound could not be loaded: " + resourcePath + " (" + e.getMessage() + ")");
        }
        clip = loadedClip;
    }

    private AudioInputStream openAudioStream(String resourcePath) throws IOException, UnsupportedAudioFileException {
        String normalizedPath = normalizeResourcePath(resourcePath);

        InputStream classpathStream = Sound.class.getResourceAsStream(normalizedPath);
        if (classpathStream != null) {
            return AudioSystem.getAudioInputStream(new BufferedInputStream(classpathStream));
        }

        for (Path candidate : getFileCandidates(normalizedPath)) {
            if (Files.isRegularFile(candidate)) {
                return AudioSystem.getAudioInputStream(new BufferedInputStream(Files.newInputStream(candidate)));
            }
        }

        throw new IOException("Resource not found: " + resourcePath);
    }

    private String normalizeResourcePath(String resourcePath) {
        if (resourcePath == null || resourcePath.isBlank()) {
            throw new IllegalArgumentException("resourcePath should not be null or blank");
        }

        return resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;
    }

    private List<Path> getFileCandidates(String normalizedPath) {
        String relativePath = normalizedPath.substring(1).replace('/', java.io.File.separatorChar);
        List<Path> candidates = new ArrayList<>(List.of(
                Path.of(relativePath),
                Path.of("..", relativePath),
                Path.of("..", "..", relativePath),
                Path.of("..", "..", "..", relativePath)
        ));

        addCodeSourceCandidates(candidates, relativePath);
        return candidates;
    }

    private void addCodeSourceCandidates(List<Path> candidates, String relativePath) {
        try {
            Path codeSourcePath = Path.of(Sound.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path basePath = Files.isDirectory(codeSourcePath) ? codeSourcePath : codeSourcePath.getParent();

            for (int i = 0; i < 5 && basePath != null; i++) {
                candidates.add(basePath.resolve(relativePath));
                basePath = basePath.getParent();
            }
        } catch (URISyntaxException | NullPointerException ignored) {
            // Fallback candidates above are still available.
        }
    }

    public void play() {
        if (clip == null) return;
        if (clip.isRunning()) clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }
}