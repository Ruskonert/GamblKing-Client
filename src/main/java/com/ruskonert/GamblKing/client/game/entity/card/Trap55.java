package com.ruskonert.GamblKing.client.game.entity.card;

import com.ruskonert.GamblKing.client.game.entity.component.ActivateCost;
import com.ruskonert.GamblKing.client.game.entity.component.Effect;
import com.ruskonert.GamblKing.client.game.entity.component.Targeting;
import com.ruskonert.GamblKing.client.game.framework.TrapCard;

public class Trap55 extends TrapCard {
    public Trap55() {
        this.setName("��Ʈ����");
        this.setDescription("��뿡�� 500�� �������� ���� ������ �Ѵ�.");
        this.setImage("55.png");

        this.setActivateCost(ActivateCost.ACTIVATED);

        this.addEffect(Effect.PLAYER_DAMAGE, 500);

        // ��뿡�� ���� �����Ѵ�.
    }
}