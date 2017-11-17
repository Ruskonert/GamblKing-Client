package com.ruskonert.GamblKing.client.connect.packet;

import com.ruskonert.GamblKing.property.ServerProperty;

public class ClientGamePacket extends ClientPacket
{
    public ClientGamePacket()
    {
        super(ServerProperty.CONNECT_GAME_SERVER);
    }
}
