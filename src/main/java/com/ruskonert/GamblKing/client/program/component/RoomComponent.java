package com.ruskonert.GamblKing.client.program.component;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.ruskonert.GamblKing.client.program.ClientProgramManager;
import com.ruskonert.GamblKing.util.ReflectionUtil;
import com.ruskonert.GamblKing.util.SystemUtil;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class RoomComponent implements Initializable
{
    public AnchorPane TopPane;
    public JFXButton QuitButton;
    public Label RoomName;
    public Label RoomNumber;
    public JFXButton StartButton;

    public AnchorPane UserPane;
    public Label PlayerName;
    public Label PlayerId;
    public Label PlayerStat;

    public AnchorPane AnotherUserPane;
    public Label CapacityStatus;
    public Label CreateTime;
    public Label AnotherPlayerName;
    public Label AnotherPlayerId;
    public Label AnotherPlayerStat;

    public JFXTextArea ChatBox;
    public TextField MessageBox;
    public JFXButton SendMessageButton;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        try
        {
            ReflectionUtil.Companion.setStaticField(ClientProgramManager.class, "roomComponent", this);
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            SystemUtil.Companion.error(e);
        }
    }
}
