package com.kabaniery.chipsserver.Rooms;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

public class RoomManager {
    private static ArrayList<Room> rooms = new ArrayList<>();
    private static long roomId = 0;
    private static final long maxLong = Long.MAX_VALUE;
    public static Room createNewRoom(@NonNull String name, int usersCount) {
        if (roomId + 1 == maxLong) {
            roomId = 0;
        }
        //TODO: обработать
        if (usersCount <= 0 || usersCount > 10) {
            return null;
        }
        return new Room(name+(++roomId), String.valueOf(roomId), usersCount);
        //TODO: добавить конструктор комнаты и запустить выполнение
    }
    @Deprecated
    public static void defaultsRooms() {
        rooms.add(new Room("room1", "room1123", 2));
        rooms.add(new Room("room2", "room2123", 3));
        rooms.add(new Room("room3", "room3123", 4));
    }

    public static String getRooms() {
        ArrayNode result = JsonNodeFactory.instance.arrayNode();
        result.add(rooms.size());
        for (Room room: rooms) {
            result.add(room.getInfo());
        }
        return result.asText();
    }

    private static int port = 10000;
    public static int getPort() {
        if (port <= 20000) {
            return port++;
        } else {
            port = 10000;
            return port;
        }
    }

}
