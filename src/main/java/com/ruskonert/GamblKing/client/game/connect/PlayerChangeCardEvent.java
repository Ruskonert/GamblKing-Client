package com.ruskonert.GamblKing.client.game.connect;

import com.ruskonert.GamblKing.client.game.DuelPlayer;
public class PlayerChangeCardEvent extends PlayerEvent
{
    public DuelPlayer player;

    public PlayerChangeCardEvent(DuelPlayer me)
    {
        super(0x10000);
        this.player = me;
    }
}
