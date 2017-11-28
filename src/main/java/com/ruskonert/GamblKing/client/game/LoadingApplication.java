package com.ruskonert.GamblKing.client.game;

import com.ruskonert.GamblKing.program.StageBuilder;
import com.ruskonert.GamblKing.util.SystemUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class LoadingApplication extends StageBuilder
{
    private static LoadingApplication loadingApplication = null;
    public static LoadingApplication getLoadingApplication() { return loadingApplication; }

    private static Stage stage;
    public static Stage getStage() { return stage; }

    @Override
    public void start(Stage stage) {
        try
        {
            FXMLLoader loader = new FXMLLoader(SystemUtil.Companion.getStyleURL("wait.fxml"));
            Parent parent;
            try
            {
                parent = loader.load();
            }
            catch(OutOfMemoryError e)
            {
                throw new RuntimeException("Java 가상 메모리 사용량이 초과되었습니다. " + "-XmX 옵션값을 이용해 heap을 더 확보하여 주십시오.");
            }
            stage.setScene(new Scene(parent, 539, 400));
            stage.setTitle("Waiting for other player");
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            LoadingApplication.stage = stage;
            loadingApplication = this;
            stage.show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

