package com.ruskonert.GamblKing.client.connect.packet;

import com.ruskonert.GamblKing.adapter.PlayerAdapter;
import com.ruskonert.GamblKing.entity.Player;

public abstract class ClientPlayerPacket extends ClientPacket
{
    private Player player;
    public Player getPlayer() { return this.player; }

    public ClientPlayerPacket(Player player, int statusNumber)
    {
        super(statusNumber);
        this.player = player;
        this.getJsonSerializers().put(Player.class, new PlayerAdapter());
    }
}
