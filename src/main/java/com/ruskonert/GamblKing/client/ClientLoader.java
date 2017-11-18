package com.ruskonert.GamblKing.client;

import com.ruskonert.GamblKing.ProgramInitializable;
import com.ruskonert.GamblKing.client.connect.ClientConnectionReceiver;
import com.ruskonert.GamblKing.client.connect.ClientUpdateConnection;
import com.ruskonert.GamblKing.client.connect.UpdateConnectionReceiver;
import com.ruskonert.GamblKing.client.event.ClientLayoutEvent;
import com.ruskonert.GamblKing.event.EventListener;
import com.ruskonert.GamblKing.event.LayoutListener;
import com.ruskonert.GamblKing.program.Register;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ClientLoader extends Application implements ProgramInitializable, Register
{
    private static Stage stage;
    public static Stage getStage() { return stage; }

    private static ClientConnectionReceiver backgroundConnection;
    public static ClientConnectionReceiver getBackgroundConnection() { return backgroundConnection; }
    public static void setBackgroundConnection(ClientConnectionReceiver backgroundConnection)
    {
        ClientLoader.backgroundConnection = backgroundConnection;
    }


    private static UpdateConnectionReceiver updateConnectionReceiver;
    public static void setupdateConnection(UpdateConnectionReceiver updateConnection)
    {
        ClientLoader.updateConnectionReceiver = updateConnection;
    }
    public static UpdateConnectionReceiver getUpdateConnectionReceiver() { return updateConnectionReceiver; }


    public static void main(String[] args)
    {
        try {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Media sound = new Media(getClass().getResource("/style/intro.mp3").toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(sound);
                    mediaPlayer.play();
                    mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                    while (true) {
                        if (isCancelled()) {
                            mediaPlayer.play();
                        }
                    }

                }
            };
            Thread t = new Thread(task);
            t.start();
            launch(args);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void updateDatabase() { ClientUpdateConnection.update(); }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/style/client_login.fxml"));
        System.out.println(getClass().getResource("/style/client_login.fxml"));
        primaryStage.setTitle("GamblKing Launcher");
        primaryStage.setScene(new Scene((Parent) loader.load(), 1150,534));
        primaryStage.setResizable(true);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        this.registerEvent(new ClientLayoutEvent());

        stage = primaryStage;


        primaryStage.show();
    }

    @Override
    public boolean initialize(Object handleInstance)
    {
        return true;
    }

    @Override
    public void registerEvent(LayoutListener listener)
    {
        listener.register(this);
    }

    @Override
    public void registerEvent(EventListener listener)
    {
        // No needed to register the events.
    }
}