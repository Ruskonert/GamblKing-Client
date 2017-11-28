package com.ruskonert.GamblKing.client.game.entity.card;

import com.ruskonert.GamblKing.client.game.entity.component.ActivateCost;
import com.ruskonert.GamblKing.client.game.entity.component.Effect;
import com.ruskonert.GamblKing.client.game.entity.component.Targeting;
import com.ruskonert.GamblKing.client.game.framework.TrapCard;

public class Trap52 extends TrapCard {
    public Trap52() {
        this.setName("�۽�Ʈ ����");
        this.setDescription("����� Ư�� ���� ī��(1��)�� �������� ������.");
        this.setImage("52.png");

        this.setActivateCost(ActivateCost.ACTIVATED);

        this.addEffect(Effect.DESTORY_MOSNTER_CARD);
        // ��� ��� ����ī�带 �������� ����
        this.setSummonSound("TrapCard52.wav");
    }
}