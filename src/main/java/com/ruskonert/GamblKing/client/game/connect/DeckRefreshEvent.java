package com.ruskonert.GamblKing.client.game.connect;

public class DeckRefreshEvent extends PlayerEvent
{
    public int count;
    public DeckRefreshEvent(int count)
    {
        super(0x10002);
        this.count = count;
    }
}
