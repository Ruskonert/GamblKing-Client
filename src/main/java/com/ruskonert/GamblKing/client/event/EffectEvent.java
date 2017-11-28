package com.ruskonert.GamblKing.client.event;

import com.ruskonert.GamblKing.client.game.entity.component.Targeting;

import java.util.List;

public abstract class EffectEvent
{
    private Targeting targeting;

    private int value;

    private List<Integer> targetIndex;

    public EffectEvent(Targeting t, int value)
    {

    }
}
