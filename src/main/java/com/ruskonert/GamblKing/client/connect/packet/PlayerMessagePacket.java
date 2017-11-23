package com.ruskonert.GamblKing.client.connect.packet;

import com.ruskonert.GamblKing.entity.Player;

public class PlayerMessagePacket extends ClientPlayerPacket
{
    private String message;
    public String getMessage() { return this.message; }

    public PlayerMessagePacket(Player player, String message)
    {
        super(player, 1100);
        this.message = message;
    }
}
