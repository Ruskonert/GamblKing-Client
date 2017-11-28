package com.ruskonert.GamblKing.client.game.entity;

import com.ruskonert.GamblKing.client.game.DuelPlayer;
import com.ruskonert.GamblKing.client.game.entity.component.Effect;

import java.util.ArrayList;
import java.util.List;

public class EffectElement
{
    private List<Integer> targetIndex = new ArrayList<>();

    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    public DuelPlayer getPlayer() {

        return player;
    }

    public void setPlayer(DuelPlayer player) {
        this.player = player;
    }

    public List<Integer> getTargetIndex() {

        return targetIndex;
    }

    private DuelPlayer player;

    private Effect effect;

    public EffectElement(Effect effect, DuelPlayer player, List<Integer> index)
    {
        this.effect = effect;
        this.player = player;
        this.targetIndex = index;
    }
}
