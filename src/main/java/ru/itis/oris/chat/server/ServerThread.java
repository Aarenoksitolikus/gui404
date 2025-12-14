package ru.itis.oris.chat.server;

import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String clientName;

    public ServerThread(Socket clientSocket) throws IOException {
        this.socket = clientSocket;
        this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        this.clientName = "Клиент-" + clientSocket.getPort();
        start();
    }

    @Override
    public void run() {
        String message;
        try {
            writer.write("Введите ваше имя: \n");
            writer.flush();
            clientName = reader.readLine().trim();
            writer.write("Добро пожаловать в чат, " + clientName + "!\n");
            writer.flush();

            Server.broadcast(clientName + " присоединился к чату!");

            while (true) {
                message = reader.readLine();

                if (message == null || message.equalsIgnoreCase("/exit")) {
                    break;
                }

                if (message.equalsIgnoreCase("/list")) {
                    sendOnlineUsers();
                    continue;
                }

                if (message.startsWith("/name ")) {
                    String newName = message.substring(6).trim();
                    String oldName = clientName;
                    clientName = newName.isEmpty() ? clientName : newName;
                    send("Вы изменили имя с " + oldName + " на " + clientName);
                    continue;
                }

                if (message.trim().isEmpty()) {
                    continue;
                }

                String formattedMessage = clientName + ": " + message;
                System.out.println(formattedMessage);
                Server.broadcast(formattedMessage);
            }
        } catch (IOException e) {
            System.out.println("Клиент " + clientName + " отключился неожиданно");
        } finally {
            try {
                Server.removeClient(this);
                socket.close();
                System.out.println("Клиент " + clientName + " отключен");
            } catch (IOException e) {
                System.err.println("Ошибка при закрытии сокета: " + e.getMessage());
            }
        }
    }

    public boolean send(String message) {
        try {
            writer.write(message + "\n");
            writer.flush();
            return true;
        } catch (IOException e) {
            System.err.println(e);
            return false;
        }
    }

    private void sendOnlineUsers() {
        try {
            writer.write("--- Онлайн (" + Server.CLIENT_LIST.size() + ") ---\n");
            for (ServerThread client : Server.CLIENT_LIST) {
                writer.write("- " + client.clientName + "\n");
            }
            writer.write("-------------------\n");
            writer.flush();
        } catch (IOException e) {
        }
    }

    public String getClientName() {
        return clientName;
    }
}