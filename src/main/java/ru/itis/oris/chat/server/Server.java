package ru.itis.oris.chat.server;

import ru.itis.oris.chat.game.GameLogic;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static final String HOST = "localhost";
    public static final int PORT = 8098;
    public static final List<ServerThread> CLIENT_LIST = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Сервер запущен на порту " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Присоединился клиент " + client.getInetAddress() + ":" + client.getPort());
                ServerThread clientThread = new ServerThread(client);
                CLIENT_LIST.add(clientThread);
                broadcast("Клиент " + client.getInetAddress() + ":" + client.getPort() + " присоединился к чату");
            }
        } catch (IOException e) {
            System.err.println("Ошибка сервера: " + e.getMessage());
        }
    }

    public static synchronized void broadcast(String message) {
        System.out.println("Рассылка: " + message);
        for (int i = 0; i < CLIENT_LIST.size(); i++) {
            ServerThread client = CLIENT_LIST.get(i);
            if (!client.send(message)) {
                CLIENT_LIST.remove(i);
                i--;
            }
        }
    }

    public static synchronized void removeClient(ServerThread client) {
        CLIENT_LIST.remove(client);
        broadcast("Клиент отключился. Осталось клиентов: " + CLIENT_LIST.size());
    }
}