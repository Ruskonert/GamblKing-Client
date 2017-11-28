package com.ruskonert.GamblKing.client.game.entity.card;


import com.ruskonert.GamblKing.client.game.entity.component.ActivateCost;
import com.ruskonert.GamblKing.client.game.entity.component.Effect;
import com.ruskonert.GamblKing.client.game.entity.component.Targeting;
import com.ruskonert.GamblKing.client.game.framework.TrapCard;

public class Trap56 extends TrapCard
{
    public Trap56()
    {
        this.setName("��Ȧ");
        this.setDescription("����ʵ�� �����ʵ��� ��� ī�带","������ ������.");
        this.setImage("56.png");

        this.setActivateCost(ActivateCost.ACTIVATED);
        this.addEffect(Effect.DESTORY_CARD,20);
        //�ʵ����� ī���� ������ count�ϴ� ���ڰ� �ڿ� ���� �� ��
    
    }
}