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
import javafx.concurrent.Task;
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

    /**
     * 필드에 있는 이미지뷰에 대한 이벤트를 등록합니다.
     * @param view
     * @param selectedIndex
     */
    private void setOnMouseEnteredPreview(ImageView view, int selectedIndex)
    {
        // 해당 이미지 뷰의 인덱스 번호를 넣습니다.
        viewIndex.put(selectedIndex, view);

        DuelComponent component = ClientProgramManager.getDuelComponent();
        DuelPlayer player = GameServerConnection.getPlayer();

        // 마우스가 이 카드에 갖다 댄 경우입니다.
        view.setOnMouseEntered(event -> {
            //
            if(player == null) return;
            CardFramework framework = null;
            try {
                framework = player.getSelectedCard(selectedIndex);
            }
            catch(IndexOutOfBoundsException e)
            {
                e.printStackTrace();
            }
            //실시간으로 현재 선택된 인덱스의 번호를 저장합니다.
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
        view.setOnMouseClicked(event ->
        {
            //단순히 카드를 소환하거나 발동합니다.
            if((GameServerConnection.getProcessPage() == Page.MAIN2 || GameServerConnection.getProcessPage() == Page.MAIN)
                 && !GameServerConnection.isSelectionMode())
            {
                GameServerConnection.getPlayer().activateCard(selectedIndex);
            }

            // 만약 함정 카드 선택 이벤트가 활성화되었다면
            // 이것은 특성 인덱스에 있는 것 중에서 카드 선택을 하는 것입니다.
            // 만약 카드 선택 모드 활성화 된 상태에서 이 카드를 선택했다면
            else if(event.getClickCount() == 1 && GameServerConnection.isSelectionMode())
            {
                // 이 카드를 실행시킬 수 있다면
                if(GameServerConnection.getUsableIndex().indexOf(selectedIndex) != -1)
                {
                    CardFramework framework = player.getSelectedCard(selectedIndex);
                    viewIndex.get(selectedIndex).setImage(framework.getImage());

                    Task<Void> animation = new Task<Void>()
                    {
                        @Override
                        protected Void call() throws Exception {
                            // 발동 이펙트 애니메이션
                            // 이 때 상대방도 이 카드가 발동되고 있는 것을 애니메이션으로 보여줘야 합니다.
                            Thread.sleep(300L);
                            return null;
                        }
                    };

                    Thread aniThread = new Thread(animation);
                    try
                    {
                        // 애니메이션이 끝날 때까지 기다립니다.
                        aniThread.join();

                        Task<Void> activate = new Task<Void>()
                        {
                            @Override
                            protected Void call() throws Exception {
                                // 효과 구축합니다. 효과 구축이 끝나기 전까지는 작업이 끝나지 않습니다.
                                GameServerConnection.getPlayer().activateEffect(framework);
                                return null;
                            }
                        };

                        Thread activateEffect = new Thread(activate);
                        activateEffect.start();
                        activateEffect.join();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                }
            }

            // 이것은 타겟팅 인덱스입니다. 즉 마법 카드나 함정 카드를 발동할 때 그 대상을 선택하는 것을 말합니다.
            // 이 녀석은 2번 눌러야 합니다.
            else if(event.getClickCount() == 2 && GameServerConnection.isTargeted())
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

                            // 이미 한번 선택한 것이라면
                            if(GameServerConnection.getTargetIndex().get(p.getSelectedIndex()) != null)
                            {
                                //다시 선택
                            }
                            else
                            {
                                GameServerConnection.getTargetIndex().add(p.getSelectedIndex());
                                GameServerConnection.getUsableIndex().remove(p.getSelectedIndex());
                            }
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
                            // 이미 한번 선택한 것이라면
                            if(GameServerConnection.getTargetIndex().get(p.getSelectedIndex()) != null)
                            {
                                //다시 선택
                            }
                            else
                            {
                                GameServerConnection.getTargetIndex().add(p.getSelectedIndex());
                                GameServerConnection.getUsableIndex().remove(p.getSelectedIndex());
                            }
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
                            // 이미 한번 선택한 것이라면
                            if(GameServerConnection.getTargetIndex().get(p.getSelectedIndex()) != null)
                            {
                                //다시 선택
                            }
                            else
                            {
                                GameServerConnection.getTargetIndex().add(p.getSelectedIndex());
                            }
                        }
                        break;
                    }
                }
            }
        });
    }
}
