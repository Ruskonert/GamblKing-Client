package com.ruskonert.GamblKing.client.game;

import com.ruskonert.GamblKing.client.game.framework.CardFramework;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Collections;

public class DuelEngine
{
    public static final Image CARD_HIDE_IMAGE = null;
    private static ArrayList<CardFramework> frameworks = new ArrayList<>();
    public static ArrayList<CardFramework> randomizeDeck()
    {
        Collections.shuffle(frameworks);
        return frameworks;
    }

    public static void addCard(CardFramework... framework)
    {

    }
}
