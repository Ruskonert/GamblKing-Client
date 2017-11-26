package com.ruskonert.GamblKing.client.event;

import com.ruskonert.GamblKing.entity.Room;
import com.ruskonert.GamblKing.event.Event;

public class RoomQuitEvent extends Event

{
    private String me;
    public String getMine() { return this.me; }

    private String other;
    public String getOther() { return this.other; }

    private Room room;
    public Room getRoom() { return this.room; }

    public RoomQuitEvent(String me, String other, Room room)
    {
        this.me = me;
        this.other = other;
        this.room = room;
    }
}
