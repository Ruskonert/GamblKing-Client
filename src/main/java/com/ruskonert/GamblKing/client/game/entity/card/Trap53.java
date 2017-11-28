package com.ruskonert.GamblKing.client.game.entity.card;

import com.ruskonert.GamblKing.client.game.entity.component.ActivateCost;
import com.ruskonert.GamblKing.client.game.entity.component.Effect;
import com.ruskonert.GamblKing.client.game.entity.component.Targeting;
import com.ruskonert.GamblKing.client.game.framework.TrapCard;

public class Trap53 extends TrapCard {
    public Trap53() {
        this.setName("���� ų");
        this.setDescription("����� Ư�� ���� ī��(2��)�� �������� ������.");
        this.setImage("53.png");

        this.setActivateCost(ActivateCost.ACTIVATED);

        this.addEffect(Effect.DESTORY_MOSNTER_CARD, 2);
        // ��� ��� ����ī�带 �������� ����
    }
}