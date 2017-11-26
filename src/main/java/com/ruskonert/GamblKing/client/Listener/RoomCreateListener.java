package com.ruskonert.GamblKing.client.Listener;

import com.ruskonert.GamblKing.client.ClientLoader;
import com.ruskonert.GamblKing.client.connect.ClientConnectionReceiver;
import com.ruskonert.GamblKing.client.connect.packet.RoomUpdatePacket;
import com.ruskonert.GamblKing.client.event.RoomCreateEvent;
import com.ruskonert.GamblKing.client.program.ClientProgramManager;
import com.ruskonert.GamblKing.client.program.component.RoomComponent;
import com.ruskonert.GamblKing.event.EventListener;
import com.ruskonert.GamblKing.event.Handle;
import com.ruskonert.GamblKing.framework.PlayerEntityFramework;
import javafx.application.Platform;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RoomCreateListener implements EventListener
{
    @Handle
    public void onCreate(RoomCreateEvent event)
    {
        Platform.runLater(() -> {
            RoomComponent component = ClientProgramManager.getRoomComponent();
                    component.RoomName.setText(event.getRoom().getName());
                    component.RoomNumber.setText("정보없음");
                    component.PlayerId.setText(event.getPlayer().getId());
                    component.PlayerName.setText(event.getPlayer().getNickname());
                    String date = new SimpleDateFormat("연결 일자: yyyy-MM-dd hh:mm:ss").format(new Date());
                    component.CreateTime.setText(date);
                    RoomUpdatePacket packet = new RoomUpdatePacket(event.getRoom());
                    packet.send();
                }
        );
    }

}
