package com.ruskonert.GamblKing.client;

import com.ruskonert.GamblKing.client.connect.ClientConnectionReceiver;
import com.ruskonert.GamblKing.client.program.ClientProgramManager;
import com.ruskonert.GamblKing.framework.PlayerEntityFramework;
import com.ruskonert.GamblKing.program.ConsoleSender;
import com.ruskonert.GamblKing.util.SystemUtil;
import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public final class ClientManager
{
    private static ConsoleSender consoleSender;
    public static ConsoleSender getConsoleSender() { return consoleSender; }

    // Player 타입으로 사용 가능합니다.
    public static PlayerEntityFramework getPlayer() { return ClientConnectionReceiver.getPlayerEntityFramework(); }

    public static void clickSound()
    {
        if(!ClientProgramManager.getGameComponent().EnableSoundEffect.isDisable()) {
            EffectSound sound = new EffectSound();
            sound.start("click.mp3");
        }
    }

    public static void menuClickSound()
    {
        if(!ClientProgramManager.getGameComponent().EnableSoundEffect.isDisable()) {
            EffectSound sound = new EffectSound();
            sound.start("menu_click.mp3");
        }
    }

    public static void setMusic(String path)
    {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Media sound = new Media(SystemUtil.Companion.getStyleURL(path).toString());
                MediaPlayer mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.play();
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                while (true) {
                    if (isCancelled()) {
                        if (ClientLoader.getMusicThread().isInterrupted()) {
                            mediaPlayer.stop();
                            throw new InterruptedException();
                        }
                        else {
                            mediaPlayer.play();
                        }
                        mediaPlayer.play();
                    }
                    else
                    {
                        if (ClientLoader.getMusicThread().isInterrupted()) {
                            mediaPlayer.stop();
                            throw new InterruptedException();
                        }
                    }
                }

            }
        };

        ClientLoader.setMusicThread(null);

        ClientLoader.setMusicThread(new Thread(task));
        ClientLoader.getMusicThread().start();

    }

}

class EffectSound
{
    private String path = null;
    private Thread clickSound = null;
    private Task<Void> clickSoundTask = new Task<Void>() {
        @Override
        protected Void call() {
            Media sound = new Media(SystemUtil.Companion.getStyleURL(path).toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setVolume(0.5D);
            mediaPlayer.play();
            while(true) {
                if (mediaPlayer.getStopTime().toMillis() == mediaPlayer.getCurrentTime().toMillis()) {
                    mediaPlayer.stop();
                    clickSound.interrupt();
                    clickSound = null;
                    break;
                }
            }
            return null;
        }
    };

    public void start(String p)
    {
        this.path = p;
        Thread sound = new Thread(clickSoundTask);
        sound.start();
    }

}
