package com.ruskonert.GamblKing.client.game.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ruskonert.GamblKing.client.game.connect.GameServerConnection;
import javafx.concurrent.Task;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;

public abstract class DuelEvent
{
    private boolean cancel;
    public void setCanceled(boolean canceled) { this.cancel = canceled; }
    public boolean isCanceled() { return this.cancel; }

    private DataOutputStream stream;
    public DataOutputStream getStream() { return this.stream; }

    private int type;
    public int getType() { return this.type; }

    private Object handleInstance;
    public Object getHandleInstance() { return this.handleInstance; }

    public DuelEvent(int type)
    {
        this.type = type;
        this.stream = GameServerConnection.getOutputStream();
    }

    public void accept()
    {
        handleInstance = this;
        Task<Void> sendMessageTask = new Task<Void>()
        {
            @Override
            protected Void call() throws Exception {
                // 신호 받는 포트입니다.
                ServerSocket sSocket = new ServerSocket(12247);

                // 이벤트 신호를 받을 수 있도록 상대에게 요청을 합니다.
                DuelEvent event = (DuelEvent) handleInstance;
                event.send(handleInstance);

                // 패킷 보내고 나서 신호가 들어오기까지 기다린 후 그 다음 턴에 실행되게끔 합니다.
                sSocket.accept();
                return null;
            }
        };
        Thread thread = new Thread(sendMessageTask);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void send(Object handleInstance)
    {
        Gson gson = new GsonBuilder().serializeNulls().create();
        try
        {
            stream.writeUTF(gson.toJson(handleInstance));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
