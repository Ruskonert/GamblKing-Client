package com.ruskonert.GamblKing.client.game.entity.card;


import com.ruskonert.GamblKing.client.game.entity.component.Effect;
import com.ruskonert.GamblKing.client.game.entity.component.Targeting;
import com.ruskonert.GamblKing.client.game.framework.MagicCard;

public final class Magic46 extends MagicCard
{
    public Magic46()
    {
        this.setName("������ ���");
        this.setDescription("���ݷ� +500");
        this.setImage("46.png");
        // �Ʒ��� �Լ��� ī�带 ���� �� �⺻���� ȣ��˴ϴ�.
        //this.setActivateCost(ActivateCost.NONE);
        this.addEffect(Effect.TOTAL_CONTROL_ATTACK_VALUE, Targeting.THIS, 500);
    
    }
}
