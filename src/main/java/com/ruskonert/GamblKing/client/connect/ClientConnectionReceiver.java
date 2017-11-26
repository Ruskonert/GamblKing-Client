package com.ruskonert.GamblKing.client.connect;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ruskonert.GamblKing.adapter.PlayerAdapter;
import com.ruskonert.GamblKing.adapter.RoomAdapter;
import com.ruskonert.GamblKing.client.ClientLoader;
import com.ruskonert.GamblKing.client.ClientManager;
import com.ruskonert.GamblKing.client.connect.packet.PlayerRefreshPacket;
import com.ruskonert.GamblKing.client.connect.packet.RoomRefreshPacket;
import com.ruskonert.GamblKing.client.event.RoomCreateEvent;
import com.ruskonert.GamblKing.client.event.RoomUpdateEvent;
import com.ruskonert.GamblKing.client.event.layout.GameLayoutEvent;
import com.ruskonert.GamblKing.client.program.ClientProgramManager;
import com.ruskonert.GamblKing.client.program.RoomApplication;
import com.ruskonert.GamblKing.client.program.SignupApplication;
import com.ruskonert.GamblKing.client.program.UpdateApplication;
import com.ruskonert.GamblKing.connect.packet.RoomConnectPacket;
import com.ruskonert.GamblKing.entity.Player;
import com.ruskonert.GamblKing.entity.Room;
import com.ruskonert.GamblKing.framework.PlayerEntityFramework;
import com.ruskonert.GamblKing.framework.RoomFramework;
import com.ruskonert.GamblKing.property.ServerProperty;
import com.ruskonert.GamblKing.util.SystemUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ClientConnectionReceiver는 서버에 받은 패킷을 바탕으로 하여 작업을 처리하는 클래스입니다.
 * 이 클래스는 수신기능만 하며 이 클래스를 통한 발신 기능은 지원하지 않습니다.
 */
public class ClientConnectionReceiver
{
    private static String id;
    public static String getId() { return id; }

    private static Socket socket;
    public static Socket getSocket() { return socket; }

    private static DataInputStream in;
    public DataInputStream getInputStream() { return in; }

    private static DataOutputStream out;
    public static DataOutputStream getOutputStream() { return out;}

    private static PlayerEntityFramework playerEntityFramework;

    public static void setPlayerEntityFramework(PlayerEntityFramework framework) { playerEntityFramework = framework; }
    public static PlayerEntityFramework getPlayerEntityFramework() { return playerEntityFramework; }

    // 패킷을 받고 보내는 작업 쓰레드입니다.
    private static Thread taskBackground;

    // 플레이어의 정보를 실시간으로 동기화하는 작업 쓰레드입니다.
    private static Thread playerSyncThread;
    public static Thread getPlayerSync() { return playerSyncThread; }
    Task<Void> playerThread = new Task<Void>() {
        @Override
        protected Void call() throws Exception {
                if(playerEntityFramework == null) return null;
                PlayerRefreshPacket packet = new PlayerRefreshPacket(playerEntityFramework);
                packet.send();
                return null;
        }
    };


    private String receivedMessage = null;

    // 서버에 연결하기 전에 작업하는 메소드입니다.
    public void initialize()
    {
        try
        {
            playerSyncThread = new Thread(playerThread);
            ClientLoader.setBackgroundConnection(this);
            String address = ClientProgramManager.getClientComponent().CustomIP.getText().isEmpty() ? ServerProperty.SERVER_ADDRESS :
                    ClientProgramManager.getClientComponent().CustomIP.getText();
            socket = new Socket(address, ServerProperty.SERVER_PORT);
            System.out.println("서버에 연결됨, 주소 호출 대상: " +  socket.getInetAddress().getHostAddress() + ":" + socket.getPort());

            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        }
        catch(IOException e)
        {
            System.out.println("서버 연결 실패, 서버가 닫혀있습니다.");
            ClientLoader.setBackgroundConnection(null);
        }
    }

    // 클라이언트 연결 동기화 서버를 새로 불러오거나 리로드합니다.
    public static void refreshClientConnection()
    {
        if(ClientLoader.getBackgroundConnection() == null)
        {
            ClientConnectionReceiver background = new ClientConnectionReceiver();
            background.initialize();

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    background.readData();
                    return null;
                }
            };

