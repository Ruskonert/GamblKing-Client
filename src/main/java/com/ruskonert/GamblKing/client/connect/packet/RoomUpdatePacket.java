package com.ruskonert.GamblKing.client.connect.packet;

import com.ruskonert.GamblKing.adapter.RoomAdapter;
import com.ruskonert.GamblKing.entity.Room;
import com.ruskonert.GamblKing.property.ServerProperty;

public class RoomUpdatePacket extends ClientPacket
{
    private Room room;
    public Room getRoom() { return room; }

    public RoomUpdatePacket(Room room)
    {
        super(ServerProperty.ROOM_UPDATE);
        this.room = room;
        this.getJsonSerializers().put(Room.class, new RoomAdapter());
    }
}
