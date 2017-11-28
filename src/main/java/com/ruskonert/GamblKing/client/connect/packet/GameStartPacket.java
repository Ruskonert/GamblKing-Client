package com.ruskonert.GamblKing.client.connect.packet;

public class GameStartPacket extends ClientPacket
{
    private String address;

    private String target;

    public GameStartPacket(String address, String waitPlayerName)
    {
        super(1800);
        this.address= address;
        this.target = waitPlayerName;
    }
}