            Thread thread = new Thread(task);
            taskBackground = thread;
            taskBackground.start();
        }
    }

    /**
     * 로그인과 회원가입과 관련한 패킷을 처리합니다.
     * @param status
     * @param requestedJsonObject
     */
    private void clientRegisterLoginSocket(int status, JsonObject requestedJsonObject)
    {
        if(status == ServerProperty.REGISTER_SUCCESSED_ACCOUNT)
        {
            SystemUtil.Companion.alert("회원가입", "회원가입 완료", requestedJsonObject.get("message").getAsString(), Alert.AlertType.INFORMATION);
            Platform.runLater(() -> SignupApplication.getStage().close());
        }

        else if(status == ServerProperty.REGISTER_FAILED_ACCOUNT)
        {
            SystemUtil.Companion.alert("실패", "회원가입 실패", requestedJsonObject.get("message").getAsString(), Alert.AlertType.ERROR);
        }

        else if(status == ServerProperty.RECEIVED_LOGIN_FAILED)
        {
            SystemUtil.Companion.alert("로그인", "로그인 실패", requestedJsonObject.get("message").getAsString(), Alert.AlertType.ERROR);
        }

        else if(status == ServerProperty.RECEVIED_LOGIN_SUCCESS)
        {
            ClientConnectionReceiver.id = ClientProgramManager.getClientComponent().InputID.getText();

            Platform.runLater(() ->ClientProgramManager.getClientComponent().InputID.setText(""));
            Platform.runLater(() ->ClientProgramManager.getClientComponent().InputPassword.setText(""));

            JsonObject jo = new JsonObject();
            jo.addProperty("statusNumber", ServerProperty.RECEVIED_LOGIN_SUCCESS);
            Platform.runLater(() -> new UpdateApplication().start(new Stage()));
        }
    }

    /**
     * 데이터를 주기적으로 읽어옵니다. 이것은 지속적으로 작업이 이루어져야 합니다.
     * 만약, 이 메소드의 작동이 멈춘다면, 패킷을 읽거나 정보를 외부에 전달할 수 없습니다.
     * 이것이 비활성화된다면, 클라이언트는 심각한 오류라고 간주하여 프로그램이 종료됩니다.
     */
    public void readData()
    {
        while (in != null)
        {
            try
            {
                // 데이터는 무조건 json 형식입니다.
                receivedMessage = in.readUTF();
            }
            catch (IOException e)
            {
                System.out.println("오류: 서버에서 연결을 끊음");
                taskBackground.interrupt();

                // 만약 온라인 접속 중이였다면
                if(GameLayoutEvent.getSystemTimeThread() != null)
                {
                    GameLayoutEvent.getSystemTimeThread().interrupt();
                        System.exit(0);
                }
                ClientLoader.setBackgroundConnection(null);
                break;
            }

            // 요청받은 데이터를 jsonObject로 바꿉니다.
            JsonObject requestedJsonObject = new Gson().fromJson(receivedMessage, JsonObject.class);
            int status = 0;

            try
            {
                status = requestedJsonObject.get("status").getAsInt();
            }
            catch (NullPointerException e)
            {
                e.printStackTrace();
            }

            // 서버로부터 서버 시간을 받았을 경우입니다.
            if(status == ServerProperty.SERVER_TIME_REQUEST)
            {
                String date = requestedJsonObject.get("time").getAsString();
                Platform.runLater(() -> ClientProgramManager.getGameComponent().ServerTime.setText(date));
            }

            // 누군가가 보낸 메세지를 담고 있는 패킷이 서버로부터 들어왔을 때입니다.
            else if(status == ServerProperty.PLAYER_MESSAGE_RECEIVED)
            {
                // 콘솔 전송기를 통해 메세지를 보냅니다.
                ClientManager.getConsoleSender().sendMessage(requestedJsonObject.get("message").getAsString());
            }

            // 플레이어 정보를 서버에서 다시 동기화합니다.
            else if(status == ServerProperty.PLAYER_REFRESH)
            {
                Gson gson = new GsonBuilder().registerTypeAdapter(Player.class, new PlayerAdapter()).create();

                // Json Format 버그를 해결하기 위해 해당 값을 파싱해 재등록합니다.
                String roomElement = SystemUtil.Companion.fixHashMap(requestedJsonObject.get("player").getAsJsonObject().get("enteredRoom").toString().replaceFirst("\"}\"", "\"}"));
                requestedJsonObject.get("player").getAsJsonObject().remove("enteredRoom");
                requestedJsonObject.get("player").getAsJsonObject().add("enteredRoom", null);

                PlayerEntityFramework player = gson.fromJson(requestedJsonObject.get("player"), PlayerEntityFramework.class);
                player.setHostAddress(socket.getInetAddress().getHostAddress());
                player.setEnteredRoom(gson.fromJson(roomElement, RoomFramework.class));
                ClientConnectionReceiver.setPlayerEntityFramework(player);
            }


            else if(status == ServerProperty.ROOM_CREATE)
            {
                Gson gson = new GsonBuilder().registerTypeAdapter(Room.class, new RoomAdapter()).create();
                RoomFramework room = gson.fromJson(requestedJsonObject.get("room"), RoomFramework.class);
                RoomCreateEvent event = new RoomCreateEvent(room, getPlayerEntityFramework());
                if(event.isCanceled()) continue;
                Thread thread = new Thread(new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Platform.runLater(() -> new RoomApplication().start(new Stage()));
                        return null;
                    }
                });
                thread.start();
                try
                {
                    thread.join();
                    event.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            else if(status == ServerProperty.ROOM_REFRESH)
            {
                Platform.runLater(() -> {
                    try {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Room.class, new RoomAdapter()).create();
                        Room[] rooms = gson.fromJson(requestedJsonObject.get("receivedRoom"), RoomFramework[].class);
                        ObservableMap<Integer, Room> roomList = FXCollections.observableHashMap();
                        ClientProgramManager.getGameComponent().TableCreateRoom.setItems(FXCollections.observableArrayList(roomList.values()));
                        Platform.runLater(() -> ClientProgramManager.getGameComponent().LastRefreshTime.setText("마지막 동기화 시간: " + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date())));
                        for(int i = 0; i < rooms.length; i++)
                        {
                            roomList.put(i, rooms[i]);
                        }
                        if (roomList.size() != 0)
                        {
                            ClientProgramManager.getGameComponent().TableRoomNumber.setCellValueFactory
                                    ( param -> new SimpleStringProperty(String.format("%06d",FXCollections.observableArrayList(roomList.values()).indexOf(param.getValue()))) );
                            ClientProgramManager.getGameComponent().TableCreateRoom.setItems(FXCollections.observableArrayList(roomList.values()));
                        }
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                });
            }

            else if(status == ServerProperty.ROOM_CONNECTED)
            {
                Gson gson = new GsonBuilder().registerTypeAdapter(Player.class, new PlayerAdapter()).registerTypeAdapter(Room.class, new RoomAdapter())
                        .create();

                // Json Format 버그를 해결하기 위해 해당 값을 파싱해 재등록합니다.
                String roomElement = SystemUtil.Companion.fixHashMap(requestedJsonObject.get("room").toString().replaceFirst("\"}\"", "\"}"));
                requestedJsonObject.get("player").getAsJsonObject().remove("enteredRoom");
                requestedJsonObject.get("player").getAsJsonObject().add("enteredRoom", null);

                requestedJsonObject.get("leader").getAsJsonObject().remove("enteredRoom");
                requestedJsonObject.get("leader").getAsJsonObject().add("enteredRoom", null);

                PlayerEntityFramework player = gson.fromJson(requestedJsonObject.get("player"), PlayerEntityFramework.class);
                PlayerEntityFramework leader = gson.fromJson(requestedJsonObject.get("leader"), PlayerEntityFramework.class);
                RoomFramework room = gson.fromJson(roomElement, RoomFramework.class);

                player.setEnteredRoom(room);
                leader.setEnteredRoom(room);
                RoomUpdateEvent event = new RoomUpdateEvent(leader, player, room);
                if(event.isCanceled()) continue;
                event.start();
            }

            else if(status == ServerProperty.ROOM_CLOSE)
            {
                if(! requestedJsonObject.get("isLeader").getAsBoolean()) {
                    ClientProgramManager.openGameLayout();
                    SystemUtil.Companion.alert("방 없어짐", "방장이 방을 없앰", "방이 사라졌습니다. 다른 방을 이용해주세요.");
                    RoomRefreshPacket refreshPacket = new RoomRefreshPacket(ClientManager.getPlayer().getId());
                    refreshPacket.send();
                }
                else
                {
                    Platform.runLater(() -> {
                        ClientProgramManager.getRoomComponent().AnotherUserPane.setVisible(false);
                        ClientProgramManager.getRoomComponent().StartButton.setDisable(true);
                        ClientProgramManager.getRoomComponent().StartButton.setText("대기중...");
                        ClientProgramManager.getRoomComponent().CapacityStatus.setText("현재 인원 (1/2)");
                    });
                }
            }

            else
            {
                // 로그인과 회원가입과 관련된 패킷을 처리합니다.
                clientRegisterLoginSocket(status, requestedJsonObject);
            }
        }
        System.out.println("Warning: ReadData() was exited.");
    }
}
