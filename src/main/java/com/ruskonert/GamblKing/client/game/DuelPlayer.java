package com.ruskonert.GamblKing.client.game;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruskonert.GamblKing.client.game.connect.GameServerConnection;
import com.ruskonert.GamblKing.client.game.entity.EffectElement;
import com.ruskonert.GamblKing.client.game.entity.component.CardType;
import com.ruskonert.GamblKing.client.game.entity.component.Targeting;
import com.ruskonert.GamblKing.client.game.event.DrawEvent;
import com.ruskonert.GamblKing.client.game.framework.CardFramework;
import com.ruskonert.GamblKing.client.game.framework.MonsterCard;
import com.ruskonert.GamblKing.client.program.ClientProgramManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class DuelPlayer implements Serializable
{
    private int life;
    public int getLife() { return this.life; }

    private ArrayList<CardFramework> deck;
    public ArrayList<CardFramework> getDeck() { return deck; }
    private transient ImageView deckImage;
    public ImageView getDeckImage() { return deckImage; }

    private ArrayList<CardFramework> card;
    public ArrayList<CardFramework> getCard() { return card; }
    private transient ArrayList<ImageView> cardImage;
    public ArrayList<ImageView> getCardImage() { return cardImage; }

    private Map<Integer, CardFramework> specialCardsField;
    public Map<Integer, CardFramework> getSpecialCardsField() { return specialCardsField; }
    private transient ArrayList<ImageView> specialCardsFieldImage;
    public ArrayList<ImageView> getSpecialCardsFieldImage() { return specialCardsFieldImage; }

    private Map<Integer, MonsterCard> monsterCardsField;
    public Map<Integer, MonsterCard>  getMonsterCardsField() { return monsterCardsField; }
    private transient ArrayList<ImageView> monsterCardsFieldImage;
    public ArrayList<ImageView> getMonsterCardsFieldImage() { return monsterCardsFieldImage; }

    private ArrayList<CardFramework> deadCards;
    public ArrayList<CardFramework> getDeadCards() { return deadCards; }
    private transient ImageView lastDeadCardImage;

    private ArrayList<CardFramework> exceptCards;
    public ArrayList<CardFramework> getExceptCards() { return exceptCards; }
    private transient ImageView lastExceptCards;

    private int selectedIndex = -1;
    public void setSelectedIndex(int selectIndex)
    {
        this.selectedIndex = selectIndex;
    }
    public int getSelectedIndex() { return this.selectedIndex; }

    private CardFramework lastSelected = null;
    public void setLastSelected(CardFramework lastSelected) { this.lastSelected = lastSelected; }
    public CardFramework getLastSelected() { return this.lastSelected; }

    private List<Integer> usableIndex;
    public List<Integer> getUsableIndex() { return usableIndex; }

    /**
     * id를 가지는 플레이어를 생성하고 기본값을 초기화합니다
     * @param id
     */
    public DuelPlayer(String id)
    {
        this.deck = DuelEngine.randomizeDeck();
        this.life = 8000;
        this.card = new ArrayList<>();
        this.cardImage = new ArrayList<>();

        this.specialCardsField = new ConcurrentHashMap<>();
        this.monsterCardsField = new ConcurrentHashMap<>();
        this.deadCards = new ArrayList<>();
        this.exceptCards = new ArrayList<>();

        this.specialCardsFieldImage = new ArrayList<>();
        this.monsterCardsFieldImage = new ArrayList<>();
        this.usableIndex = new ArrayList<>();

        Platform.runLater(() ->ClientProgramManager.getDuelComponent().MyName.setText(id));

        // 카드 위치를 지정해줍니다.
        // 순서대로 대입됩니다.
        monsterCardsFieldImage.add(ClientProgramManager.getDuelComponent().MonsterField1);
        monsterCardsFieldImage.add(ClientProgramManager.getDuelComponent().MonsterField2);
        monsterCardsFieldImage.add(ClientProgramManager.getDuelComponent().MonsterField3);
        monsterCardsFieldImage.add(ClientProgramManager.getDuelComponent().MonsterField4);
        monsterCardsFieldImage.add(ClientProgramManager.getDuelComponent().MonsterField5);

        specialCardsFieldImage.add(ClientProgramManager.getDuelComponent().SpecialField1);
        specialCardsFieldImage.add(ClientProgramManager.getDuelComponent().SpecialField2);
        specialCardsFieldImage.add(ClientProgramManager.getDuelComponent().SpecialField3);
        specialCardsFieldImage.add(ClientProgramManager.getDuelComponent().SpecialField4);
        specialCardsFieldImage.add(ClientProgramManager.getDuelComponent().SpecialField5);

        cardImage.add(ClientProgramManager.getDuelComponent().MyCard1);
        cardImage.add(ClientProgramManager.getDuelComponent().MyCard2);
        cardImage.add(ClientProgramManager.getDuelComponent().MyCard3);
        cardImage.add(ClientProgramManager.getDuelComponent().MyCard4);
        cardImage.add(ClientProgramManager.getDuelComponent().MyCard5);
        cardImage.add(ClientProgramManager.getDuelComponent().MyCard6);
        cardImage.add(ClientProgramManager.getDuelComponent().MyCard7);
    }

    public void goToDead(CardFramework framework)
    {
        // card image change on field
        this.deadCards.add(framework);
        // 카드 묘지 이벤트
        // 마지막 묘지 카드의 이미지 변경
    }

    public void goToExcept(CardFramework framework)
    {
        // card image change on field
        this.exceptCards.add(framework);
        // 카드 제외 이벤트
        // 마지막 제외 카드의 이미지 변경
    }

    public void useMyCard(int cardIndex)
    {
        if(cardIndex > 7 || cardIndex < 0)
        {
            throw new RuntimeException("wrong card index number");
        }
        else
        {
            CardFramework framework = card.get(cardIndex);
            card.remove(framework);
            // 패에서 가져온 카드 이미지 제거후 패 이미지 다시 정렬
            // 카드 발동 이벤트
        }
    }

    // 카드 드로우을 처리합니다. isFirst는 처음 드로우인지 결정합니다.
    public void draw(boolean isFirst)
    {
        // 덱이 없다면
        if(getDeck().size() == 0)
        {
            // 더이상 뽑을 카드가 없으므로 당신은 패배자입니다.
            // 상대방이 이겼습니다.
            return;
        }
        // 카드를 드로우합니다. 그리고 그 뽑은 카드를 반환합니다.
        CardFramework f = this.cardDraw();

        // 만약 처음이 아니라면 (2번째 턴 이상이라면)
        if(!isFirst)
        {
            Task<Void> event = new Task<Void>() {
                @Override
                protected Void call() throws Exception
                {
                    // 카드 뽑기 전에 상대에게 승인 요청을 보냅니다.
                    // 이 때 요청 패킷에는 자신이 뽑은 카드의 정보가 담겨있습니다.
                    DrawEvent event = new DrawEvent(f);
                    // 확인 신호를 받을 때까지 기다립니다.
                    event.accept();
                    return null;
                }
            };

            Thread drawEventThread = new Thread(event);
            drawEventThread.start();

            try
            {
                drawEventThread.join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
    /**
     * 카드의 데이터를 리스트에 올리고 그에 따른 그림을 업데이트합니다.
     * @return
     */
    public CardFramework cardDraw()
    {
        CardFramework card = this.deck.get(this.deck.size() - 1);

        // 카드를 뽑습니다.
        this.getCard().add(card);

        // 뽑은 카드를 제거합니다.
        this.deck.remove(card);
        Platform.runLater(() -> ClientProgramManager.getDuelComponent().DeckCount.setText(String.valueOf(this.deck.size())));

        // 덱 새로고침
        this.refreshDeckImage();

        // 이미지 새로고침
        this.refreshCardImage();
        return card;
    }


    public void destory(int selectedIndex)
    {

    }

    // 카드를 발동합니다. 이것은 메인 페이즈와 메인 페이즈2 인 경우에만 사용 가능합니다.
    public void activateCard(int index)
    {
            // 패에서 발동하는 경우
            if(index >= 0 && index <= 7)
            {
                // 이 카드의 정보를 가지고 옵니다.
                CardFramework framework = this.getSelectedCard(index);
                // 몬스터 카드라면 패에서 소환하는 것과 같습니다.
                if(framework.getCardType() == CardType.MONSTER)
                {
                    // 몬스터를 더이상 소환할 수 없다면
                    if(this.monsterCardsField.size() >= 5)
                    {
                        Alert alert =
                                new Alert(Alert.AlertType.INFORMATION, "몬스터 존이 가득찼습니다.",
                                        ButtonType.OK);
                        alert.setTitle("카드 소환 불가");
                        alert.showAndWait();
                    }
                    // 소환 가능하다면
                    else
                    {
                        // 만약 이번 턴에 1번 소환했을 경우
                        if(GameServerConnection.isSummoned())
                        {
                            Alert alert =
                                    new Alert(Alert.AlertType.INFORMATION, "자신에 전체 턴에서 몬스터는 1마리만 소환 가능합니다.", ButtonType.OK);
                            alert.setTitle("카드 소환 불가");
                            alert.showAndWait();
                            return;
                        }
                        // 선택한 카드를 패에서 제거합니다.
                        this.getCard().remove(framework);
                        // 패 이미지를 다시 불러옵니다.
                        this.refreshCardImage();
                        // 상대방도 마찬가지로 다시 새로고침합니다.

                        // 필드에 비어있는 자리에 몬스터가 나옵니다.
                        for(int i = 0; i < 5; i++)
                        {
                            if(this.monsterCardsField.get(i) == null)
                            {
                                this.monsterCardsField.put(i, (MonsterCard) framework);
                                GameServerConnection.setSummoned(true);
                                framework.playSummonSound();

                                // 필드 위 몬스터 카드의 모습을 바꿔줍니다.
                                this.refreshMonsterImage();
                                // 상대방도 몬스터 필드 이미지를 바꿔줘야 합니다.

                                // 몬스터 소환 이벤트을 승인받아 정상적으로 마무리합니다.
                                break;
                            }
                        }
                    }
                }
                else if(framework.getCardType() == CardType.MAGIC)
                {
                    if(this.specialCardsField.size() >= 5)
                    {
                        Alert alert =
                                new Alert(Alert.AlertType.INFORMATION, "스폐셜 존이 가득찼습니다.",
                                        ButtonType.OK);
                        alert.setTitle("카드 사용 불가");
                    }
                    else
                    {
                        this.getCard().remove(framework);
                        this.refreshCardImage();
                        for (int i = 0; i < 5; i++)
                        {
                            if (this.specialCardsField.get(i) == null) {
                                this.specialCardsField.put(i, (MonsterCard) framework);
                                this.refreshSpecialFieldImage();
                                this.activateEffect(framework);
                                break;
                            }
                        }
                    }
                }
                else
                {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "함정 카드는 바로 사용이 불가능합니다. 세트 후 사용할 수 있습니다.", ButtonType.OK);
                    alert.setTitle("카드 사용 불가");
                }
            }
            // 스폐셜 존에서 발동
            else if(13 <= index && index <= 17)
            {
                if(! this.usableIndex.contains(index))
                {
                    // 이 카드는 현재 사용할 수 없습니다.
                    // 메세지 박스 띄우기
                }
                else
                {
                    CardFramework framework = this.getSelectedCard(index);
                    // 엑티베이트 효과 그림 띄우기
                    framework.setShow(true);


                    this.refreshSpecialFieldImage();

                    // 함정 카드 엑티비티 이벤트 승인
                }

            }
            // CardActivateEvent 승인 대기

    }

    // 효과를 구축합니다.
    public void activateEffect(CardFramework framework)
    {
        for(EffectBuilder b : framework.getEffects())
        {
            int count = b.getCount();
            Targeting targeting = b.getTargeting();

            // 카드를 무조건 1장 이상 타켓팅이 있는 경우
            if(count > 0)
            {
                if (targeting != null) {
                    GameServerConnection.setTargetArea(targeting);
                    GameServerConnection.setTargeted(true);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "카드 " + b.getCount() + "개를 선택하세요.", ButtonType.OK);
                    alert.setTitle("카드 선택");
                    Task<Void> s = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            while (GameServerConnection.isTargeted()) {
                                if (GameServerConnection.getTargetIndex().size() == count) {
                                    break;
                                }
                            }
                            return null;
                        }
                    };

                    Thread selection = new Thread(s);
                    selection.start();
                    try {
                        selection.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //  갯수를 다 선택하기 전까지는 기다립니다.
                }
                if(targeting == Targeting.ALL)
                {
                    GameServerConnection.getEffects().add(new EffectElement(b.getEffect(), GameServerConnection.getProcessTarget(),
                            GameServerConnection.getTargetIndex()));
                    GameServerConnection.getTargetIndex().clear();

                    GameServerConnection.getEffects().add(new EffectElement(b.getEffect(), GameServerConnection.getPlayer(),
                            GameServerConnection.getTargetIndex()));
                    GameServerConnection.getTargetIndex().clear();
                }
                else if(targeting == Targeting.THIS)
                {
                    GameServerConnection.getEffects().add(new EffectElement(b.getEffect(), GameServerConnection.getPlayer(),
                            GameServerConnection.getTargetIndex()));
                    GameServerConnection.getTargetIndex().clear();
                }
                else
                {
                    GameServerConnection.getEffects().add(new EffectElement(b.getEffect(), GameServerConnection.getProcessTarget(),
                            GameServerConnection.getTargetIndex()));
                    GameServerConnection.getTargetIndex().clear();
                }
            }
            // 카드 타켓팅이 없는 순수 효과인 경우
            else
            {
                if(targeting == Targeting.ALL)
                {

                }
                else if(targeting == Targeting.THIS)
                {

                }
                else
                {

                }
            }
        }
    }

    // 몬스터 카드 이미지를 새로 가져옵니다.
    public void refreshMonsterImage()
    {
        Task<Void> refreshCardThread = new Task<Void>() {
            @Override
            protected Void call() throws Exception
            {
                // 몬스터 필드의 인덱스 번호입니다.
                for(int i = 7; i <= 12; i++)
                {
                    ImageView view = cardImage.get(i);
                    // 만약 카드의 모습은 있는데 그 인덱스 자리에 카드 정보가 없다면 -> 파괴된 카드
                    if(card.get(i) == null && view.getImage() != null)
                    {
                        // this.cardImage.get(i).getImage()에 없어지는 이펙트 애니메이션 작동
                        //
                        Platform.runLater(() -> view.setImage(null));
                        try
                        {
                            Thread.sleep(600L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    // 아니라면
                    else
                    {
                        CardFramework framework = card.get(i);

                        //만약 몬스터가 세트 상태이고 이미지가 있다면
                        if(!framework.isHide() && view.getImage() == DuelEngine.CARD_HIDE_IMAGE)
                        {
                            // 세트된 카드가 발동된 이펙트 애니메이션 작동
                        }
                        Platform.runLater(() -> view.setImage(framework.isHide() ? DuelEngine.CARD_HIDE_IMAGE : framework.getImage()));
                        try {
                            Thread.sleep(30L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }
        };
        Thread thread = new Thread(refreshCardThread);
        thread.start();
    }

    // 패에 있는 이미지를 새로 불러옵니다.
    public void refreshCardImage()
    {
        Task<Void> refreshCardThread = new Task<Void>() {
            @Override
            protected Void call() throws Exception
            {
                // 자신의 패 이미지를 바꿉니다.
                for(int i = 0; i < 7; i++)
                {
                    ImageView view = cardImage.get(i);
                    if(card.get(i) == null && view.getImage() != null)
                    {
                        // this.cardImage.get(i).getImage()에 없어지는 이펙트 애니메이션 작동
                        Platform.runLater(() -> view.setImage(null));
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        view.setVisible(false);
                    }
                    else
                    {
                        CardFramework framework = card.get(i);
                        Platform.runLater(() -> view.setImage(framework.getImage()));
                        view.setVisible(true);
                        try {
                            Thread.sleep(30L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }
        };
        Thread thread = new Thread(refreshCardThread);
        thread.start();
    }

    public void refreshDeckImage()
    {

    }


    public void refreshSpecialFieldImage()
    {
        Task<Void> refreshCardThread = new Task<Void>() {
            @Override
            protected Void call() throws Exception
            {
                // 자신의 패 이미지를 바꿉니다.
                for(int i = 13; i <= 17; i++)
                {
                    ImageView view = cardImage.get(i);
                    if(card.get(i) == null && view.getImage() != null)
                    {
                        // this.cardImage.get(i).getImage()에 없어지는 이펙트 애니메이션 작동
                        Platform.runLater(() -> view.setImage(null));
                        try
                        {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        CardFramework framework = card.get(i);
                        if(!framework.isHide() && view.getImage() == DuelEngine.CARD_HIDE_IMAGE)
                        {
                            // 세트된 카드가 발동된 이펙트 애니메이션 작동
                        }
                        Platform.runLater(() -> view.setImage(framework.isHide() ? DuelEngine.CARD_HIDE_IMAGE : framework.getImage()));
                        try {
                            Thread.sleep(30L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }
        };
        Thread thread = new Thread(refreshCardThread);
        thread.start();
    }

    public CardFramework getSelectedCard(int selectedIndex)
    {
        if(selectedIndex < 0 || selectedIndex < 40) try
        {
            throw new IllegalAccessException("out of range number (0-40)");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if(selectedIndex >= 0 && selectedIndex <= 6) return this.card.get(selectedIndex);
        else if(selectedIndex >= 7 && selectedIndex <= 12) return this.monsterCardsField.get(selectedIndex);
        else if(selectedIndex >= 13 && selectedIndex <= 17) return this.specialCardsField.get(selectedIndex);
        else if(selectedIndex == 18) return null; // not implemented
        else if(selectedIndex == 19) return null; // not implemented
        else if(selectedIndex == 20) try {
            throw new IllegalAccessException("덱 카드를 가져오려면 getDeck()을 사용하십시오.");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        else if(selectedIndex >= 21 && selectedIndex <= 40) return this.getOthersCard(selectedIndex - 21);
        else throw new NullPointerException("out of range number (0-40)");
    }

    public ImageView getSelectedImageView(int selectedIndex)
    {
        if(selectedIndex < 0 || selectedIndex < 40) try
        {
            throw new IllegalAccessException("out of range number (0-40)");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if(selectedIndex >= 0 && selectedIndex <= 6) return this.monsterCardsFieldImage.get(selectedIndex);
        else if(selectedIndex >= 7 && selectedIndex <= 12) return this.monsterCardsFieldImage.get(selectedIndex);
        else if(selectedIndex >= 13 && selectedIndex <= 17) return this.specialCardsFieldImage.get(selectedIndex);
        else if(selectedIndex == 18) return this.lastDeadCardImage;
        else if(selectedIndex == 19) return this.lastExceptCards;
        else if(selectedIndex == 20) return this.deckImage;
        else if(selectedIndex >= 21 && selectedIndex <= 26) return null;
        else throw new NullPointerException("out of range number (0-40)");
    }

    private CardFramework getOthersCard(int selectedIndex)
    {
        Task<CardFramework> com = new Task<CardFramework>() {
            @Override
            protected CardFramework call() throws Exception {
                JsonObject object = new JsonObject();
                object.addProperty("selectedIndex", selectedIndex);
                ServerSocket socket = new ServerSocket(12246);
                Socket acceptSocket = socket.accept();
                new DataOutputStream(acceptSocket.getOutputStream()).writeUTF(object.toString());
                String jsonMessage = new DataInputStream(acceptSocket.getInputStream()).readUTF();
                return new Gson().fromJson(jsonMessage, CardFramework.class);
            }
        };
        try
        {
            return com.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}
