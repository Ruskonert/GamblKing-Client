package com.ruskonert.GamblKing.client.connect.packet;

import com.ruskonert.GamblKing.property.ServerProperty;

public class RoomRefreshPacket extends ClientPacket
{
    private String username;
    public String getUsername() { return username; }

    public RoomRefreshPacket(String username)
    {
        super(ServerProperty.ROOM_REFRESH);
        this.username = username;
    }
}
