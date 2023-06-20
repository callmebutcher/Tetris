package com.callmebutcher.tetris;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer {

    URL clearLinePath = this.getClass().getResource("sounds/line.wav");
    URL gameOverPath = this.getClass().getResource("sounds/gameOver.wav");
    URL titlePath = this.getClass().getResource("sounds/title.wav");

    private Clip clearLineSound;
    private Clip gameOverSound;
    private static Clip titleSound;

    private static boolean musicStatus = false;
    private static boolean isPlaying = true;

    public AudioPlayer() {
        try {
            clearLineSound = AudioSystem.getClip();
            gameOverSound = AudioSystem.getClip();
            titleSound = AudioSystem.getClip();

            titleSound.open(AudioSystem.getAudioInputStream(titlePath));
            clearLineSound.open(AudioSystem.getAudioInputStream(clearLinePath));
            gameOverSound.open(AudioSystem.getAudioInputStream(gameOverPath));

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    public void playClearLine() {
        clearLineSound.setFramePosition(0);
        clearLineSound.start();
    }

    public void playGameOver() {
        gameOverSound.setFramePosition(0);
        gameOverSound.start();
    }

    public static void playTitle() {
        musicStatus = true;
        titleSound.loop(Clip.LOOP_CONTINUOUSLY);
        titleSound.start();
    }

    public static void stopTitle() {
        titleSound.stop();
        musicStatus = false;
        titleSound.setFramePosition(0);
    }

    public static boolean getMusicStatus() {
        return musicStatus;
    }

    public static boolean isPlaying() {
        return isPlaying;
    }

    public static void setMusicStatus(boolean status) {
        musicStatus = status;
    }
}