package com.ruskonert.GamblKing.client.game.connect;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

public abstract class PlayerEvent
{
    private int type;
    public PlayerEvent(int type)
    {
        this.type = type;
    }
    public void send()
    {
        Gson gson = new GsonBuilder().serializeNulls().create();

        try {
            GameServerConnection.getOutputStream().writeUTF(gson.toJson(this));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
