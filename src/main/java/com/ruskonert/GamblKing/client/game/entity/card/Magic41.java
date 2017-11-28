package com.ruskonert.GamblKing.client.game.entity.card;

import com.ruskonert.GamblKing.client.game.entity.component.Effect;
import com.ruskonert.GamblKing.client.game.entity.component.Targeting;
import com.ruskonert.GamblKing.client.game.framework.MagicCard;

public final class Magic41 extends MagicCard
{
    public Magic41()
    {
        this.setName("����");
        this.setDescription("���� �ʵ��� ���ݷ��� ���������� 500 �ø���.");
        this.setImage("41.png");
        this.addEffect(Effect.TOTAL_CONTROL_ATTACK_VALUE, Targeting.THIS, 500);
	this.setSummonSound("MagicCard41.mp3");
    }
}