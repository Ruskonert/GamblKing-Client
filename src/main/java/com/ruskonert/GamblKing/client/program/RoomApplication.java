package com.ruskonert.GamblKing.client.program;

import com.ruskonert.GamblKing.client.ClientLoader;
import com.ruskonert.GamblKing.client.ClientManager;
import com.ruskonert.GamblKing.client.event.layout.RoomLayoutEvent;
import com.ruskonert.GamblKing.program.StageBuilder;
import com.ruskonert.GamblKing.util.SystemUtil;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class RoomApplication extends StageBuilder
{
    private static RoomApplication roomApplication = null;
    public static RoomApplication getRoomApplication() { return roomApplication; }

    private static  Stage stage;
    public static Stage getStage() { return stage; }

    public static void setStage(Stage stage) { RoomApplication.stage = stage; }

    public void close()
    {
        stage.close();
    }

    @Override
    public void start(Stage stage) {
        try
        {
            Platform.runLater(() -> {
                        ClientLoader.getMusicThread().interrupt();
                        ClientManager.setMusic("ready.mp3"); });

            FXMLLoader loader = new FXMLLoader(SystemUtil.Companion.getStyleURL("room.fxml"));
            Parent parent = loader.load();
            stage.setScene(new Scene(parent, 600, 400));
            stage.setTitle("대기실");
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            RoomApplication.stage = stage;
            roomApplication = this;
            this.registerEvent(new RoomLayoutEvent());
            Platform.runLater(() -> {
                GameApplication.getStage().close();
                stage.show();
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
