package com.ruskonert.GamblKing.client.game.connect;

import com.ruskonert.GamblKing.client.game.event.Page;

public class PlayerPageEvent extends PlayerEvent
{
    private Page page;

    public PlayerPageEvent(Page page)
    {
        super(0x10020);
        this.page = page;
    }
}
