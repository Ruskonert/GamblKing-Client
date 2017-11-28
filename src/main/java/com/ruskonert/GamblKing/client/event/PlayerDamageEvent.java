package com.ruskonert.GamblKing.client.event;

import com.ruskonert.GamblKing.client.game.entity.component.Targeting;
import com.ruskonert.GamblKing.client.game.event.DuelEvent;

public class PlayerDamageEvent extends EffectEvent
{
    public PlayerDamageEvent(Targeting t, int value)
    {
        super(t, value);
    }
}
