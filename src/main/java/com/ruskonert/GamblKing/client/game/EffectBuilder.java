package com.ruskonert.GamblKing.client.game;

import com.ruskonert.GamblKing.client.game.entity.component.Effect;
import com.ruskonert.GamblKing.client.game.entity.component.Targeting;
import com.ruskonert.GamblKing.client.game.framework.CardFramework;

import java.util.ArrayList;

public class EffectBuilder
{
    private Targeting targeting = Targeting.ALL;
    public void setTargeting(Targeting t) { this.targeting = t; }

    public Targeting getTargeting() { return this.targeting; }
    private Effect effect = null;

    public Effect getEffect() { return this.effect; }

    private DuelPlayer player;
    public

    private int value;
    public void setValue(int value) { this.value = value; }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {

        this.count = count;
    }

    private int count = 1;

    private ArrayList<CardFramework> targetCard = new ArrayList<CardFramework>();
    public void addTarget(CardFramework framework) { this.targetCard.add(framework); }

    public EffectBuilder(Effect effect)
    {
        this.effect = effect;
    }

    public EffectBuilder(Effect effect, int count)
    {
        this.effect = effect;
        this.count = count;
    }

    public EffectBuilder(Effect effect, Targeting targeting, int count)
    {
        this.effect = effect;
        this.targeting = targeting;
        this.count = count;
    }

    public void apply()
    {
        switch(effect)
        {
            case PLAYER_DAMAGE:
            {
                DuelPlayer player;
                if(targeting == Targeting.THIS)
                {


                    // PlayerDamageEvent 발생
                    break;
                }
                else if(targeting == Targeting.OTHER)
                {
                    // PlayerDamageEvent 발생
                    break;
                }
            }
            case DESTORY_MOSNTER_CARD:
            {
                // MonsterCardDestoryEvent 발생
                break;
            }
            case DESTORY_CARD:
            {
                // CardDestoryEvent 발생
                break;
            }
            case DESTORY_SPECIAL_CARD:
            {
                //SpecialCardDestoryEvent 발생
                break;
            }
            case CONTROL_ATTACK_VALUE:
            {
                // AttackControlEvent 발생
                break;
            }
            case CONTROL_DEFENSE_VALUE:
            {
                // AttackControlEvent 발생
                break;
            }

        }
    }
}
