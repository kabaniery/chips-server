package com.kabaniery.chipsserver.Rooms;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.kabaniery.chipsserver.game.UserGame;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;

//Класс подключения
public class UserConnector implements Runnable {
    private int port;
    private int connected = 0;
    private User user;
    private Room room;

    public ArrayNode getInfo() {
        return user.getInfo();
    }

    public int getConnected() {
        return connected;
    }

    public UserConnector(int port, Room room) {
        this.port = port;
        this.room = room;
    }

    public void openPort() {
        Thread thread = new Thread(this);
        thread.start();
    }
    @Override
    public void run() {
        try {
            ServerSocket socket = new ServerSocket(port);
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - 5000 < startTime) {
                Socket clientSocket = socket.accept();
                //Соединение получено. Сделать индикатор
                user = new User(clientSocket, room);
                Thread thread = new Thread(user);
                thread.start();
                connected = 1;
            }
        } catch (IOException ignored) {}
        connected = -1;
    }

    //Класс выполняющий общение с клиентом
    public class User implements Runnable, Closeable {
        private Socket handler;
        private String name;
        BufferedReader in;
        PrintWriter out;
        private Room room;
        //Формат вывода: имя

        public String getName() {
            return this.name;
        }

        public ArrayNode getInfo() {
            ArrayNode result = JsonNodeFactory.instance.arrayNode();
            result.add(name);
            return result;
        }

        public User(Socket handler, Room room) throws IOException {
            this.handler = handler;
            this.room = room;
            in = new BufferedReader(new InputStreamReader(handler.getInputStream()));
            out = new PrintWriter(handler.getOutputStream());
        }


        //Работа с клиентом
        @Override
        public void run() {
            //Выдаём информацию о комнате
            ObjectMapper objectMapper = new ObjectMapper();
            while (true) {
                try {
                    if (in.ready()) {
                        wait(80);
                        String quest = in.readLine();
                        JsonNode node = objectMapper.readTree(quest);
                        //TODO: добавить реакцию на классические JSON
                        //Если мы не ждём ответа, то это должен быть запрос по канону
                        if (node.isArray()) {
                            ArrayNode canonQuest = (ArrayNode) node;
                            if (lastQuestion == null) {
                                switch (canonQuest.get(0).asText()) {
                                    case "d" -> {
                                        userHandler.
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    break;
                }
            }
        }


        //Общение с сокетом
        private ArrayDeque<String> postQueue = new ArrayDeque<>();

        private String lastQuestion = null;

        public String addPost(){}

        private UserGame userHandler = null;
        public void setGameHandler(UserGame userGame) {
            userHandler = userGame;
        }

        @Override
        public void close() throws IOException {
            this.in.close();
            this.out.close();
        }
    }
}
