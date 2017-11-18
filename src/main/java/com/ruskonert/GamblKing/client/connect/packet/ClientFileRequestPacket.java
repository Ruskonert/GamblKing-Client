package com.ruskonert.GamblKing.client.connect.packet;

import com.google.gson.Gson;
import com.ruskonert.GamblKing.property.ServerProperty;
import com.ruskonert.GamblKing.util.SystemUtil;

import java.util.HashMap;

public class ClientFileRequestPacket extends ClientPacket
{
    private String data;

    public HashMap<String, String> getData() {
        return new Gson().fromJson(SystemUtil.Companion.fixHashMap(data), HashMap.class);
    }

    public ClientFileRequestPacket()
    {
        super(ServerProperty.CLIENT_FILE_REQUEST);
    }
}
