package com.ruskonert.GamblKing.client.event.layout;

import com.ruskonert.GamblKing.MessageType;
import com.ruskonert.GamblKing.client.ClientManager;
import com.ruskonert.GamblKing.client.connect.ClientConnectionReceiver;
import com.ruskonert.GamblKing.client.connect.packet.*;
import com.ruskonert.GamblKing.client.program.ClientProgramManager;
import com.ruskonert.GamblKing.client.program.component.GameComponent;
import com.ruskonert.GamblKing.entity.Player;
import com.ruskonert.GamblKing.entity.Room;
import com.ruskonert.GamblKing.event.LayoutListener;
import com.ruskonert.GamblKing.framework.PlayerEntityFramework;
import com.ruskonert.GamblKing.framework.RoomFramework;
import com.ruskonert.GamblKing.program.ConsoleSender;
import com.ruskonert.GamblKing.util.SystemUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;

public class GameLayoutEvent implements LayoutListener
{
    private static Thread systemTimeThread = null;
    public static Thread getSystemTimeThread() { return systemTimeThread; }

    @Override
    public void register(Object o)
    {
        ClientProgramManager.getGameComponent().TableRoomName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
        ClientProgramManager.getGameComponent().TableRoomStatus.setCellValueFactory(param -> new SimpleStringProperty("대기중"));
        ClientProgramManager.getGameComponent().TableRoomPlayerStat.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLeaderName()));
        ClientProgramManager.getGameComponent().TableRoomCreate.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getCreateDate()));
        ClientProgramManager.getGameComponent().TableCreateRoom.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2 )
            {
                // 플레이어가 클릭한 항목을 읽어옵니다.
                TableView<Room> roomTableView = ClientProgramManager.getGameComponent().TableCreateRoom;
                if(roomTableView.getSelectionModel().getSelectedItems().size() == 0) return;
                Room room = roomTableView.getSelectionModel().getSelectedItem();
                RoomFramework framework = (RoomFramework)room;
                PlayerEntityFramework player = ClientConnectionReceiver.getPlayerEntityFramework();

                // 이 플레이어는 그 방에 입장하는 것입니다.
                framework.setWaitPlayer(player.getNickname());
                player.setEnteredRoom((RoomFramework) room);

                // 이 정보를 서버로 업데이트합니다.
                PlayerRefreshPacket playerRefreshPacket = new PlayerRefreshPacket(player);
                playerRefreshPacket.send();

                // 방에 엑세스하는 패킷을 전송합니다.
                RoomAccessPacket packet = new RoomAccessPacket(room);
                packet.send();
            }
        });

        GameComponent component = ClientProgramManager.getGameComponent();
        threadInitialize();

        component.SendMessageButton.setOnMouseClicked(event -> { ClientManager.clickSound();sendRoomMessage(); } );

        component.MessageBox.setOnKeyPressed(event -> sendRoomMessage());

        component.RefreshRoomList.setOnMouseClicked(event -> {
            ClientManager.clickSound();
            RoomRefreshPacket refreshPacket = new RoomRefreshPacket(ClientManager.getPlayer().getId());
            refreshPacket.send();
        });

        component.CreateRoomButton.setOnMouseClicked(event -> {
            ClientManager.clickSound();
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
            ClientManager.clickSound();
            String message = component.MessageBox.getText();
            sendMessage(message);
        });

        component.MessageBox.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER)
            {
                String message = component.MessageBox.getText();
                sendMessage(message);
            }
        });

        component.OnlineButton.setOnMouseClicked(event -> {
            if(!component.OnlinePage.isVisible()) ClientManager.menuClickSound();
            component.HomePane.setVisible(false);
            component.HomeButton.setStyle("-fx-background-color:#95AFBF;");
            component.OnlinePage.setVisible(true);
            component.OnlineButton.setStyle("-fx-background-color:#BFBCBA;");
            component.SettingPage.setVisible(false);
            component.SettingsButton.setStyle("-fx-background-color:#95AFBF;");
        });

        component.HomeButton.setOnMouseClicked(event -> {
            if(!component.HomePane.isVisible()) ClientManager.menuClickSound();
            component.HomePane.setVisible(true);
            component.HomeButton.setStyle("-fx-background-color:#BFBCBA;");
            component.OnlinePage.setVisible(false);
            component.OnlineButton.setStyle("-fx-background-color:#95AFBF;");
            component.SettingPage.setVisible(false);
            component.SettingsButton.setStyle("-fx-background-color:#95AFBF");
        });

        component.SettingsButton.setOnMouseClicked(event -> {
            if(!component.SettingPage.isVisible()) ClientManager.menuClickSound();
            component.HomePane.setVisible(false);
            component.HomeButton.setStyle("-fx-background-color:#95AFBF;");
            component.OnlinePage.setVisible(false);
            component.OnlineButton.setStyle("-fx-background-color:#95AFBF");
            component.SettingPage.setVisible(true);
            component.SettingsButton.setStyle("-fx-background-color:#BFBCBA");
        });
    }

    private void sendRoomMessage() {
        GameComponent component = ClientProgramManager.getGameComponent();
        StringProperty p = component.ChatBox.textProperty();

        if(component.MessageBox.getText().isEmpty()) {
            p.setValue(p.getValue() + "\n" + "[ERROR] 보낼 메세지를 입력해주십시오.");
            return;
        }

        ClientPacket packet = new ClientPacket(1500) {

            String message = null;
            String id = null;
            String name = null;

            @Override
            public void send() {
                message = component.MessageBox.getText();
                id = ClientManager.getPlayer().getId();
                name = ClientManager.getPlayer().getNickname();
                super.send(ClientConnectionReceiver.getOutputStream(), this);
            }
        };

        packet.send();
    }


    private void sendMessage(String message) {
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
    }

    public static void threadInitialize()
    {
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
    }
}
