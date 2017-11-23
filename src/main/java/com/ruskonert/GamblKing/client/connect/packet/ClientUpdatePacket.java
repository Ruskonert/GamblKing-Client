package com.ruskonert.GamblKing.client.connect.packet;

import com.ruskonert.GamblKing.property.ServerProperty;
import com.ruskonert.GamblKing.util.SecurityUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ClientUpdatePacket extends ClientPacket
{
    public ClientUpdatePacket()
    {
        super(ServerProperty.SEND_UPDATE_REQURST);
    }

    private static Map<String, File> updateFiles = new HashMap<>();
    public static Map<String, File> getUpdateFiles()  { return updateFiles; }

    public synchronized static void update()
    {
        updateFiles(new File("data"));
    }

    private static void updateFiles(File path)
    {
        for(File file : path.listFiles())
        {
            if(file.isDirectory())
            {
                ClientUpdatePacket.updateFiles(file);
            }
            else
            {
                String sha256 = null;
                try
                {
                    sha256 = SecurityUtil.Companion.extractFileHashSHA256(file.getPath());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                updateFiles.put(sha256, file);
            }
        }
    }
}
