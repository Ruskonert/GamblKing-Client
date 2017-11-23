package com.ruskonert.GamblKing.client.program;

import com.ruskonert.GamblKing.client.program.component.ClientComponent;
import com.ruskonert.GamblKing.client.program.component.GameComponent;
import com.ruskonert.GamblKing.client.program.component.SignupComponent;
import com.ruskonert.GamblKing.client.program.component.UpdateComponent;

public class ClientProgramManager
{
    private static SignupComponent signupComponent;

    private static ClientComponent clientComponent;

    private static UpdateComponent updateComponent;

    private static GameComponent gameComponent;

    public static SignupComponent getSignupComponent() { return signupComponent; }
    public static ClientComponent getClientComponent() { return clientComponent; }
    public static UpdateComponent getUpdateComponent() { return updateComponent; }
    public static GameComponent getGameComponent() { return gameComponent; }
}
