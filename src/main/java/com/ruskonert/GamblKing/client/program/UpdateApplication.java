package com.ruskonert.GamblKing.client.program;

import com.ruskonert.GamblKing.client.connect.packet.ClientUpdatePacket;
import com.ruskonert.GamblKing.program.StageBuilder;
import com.ruskonert.GamblKing.util.SystemUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UpdateApplication extends StageBuilder
{
    private static UpdateApplication updateApplication = null;
    public static UpdateApplication getUpdateApplication() { return updateApplication; }

    private static Stage stage;
    public static Stage getStage() { return stage; }

    public void close()
    {
        stage.close();
    }

    @Override
    public void start(Stage stage)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(SystemUtil.Companion.getStyleURL("update.fxml"));
            Parent parent = loader.load();
            stage.setScene(new Scene(parent, 600, 200));
            stage.setTitle("Update");

            stage.setResizable(false);

            updateApplication = this;
            UpdateApplication.stage = stage;

            // 업데이트 서버 요청을 보냅니다.
            ClientUpdatePacket packet = new ClientUpdatePacket();
            packet.send();

            stage.show();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
