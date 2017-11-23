package com.ruskonert.GamblKing.client.program;

import com.ruskonert.GamblKing.client.ClientLoader;
import com.ruskonert.GamblKing.client.connect.ClientConnectionReceiver;
import com.ruskonert.GamblKing.client.connect.UpdateConnectionReceiver;
import com.ruskonert.GamblKing.client.event.GameLayoutEvent;
import com.ruskonert.GamblKing.client.framework.ConsoleFramework;
import com.ruskonert.GamblKing.entity.Player;
import com.ruskonert.GamblKing.program.StageBuilder;
import com.ruskonert.GamblKing.util.SystemUtil;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.IOException;

public class GameApplication extends StageBuilder
{
    public GameApplication()
    {

    }

    private static GameApplication gameApplication = null;
    public static GameApplication getGameApplication() { return gameApplication; }

    private static  Stage stage;
    public static Stage getStage() { return stage; }

    @Override
    public void start(Stage stage)
    {
        try
        {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Media sound = new Media(SystemUtil.Companion.getStyleURL("MainStage.mp3").toString());
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

            ClientLoader.setMusicThread(null);

            FXMLLoader loader = new FXMLLoader(SystemUtil.Companion.getStyleURL("game.fxml"));
            Parent parent = null;
            try {
                parent = loader.load();
            }
            catch(OutOfMemoryError e)
            {
                throw new RuntimeException("Java 가상 메모리 사용량이 초과되었습니다. " +
                        "-XmX 옵션값을 이용해 heap을 더 확보하여 주십시오.");
            }
            stage.setScene(new Scene(parent, 1066, 700));
            stage.setTitle("GamblKing Launcher");
            stage.setResizable(false);
            GameApplication.stage = stage;
            gameApplication = this;

            this.registerEvent(new GameLayoutEvent());
            ConsoleFramework.initialize();
            GameLayoutEvent.getSystemTimeThread().start();

            stage.show();

            Player player = ClientConnectionReceiver.getPlayerEntityFramework();

            ClientProgramManager.getGameComponent().ConnectedIP.setText("접속된 IP: " + (player.getHostAddress() == null ? "로컬 호스트" : player.getHostAddress()));
            ClientProgramManager.getGameComponent().LastConnected.setText("마지막 접속 날짜: " + player.getLastConnected());
            ClientProgramManager.getGameComponent().Username.setText(player.getNickname() + "님 (" + player.getId() + ")");
            ClientProgramManager.getGameComponent().LastBattle.setText("마지막으로 싸운 상대: " + (player.getLastBattlePlayer() == null ? "정보없음" : player.getLastBattlePlayer()));
            ClientProgramManager.getGameComponent().UserScore.setText(String.format("전적: %d승 %d패 (%3.1f%%)", player.getVictory(), player.getDefeated(),
                    (double)player.getVictory() / (player.getDefeated() + player.getVictory()) ==0 ? 1.0 : (player.getDefeated() + player.getVictory())));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}