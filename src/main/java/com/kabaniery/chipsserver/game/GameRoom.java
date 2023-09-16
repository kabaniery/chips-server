package com.kabaniery.chipsserver.game;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.kabaniery.chipsserver.Rooms.UserConnector;

import java.util.ArrayList;
import java.util.Arrays;

//Класс, который запускает игру
public class GameRoom {
    public ArrayList<UserGame> allPlayers = new ArrayList<>();
    private ArrayList<UserGame> currentQueue;
    private int currentPlayer;
    private UserGame admin;
    private int maxId;
    private int startChips;
    private int roomNumber;


    public ArrayNode getInfo() {
        ArrayNode reply = JsonNodeFactory.instance.arrayNode();
        reply.add("rI");
        reply.add(roomNumber);
        reply.add(allPlayers.size());
        ArrayNode player;
        for (UserGame user:  allPlayers) {
            
        }
    }
    public GameRoom(int countPlayers, int startChipsCount, ArrayList<UserConnector.User> users, int roomNumber) {
        int index = 0;
        this.startChips = startChipsCount;
        this.roomNumber = roomNumber;
        UserGame us;
        for (UserConnector.User user: users) {
            //TODO: Добавить реализацию аватаров
            us = new UserGame(user, user.getName(), 0, index++, index, startChipsCount, this);
            allPlayers.add(us);
        }
        maxId = index++;
        currentPlayer = 0;
    }
    //Запускает раунд игры
    private long startRound() {
        long allBets = 0;
        //Текущая ставка
        int currentBet = 0;
        //ПОЗИЦИЯ игрока и его ставка
        int[] bets = new int[currentQueue.size()];
        //-3 - ставку ещё не делал, -2 - поставил всё, -1 - сбросил карты
        Arrays.fill(bets, -3);
        //Если текущая ставка повысилась
        boolean needRepeat = false;
        //Обход игроков
        do {
            for (UserGame user : currentQueue) {
                //Если ставка была уравнена
                if (bets[user.position] == currentBet) {
                    break;
                }
                //Если пользователь поставил всё или сбросил, его не трогаем
                if (bets[user.position] == -2 || bets[user.position] == -1)
                    continue;
                //Получаем ставку
                int bet = user.getBet(currentBet);

                //Добавляем деньги в общий банк
                if (bet == -2) {
                    int temp = user.checkAllIn();
                    allBets += temp;
                    if (temp > currentBet) {
                        needRepeat = true;
                        currentBet = temp;
                    }
                } else if (bet != -1) {
                    allBets += bet;
                }

                //Оформляем вылет и ставку
                if (bet == -1) {
                    currentQueue.remove(user);
                    //Если останется после фолда один игрок, то мы выходим нахуй
                    if (currentQueue.size() == 1) {
                        needRepeat = false;
                        break;
                    }
                } else if (currentBet < bet) {
                    needRepeat = true;
                    currentBet = bet;
                }
                bets[user.position] = bet;
            }
        } while (!needRepeat);
        return allBets;
    }
    //Запускает игру
    private void startGame() {
        //TODO: добавить проверку на игроков без фишек
        currentQueue = (ArrayList<UserGame>) allPlayers.clone();
        //Общий банк
        long bank = 0;
        while (true) {
            //Получаем банк с раунда
            bank += startRound();
            //Если игрок 1, то ставку выдаем)
            if (currentQueue.size() == 1) {
                currentQueue.get(0).setResultOfMatch((int)bank);
                return;
                //ЗАКРУГЛЯЕМСЯ
            }
            if (currentQueue.size() == 0) {
                return;
            }
            //Спрашиваем админа, надо ли раздавать деньгу
            ArrayList<Integer> winners;
            if ((winners = admin.selectWinners()).size() > 0) {
                //МБ добавить инфо остальным о старте выбора победителя
                //Победители через allIn
                ArrayList<UserGame> aWinners = new ArrayList<>();
                //Победители через норм ставку
                ArrayList<UserGame> uWinners = new ArrayList<>();
                for (UserGame user: currentQueue) {
                    if (winners.contains(user.position)) {
                        if (user.getAllIn() > 0)
                            aWinners.add(user);
                        else
                            uWinners.add(user);
                    }
                }
                //А теперь начинается дрочь
                int countWinners = aWinners.size() + uWinners.size();
                //Если существуют уебки
                if (!aWinners.isEmpty()) {
                    //Каждый уебок получает поставленную долю от всех участников, деленную на кол-во победителей
                    for (UserGame winner: aWinners) {
                        int money = winner.getAllIn() * currentQueue.size() / countWinners;
                        bank -= money;
                        winner.setResultOfMatch(money);
                    }
                    //Если норм чуваков нет среди победивших, то остатки ставок раздаются обратно тем, кто остался
                    if (uWinners.isEmpty()) {
                        currentQueue.removeAll(aWinners);
                        countWinners = currentQueue.size();
                        for (UserGame user: currentQueue) {
                            user.setResultOfMatch((int) (bank / countWinners));
                        }
                    }
                }
                //Если норм чуваки существуют, то они делят между собой оставшийся банк. В теории, там большая часть остается
                countWinners = uWinners.size();
                for (UserGame winner: uWinners) {
                    winner.setResultOfMatch((int) bank / countWinners);
                }
                //Когда деньги разделены, то закругляемся
            }
        }
    }



    //Функции для действий в комнате другими игроками
    //Отключение от комнаты и передача админки по ситуации
    public void disconnect(UserGame user) {
        currentQueue.remove(user);
        allPlayers.remove(user);
        if (user.getAdmin()) {
            if (allPlayers.isEmpty()) {
                //TODO: уничтожить комнату
            } else {
                admin = allPlayers.get(0);
            }
        }
    }
    //Подключить нового игрока в комнату
    public void connectToRoom(UserConnector.User user) {
        allPlayers.add(new UserGame(user, user.getName(), 0, /*TODO:добавить поиск свободной позиции*/ 0, startChips, maxId++, this));
    }
    //Выставить игроку фишки (только для админа)
    public void setChips(UserGame admin, int id, int count) {
        if (this.admin == admin) {
            for (UserGame user : allPlayers) {
                if (user.id == id) {
                    user.setCountChips(this, count);
                    break;
                }
            }
        }
    }
    //Переместить игрока
    public void replacePlayer(UserGame admin, int id, int place) {
        if (this.admin != admin) {
            return;
        }
        UserGame target = null;
        for (UserGame user: allPlayers) {
            if (user.id == id) {
                target = user;
                break;
            }
        }
        //Если необходимо всех до него двинуть налево
        if (target.position < place) {
            for (UserGame user: allPlayers) {
                if (user.position > target.position && user.position <= place) {
                    user.position--;
                }
            }
            target.position = place;
            //Обратный случай
        } else {
            for (UserGame user: allPlayers) {
                if (user.position >= place && user.position < target.position) {
                    user.position++;
                }
            }
            target.position = place;
        }
    }
}
