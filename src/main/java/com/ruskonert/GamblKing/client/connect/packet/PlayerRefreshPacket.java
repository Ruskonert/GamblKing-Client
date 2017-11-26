package com.ruskonert.GamblKing.client.connect.packet;

import com.ruskonert.GamblKing.entity.Player;
import com.ruskonert.GamblKing.property.ServerProperty;

public class PlayerRefreshPacket extends ClientPlayerPacket
{
    public PlayerRefreshPacket(Player player)
    {
        super(player, ServerProperty.PLAYER_REFRESH);
    }

}
