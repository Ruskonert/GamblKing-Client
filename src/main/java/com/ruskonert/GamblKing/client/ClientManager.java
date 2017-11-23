package com.ruskonert.GamblKing.client;

import com.ruskonert.GamblKing.client.connect.ClientConnectionReceiver;
import com.ruskonert.GamblKing.framework.PlayerEntityFramework;
import com.ruskonert.GamblKing.program.ConsoleSender;

public final class ClientManager
{
    private static ConsoleSender consoleSender;
    public static ConsoleSender getConsoleSender() { return consoleSender; }

    // Player 타입으로 사용 가능합니다.
    public static PlayerEntityFramework getPlayer() { return ClientConnectionReceiver.getPlayerEntityFramework(); }
}
