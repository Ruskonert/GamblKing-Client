package com.ruskonert.GamblKing.client.event;
import com.ruskonert.GamblKing.entity.Player;
import com.ruskonert.GamblKing.entity.Room;
import com.ruskonert.GamblKing.event.Event;

public class RoomCreateEvent extends Event
{
    private Room room;
    private Player player;

    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public Room getRoom()
    {

        return room;
    }

    public void setRoom(Room room)
    {
        this.room = room;
    }

    public RoomCreateEvent(Room room, Player player)
    {
        this.room = room;
        this.player = player;
    }
}
