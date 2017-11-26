package com.ruskonert.GamblKing.client.program;

import com.ruskonert.GamblKing.client.ClientLoader;
import com.ruskonert.GamblKing.client.ClientManager;
import com.ruskonert.GamblKing.client.event.layout.GameLayoutEvent;
import com.ruskonert.GamblKing.client.program.component.ClientComponent;
import com.ruskonert.GamblKing.client.program.component.GameComponent;
import com.ruskonert.GamblKing.client.program.component.RoomComponent;
import com.ruskonert.GamblKing.client.program.component.SignupComponent;
import com.ruskonert.GamblKing.client.program.component.UpdateComponent;
import javafx.application.Platform;

public class ClientProgramManager
{
    private static SignupComponent signupComponent;

    private static ClientComponent clientComponent;

    private static UpdateComponent updateComponent;

    private static GameComponent gameComponent;

    private static RoomComponent roomComponent;

    public static SignupComponent getSignupComponent() { return signupComponent; }
    public static ClientComponent getClientComponent() { return clientComponent; }
    public static UpdateComponent getUpdateComponent() { return updateComponent; }
    public static GameComponent getGameComponent() { return gameComponent; }
    public static RoomComponent getRoomComponent() { return roomComponent; }


    public static void openGameLayout()
    {
        Platform.runLater(() -> {
            RoomApplication.getStage().close();
            RoomApplication.setStage(null);
            ClientLoader.getMusicThread().interrupt();

            ClientManager.setMusic("MainStage.mp3");
            GameApplication.getStage().show();


            GameLayoutEvent.threadInitialize();
            GameLayoutEvent.getSystemTimeThread().start();
        });
    }
}
