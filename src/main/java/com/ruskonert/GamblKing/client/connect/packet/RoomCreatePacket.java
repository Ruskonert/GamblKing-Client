package com.ruskonert.GamblKing.client.connect.packet;

import com.ruskonert.GamblKing.adapter.PlayerAdapter;
import com.ruskonert.GamblKing.entity.Player;
import com.ruskonert.GamblKing.property.ServerProperty;

public class RoomCreatePacket extends ClientPacket
{
    private String name;
    public String getName() {
        return name;
    }

    private Player player;
    public Player getPlayer() {
        return player;
    }

    public RoomCreatePacket(Player who, String name)
    {
        super(ServerProperty.ROOM_CREATE);
        this.name = name;
        this.player = who;
        this.getJsonSerializers().put(Player.class, new PlayerAdapter());
    }
}
