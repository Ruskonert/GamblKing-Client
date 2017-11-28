package com.ruskonert.GamblKing.client.game.event;

import com.ruskonert.GamblKing.client.game.framework.CardFramework;

public class DrawEvent extends DuelEvent
{
    private CardFramework card;
    public CardFramework getCard() { return this.card; }

    public DrawEvent(CardFramework framework)
    {
        super(0x10);
        this.card = framework;
    }
}
