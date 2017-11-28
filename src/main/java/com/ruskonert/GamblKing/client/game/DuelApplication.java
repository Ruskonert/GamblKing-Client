package com.ruskonert.GamblKing.client.game;

import com.ruskonert.GamblKing.client.ClientManager;
import com.ruskonert.GamblKing.client.framework.ConsoleFramework;
import com.ruskonert.GamblKing.client.game.event.DuelLayoutEvent;
import com.ruskonert.GamblKing.client.program.ClientProgramManager;
import com.ruskonert.GamblKing.program.StageBuilder;
import com.ruskonert.GamblKing.util.SystemUtil;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;

public class DuelApplication extends StageBuilder
{
    private static DuelApplication duelApplication = null;
    public static DuelApplication getDuelApplication() { return duelApplication; }

    private static Stage stage;
    public static Stage getStage() { return stage; }

    @Override
    public void start(Stage stage) {
        try
        {
            FXMLLoader loader = new FXMLLoader(SystemUtil.Companion.getStyleURL("duel.fxml"));
            Parent parent = null;
            try {
                parent = loader.load();
            }
            catch(OutOfMemoryError e)
            {
                throw new RuntimeException("Java 가상 메모리 사용량이 초과되었습니다. " +
                        "-XmX 옵션값을 이용해 heap을 더 확보하여 주십시오.");
            }
            ClientProgramManager.getDuelComponent().DuelField.setOpacity(0.0D);
            stage.setScene(new Scene(parent, 1260, 775));
            stage.setTitle("GamblKing Game Launcher");
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            DuelApplication.stage = stage;
            duelApplication = this;
            this.registerEvent(new DuelLayoutEvent());
            ConsoleFramework.initialize();
            stage.show();

            // 게임 화면을 서서히 보여줍니다.
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(1000L);
                    double d = 0.0D;
                    while(d <= 1.0d)
                    {
                        ClientProgramManager.getDuelComponent().DuelField.setOpacity(d);
                        d += 0.01D;
                        Thread.sleep(200L);
                    }
                    ClientManager.setMusic("game.mp3");
                    return null;
                }
            };
            Thread r = new Thread(task);
            r.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
