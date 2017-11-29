package com.ruskonert.GamblKing.client.game.connect;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruskonert.GamblKing.client.ClientManager;
import com.ruskonert.GamblKing.client.game.DuelApplication;
import com.ruskonert.GamblKing.client.game.DuelPlayer;
import com.ruskonert.GamblKing.client.game.EffectBuilder;
import com.ruskonert.GamblKing.client.game.LoadingApplication;
import com.ruskonert.GamblKing.client.game.entity.EffectElement;
import com.ruskonert.GamblKing.client.game.entity.component.CardType;
import com.ruskonert.GamblKing.client.game.entity.component.Targeting;
import com.ruskonert.GamblKing.client.game.event.DuelLayoutEvent;
import com.ruskonert.GamblKing.client.game.event.Page;
import com.ruskonert.GamblKing.client.game.framework.CardFramework;
import com.ruskonert.GamblKing.client.program.ClientProgramManager;
import com.ruskonert.GamblKing.client.program.component.DuelComponent;
import com.ruskonert.GamblKing.util.SystemUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 클라이언트콰 클라이언트가 게임 중 교환되는 정보를 처리합니다.
 * 게임이 진행될 수 있도록 패킷을 전송하거나 받아 처리하고 데이터나 속성값을 제어할 수 있도록 합니다.
 */
public class GameServerConnection
{
    //
    // 아래에 있는 모든 변수들은 static 상태라도 아무 관련이 없습니다.
    // 이 클래스는 클라이언트마다 별도로 사용되는 것이므로 더욱 효과적으로 사용할 수 있습니다.
    // 잦은 static 남용은 메모리 사용에 지장을 주지만, 아래 변수 모두 주기적으로 사용됩니다.
    private static Thread backgroundThread;
    private static Map<Integer, List<CardFramework>> effectTarget;

    // 당신이 방장이라면 ServerSocket은 null이 아닐 것입니다.
    private ServerSocket socket = null;

    // 효과 스택입니다. 페이지마다 자주 사용됩니다.
    // 플레이어끼리 서로 카드를 발동하면서 효과가 스택에 쌓입니다.
    // 이것은 서로 효과를 무효화한 후, 남는 효과를 발동할 떄 사용합니다.
    private static Map<Integer, EffectBuilder> effectStack = new ConcurrentHashMap<>();

    private static boolean isFirst;

    // 플레이어 타켓입니다.
    private static DuelPlayer processTarget;
    public static DuelPlayer getProcessTarget() { return processTarget; }

    // 현재 진행되고 있는 페이지입니다.
    private static Page processPage;
    public static Page getProcessPage() {
        return processPage;
    }

    private static boolean summoned;
    public static void setSummoned(boolean summoned) {
        GameServerConnection.summoned = summoned;
    }
    public static boolean isSummoned() { return summoned; }

    // 데이터를 읽어올 때 사용하는 소켓입니다.
    private static Socket readSocket  = null;

    private static DataInputStream inputStream;
    private static DataOutputStream outputStream;
    public static DataOutputStream getOutputStream() { return outputStream; }

    // 자신의 플레이어 정보가 담긴 데이터입니다.
    private static DuelPlayer player;
    public static DuelPlayer getPlayer() { return player; }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static List<Integer> targetIndex = new ArrayList<>();
    public static List<Integer> getTargetIndex() { return targetIndex; }

    private static boolean target;
    // 마우스로 무언가를 선택할 때 이것이 타켓 지정 모드인지 참 거짓으로 나타냅니다.
    public static boolean isTargeted() { return target; }

    // 마우스로 무언가를 선택할 때 이것이 타켓 지정 모드인지 결정합니다.
    public static void setTargeted(boolean targeted) {
        GameServerConnection.target = targeted;
    }

