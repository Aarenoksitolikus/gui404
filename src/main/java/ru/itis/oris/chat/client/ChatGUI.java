package ru.itis.oris.chat.client;

import ru.itis.oris.chat.server.Server;

import javax.swing.*;
import java.awt.*;

public class ChatGUI extends JFrame {
    JTextArea chatArea;
    JTextField inputField;
    private final ChatWorker worker;

    public ChatGUI() {
        this.setLayout(new BorderLayout());
        setSize(500, 400);
        chatArea = new JTextArea();
        chatArea.setPreferredSize(new Dimension(500, 300));
        inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(400, 100));
        JButton sendButton = new JButton();
        sendButton.setText("Send");
        sendButton.setPreferredSize(new Dimension(100, 100));

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        add(chatArea, BorderLayout.NORTH);
        add(inputField, BorderLayout.WEST);
        add(sendButton, BorderLayout.EAST);

        setVisible(true);

        worker = new ChatWorker(Server.HOST, Server.PORT, chatArea, inputField);
        worker.execute();
    }

    @Override
    public void dispose() {
        this.worker.done();
        super.dispose();
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (!message.trim().isEmpty()) {
            worker.sendMessage(message);
            inputField.setText("");
        }
    }

    public static void main(String[] args) {
        new ChatGUI();
    }
}