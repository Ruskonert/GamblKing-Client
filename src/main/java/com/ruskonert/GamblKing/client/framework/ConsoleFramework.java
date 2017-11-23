package com.ruskonert.GamblKing.client.framework;

import com.ruskonert.GamblKing.MessageType;
import com.ruskonert.GamblKing.client.ClientManager;
import com.ruskonert.GamblKing.client.connect.packet.PlayerMessagePacket;
import com.ruskonert.GamblKing.client.program.ClientProgramManager;
import com.ruskonert.GamblKing.program.ConsoleSender;
import com.ruskonert.GamblKing.util.ReflectionUtil;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class ConsoleFramework implements ConsoleSender
{
    private ConsoleFramework()
    {
        if(ClientManager.getConsoleSender() != null)
        {
            throw new RuntimeException("ConsoleFramework was already initialized!");
        }
    }

    public static void initialize()
    {
        try
        {
            ReflectionUtil.Companion.setStaticField(ClientManager.class, "consoleSender", new ConsoleFramework());
        } catch (NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public TextField getCommandField()
    {
        return ClientProgramManager.getGameComponent().MessageBox;
    }

    @Override
    public TextArea getConsoleScreen()
    {
        return ClientProgramManager.getGameComponent().ChatBox;
    }

    @Override
    public StringProperty getMessageProperty()
    {
        return ClientProgramManager.getGameComponent().ChatBox.textProperty();
    }

    @Override
    public void sendRawMessage(String message)
    {
        StringProperty property = this.getMessageProperty();
        String builder = property.getValue() + "\n" +
                "[" +
                new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) +
                "] " +
                message;
        property.setValue(builder);
    }

    @Override
    public void sendMessage(String message, MessageType type)
    {
        StringProperty property = this.getMessageProperty();
        String builder = property.getValue() + "\n" +
                "[" +
                new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) +
                " " + type.getValue() + "] " +
                message;
        property.setValue(builder);
    }

    @Override
    public void log(String message)
    {
        this.sendMessage(message, MessageType.INFO);
    }

    @Override
    public void clearScreen()
    {
        this.getMessageProperty().setValue("");
    }

    @Override
    public void clearCommandField()
    {
        this.getCommandField().setText("");
    }

    @Override
    public void dispatch(String command)
    {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void sendAll(String message)
    {
        PlayerMessagePacket packet = new PlayerMessagePacket(ClientManager.getPlayer(), message);
        packet.send();
    }

    @Override
    public void sendMessage(String message)
    {
        StringProperty property = ClientProgramManager.getGameComponent().ChatBox.textProperty();
        String builder = property.getValue() + "\n" +
                "[" +
                new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) +
                "] " +
                message;
        property.setValue(builder);
    }
}