    private static Targeting targetArea;
    public static Targeting getTargetArea() { return targetArea; }
    public static void setTargetArea(Targeting targetArea) {
        GameServerConnection.targetArea = targetArea;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////

    private static List<EffectElement> effectElements = new ArrayList<>();
    public static List<EffectElement> getEffects() { return effectElements; }

    private static boolean finished = false;
    public static void setFinished(boolean f) { finished = f; }

    private static List<Integer> usable = new ArrayList<>();
    public static List<Integer> getUsableIndex() { return usable; }


    // 방장일 때 이것이 먼저 호출될 것입니다.
    // 참여자에게 신호를 호출하기 전에 가장 먼저 호출됩니다.
    // 방장이 게임을 시작합니다.
    public void createRoom()
    {
        Task<Void> v = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try
                {
                    socket = new ServerSocket(8822);
                    Platform.runLater(() -> new LoadingApplication().start(new Stage()));

                    // 연결 기다립니다.
                    try {
                        readSocket = socket.accept();
                    }
                    catch(SocketTimeoutException e)
                    {
                        // 상대방과 연결되지 않습니다.
                        // 연결을 종료합니다.
                        SystemUtil.Companion.alert("연결 끊김", "상대방이 연결할 수 없음", "상대방과 연결할 수 없었습니다. 게임을 진행할 수 없습니다.");
                        e.printStackTrace();
                    }

                    Platform.runLater(() -> LoadingApplication.getStage().close());

                    // 연결이 수립되었습니다.
                    GameServerConnection.inputStream = new DataInputStream(readSocket.getInputStream());
                    GameServerConnection.outputStream = new DataOutputStream(readSocket.getOutputStream());

                    Platform.runLater(() -> {
                        new DuelApplication().start(new Stage());
                        // 플레이어를 만들어줍니다.
                        player = new DuelPlayer(ClientManager.getPlayer().getId());
                    });

                    try {
                        Thread.sleep(3000L);
                        new DuelLayoutEvent().register(DuelApplication.getDuelApplication());
                        sendOnlyPacket(0x100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                backgroundThread = new Thread(background);
                backgroundThread.start();
                return null;
            }
        };
        Thread serverThread = new Thread(v);
        serverThread.start();
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 참여자일 때 먼저 호출될 것입니다.
    // 방장이 오픈한 서버 소켓을 통해 게임을 연결합니다.
    public void joinRoom(String address)
    {
        Task<Void> v = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                //처음 시작할 때 이것이 null이 아닐 수 없습니다.
                // 이것이 존재한다는 것은 방장이라는 것과 같습니다.
                if(socket != null)
                {
                    throw new RuntimeException("현재 방장인 상태입니다. 다른 방에 참가하는 것은 불가능합니다.");
                }
                try {
                    // 방장에 연결합니다.
                    readSocket = new Socket(address, 8822);
                    Platform.runLater(() ->new DuelApplication().start(new Stage()));
                    // 연결이 수립되었습니다.
                    GameServerConnection.inputStream = new DataInputStream(readSocket.getInputStream());
                    GameServerConnection.outputStream = new DataOutputStream(readSocket.getOutputStream());
                    player = new DuelPlayer(ClientManager.getPlayer().getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                backgroundThread = new Thread(background);
                backgroundThread.start();
                return null;
            }
        };
        Thread joinThread = new Thread(v);
        joinThread.start();
    }

    // 정보를 상대방에게 전달합니다.
    private static void send(String jsonMessage)
    {
        try {
            outputStream.writeUTF(jsonMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 상태 정보만 전달합니다.
    private static void sendOnlyPacket(int status)
    {
        synchronized (GameServerConnection.class) {
            JsonObject object = new JsonObject();
            object.addProperty("type", status);
            send(object.toString());
        }
    }

    private static boolean selectionMode;
    public static boolean isSelectionMode() { return selectionMode; }

    // 데이터를 주기적으로 받습니다.
    private static Task<Void> background = new Task<Void>() {
        @Override
        protected Void call() throws Exception {
            while(inputStream != null)
            {
                String readJsonString = inputStream.readUTF();
                JsonObject jsonData = new Gson().fromJson(readJsonString, JsonObject.class);

                // 데이터를 받을 때, 각각의 패킷을 해석하고, 이벤트를 발생하게끔 합니다.
                // 데이터에 상대방의 데이터 정보가 담겨 있다면 가져옵니다.
                if(jsonData.get("player") != null) processTarget = new Gson().fromJson(jsonData.get("player").getAsString(), DuelPlayer.class);

                // 이것은 상대방이 나의 카드 정보를 요청한 것입니다.
                // 해당하는 카드를 보내면 됩니다.
                // selectedIndex가 있는 경우는 이것을 제외하고 없습니다.
                if(jsonData.get("selectedIndex") != null)
                {
                    CardFramework framework = GameServerConnection.getPlayer().getSelectedCard(jsonData.get("selectedIndex").getAsInt());
                    Socket cardSocket = new Socket(readSocket.getInetAddress().getHostAddress(), 12246);
                    new DataOutputStream(cardSocket.getOutputStream()).writeUTF(new Gson().toJson(framework));
                    continue;
                }

                switch(jsonData.get("type").getAsInt())
                {
                    // 카드의 전체적인 이미지 및 수량을 다시 출력하라고 하는 신호입니다.
                    // 이것은 카드의 발동, 카드의 파괴같은 이벤트가 발생할 때마다 실시간으로 신호를 받습니다.
                    // 이것은 필드에 카드가 변화하는 것이므로 이미지 또한 바뀌어야 합니다.
                    // 굉장히 무거운 작업이므로 성능 저하에 유의합니다.

                    // 상대의 덱의 갯수를 변경합니다.
                    case 0x10002:
                    {
                        int count = jsonData.get("count").getAsInt();
                        Platform.runLater(() -> ClientProgramManager.getDuelComponent().OtherDeckCount.setText(String.valueOf(count)));
                    }

                    // 상대방으로부터 모든 카드 위치를 읽어옵니다.
                    // 패의 위치, 세트한 카드 이미지, 몬스터등 아주 무거운 작업입니다.
                    case 0x10000:
                    {
                        player.getOtherCards().addAll(processTarget.getCard());
                    }

                    case 0x10020:
                    {
                        DuelComponent.changeTurnButtonColor(false);
                        Page now = new Gson().fromJson(jsonData.get("page"), Page.class);
                        DuelComponent.switchPageButton(now, true);
                        break;
                    }

                    /////////////////////////////////////////// 처음 시작 구간 ///////////////////////////////////////////////

                    // <참여자 전용>
                    // 방장으로부터 플레이어를 만들라는 신호를 받은 경우입니다.
                    case 0x100:
                    {
                        // 플레이어를 만듭니다.
                        player = new DuelPlayer(ClientManager.getPlayer().getId());

                        // 생성 후 초기 드로우 페이지 준비 신호를 넘깁니다.
                        sendOnlyPacket(0x200);
                        break;
                    }


                    // <공통>
                    // 상대방이 카드를 다 뽑았다는 신호를 받은 경우입니다.
                    // 이제 당신만 카드를 뽑으면 될 것입니다.
                    case 0x1001:
                    {

                        initDraw(true);
                        break;
                    }

                    //
                    // <방장 전용>
                    //
                    // 참여자로부터 플레이어를 다 만들었다고 신호를 받은 경우입니다.
                    // 이제 순서를 정할 것입니다.
                    // 순서를 정한 후 카드 5장을 순차대로 뽑을 것입니다.
                    case 0x200:
                    {
                        int first = new Random().nextInt(2);
                        // 만약 방장이 먼저라면
                        if(first == 0)
                        {
                            // 상대에게 후발 주자이라고 보냅니다.
                            JsonObject object = new JsonObject();
                            object.addProperty("isFirst", true);
                            object.addProperty("type", 0x101);
                            isFirst = true;
                            send(object.toString());

                            // 카드를 뽑습니다.
                            // 상대는 아직 카드를 안 뽑은 상태입니다.
                            initDraw(false);
                        }
                        // 상대방이 먼저라면
                        else
                        {
                            // 당신이 후발입니다. <메세지 출력>
                            JsonObject object = new JsonObject();
                            object.addProperty("isFirst", false);
                            object.addProperty("type", 0x101);
                            isFirst = true;
                            send(object.toString());
                        }
                        break;
                    }

                    //
                    // <참여자 전용>
                    //
                    // 참여자로부터 플레이어를 다 만들었다고 신호를 받은 경우입니다.
                    // 이제 순서를 정할 것입니다.
                    // 순서를 정한 후 카드 5장을 순차대로 뽑을 것입니다.
                    case 0x101:
                    {
                        // 상대방의 선 드로우 여부를 확인합니다.
                        boolean first = jsonData.get("isFirst").getAsBoolean();
                        isFirst = true;
                        // 만약 자신이 먼저라면
                        if(!first)
                        {
                            // 당신이 선공입니다. <메세지 출력>
                            // 상대는 아직 카드를 안 뽑은 상태입니다.
                            initDraw(false);
                        }
                        else
                        {
                            // 당신은 후공입니다.
                            // 상대방이 카드를 다 뽑을 때까지 기다립니다.
                            // 이 패킷은 알아서 수신됩니다.
                        }
                        break;
                    }

                    //////////////////////////////////////////////////////////////////////////////////////////////////////////

                    /////////////////////////////////////////////// 한번만 실행됨 ////////////////////////////////////////////////

                    //
                    // <방장 또는 참여자>
                    // 초기 5장 드로우 페이지가 끝났습니다.
                    // 마지막에 5장을 드로우한 유저가 이 신호를 받을 것입니다. 즉, 마지막에 드로우한 사람은 선공이 아닙니다.
                    // 이제 본격적으로 드로우 페이지을 실행하라고 상대에게 넘깁니다.
                    case 0x1002:
                    {
                        DuelComponent.changeTurnButtonColor(false);
                        JsonObject object = new JsonObject();
                        object.addProperty("type", 0x00);
                        object.addProperty("isFirst", true);
                        send(object.toString());
                        break;
                    }

                    //////////////////////////////////////////////////////////////////////////////////////////////////////////

                    /////////////////////////////////////////////// 페이지 순서 /////////////////////////////////////////////////

                    // 페이지는 상대가 이벤트를 확인한 후 각종 발동 이벤트를 받은 후에 허가가 난 후, 다음 페이지로 가거나 계속 진행하는 것입니다.
                    // 이걸 유의하셔야 합니다.

                    // <플레이어>
                    // 상대로부터 드로우 페이지를 시작하라고 받은 경우입니다.
                    case 0x00:
                    {
                        // 페이지 버튼을 내 턴으로 합니다.
                        DuelComponent.changeTurnButtonColor(true);
                        // 드로우 페이지로 변경합니다.
                        processPage = Page.DRAW;
                        // 스위치 상태를 변경합니다.
                        DuelComponent.switchPageButton(processPage);

                        // 카드 1장을 뽑습니다.
                        // 카드을 뽑을 때, 초기라면 상대방은 아무 이벤트를 발동하지 않고 바로 드로우 페이지가 끝날 것입니다.
                        Task<Void> t = new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                player.draw(jsonData.get("isFirst").getAsBoolean());
                                return null;
                            }
                        };

                        Thread drawThread = new Thread(t);
                        drawThread.start();

                        // 드로우 이벤트가 끝날 때가지 기다립니다.
                        // 여기에는 상대가 중간에 사용될 수 있는 것까지도 처리합니다.
                        drawThread.join();

                        // 1초 대기합니다.
                        Thread.sleep(1000L);

                        // 메인 페이지로 갑니다.
                        processPage = Page.MAIN;

                        // 스위치 상태를 변경합니다.
                        DuelComponent.switchPageButton(processPage);

                        // 이 다음부터는 버튼 리스너, 패킷을 통해 통신됩니다.
                        // 여기서 플레이어가 카드를 세트하든, 소환하듯 결정하고 그에 따라서 흐름이 이어질 것입니다.
                        break;
                    }

                    // <바라 보고 있는 플레이어>
                    // 상대의 draw() 또는 페이즈가 넘어갈 때, 카드를 발동할 때, 공격 페이지를 받을 때 등
                    // 이벤트가 발생하면 무조건 0x10 패킷을 호출 받습니다. 그 때 발동하거나 기준이 되는 card 정보가 담겨 있습니다.
                    // 상대
                    // 즉, 상대방이 드로우를 할 때, 이것이 실행될 것입니다.
                    // 상대에 AcceptedEvent에서 ServerSocket:12247을 열고 승인 신호를 대기하고 있으며 0x10 패킷이
                    // inputStream으로 들어온 상태입니다.
                    // 상대 드로우 페이지 중 드로우 이벤트를 받을 때, 선공이라면 자신이 발동할 카드는 전혀 없습니다.
                    // 처음이 아니라면, 조건에 맞는 함정 카드가 있을 때 정상적으로 사용할 수 있습니다.
                    case 0x10:
                    {
                        // 처음 턴하는 상황인지 검사합니다.
                        boolean isFirst = jsonData.get("isFirst").getAsBoolean();

                        // 처음 뽑는다면 세트된 함정카드는 당연히 없습니다.
                        // 그냥 승인합니다.
                        if(isFirst)
                        {
                            new Socket(readSocket.getInetAddress().getHostAddress(), 12247);
                        }
                        else
                        {
                            // 이벤트를 받은 정보를 읽고 발동 할 수 있는 카드를 체크합니다.
                            // 스폐셜 필드에 뒤집어져 있는 카드 중 함정카드를 가지고 옵니다.
                            Map<Integer, CardFramework> specialCardsField = player.getSpecialCardsField();
                            for(int i = 13; i <= 17; i++)
                            {
                                if(specialCardsField.get(i) != null)
                                {
                                    CardFramework f = specialCardsField.get(i);
                                    // 함정 카드이면서 뒤집어져 있다면
                                    if(f.isHide() && f.getCardType() == CardType.TARP)
                                    {
                                        // 해당 인덱스를 번호에 해당되는 카드 사용할 수 있다는 뜻입니다.
                                        usable.add(i);
                                    }
                                }
                            }

                            //만약 발동할 수 있는 카드가 있다면
                            if(usable.size() != 0)
                            {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION, "발동할 수 있는 카드가 있습니다. 발동하시겠습니까?", ButtonType.OK, ButtonType.CANCEL);
                                alert.setTitle("카드 사용 가능");
                                Optional<ButtonType> result = alert.showAndWait();
                                if(result.get() == ButtonType.OK)
                                {
                                    // 타켓팅 모드가 활성화되었습니다. 마우스를 통해 인덱스에 있는 것 중에서 카드를 사용할 수 있습니다.
                                    GameServerConnection.selectionMode = true;
                                    Task<Void> t = new Task<Void>() {
                                        @Override
                                        protected Void call() throws Exception
                                        {
                                            while(!finished)
                                            {
                                                // 선택을 다할 때까지 계속 돌립니다.
                                            }
                                            return null;
                                        }
                                    };
                                    Thread wait = new Thread(t);
                                    //이 일이 끝날 때까지 기다립니다.
                                    wait.join();

                                    GameServerConnection.finished = false;
                                    GameServerConnection.selectionMode = false;
                                    // 이펙트 스택이 쌓였다면 이 효과를 이제 발동시키면 되는 것입니다.
                                    if(effectStack.values().size() != 0)
                                    {
                                        // 효과 발동!
                                    }
                                    else
                                    {
                                        // 아무 효과가 누적되지 않았으므로 승인합니다.
                                        new Socket(readSocket.getInetAddress().getHostAddress(), 12247);
                                    }
                                }
                                else
                                {
                                    // 카드를 발동하지 않겠다고 취소했으므로 승인합니다.
                                    new Socket(readSocket.getInetAddress().getHostAddress(), 12247);
                                }
                            }
                            else
                            {
                                // 발동할 수 있는 카드가 없다면 승인합니다.
                                new Socket(readSocket.getInetAddress().getHostAddress(), 12247);
                            }
                        }
                        break;
                    }
                    default:
                    {

                    }
                }
            }
            return null;
        }
    };

    // 5장 드로우합니다.
    // finish는 상대방이 카드를 5장 뽑은 것인지 확인하는 것입니다.
    private static void initDraw(boolean finish)
    {
        Task<Void> draw = new Task<Void>() {
            @Override protected Void call() throws Exception {
                for(int i = 0; i < 5; i++) {
                    player.cardDraw();
                    // 상대방도 이미지 변경하는 패킷 전송
                    GameServerConnection.getPlayer().refreshCardImage();
                    Thread.sleep(1000L);
                }
                return null;
            }
        };

        Thread thread = new Thread(draw);
        thread.start();
        try
        {
            thread.join();
            // 상대방이 아직 패를 안 뽑은 경우에는
            // 상대방도 카드를 뽑아야 합니다.
            if(!finish) sendOnlyPacket(0x1001);

            // 상대방이 이미 패를 다 뽑은 경우에는
            // 초기 드로우 페이지를 마치고 본격적으로 게임을 시작합니다.
            else sendOnlyPacket(0x1002);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 바라보는 플레이어가 효과를 발동합니다.
    // EffectBuilder가 apply 할 때, 그에 맞게 패킷을 보낼 것입니다.
    // 이것이 발동될 때, 자기 차례의 플레이어는 이때까지도 바라보는 플레이어의 승인 대기를 위해 대기하고 있을 것입니다.
    private static void useEffect()
    {
        if(effectStack.size() != 0)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "발동할 수 있는 카드가 있습니다. 발동하시겠습니까?", ButtonType.OK, ButtonType.CANCEL);
            alert.setTitle("카드 사용 가능");
            Optional<ButtonType> result = alert.showAndWait();
            // OK 했을 경우
            if (result.get() == ButtonType.OK)
            {
                while(effectStack.size() != 0)
                {
                    Platform.runLater(() -> effectStack.get(effectStack.size() -1).apply());
                }
            }
        }
        else
        {
            try
            {
                // 승인 신호를 보냅니다.
                new Socket(readSocket.getInetAddress().getHostAddress(), 12247);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
