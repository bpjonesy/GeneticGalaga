package edu.umsl;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundPlayer extends Thread {

    private AudioInputStream audioInputStream;

    private AudioListener listener = new AudioListener();

    public SoundPlayer(String soundFile) {
        try {
            URL url = this.getClass().getClassLoader().getResource(soundFile);
            audioInputStream = AudioSystem.getAudioInputStream(url);
        } catch (UnsupportedAudioFileException | IOException ex) {
            Logger.getLogger(SoundPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void play() throws LineUnavailableException, IOException {
        Clip clip = AudioSystem.getClip();
        clip.addLineListener(listener);
        clip.open(audioInputStream);
        try {
            clip.start();
            listener.waitUntilDone();
        } catch (InterruptedException ex) {
            Logger.getLogger(SoundPlayer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            clip.close();
        }
        audioInputStream.close();
    }

    @Override
    public void run() {
        try {
            this.play();
        } catch (IOException | LineUnavailableException ex) {
            Logger.getLogger(SoundPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class AudioListener implements LineListener {

        private boolean done = false;

        @Override
        public synchronized void update(LineEvent event) {
            Type eventType = event.getType();
            if (eventType == Type.STOP || eventType == Type.CLOSE) {
                done = true;
                notifyAll();
            }
        }

        public synchronized void waitUntilDone() throws InterruptedException {
            while (!done) {
                wait();
            }
        }
    }

}
