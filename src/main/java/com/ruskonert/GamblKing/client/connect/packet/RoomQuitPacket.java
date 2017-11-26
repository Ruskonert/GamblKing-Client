package com.ruskonert.GamblKing.client.connect.packet;

import com.ruskonert.GamblKing.property.ServerProperty;

public class RoomQuitPacket extends ClientPacket
{
    private String playerName;
    public String getPlayerName() { return this.playerName; }

    private boolean isExit;
    public boolean isExited() { return isExit;}

    public RoomQuitPacket(String playerName)
    {
        super(ServerProperty.ROOM_QUIT);
        this.playerName = playerName;
        this.isExit = false;
    }

    public RoomQuitPacket(String playerName, boolean isExit)
    {
        super(ServerProperty.ROOM_QUIT);
        this.playerName = playerName;
        this.isExit = isExit;
    }
}
