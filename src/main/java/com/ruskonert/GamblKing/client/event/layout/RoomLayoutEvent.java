package com.ruskonert.GamblKing.client.event.layout;

import com.ruskonert.GamblKing.client.ClientManager;
import com.ruskonert.GamblKing.client.event.RoomQuitEvent;
import com.ruskonert.GamblKing.client.program.ClientProgramManager;
import com.ruskonert.GamblKing.client.program.component.RoomComponent;
import com.ruskonert.GamblKing.entity.Player;
import com.ruskonert.GamblKing.event.LayoutListener;

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
    }
}
