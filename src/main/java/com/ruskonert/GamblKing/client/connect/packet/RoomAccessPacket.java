package com.ruskonert.GamblKing.client.connect.packet;

import com.ruskonert.GamblKing.adapter.RoomAdapter;
import com.ruskonert.GamblKing.entity.Room;
import com.ruskonert.GamblKing.property.ServerProperty;

public class RoomAccessPacket extends ClientPacket
{
    private Room room;
    public Room getRoom() { return this.room; }

    public RoomAccessPacket(Room room)
    {
        super(ServerProperty.ROOM_ACCESS);
        this.room = room;
        this.getJsonSerializers().put(Room.class, new RoomAdapter());
    }
}
