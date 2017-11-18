package com.ruskonert.GamblKing.client.connect.packet;

import com.ruskonert.GamblKing.property.ServerProperty;

public class ClientGamePacket extends ClientPacket
{
    private String id;
    public String getId() { return this.id; }

    public ClientGamePacket(String id)
    {
        super(ServerProperty.CONNECT_GAME_SERVER);
        this.id = id;
    }
}
