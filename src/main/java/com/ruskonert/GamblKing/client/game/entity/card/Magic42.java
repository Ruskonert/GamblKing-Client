package com.ruskonert.GamblKing.client.game.entity.card;

import com.ruskonert.GamblKing.client.game.entity.component.Effect;
import com.ruskonert.GamblKing.client.game.entity.component.Targeting;
import com.ruskonert.GamblKing.client.game.framework.MagicCard;

public final class Magic42 extends MagicCard
{
    public Magic42()
    {
        this.setName("��������");
        this.setDescription("��� ü���� ���� 500 ���ҽ�Ų��.");
        this.setImage("42.png");
        this.addEffect(Effect.PLAYER_DAMAGE, Targeting.THIS, 500);
    }
}