package com.ruskonert.GamblKing.client.connect.packet;

import com.ruskonert.GamblKing.property.ServerProperty;

public class ServerTimePacket extends ClientPacket
{
    public ServerTimePacket()
    {
        super(ServerProperty.SERVER_TIME_REQUEST);
    }
}
