package com.ruskonert.GamblKing.client.game.entity.component;

/**
 * 카드가 필드에 사용하기 위한 조건부입니다.
 */
public enum ActivateCost
{
    // 아무 카드를 필드에 냈을 때, 발동하는 조건입니다.
    ACTIVATED,

    // 아무 카드가 파괴되었을 때 발동되는 조건입니다.
    DESTORYED,

    // 엔드 페이지일 때, 발동하는 조건입니다.
    END_PAGE,

    // 발동 조건이 없습니다. 필드에 내놓는 순간 즉각 발동됩니다. 이것은 마법 카드에서만 사용이 가능합니다.
    NONE,

    // 몬스터가 소환되었을 때 발동하는 조건입니다.
    MONSTER_SUMMONED,

    // 마법 또는 함정카드를 발동했을 때 발동하는 조건입니다.
    SPECIAL_CARD_ACTIVATED,

    // 함정 카드가 발동되었을 때 발동하는 조건입니다.
    TRAP_CARD_ACTIVATED,

    // 마법 카드가 발동되었을 때 발동하는 조건입니다.
    MAGIC_CARD_ACTIVATED,

    // 플레이어가 공격했을 때 발동하는 조건입니다.
    PLYER_ATTACK,

    // 플레이어가 데미지를 입었을 때 발동하는 조건입니다.
    PLAYER_DAMAGERD
}
