package com.kabaniery.chipsserver.Rooms;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.net.Socket;
import java.util.ArrayList;

public class Room {
    private ArrayNode roomInfo;
    private String name;
    private String id;
    //TODO: поменять
    private final int maxUsers;

    private ArrayList<UserConnector> users;


    //Вызывается при попытке подключиться
    public int tryToConnect() {
        if (users.size() < maxUsers) {
            int port = RoomManager.getPort();
            UserConnector temp = new UserConnector(port, this);
            users.add(temp);
            return port;
            //TODO: добавить счётчик времени
        }
        return -1;
    }

    public void closeConnection(UserConnector connector) {
        users.remove(connector);
    }

    public Room(String name, String id, int maxUsers) {
        this.id = id;
        this.name = name;
        this.maxUsers = maxUsers;
        this.users = new ArrayList<>();
        roomInfo = JsonNodeFactory.instance.arrayNode();
        roomInfo.add(name);
        //TODO: Убрать и заменить
        roomInfo.add(maxUsers);
    }

    //TODO: Добавить действий
    //Формат выходных данных: название комнаты, макс количество игроков, текущее количество игроков, игроки
    public JsonNode getInfo() {
        ArrayNode result = JsonNodeFactory.instance.arrayNode();
        result.add(name);
        result.add(maxUsers);
        result.add(users.size());
        for (UserConnector user: users) {
            result.add(user.getInfo());
        }
        return roomInfo;
    }
}
