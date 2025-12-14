package ru.itis.oris.chat.client;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class ChatWorker extends SwingWorker<Void, String> {
    public Socket socket;
    public BufferedReader reader;
    public BufferedWriter writer;
    public JTextArea chat;
    public JTextField inputField;

    public ChatWorker(String host, int port, JTextArea chat, JTextField inputField) {
        publish("Попытка подключиться к серверу");

        try {
            this.socket = new Socket(host, port);
            publish("Подключение установлено");
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.chat = chat;
            this.inputField = inputField;
        } catch (IOException e) {
            publish("Произошла ошибка подключения");
        }
    }

    @Override
    protected Void doInBackground() throws Exception {
        publish("Мы теперь подключены к серверу");

        String message;
        while ((message = reader.readLine()) != null) {
            publish(message);
        }

        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        for (String message : chunks) {
            chat.append(message + "\n");
        }
    }

    @Override
    protected void done() {
        try {
            reader.close();
            writer.close();
            socket.close();
            super.done();
        } catch (IOException e) {
            publish("Ошибка отключения");
        }
    }

    public void sendMessage(String message) {
        SwingWorker<Void, Void> sender = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                writer.write(message + "\n");
                writer.flush();
                return null;
            }
        };

        sender.execute();
    }
}
