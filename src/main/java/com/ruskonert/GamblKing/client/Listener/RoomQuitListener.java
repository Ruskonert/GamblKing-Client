package com.ruskonert.GamblKing.client.Listener;

import com.ruskonert.GamblKing.client.ClientManager;
import com.ruskonert.GamblKing.client.connect.packet.RoomQuitPacket;
import com.ruskonert.GamblKing.client.connect.packet.RoomRefreshPacket;
import com.ruskonert.GamblKing.client.event.RoomQuitEvent;
import com.ruskonert.GamblKing.client.program.ClientProgramManager;
import com.ruskonert.GamblKing.entity.Room;
import com.ruskonert.GamblKing.event.EventListener;
import com.ruskonert.GamblKing.event.Handle;
import com.ruskonert.GamblKing.util.SystemUtil;

public class RoomQuitListener implements EventListener
{
    // 자신이 방을 나갔을 때 발생하는 이벤트를 처리합니다.
    @Handle
    public void onQuit(RoomQuitEvent event)
    {
        Room room = event.getRoom();

        //만약 자신이 리더라면
        if(room.getLeaderName().equalsIgnoreCase(event.getMine()))
        {
            // 방을 지워야 하므로 접속한 다른 플레이어가 있는지 검사합니다.
            // 누군가 입장한 상태라면
            if(!(room.getWaitPlayerName() == null))
            {

                // 이 플레이어에게 퇴장 요청을 하도록 합니다.
                RoomQuitPacket quitPacket = new RoomQuitPacket(event.getOther());
                quitPacket.send();
            }

            // 방장 또한 퇴장 요청을 하도록 합니다.
            RoomQuitPacket quitPacket = new RoomQuitPacket(room.getLeaderName());
            quitPacket.send();

            // 방 레이아웃을 닫고 대기실 레이아웃을 다시 엽니다.
            ClientProgramManager.openGameLayout();
            SystemUtil.Companion.alert("경고", "방 없어짐", "대기실이 없어졌습니다.");
            RoomRefreshPacket refreshPacket = new RoomRefreshPacket(ClientManager.getPlayer().getId());
            refreshPacket.send();
        }

        // 아니라면 (참가자라면)
        else
        {
            // 방을 안전하게 나갈 수 있도록 요청합니다.
            RoomQuitPacket quitPacket = new RoomQuitPacket(event.getMine(), true);
            quitPacket.send();

            // 방 레이아웃을 닫고 대기실 레이아웃을 다시 엽니다.
            ClientProgramManager.openGameLayout();
            SystemUtil.Companion.alert("퇴장", "대기실 퇴장", "방에서 나갔습니다.");
            RoomRefreshPacket refreshPacket = new RoomRefreshPacket(ClientManager.getPlayer().getId());
            refreshPacket.send();
        }
    }
}
