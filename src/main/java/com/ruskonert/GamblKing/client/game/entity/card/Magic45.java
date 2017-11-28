package com.ruskonert.GamblKing.client.game.entity.card;

import com.ruskonert.GamblKing.client.game.entity.component.Effect;
import com.ruskonert.GamblKing.client.game.entity.component.Targeting;
import com.ruskonert.GamblKing.client.game.framework.MagicCard;

public final class Magic45 extends MagicCard
{
    public Magic45()
    {
        this.setName("Ȥ�ѱ�");
        this.setDescription("�ʹ� �߿��� �ߵ� ���� ����. ���� �Ʊ� �ʵ��� ������� 500 �����.");
        this.setImage("45.png");
        this.addEffect(Effect.TOTAL_CONTROL_DEFENSE_VALUE, Targeting.OTHER, -500);
        this.addEffect(Effect.TOTAL_CONTROL_DEFENSE_VALUE, Targeting.THIS, -500);
    }
}