import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.JavaSoundAudioDevice;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BackgroundMusicPlayer implements AutoCloseable {
    private static final float DEFAULT_MUSIC_VOLUME = 0.25f;
    private static final float VOLUME_STEP = 0.05f;
    private static final float MIN_VOLUME = 0f;
    private static final float MAX_VOLUME = 1f;

    private final String resourcePath;
    private final Object stateLock = new Object();
    private final float millisecondsPerFrame;

    private volatile boolean shutdown;
    private volatile boolean muted;
    private volatile boolean paused;
    private volatile float musicVolume = DEFAULT_MUSIC_VOLUME;
    private AdvancedPlayer currentPlayer;
    private Thread playbackThread;
    private int resumeFrame;
    private int currentStartFrame;
    private boolean playbackInterrupted;
    private long playbackStartNanos;

    public BackgroundMusicPlayer(String resourcePath) {
        this.resourcePath = normalizeResourcePath(resourcePath);
        this.millisecondsPerFrame = detectMillisecondsPerFrame();
    }

    public void playLoop() {
        muted = false;
        paused = false;

        synchronized (stateLock) {
            if (shutdown) {
                return;
            }

            if (playbackThread == null || !playbackThread.isAlive()) {
                playbackThread = new Thread(this::runPlaybackLoop, "pong-background-music");
                playbackThread.setDaemon(true);
                playbackThread.start();
            }

            stateLock.notifyAll();
        }
    }

    public void setPaused(boolean paused) {
        this.paused = paused;

        if (paused) {
            stopCurrentPlayer();
            return;
        }

        synchronized (stateLock) {
            stateLock.notifyAll();
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean toggleMuted() {
        setMuted(!muted);
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;

        if (muted) {
            stopCurrentPlayer();
            return;
        }

        synchronized (stateLock) {
            stateLock.notifyAll();
        }
    }

    public boolean isMuted() {
        return muted;
    }

    public void decreaseVolume() {
        setVolume(musicVolume - VOLUME_STEP);
    }

    public void increaseVolume() {
        setVolume(musicVolume + VOLUME_STEP);
    }

    public float getVolume() {
        return musicVolume;
    }

    private void setVolume(float volume) {
        musicVolume = Math.max(MIN_VOLUME, Math.min(MAX_VOLUME, volume));
    }

    @Override
    public void close() {
        shutdown = true;
        muted = true;
        paused = true;
        stopCurrentPlayer();

        synchronized (stateLock) {
            stateLock.notifyAll();
        }

        if (playbackThread != null) {
            try {
                playbackThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void runPlaybackLoop() {
        while (!shutdown) {
            waitUntilPlaybackAllowed();
            if (shutdown) {
                return;
            }

            try (InputStream inputStream = openStream()) {
                int startFrame;
                synchronized (stateLock) {
                    playbackInterrupted = false;
                    startFrame = resumeFrame;
                    currentStartFrame = startFrame;
                    playbackStartNanos = System.nanoTime();
                }

                AdvancedPlayer player = new AdvancedPlayer(
                        new BufferedInputStream(inputStream),
                        new QuietAudioDevice()
                );
                player.setPlayBackListener(new PlaybackListener() {
                    @Override
                    public void playbackFinished(javazoom.jl.player.advanced.PlaybackEvent event) {
                        handlePlaybackFinished();
                    }
                });

                synchronized (stateLock) {
                    currentPlayer = player;
                }

                player.play(startFrame, Integer.MAX_VALUE);
            } catch (IOException | JavaLayerException e) {
                System.err.println("Background music could not be played: " + resourcePath + " (" + e.getMessage() + ")");
                shutdown = true;
            } finally {
                synchronized (stateLock) {
                    currentPlayer = null;
                    playbackStartNanos = 0;
                    playbackInterrupted = false;
                    stateLock.notifyAll();
                }
            }
        }
    }

    private void handlePlaybackFinished() {
        synchronized (stateLock) {
            if (!shutdown && !playbackInterrupted && !muted && !paused) {
                resumeFrame = 0;
            }
        }
    }

    private void waitUntilPlaybackAllowed() {
        synchronized (stateLock) {
            while (!shutdown && (muted || paused)) {
                try {
                    stateLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    shutdown = true;
                    return;
                }
            }
        }
    }

    private void stopCurrentPlayer() {
        synchronized (stateLock) {
            if (currentPlayer != null) {
                playbackInterrupted = true;
                updateResumeFrameFromElapsedPlayback();
                currentPlayer.stop();

                while (currentPlayer != null && !shutdown) {
                    try {
                        stateLock.wait(200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        shutdown = true;
                        return;
                    }
                }
            }
        }
    }

    private void updateResumeFrameFromElapsedPlayback() {
        if (millisecondsPerFrame <= 0 || playbackStartNanos == 0) {
            return;
        }

        double elapsedMilliseconds = (System.nanoTime() - playbackStartNanos) / 1_000_000.0;
        int elapsedFrames = (int) Math.max(0, Math.floor(elapsedMilliseconds / millisecondsPerFrame));
        resumeFrame = Math.max(resumeFrame, currentStartFrame + elapsedFrames);
    }

    private InputStream openStream() throws IOException {
        InputStream classpathStream = BackgroundMusicPlayer.class.getResourceAsStream(resourcePath);
        if (classpathStream != null) {
            return classpathStream;
        }

        for (Path candidate : getFileCandidates(resourcePath)) {
            if (Files.isRegularFile(candidate)) {
                return Files.newInputStream(candidate);
            }
        }

        throw new IOException("Resource not found");
    }

    private float detectMillisecondsPerFrame() {
        Bitstream bitstream = null;

        try (InputStream inputStream = openStream()) {
            bitstream = new Bitstream(new BufferedInputStream(inputStream));
            Header header = bitstream.readFrame();
            if (header != null) {
                return header.ms_per_frame();
            }
        } catch (IOException | BitstreamException e) {
            System.err.println("Background music metadata could not be read: " + resourcePath + " (" + e.getMessage() + ")");
        } finally {
            if (bitstream != null) {
                try {
                    bitstream.close();
                } catch (BitstreamException ignored) {
                    // Ignore close errors during metadata detection.
                }
            }
        }

        return 26f;
    }

    private String normalizeResourcePath(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("resourcePath should not be null or blank");
        }

        return value.startsWith("/") ? value : "/" + value;
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
            Path codeSourcePath = Path.of(BackgroundMusicPlayer.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path basePath = Files.isDirectory(codeSourcePath) ? codeSourcePath : codeSourcePath.getParent();

            for (int i = 0; i < 5 && basePath != null; i++) {
                candidates.add(basePath.resolve(relativePath));
                basePath = basePath.getParent();
            }
        } catch (URISyntaxException | NullPointerException ignored) {
            // Fallback candidates above are still available.
        }
    }

    private final class QuietAudioDevice extends JavaSoundAudioDevice {

        @Override
        protected void writeImpl(short[] samples, int offs, int len) throws JavaLayerException {
            float volumeFactor = musicVolume;
            if (volumeFactor >= 0.999f) {
                super.writeImpl(samples, offs, len);
                return;
            }

            short[] adjustedSamples = new short[len];
            for (int i = 0; i < len; i++) {
                adjustedSamples[i] = (short) Math.round(samples[offs + i] * volumeFactor);
            }

            super.writeImpl(adjustedSamples, 0, len);
        }
    }
}

