package com.ruskonert.GamblKing.client.connect.packet;

import com.ruskonert.GamblKing.client.ClientLoader;
import com.ruskonert.GamblKing.connect.Packet;
import com.ruskonert.GamblKing.util.SystemUtil;
import javafx.scene.control.Alert;

public abstract class ClientPacket extends Packet
{
    public ClientPacket(int statusNumber)
    {
        super(statusNumber);
    }

    public void send()
    {
        try {
            super.send(ClientLoader.getBackgroundConnection().getOutputStream(), this);
        }
        catch(NullPointerException e)
        {
            SystemUtil.Companion.alert("오류", "서버가 닫혀있음", "메인 서버가 닫혀있어서 데이터를 연결할 수 없습니다.", Alert.AlertType.ERROR);
        }
    }

    public static void sendPacket(Packet packet)
    {
        try
        {
        packet.send(ClientLoader.getBackgroundConnection().getOutputStream(), packet);
        }
        catch(NullPointerException e)
        {
            SystemUtil.Companion.alert("오류", "서버가 닫혀있음", "메인 서버가 닫혀있어서 데이터를 연결할 수 없습니다.", Alert.AlertType.ERROR);
        }
    }
}
