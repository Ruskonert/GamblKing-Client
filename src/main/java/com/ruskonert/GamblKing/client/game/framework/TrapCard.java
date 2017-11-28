package com.ruskonert.GamblKing.client.game.framework;

import com.ruskonert.GamblKing.client.game.entity.component.CardType;

/**
 * 함정 카드는 먼저 자신 페이지에서 카드를 뒤집어 놓고, 다음 페이지부터 발동할 수 있는 특별한 카드입니다.
 * @see com.ruskonert.GamblKing.client.game.entity.component.ActivateCost#NONE
 */
public abstract class TrapCard extends CardFramework
{
    public TrapCard() { super(CardType.TARP); }

    public void addEffect(Object... args) { /* Complied code */ }
}
