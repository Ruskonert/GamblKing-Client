package com.ruskonert.GamblKing.client.game.event;

import com.ruskonert.GamblKing.client.game.DuelPlayer;
import com.ruskonert.GamblKing.client.game.connect.GameServerConnection;
import com.ruskonert.GamblKing.client.game.entity.component.CardType;
import com.ruskonert.GamblKing.client.game.entity.component.Targeting;
import com.ruskonert.GamblKing.client.game.framework.CardFramework;
import com.ruskonert.GamblKing.client.game.framework.MonsterCard;
import com.ruskonert.GamblKing.client.program.ClientProgramManager;
import com.ruskonert.GamblKing.client.program.component.DuelComponent;
import com.ruskonert.GamblKing.event.LayoutListener;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;

import java.util.HashMap;
import java.util.Map;

public class DuelLayoutEvent implements LayoutListener
{
    private static Map<Integer, ImageView> viewIndex = new HashMap<>();
    public static Map<Integer, ImageView> getViewIndex() { return viewIndex; }

    @Override
    public void register(Object o)
    {
        // 프리뷰 인덱스 설정
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().MyCard1, 0);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().MyCard2, 1);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().MyCard3, 2);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().MyCard4, 3);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().MyCard5, 4);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().MyCard6, 5);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().MyCard7, 6);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().MonsterField1, 7);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().MonsterField2, 8);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().MonsterField3, 9);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().MonsterField4, 10);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().MonsterField5, 12);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().SpecialField1, 13);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().SpecialField2, 14);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().SpecialField3, 15);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().SpecialField4, 16);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().SpecialField5, 17);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().MyDead, 18);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().MyExcept, 19);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().MyDeck, 20);

        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().OthersCard1, 21);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().OthersCard2, 22);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().OthersCard3, 23);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().OthersCard4, 24);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().OthersCard5, 25);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().OthersCard6, 26);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().OthersCard7, 27);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().OthersMonsterField1, 28);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().OthersMonsterField2, 29);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().OthersMonsterField3, 30);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().OthersMonsterField4, 31);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().OthersMonsterField5, 32);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().OthersSpecialField1, 33);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().OthersSpecialField2, 34);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().OthersSpecialField3, 35);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().OthersSpecialField4, 36);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().OthersSpecialField5, 37);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().OthersDead, 38);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().OthersExecpt, 39);
        this.setOnMouseEnteredPreview(ClientProgramManager.getDuelComponent().OthersDeck, 40);
    }

    private void setOnMouseEnteredPreview(ImageView view, int selectedIndex)
    {
        viewIndex.put(selectedIndex, view);
        DuelComponent component = ClientProgramManager.getDuelComponent();
        DuelPlayer player = GameServerConnection.getPlayer();
        view.setOnMouseEntered(event -> {
            CardFramework framework = player.getCard().get(selectedIndex);
            player.setSelectedIndex(selectedIndex);
            if(framework != null) {
                component.SelectedCardImage.setImage(view.getImage());
                component.SelectedCardDescription.setText(framework.getDescription());
                if (framework.getCardType() == CardType.MONSTER)
                    component.SelectedCardStat.setText(((MonsterCard) framework).getAttack() + "/" + ((MonsterCard) framework).getDefense());
                else
                    component.SelectedCardStat.setText("");
            }
        });
        view.setOnMouseClicked(event -> {
            // 만약 카드 선택 이벤트가 활성화되었다면
            if(event.getClickCount() == 2 && GameServerConnection.isTargeted())
            {
                Targeting t = GameServerConnection.getTargetArea();
                switch(t)
                {
                    case THIS: {
                        if (selectedIndex > 20) {
                            Alert alert = new Alert(Alert.AlertType.WARNING, "자신의 카드를 선택하세요.", ButtonType.OK);
                            alert.showAndWait();
                        }
                        else if(selectedIndex == 18 || selectedIndex == 19 || selectedIndex == 20)
                        {
                            Alert alert = new Alert(Alert.AlertType.WARNING, "해당 카드 필드는 선택할 수 없습니다.", ButtonType.OK);
                            alert.showAndWait();
                        }
                        else
                        {
                            DuelPlayer p = GameServerConnection.getPlayer();
                            GameServerConnection.getTargetIndex().add(p.getSelectedIndex());

                        }
                        break;
                    }
                    case ALL:
                    {
                        if(selectedIndex == 18 || selectedIndex == 19 || selectedIndex == 20
                                || selectedIndex == 38 || selectedIndex == 39 || selectedIndex == 40)
                        {
                            Alert alert = new Alert(Alert.AlertType.WARNING, "해당 카드 필드는 선택할 수 없습니다.", ButtonType.OK);
                            alert.showAndWait();
                        }
                        else
                        {
                            DuelPlayer p = GameServerConnection.getPlayer();
                            GameServerConnection.getTargetIndex().add(p.getSelectedIndex());
                        }
                        break;
                    }
                    case OTHER: {
                        if (selectedIndex <= 20) {
                            Alert alert = new Alert(Alert.AlertType.WARNING, "상대 카드를 선택하세요.", ButtonType.OK);
                            alert.showAndWait();
                        }
                        else if(selectedIndex == 38 || selectedIndex == 39 || selectedIndex == 40)
                        {
                            Alert alert = new Alert(Alert.AlertType.WARNING, "해당 카드 필드는 선택할 수 없습니다.", ButtonType.OK);
                            alert.showAndWait();
                        }
                        else
                        {
                            DuelPlayer p = GameServerConnection.getPlayer();
                            GameServerConnection.getTargetIndex().add(p.getSelectedIndex());
                        }
                        break;
                    }
                }
            }
        });
        view.setOnMouseClicked(event -> player.setLastSelected(player.getselectOtherCard(player.getSelectedIndex())));
    }



}
