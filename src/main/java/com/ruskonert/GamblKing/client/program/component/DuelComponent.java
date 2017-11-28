package com.ruskonert.GamblKing.client.program.component;

import com.jfoenix.controls.JFXButton;
import com.ruskonert.GamblKing.client.game.event.Page;
import com.ruskonert.GamblKing.client.program.ClientProgramManager;
import com.ruskonert.GamblKing.util.ReflectionUtil;
import com.ruskonert.GamblKing.util.SystemUtil;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class DuelComponent implements Initializable
{
    public JFXButton DP;
    public JFXButton AP;
    public JFXButton MP;
    public JFXButton MP2;
    public JFXButton EP;

    public ImageView MonsterField1;
    public ImageView MonsterField2;
    public ImageView MonsterField3;
    public ImageView MonsterField4;
    public ImageView MonsterField5;

    public ImageView SpecialField1;
    public ImageView SpecialField2;
    public ImageView SpecialField3;
    public ImageView SpecialField4;
    public ImageView SpecialField5;

    public ImageView MyDeck;
    public ImageView MyExcept;
    public ImageView MyDead;

    public ImageView MyCard1;
    public ImageView MyCard2;
    public ImageView MyCard3;
    public ImageView MyCard4;
    public ImageView MyCard5;
    public ImageView MyCard6;
    public ImageView MyCard7;



    public ImageView OthersMonsterField5;
    public ImageView OthersMonsterField4;
    public ImageView OthersMonsterField3;
    public ImageView OthersMonsterField2;
    public ImageView OthersMonsterField1;

    public ImageView OthersSpecialField5;
    public ImageView OthersSpecialField4;
    public ImageView OthersSpecialField3;
    public ImageView OthersSpecialField2;
    public ImageView OthersSpecialField1;

    public ImageView OthersExecpt;
    public ImageView OthersDeck;
    public ImageView OthersDead;

    public ImageView OthersCard7;
    public ImageView OthersCard6;
    public ImageView OthersCard5;
    public ImageView OthersCard4;
    public ImageView OthersCard3;
    public ImageView OthersCard2;
    public ImageView OthersCard1;

    public Label MyMonsterStat1;
    public Label MyMonsterStat5;
    public Label MyMonsterStat4;
    public Label MyMonsterStat3;
    public Label MyMonsterStat2;

    public Label OthersMonsterStat5;
    public Label OthersMonsterStat1;
    public Label OthersMonsterStat2;
    public Label OthersMonsterStat3;
    public Label OthersMonsterStat4;



    public ImageView SelectedCardImage;
    public TextField SelectedCardName;
    public TextField SelectedCardDescription;
    public TextField SelectedCardStat;

    public Label OthersName;
    public Label MyName;

    public AnchorPane DuelField;
    public ImageView Background;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try
        {
            ReflectionUtil.Companion.setStaticField(ClientProgramManager.class, "duelComponent", this);
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            SystemUtil.Companion.error(e);
        }
    }

    // 페이지의 색깔을 바꿉니다.
    public static void changeTurnButtonColor(boolean turn)
    {
        if(!turn)
        {
            // 상대방 차례라는 색깔로 바꿉니다.
            Platform.runLater(() ->
            {
                ClientProgramManager.getDuelComponent().AP.setStyle("-fx-background-color: #5d5d5d");
                ClientProgramManager.getDuelComponent().DP.setStyle("-fx-background-color: #5d5d5d");
                ClientProgramManager.getDuelComponent().MP.setStyle("-fx-background-color: #5d5d5d");
                ClientProgramManager.getDuelComponent().MP2.setStyle("-fx-background-color: #5d5d5d");
                ClientProgramManager.getDuelComponent().EP.setStyle("-fx-background-color: #5d5d5d");
            });
        }
        else
        {
            // 내 차례라는 색깔로 바꿉니다.
            Platform.runLater(() ->
            {
                ClientProgramManager.getDuelComponent().AP.setStyle("-fx-background-color: white");
                ClientProgramManager.getDuelComponent().DP.setStyle("-fx-background-color: white");
                ClientProgramManager.getDuelComponent().MP.setStyle("-fx-background-color: white");
                ClientProgramManager.getDuelComponent().MP2.setStyle("-fx-background-color: white");
                ClientProgramManager.getDuelComponent().EP.setStyle("-fx-background-color: white");
            });
        }

        // 자신만 색깔이 바뀌면 안되므로 상대방도 색깔을 바꾸는 패킷을 보냅니다.
    }


    // 버튼의 상태를 페이지 상황에 따라 바꿉니다.
    // 이것은 상대방도 바꿔줘야 합니다.
    public static void switchPageButton(Page processPage)
    {
        switch(processPage)
        {
            case END:
                ClientProgramManager.getDuelComponent().DP.setDisable(true);
                ClientProgramManager.getDuelComponent().MP.setDisable(true);
                ClientProgramManager.getDuelComponent().AP.setDisable(true);
                ClientProgramManager.getDuelComponent().MP2.setDisable(true);
                ClientProgramManager.getDuelComponent().EP.setDisable(false);
                // packet
            case DRAW:
                ClientProgramManager.getDuelComponent().DP.setDisable(false);
                ClientProgramManager.getDuelComponent().MP.setDisable(true);
                ClientProgramManager.getDuelComponent().AP.setDisable(true);
                ClientProgramManager.getDuelComponent().MP2.setDisable(true);
                ClientProgramManager.getDuelComponent().EP.setDisable(true);
                // packet
            case MAIN:
                ClientProgramManager.getDuelComponent().DP.setDisable(true);
                ClientProgramManager.getDuelComponent().MP.setDisable(false);
                ClientProgramManager.getDuelComponent().AP.setDisable(true);
                ClientProgramManager.getDuelComponent().MP2.setDisable(true);
                ClientProgramManager.getDuelComponent().EP.setDisable(true);
                // packet
                break;
            case MAIN2:
                ClientProgramManager.getDuelComponent().DP.setDisable(true);
                ClientProgramManager.getDuelComponent().MP.setDisable(false);
                ClientProgramManager.getDuelComponent().AP.setDisable(true);
                ClientProgramManager.getDuelComponent().MP2.setDisable(false);
                ClientProgramManager.getDuelComponent().EP.setDisable(true);
                // packet
            case ATTACK:
                ClientProgramManager.getDuelComponent().DP.setDisable(true);
                ClientProgramManager.getDuelComponent().MP.setDisable(true);
                ClientProgramManager.getDuelComponent().AP.setDisable(false);
                ClientProgramManager.getDuelComponent().MP2.setDisable(true);
                ClientProgramManager.getDuelComponent().EP.setDisable(true);
                // packet
        }
    }
}
