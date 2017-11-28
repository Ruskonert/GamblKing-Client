package com.ruskonert.GamblKing.client.game.entity.card;


import com.ruskonert.GamblKing.client.game.entity.component.ActivateCost;
import com.ruskonert.GamblKing.client.game.entity.component.Effect;
import com.ruskonert.GamblKing.client.game.entity.component.Targeting;
import com.ruskonert.GamblKing.client.game.framework.TrapCard;

public class Trap59 extends TrapCard
{
    public Trap59()
    {
        this.setName("�޼Ұ���");
        this.setDescription("������ life�� 400��ŭ ���ҽ�Ų��.");
        this.setImage("59.png");
  
        this.addEffect(Effect.PLAYER_DAMAGE, Targeting.OTHER, 400);
    }
}