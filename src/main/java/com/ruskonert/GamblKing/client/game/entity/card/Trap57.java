package com.ruskonert.GamblKing.client.game.entity.card;


import com.ruskonert.GamblKing.client.game.entity.component.ActivateCost;
import com.ruskonert.GamblKing.client.game.entity.component.Effect;
import com.ruskonert.GamblKing.client.game.entity.component.Targeting;
import com.ruskonert.GamblKing.client.game.framework.TrapCard;

public class Trap57 extends TrapCard
{
    public Trap57()
    {
        this.setName("����Ŭ��");
        this.setDescription("�ʵ忡 �ִ� ����ī�� ������ �ı��Ѵ�.");
        this.setImage("57.png");
        //Effect���� �ʿ�
    	this.setSummonSound("TrapCard57.mp3");
    }
}