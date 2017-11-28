package com.ruskonert.GamblKing.client.connect;

import com.google.gson.JsonObject;
import com.ruskonert.GamblKing.client.program.ClientProgramManager;
import com.ruskonert.GamblKing.property.ServerProperty;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FileReceiver
{
    private static Thread receiverThread;

    public static void start(ServerSocket serverSocket)
    {
        new FileReceiver(serverSocket);
    }

    private ServerSocket filesocket;

    private int total = 0;

    private int process = 0;

    private FileReceiver(ServerSocket serverSocket)
    {
        Task<Void> backgroundTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception
            {
                filesocket = serverSocket;
                Socket socket = filesocket.accept();

                BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
                DataInputStream dis = new DataInputStream(bis);

                File file = new File("update");
                if(! file.exists()) file.mkdir();

                int filesCount = dis.readInt();
                File[] files = new File[filesCount];
                total = filesCount;
                for(int i = 0; i < filesCount; i++)
                {
                    long fileLength = dis.readLong();
                    String fileName = dis.readUTF();

                    files[i] = new File(fileName);

                    FileOutputStream fos = new FileOutputStream(files[i]);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    Platform.runLater(() -> ClientProgramManager.getUpdateComponent().UpdateLabel.setText("Downloading the file:" + fileName));
                    for(int j = 0; j < fileLength; j++) bos.write(bis.read());
                    bos.close();

                    Platform.runLater(() -> ClientProgramManager.getUpdateComponent().UpdateProgressBar.setProgress((double) process / total));
                    Thread.sleep(50L);
                    process++;
                }
                dis.close();

                Platform.runLater(() -> ClientProgramManager.getUpdateComponent().UpdateLabel.setText("Preparing the client launcher..."));
                Platform.runLater(() -> ClientProgramManager.getUpdateComponent().UpdateProgressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS));

                File[] fileList = new File("update/").listFiles();
                if(fileList != null) {
                    for (File f : fileList) {
                        try {
                            f.renameTo(new File("data/" + f.getName()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                JsonObject object = new JsonObject();
                object.addProperty("status", ServerProperty.SEND_UPDATE_FILE_REQUEST_COMPLETED);
                object.addProperty("id", ClientConnectionReceiver.getId());
                object.addProperty("ipAddress", UpdateConnectionReceiver.getSocket().getInetAddress().getHostAddress());
                UpdateConnectionReceiver.getOutputStream().writeUTF(object.toString());
                receiverThread.interrupt();
                return null;
            }
        };

        receiverThread = new Thread(backgroundTask);
        receiverThread.start();
    }
}
