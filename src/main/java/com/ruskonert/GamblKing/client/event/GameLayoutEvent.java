package com.ruskonert.GamblKing.client.event;

import com.ruskonert.GamblKing.MessageType;
import com.ruskonert.GamblKing.client.ClientManager;
import com.ruskonert.GamblKing.client.connect.packet.RoomCreatePacket;
import com.ruskonert.GamblKing.client.connect.packet.ServerTimePacket;
import com.ruskonert.GamblKing.client.program.ClientProgramManager;
import com.ruskonert.GamblKing.client.program.component.GameComponent;
import com.ruskonert.GamblKing.entity.Player;
import com.ruskonert.GamblKing.event.LayoutListener;
import com.ruskonert.GamblKing.program.ConsoleSender;
import com.ruskonert.GamblKing.util.SystemUtil;
import javafx.concurrent.Task;
import javafx.scene.input.KeyCode;

public class GameLayoutEvent implements LayoutListener
{
    private static Thread systemTimeThread = null;
    public static Thread getSystemTimeThread() { return systemTimeThread; }

    @Override
    public void register(Object o)
    {
        GameComponent component = ClientProgramManager.getGameComponent();
        systemTimeThread = new Thread(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while(true)
                {
                    if(systemTimeThread.isInterrupted())
                    {
                        throw new InterruptedException();
                    }
                    try
                    {
                        new ServerTimePacket().send();
                    }
                    catch(NullPointerException e)
                    {
                        break;
                    }
                    Thread.sleep(1000L);
                }
                return null;
            }
        });

        component.CreateRoomButton.setOnMouseClicked(event -> {
            String name = component.CreateRoomName.getText();
            Player who = ClientManager.getPlayer();

            if(who.isEnteredRoom())
            {
                SystemUtil.Companion.alert("오류", "이미 방에 있음", "방에 입장한 상태에서는 방을 생성할 수 없습니다.");
                return;
            }

            RoomCreatePacket packet = new RoomCreatePacket(who, name);
            packet.send();
        });

        component.SendMessageButton.setOnMouseClicked(event -> {
            String message = component.MessageBox.getText();
            if(message.isEmpty())
            {
                ClientManager.getConsoleSender().sendMessage("메세지가 없습니다.", MessageType.ERROR);
            }
            else
                {
                ConsoleSender sender = ClientManager.getConsoleSender();
                sender.clearCommandField();
                sender.sendAll(message);
            }
        });

        component.MessageBox.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER)
            {
                String message = component.MessageBox.getText();
                if(message.isEmpty()) {
                    ClientManager.getConsoleSender().sendMessage("메세지가 없습니다.", MessageType.ERROR);
                }
                else
                    {
                    ConsoleSender sender = ClientManager.getConsoleSender();
                    sender.clearCommandField();

                    sender.sendAll(message);
                }
            }
        });

        component.OnlineButton.setOnMouseClicked(event -> {
            component.HomePane.setVisible(false);
            component.HomeButton.setStyle("-fx-background-color:#95AFBF;");
            component.OnlinePage.setVisible(true);
            component.OnlineButton.setStyle("-fx-background-color:#BFBCBA;");
            component.SettingPage.setVisible(false);
            component.SettingsButton.setStyle("-fx-background-color:#95AFBF;");
        });

        component.HomeButton.setOnMouseClicked(event -> {
            component.HomePane.setVisible(true);
            component.HomeButton.setStyle("-fx-background-color:#BFBCBA;");
            component.OnlinePage.setVisible(false);
            component.OnlineButton.setStyle("-fx-background-color:#95AFBF;");
            component.SettingPage.setVisible(false);
            component.SettingsButton.setStyle("-fx-background-color:#95AFBF");
        });

        component.SettingsButton.setOnMouseClicked(event -> {
            component.HomePane.setVisible(false);
            component.HomeButton.setStyle("-fx-background-color:#95AFBF;");
            component.OnlinePage.setVisible(false);
            component.OnlineButton.setStyle("-fx-background-color:#95AFBF");
            component.SettingPage.setVisible(true);
            component.SettingsButton.setStyle("-fx-background-color:#BFBCBA");
        });
    }

}
