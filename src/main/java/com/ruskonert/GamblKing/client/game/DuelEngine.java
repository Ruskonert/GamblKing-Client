package com.ruskonert.GamblKing.client.game;

import com.ruskonert.GamblKing.client.game.framework.CardFramework;
import com.ruskonert.GamblKing.util.SystemUtil;
import javafx.scene.image.Image;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

public class DuelEngine
{
    public static Image CARD_HIDE_IMAGE = null;

    static
    {
        try {
            CARD_HIDE_IMAGE  = new Image(SystemUtil.Companion.getStylePath("card_back.png").toURI().toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<CardFramework> frameworks = new ArrayList<>();
    public static ArrayList<CardFramework> randomizeDeck() {
        Collections.shuffle(frameworks);
        return frameworks;

    }

    static
    {
        try
        {
            for(int i = 1; i < 10; i++)
            {
                    Class<?> clazz = Class.forName("com.ruskonert.GamblKing.client.game.entity.card.Monster0" + String.valueOf(i));
                    Constructor<?> c = clazz.getConstructors()[0];
                    CardFramework f = (CardFramework) c.newInstance();
                    frameworks.add(f);
            }

            for(int i = 10; i < 41; i++)
            {
                Class<?> clazz = Class.forName("com.ruskonert.GamblKing.client.game.entity.card.Monster" + String.valueOf(i));
                Constructor<?> c = clazz.getConstructors()[0];
                CardFramework f = (CardFramework) c.newInstance();
                frameworks.add(f);
            }

            for(int i = 41; i < 51; i++)
            {
                if(i == 43) continue;
                Class<?> clazz = Class.forName("com.ruskonert.GamblKing.client.game.entity.card.Magic" + String.valueOf(i));
                Constructor<?> c = clazz.getConstructors()[0];
                CardFramework f = (CardFramework) c.newInstance();
                frameworks.add(f);
            }

            for(int i = 52; i < 61; i++)
            {
                Class<?> clazz = Class.forName("com.ruskonert.GamblKing.client.game.entity.card.Trap" + String.valueOf(i));
                Constructor<?> c = clazz.getConstructors()[0];
                CardFramework f = (CardFramework) c.newInstance();
                frameworks.add(f);
            }
        }
         catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException e)
         {
             e.printStackTrace();
         }
    }


    public static void addCard(CardFramework... framework)
    {
    }
}
