package com.ruskonert.GamblKing.client.Listener;
import com.ruskonert.GamblKing.client.event.RoomUpdateEvent;
import com.ruskonert.GamblKing.client.program.ClientProgramManager;
import com.ruskonert.GamblKing.client.program.RoomApplication;
import com.ruskonert.GamblKing.client.program.component.RoomComponent;
import com.ruskonert.GamblKing.event.EventListener;
import com.ruskonert.GamblKing.event.Handle;
import javafx.application.Platform;
import javafx.stage.Stage;

public class RoomUpdateListener implements EventListener
{
    @Handle
    public void onUpdate(RoomUpdateEvent event)
    {

        Platform.runLater(() ->
        {
            RoomComponent component = null;
            // 만들어진 방에 들어가는 경우
            // 이 경우에는 Room Layout이 만들어지지 않았습니다. 만들고 정보를 업데이트합니다.
            if(RoomApplication.getStage() == null)
            {
                new RoomApplication().start(new Stage());
                component = ClientProgramManager.getRoomComponent();

                component.RoomName.setText(event.getRoom().getName());
                component.RoomNumber.setText("정보없음");
                component.CreateTime.setText("연결 일자: " + event.getRoom().getCreateDate());
                component.PlayerStat.setText(String.format("전적: %d승 %d패 (%.1f%%)", event.getLeader().getVictory(), event.getLeader().getDefeated(),
                event.getLeader().getVictory() == 0 ? 0.0 : event.getLeader().getVictory() / (double)(event.getLeader().getVictory()
                        + event.getLeader().getDefeated())));

                component.PlayerId.setText(event.getLeader().getId());
                component.PlayerName.setText(event.getLeader().getNickname());
                component.StartButton.setVisible(false);
            }
            else
            {
                component = ClientProgramManager.getRoomComponent();
                component.StartButton.setDisable(false);
                ClientProgramManager.getRoomComponent().StartButton.setText("시작하기");
            }

            if(component == null) component = ClientProgramManager.getRoomComponent();

            // 자신이 리더이고 다른 사람이 들어오는 경우
            // 이미 만들어진 Layout에서 사람 리스트만 추가합니다.
            component.AnotherPlayerId.setText(event.getOther().getId());
            component.AnotherPlayerName.setText(event.getOther().getNickname());
            component.AnotherPlayerStat.setText(String.format("전적: %d승 %d패 (%.1f%%)", event.getOther().getVictory(), event.getOther().getDefeated(),
                    event.getOther().getVictory() == 0 ? 0.0 : event.getOther().getVictory() / (double)(event.getOther().getVictory()
                            + event.getOther().getDefeated())));
            ClientProgramManager.getRoomComponent().CapacityStatus.setText("현재 인원 (2/2)");

            component.AnotherUserPane.setVisible(true);
        });

    }
}
