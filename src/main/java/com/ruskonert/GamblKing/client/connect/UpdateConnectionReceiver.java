package com.ruskonert.GamblKing.client.connect;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruskonert.GamblKing.client.ClientLoader;
import com.ruskonert.GamblKing.client.connect.packet.ClientFileRequestPacket;
import com.ruskonert.GamblKing.client.connect.packet.ClientGamePacket;
import com.ruskonert.GamblKing.client.program.ClientProgramManager;
import com.ruskonert.GamblKing.client.program.GameApplication;
import com.ruskonert.GamblKing.client.program.UpdateApplication;
import com.ruskonert.GamblKing.framework.PlayerEntityFramework;
import com.ruskonert.GamblKing.property.ServerProperty;
import com.ruskonert.GamblKing.util.SecurityUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdateConnectionReceiver
{
    private static Socket socket;
    public static Socket getSocket() { return socket; }

    private static DataInputStream in;
    public static DataInputStream getInputStream() { return in; }

    private static DataOutputStream out;
    public static DataOutputStream getOutputStream() { return out;}

    private static Thread taskBackground;
    private String receivedMessage = null;

    // 클라이언트 업데이트 서버를 새로 불러오거나 리로드합니다.
    public static void refreshUpdateConnection()
    {
        if(ClientLoader.getUpdateConnectionReceiver() == null)
        {
            UpdateConnectionReceiver background = new UpdateConnectionReceiver();
            background.initialize();

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                        background.readData();
                        return null;
                }
            };

            taskBackground = new Thread(task);
            taskBackground.start();
        }
    }

    // 서버에 연결하기 전에 작업하는 메소드입니다.
    public void initialize()
    {
        try
        {
            ClientLoader.setUpdateConnection(this);
            String address = ClientProgramManager.getClientComponent().CustomIP.getText().isEmpty() ? ServerProperty.SERVER_ADDRESS :
                    ClientProgramManager.getClientComponent().CustomIP.getText();

            socket = new Socket(address, ServerProperty.SERVER_UPDATE_PORT);

            System.out.println("업데이트 서버에 연결되었습니다. 서버 주소: " +  socket.getInetAddress().getHostAddress() + ":" + socket.getPort());

            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        }
        catch(IOException e)
        {
            System.out.println("업데이트 서버 연결 실패, 메인 서버가 닫혀있습니다.");
            ClientLoader.setUpdateConnection(null);
        }
    }

    private boolean isLoading = false;

    @SuppressWarnings("ResultOfMethodCallIgnored")
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
                ClientLoader.setUpdateConnection(null);
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

            if(status == ServerProperty.CONNECT_GAME_SERVER)
            {
                PlayerEntityFramework framework = new Gson().fromJson(requestedJsonObject.get("player").getAsString(), PlayerEntityFramework.class);
                ClientConnectionReceiver.setPlayerEntityFramework(framework);

                try {
                Task<Void> t = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception
                    {
                        Platform.runLater(() ->
                        {
                            ClientLoader.getStage().close();
                            ClientLoader.getMusicThread().interrupt();
                        });
                        Thread.sleep(1000L);
                        Platform.runLater(() -> ClientProgramManager.getUpdateComponent().UpdateLabel.setText("게임에 접속합니다..."));
                        isLoading = true;
                        Thread.sleep(2000L);
                        Platform.runLater(() -> UpdateApplication.getStage().close());
                        Thread.sleep(2000L);
                        return null;
                    }
                };
                    Thread thread = new Thread(t);
                    thread.start();
                    thread.join();
                    Platform.runLater(() ->
                    {
                        ClientGamePacket packet = new ClientGamePacket(framework.getId());
                        packet.send();
                        Stage stage = new Stage();
                        new GameApplication().start(stage);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // 업데이트 서버에 요청했던 업데이트 요청 파일에 대한 패킷을 받습니다.
            // 업데이트 서버에 가져온 업데이트 파일들이 클라이언트에 제대로 있는지 확인합니다.
            // 없다면, 업데이트 서버에서 파일을 다운받습니다.
            if (status == ServerProperty.SEND_UPDATE_REQURST_RECEIVED)
            {
                // 서버에서 받은 디렉토리 정보를 바탕으로 하여 ClientRequestedPacket으로 새로 만듭니다.
                ClientFileRequestPacket packet = new Gson().fromJson(requestedJsonObject, ClientFileRequestPacket.class);
                List<String> receivedFileHashList = new ArrayList<>();
                List<String> downloadFileList = new ArrayList<>();

                // 업데이트를 해야 하는 파일이 하나도 없을 경우
                if (packet.getData() == null || packet.getData().isEmpty())
                {
                    // 업데이트 할 파일이 없으므로 즉시 게임 서버로 연결합니다.
                    JsonObject object = new JsonObject();
                    object.addProperty("status", ServerProperty.SEND_UPDATE_FILE_REQUEST_COMPLETED);
                    object.addProperty("id", ClientConnectionReceiver.getId());
                    object.addProperty("ipAddress", UpdateConnectionReceiver.getSocket().getInetAddress().getHostAddress());
                    Platform.runLater(() -> ClientProgramManager.getUpdateComponent().UpdateLabel.setText("Preparing the client launcher..."));
                    try {
                        UpdateConnectionReceiver.getOutputStream().writeUTF(object.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                // 서버에서 보내준 업데이트 파일을 가져옵니다.
                // Key는 고유 해시값이며, value는 파일 경로입니다.

                File dir = new File("data");
                if(! dir.exists()) dir.mkdir();

                for (Map.Entry<String, String> e : packet.getData().entrySet())
                {
                    try {
                        // 클라이언트의 데이터 폴더를 가져옵니다.
                        File checkFile = new File("data/" + e.getValue());

                        // 클라이언트의 데이터 폴더 내 파일을 확인합니다.
                        if (checkFile.exists()) {
                            try {
                                String sha = SecurityUtil.Companion.extractFileHashSHA256(checkFile.getPath());

                                // 만약 그 파일이 있음에도 불구하고 업데이트 서버에서 받은 파일과 다른 것이라면 (오래된 파일이라면)
                                if (!e.getKey().equalsIgnoreCase(sha)) {

                                    // 다운로드 목록 리스트에 추가합니다.
                                    receivedFileHashList.add(e.getKey());
                                    downloadFileList.add("data/" + e.getValue());
                                }
                            } catch (Exception ee) {
                                ee.printStackTrace();
                            }
                        }
                        // 데이터가 없다면
                        else
                        {
                            // 서버에서 보내준 모든 업데이트 파일을 다운받을 수 있도록 목록 리스트에 모두 추가합니다.
                            receivedFileHashList.add(e.getKey());
                            downloadFileList.add("data/" + e.getValue());
                        }

                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }

                }

                String receiverIp = getSocket().getInetAddress().getHostAddress();
                JsonObject object = new JsonObject();
                object.addProperty("status", ServerProperty.SEND_UPDATE_FILE_REQUEST);
                object.addProperty("data", new Gson().toJson(receivedFileHashList.toArray(new String[receivedFileHashList.size()])));
                object.addProperty("ipAddress", receiverIp);
                try
                {
                    // 클라이언트 리시버를 새로 만듭니다.
                    // 이제 서버에서 파일 수신이 들어올 때마다 파일을 다운로드 할 것입니다.
                    FileReceiver.start(new ServerSocket(7746));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                // 서버에게 원하는 업데이트 자료에 대해 수신 요청을 합니다.
                this.send(object.toString());
            }
        }
    }

    public void send(String message)
    {
        try
        {
            out.writeUTF(message);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}