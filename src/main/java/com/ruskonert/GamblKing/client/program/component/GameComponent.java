package com.ruskonert.GamblKing.client.program.component;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import com.ruskonert.GamblKing.client.program.ClientProgramManager;
import com.ruskonert.GamblKing.util.ReflectionUtil;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public final class GameComponent implements Initializable
{
    public AnchorPane HomeButton;
    public AnchorPane OnlineButton;
    public AnchorPane SettingsButton;
    public Label ServerTime;
    
    public AnchorPane HomePane;
    public TextArea ChatBox;
    public ListView PlayerBox;
    public TextField MessageBox;
    public JFXButton SendMessageButton;
    public JFXToggleButton EnableBGM;
    public JFXToggleButton EnableSoundEffect;
    
    public AnchorPane OnlinePage;
    public TableView TableCreateRoom;
    public TableColumn TableRoomNumber;
    public TableColumn TableRoomName;
    public JFXButton RefreshRoomList;
    public Label LastRefreshTime;
    public TextField CreateRoomName;
    public JFXButton CreateRoomButton;
    
    public AnchorPane UserPanel;
    public Label LastConnected;
    public Label UserScore;
    public Label LastBattle;
    public Label ConnectedIP;
    public AnchorPane SettingPage;
    public Label Username;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try
        {
            ReflectionUtil.Companion.setStaticField(ClientProgramManager.class, "gameComponent", this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
