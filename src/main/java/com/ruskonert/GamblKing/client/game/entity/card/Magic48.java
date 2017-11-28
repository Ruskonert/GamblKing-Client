package com.ruskonert.GamblKing.client.game.entity.card;


import com.ruskonert.GamblKing.client.game.entity.component.Effect;
import com.ruskonert.GamblKing.client.game.entity.component.Targeting;
import com.ruskonert.GamblKing.client.game.framework.MagicCard;

public final class Magic48 extends MagicCard
{
    public Magic48()
    {
        this.setName("����");
        this.setDescription("��� �ʵ��� ������ ������ 200 �����.");
        this.setImage("48.png");
        // �Ʒ��� �Լ��� ī�带 ���� �� �⺻���� ȣ��˴ϴ�.
        //this.setActivateCost(ActivateCost.NONE);
        this.addEffect(Effect.TOTAL_CONTROL_DEFENSE_VALUE, Targeting.THIS, -200);
    }
}
