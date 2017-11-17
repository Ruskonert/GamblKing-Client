package com.ruskonert.GamblKing.client.connect.packet;

import com.ruskonert.GamblKing.client.ClientLoader;
import com.ruskonert.GamblKing.connect.Packet;

public abstract class ClientPacket extends Packet
{
    public ClientPacket(int statusNumber)
    {
        super(statusNumber);
    }

    public void send()
    {
        super.send(ClientLoader.getBackgroundConnection().getOutputStream(), this);
    }

    public static void sendPacket(Packet packet)
    {
        packet.send(ClientLoader.getBackgroundConnection().getOutputStream(), packet);
    }
}
