package com.ruskonert.GamblKing.client.connect.packet;

import com.ruskonert.GamblKing.entity.Player;
import com.ruskonert.GamblKing.property.ServerProperty;

public class PlayerMessagePacket extends ClientPlayerPacket
{
    private String message;
    public String getMessage() { return this.message; }

    public PlayerMessagePacket(Player player, String message)
    {
        super(player, ServerProperty.PLAYER_MESSAGE_RECEIVED);
        this.message = message;
    }
}
