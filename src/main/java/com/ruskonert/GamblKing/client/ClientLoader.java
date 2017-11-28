package com.ruskonert.GamblKing.client;

import com.ruskonert.GamblKing.client.Listener.RoomCreateListener;
import com.ruskonert.GamblKing.client.Listener.RoomQuitListener;
import com.ruskonert.GamblKing.client.Listener.RoomUpdateListener;
import com.ruskonert.GamblKing.client.connect.ClientConnectionReceiver;
import com.ruskonert.GamblKing.client.connect.UpdateConnectionReceiver;
import com.ruskonert.GamblKing.client.connect.packet.ClientUpdatePacket;
import com.ruskonert.GamblKing.client.event.layout.ClientLayoutEvent;
import com.ruskonert.GamblKing.client.program.UpdateApplication;
import com.ruskonert.GamblKing.event.EventController;
import com.ruskonert.GamblKing.event.EventListener;
import com.ruskonert.GamblKing.event.LayoutListener;
import com.ruskonert.GamblKing.program.Register;
import com.ruskonert.GamblKing.ProgramInitializable;

import com.ruskonert.GamblKing.util.SystemUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ClientLoader extends Application implements ProgramInitializable, Register
{
    private static Thread musicThread;
    public static Thread getMusicThread() { return musicThread; }
    public static void setMusicThread(Thread t) { musicThread = t; }

    private static ClientConnectionReceiver backgroundConnection;
    public static ClientConnectionReceiver getBackgroundConnection() { return backgroundConnection; }
    public static void setBackgroundConnection(ClientConnectionReceiver backgroundConnection)
    {
        ClientLoader.backgroundConnection = backgroundConnection;
    }

    private static UpdateConnectionReceiver updateConnectionReceiver;
    public static void setUpdateConnection(UpdateConnectionReceiver updateConnection)
    {
        if(updateConnection == null)
        {
            Platform.runLater(() -> {
                if (UpdateApplication.getStage() != null) {
                    UpdateApplication.getStage().hide();
                } });
        }
        ClientLoader.updateConnectionReceiver = updateConnection;
    }
    public static UpdateConnectionReceiver getUpdateConnectionReceiver() { return updateConnectionReceiver; }

    public static void main(String[] args)
    {
        try {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Media sound = new Media(SystemUtil.Companion.getStyleURL("intro.mp3").toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(sound);
                    mediaPlayer.play();
                    mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                    while (true)
                    {
                        if (isCancelled()) {
                            if (musicThread.isInterrupted()) {
                                mediaPlayer.stop();
                                throw new InterruptedException();
                            }
                            else {
                                mediaPlayer.play();
                            }
                        }
                        else
                        {
                            if (musicThread.isInterrupted())
                            {
                                mediaPlayer.stop();
                                throw new InterruptedException();
                            }
                        }
                    }
                }
            };
            musicThread = new Thread(task);
            musicThread.start();
            launch(args);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void updateDatabase() { ClientUpdatePacket.update(); }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Platform.setImplicitExit(false);
        FXMLLoader loader = new FXMLLoader(SystemUtil.Companion.getStyleURL("client_login.fxml"));
        primaryStage.setTitle("GamblKing Launcher");
        primaryStage.setScene(new Scene(loader.load(), 1150,534));
        primaryStage.setResizable(true);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        this.registerEvent(new ClientLayoutEvent());
        this.registerEvent(new RoomCreateListener());
        this.registerEvent(new RoomUpdateListener());
        this.registerEvent(new RoomQuitListener());

        stage = primaryStage;

        primaryStage.show();
    }

    private static Stage stage;
    public static Stage getStage() { return stage; }

    @Override public boolean initialize(Object handleInstance)
    {
        return true;
    }

    @Override public void registerEvent(LayoutListener listener)
    {
        listener.register(this);
    }

    @Override public void registerEvent(EventListener listener)
    {
        EventController.signatureListener(listener);
    }
}