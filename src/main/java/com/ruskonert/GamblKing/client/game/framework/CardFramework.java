package com.ruskonert.GamblKing.client.game.framework;

import com.ruskonert.GamblKing.client.game.EffectBuilder;
import com.ruskonert.GamblKing.client.game.entity.component.ActivateCost;
import com.ruskonert.GamblKing.client.game.entity.component.CardType;
import com.ruskonert.GamblKing.client.game.entity.component.Effect;
import javafx.scene.image.Image;

import java.util.List;
import java.util.Map;

public abstract class CardFramework
{

    private Image image;
    public Image getImage() { return this.image; }
    public void setImage(Image image) { this.image = image; }

    private String name;

    public CardFramework(CardType cardType)
    {
        this.cardType = cardType;
    }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    private String description;
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public void setDescription(String... description)
    {
        StringBuilder builder = new StringBuilder();
        for(String s : description)
        {
            builder.append(s).append("\n");
        }
        this.description = builder.toString();
    }

    // 소환되었을 때, 나오는 이펙트를 설정합니다.
    public void setSummonSound(String path) { /* Complied code */ }

    private boolean show;
    public boolean isHide() { return !this.show; }

    private CardType cardType;
    public CardType getCardType() { return this.cardType; }

    private ActivateCost activateCost = ActivateCost.NONE;
    /**
     * 해당 카드가 발동해야 할 조건입니다. 임의적으로 지정해주지 않으면
     * 기본 발동 조건은 전혀 없습니다.
     * @param activateCost
     */
    public void setActivateCost(ActivateCost activateCost) {
        this.activateCost = activateCost;
    }

    /**
     * 발동 조건을 가져옵니다.
     * @return
     */
    public ActivateCost getActivateCost() {
        return activateCost;
    }

    private List<EffectBuilder> effects;
    public List<EffectBuilder> getEffects() { return effects; }

    // image
    // getter image
    public void setImage(String path) { /* Complied code */  getClass().getResource("/style/" + path); }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof CardFramework)
        {
            CardFramework framework = (CardFramework)obj;
            return (this.getCardType() == framework.getCardType()) && this.getName().equalsIgnoreCase(framework.getName());
        }
        else
        {
            return false;
        }
    }

    public void setShow(boolean show)
    {
        this.show = show;
    }
}
