package com.kabaniery.chipsserver.game;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.kabaniery.chipsserver.Rooms.UserConnector;

import java.util.ArrayList;

public class UserGame {
    private UserConnector.User user;
    private int countChips;
    private String name;
    //?????????????????????????????????????????????????????????
    private int avatar;
    public int position;
    public int id;
    private boolean isAdmin;
    private GameRoom linkedRoom;
    private boolean isfault = false;

    public UserGame(UserConnector.User user, String name, int avatar, int position, int countChips, int id, GameRoom father) {
        this.linkedRoom = father;
        this.user = user;
        this.name = name;
        this.avatar = avatar;
        this.position = position;
        this.isAdmin = false;
        this.countChips = countChips;
    }

    public ArrayNode getInfo() {
        ArrayNode result = JsonNodeFactory.instance.arrayNode();
        result.add(name);
        //TODO: проверить надо ли
        result.add(avatar);
        result.add(position);
        result.add(isfault);
        result.add(countChips);
        return result;
    }

    public boolean getAdmin() {
        return this.isAdmin;
    }


    //Коннектор с сущностью общения
    enum COMMANDS_LIST_EX {
        DISCONNECT,
        INFO,
        BAN,
        MOVE,
        SET_MONEY
    }
    public ArrayNode addQuest(COMMANDS_LIST_EX command, ArrayNode content) {
        ArrayNode reply = JsonNodeFactory.instance.arrayNode();
        switch (command) {
            case DISCONNECT -> {
                linkedRoom.disconnect(this);
                reply.add("rd");
            }
            case INFO -> {
                linkedRoom.
            }
        }
    }




    //TODO: Сделать
    //Отделение выигрышей.
    //Возвращает нуль, если победителей нет и их ID, если они есть
    public ArrayList<Integer> selectWinners() {
        if (this.isAdmin) {
            //TODO: ОБЩЕНИЕ, СУКА СДЕЛАТЬ
            int[] res = {1, 2};
            return res;
        }
        return null;
    }


    //Отделение фишек в игре... Найс в доту решил пойти
    private int countWastedChips = 0;
    private boolean isAllIn = false;
    //TODO: реализовать общение с пользователем, который делает ставку. Если фолдит, то вернуть -1, если ставит всё, то верунть -2.
    public int getBet(int currentBet) {
        int bet = 0;//Заменить
        //TODO: тут будет обработка фолда
        if (bet == -1) {
            this.isfault = true;
        }
        if (bet > this.countChips) {
            bet = this.countChips;
        }
        if (bet == this.countChips) {
            return bet;
        }
        countWastedChips += bet;
        this.countChips -= bet;
        return bet;
    }
    //Возвращает кол-во фишек и снимает их. Для all in
    public int checkAllIn() {
        int res = this.countChips;
        countWastedChips += this.countChips;
        this.countChips = 0;
        return res;
    }
    //Возвращает все деньги. Их необходимо показать каждому пользователю
    public void setResultOfMatch(int money) {
        this.countWastedChips = 0;
        this.isAllIn = false;
        this.countChips += money;
    }
    public int getAllIn() {
        if (this.isAllIn) {
            return this.countWastedChips;
        } else {
            return 0;
        }
    }
    //Выставляет количество фишек. Можно добавить защиту, тип только из комнаты
    public void setCountChips(GameRoom room, int count) {
        if (room == linkedRoom) {
            this.countChips = count;
        }
    }
}
