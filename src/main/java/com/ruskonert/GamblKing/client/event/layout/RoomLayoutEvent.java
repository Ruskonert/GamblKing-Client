package com.ruskonert.GamblKing.client.event.layout;

import com.ruskonert.GamblKing.client.ClientLoader;
import com.ruskonert.GamblKing.client.ClientManager;
import com.ruskonert.GamblKing.client.connect.ClientConnectionReceiver;
import com.ruskonert.GamblKing.client.connect.packet.GameStartPacket;
import com.ruskonert.GamblKing.client.event.RoomQuitEvent;
import com.ruskonert.GamblKing.client.game.connect.GameServerConnection;
import com.ruskonert.GamblKing.client.program.ClientProgramManager;
import com.ruskonert.GamblKing.client.program.RoomApplication;
import com.ruskonert.GamblKing.client.program.component.RoomComponent;
import com.ruskonert.GamblKing.entity.Player;
import com.ruskonert.GamblKing.event.LayoutListener;
import javafx.application.Platform;

public class RoomLayoutEvent implements LayoutListener
{
    @Override
    public void register(Object handleInstance)
    {
        RoomComponent component = ClientProgramManager.getRoomComponent();

        component.QuitButton.setOnMouseClicked(event -> {
            Player player = ClientManager.getPlayer();

            RoomQuitEvent event2 = new RoomQuitEvent(player.getId(), ClientManager.getPlayer().getEnteredRoom().getWaitPlayerName(), ClientManager.getPlayer().getEnteredRoom());
            if(event2.isCanceled()) return;
            event2.start();
        });

        component.StartButton.setOnMouseClicked(event -> {
            ClientConnectionReceiver.setGameServerConnection(new GameServerConnection());
            Platform.runLater(() -> {
                RoomApplication.getStage().close();
                ClientLoader.getMusicThread().interrupt();
                ClientConnectionReceiver.getGameServerConnection().createRoom();
                GameStartPacket packet  = new GameStartPacket(ClientConnectionReceiver.getSocket().getLocalAddress().getHostAddress(),
                        ClientManager.getPlayer().getEnteredRoom().getWaitPlayerName());
                packet.send();
            });
        });
    }
}
