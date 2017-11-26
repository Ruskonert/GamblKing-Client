package com.ruskonert.GamblKing.client.event;

import com.ruskonert.GamblKing.entity.Player;
import com.ruskonert.GamblKing.entity.Room;
import com.ruskonert.GamblKing.event.Event;
import com.ruskonert.GamblKing.framework.PlayerEntityFramework;

public class RoomUpdateEvent extends Event
{
    private Player leader;
    public Player getLeader() { return this.leader; }

    private Player other;
    public Player getOther() { return this.other; }

    private Room room;
    public Room getRoom() { return this.room; }

    public RoomUpdateEvent(PlayerEntityFramework leader, Player other, Room room)
    {
        this.leader = leader;
        this.other = other;
        this.room = room;
    }

}
