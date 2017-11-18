package com.ruskonert.GamblKing.client.connect;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruskonert.GamblKing.client.ClientLoader;
import com.ruskonert.GamblKing.client.program.SignupApplication;
import com.ruskonert.GamblKing.client.program.UpdateApplication;
import com.ruskonert.GamblKing.property.ServerProperty;
import com.ruskonert.GamblKing.util.SystemUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ClientConnectionReceiver
{
    private static String id;
    public static String getId() { return id; }

    private Socket socket;
    public Socket getSocket() { return this.socket; }

    private DataInputStream in;
    public DataInputStream getInputStream() { return this.in; }

    private DataOutputStream out;
    public DataOutputStream getOutputStream() { return this.out;}

    private static Thread taskBackground;

    private String receivedMessage = null;

    // 서버에 연결하기 전에 작업하는 메소드입니다.
    public void initialize()
    {
        try
        {
            ClientLoader.setBackgroundConnection(this);
            socket = new Socket(ServerProperty.SERVER_ADDRESS, ServerProperty.SERVER_PORT);
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

    // 로그인과 관련한 패킷을 처리합니다.
    private void clientRegisterLoginSocket(int status, JsonObject requestedJsonObject)
    {
        if(status == ServerProperty.REGISTER_SUCCESSED_ACCOUNT)
        {
            SystemUtil.Companion.alert("회원가입", "회원가입 완료", requestedJsonObject.get("message").getAsString(), Alert.AlertType.INFORMATION);
            Platform.runLater(() -> SignupApplication.getStage().close());
        }

        if(status == ServerProperty.REGISTER_FAILED_ACCOUNT)
        {
            SystemUtil.Companion.alert("실패", "회원가입 실패", requestedJsonObject.get("message").getAsString(), Alert.AlertType.ERROR);
        }

        if(status == ServerProperty.RECEIVED_LOGIN_FAILED)
        {
            SystemUtil.Companion.alert("로그인", "로그인 실패", requestedJsonObject.get("message").getAsString(), Alert.AlertType.ERROR);
            this.id = null;
        }

        if(status == ServerProperty.RECEVIED_LOGIN_SUCCESS)
        {
            JsonObject jo = new JsonObject();
            jo.addProperty("statusNumber", ServerProperty.RECEVIED_LOGIN_SUCCESS);
            jo.addProperty("message", "Connecting update server from " + this.getSocket().getInetAddress().getHostAddress());
            Platform.runLater(() -> new UpdateApplication().start(new Stage()));
        }
    }

    // 데이터를 주기적으로 읽어옵니다
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

            }

            // 로그인과 회원가입과 관련된 패킷을 처리합니다.
            clientRegisterLoginSocket(status, requestedJsonObject);
        }
    }

    public void send(String message)
    {
        try
        {
            out.writeUTF(message);
        }

        catch(SocketException e)
        {
            ClientConnectionReceiver.refreshClientConnection();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
