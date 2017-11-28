package com.ruskonert.GamblKing.client.game.entity.card;

import com.ruskonert.GamblKing.client.game.entity.component.Effect;
import com.ruskonert.GamblKing.client.game.entity.component.Targeting;
import com.ruskonert.GamblKing.client.game.framework.MagicCard;

public final class Magic44 extends MagicCard
{
    public Magic44()
    {
        this.setName("�⸻���");
        this.setDescription("�������� ������ �����Ѵ�. ��� �ʵ��� ������� ���������� 500 �����.");
        this.setImage("44.png");
        this.addEffect(Effect.TOTAL_CONTROL_DEFENSE_VALUE, Targeting.OTHER, -500);
    }
}