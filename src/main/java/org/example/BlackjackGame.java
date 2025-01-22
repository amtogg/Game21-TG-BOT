package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Класс, описывающий одну сессию игры в "21".
 */
public class BlackjackGame {

    // Константы
    public static final int MIN_CARD_RANK = 1;
    public static final int MAX_CARD_RANK = 10;
    public static final int POINTS_TO_WIN = 21;

    // Состояние игры
    private List<Integer> deck;   // колода
    private int dealerTotal;      // очки дилера
    private int playerTotal;      // очки игрока
    private boolean isPlaying;    // идёт ли игра
    private int lastDrawnCard;    // последняя взятая карта (для отображения пользователю)

    /**
     * Конструктор. Инициализирует пустое состояние,
     * но колода и раздача карт будут формироваться в методе startGame().
     */
    public BlackjackGame() {
        this.deck = new ArrayList<>();
        this.dealerTotal = 0;
        this.playerTotal = 0;
        this.isPlaying = false;
        this.lastDrawnCard = 0;
    }

    /**
     * Запуск новой игры: сбор/перемешивание колоды и начальная раздача.
     */
    public void startGame() {
        this.deck = buildDeck();
        // Первая карта дилера
        dealerTotal = deck.remove(deck.size() - 1);
        // Две карты игроку
        playerTotal = deck.remove(deck.size() - 1);
        playerTotal += deck.remove(deck.size() - 1);
        // Флаг, что игра идёт
        isPlaying = true;
    }

    /**
     * Игрок берёт карту.
     */
    public void drawCardForPlayer() {
        if (!isPlaying) {
            return;
        }
        if (deck.isEmpty()) {
            return;
        }
        lastDrawnCard = deck.remove(deck.size() - 1);
        playerTotal += lastDrawnCard;
    }

    /**
     * Логика, когда игрок остановился и ход переходит дилеру:
     * дилер берёт карты, пока не наберёт 17 или больше.
     */
    public void dealerPlay() {
        if (!isPlaying) {
            return;
        }
        while (dealerTotal < 17 && !deck.isEmpty()) {
            int card = deck.remove(deck.size() - 1);
            dealerTotal += card;
        }
    }

    /**
     * Завершение игры. Флаг снимается, чтобы не продолжать обработку команд.
     */
    public void endGame() {
        this.isPlaying = false;
    }

    /**
     * Собираем и перемешиваем колоду из 1..MAX_CARD_RANK
     * (по 4 карты каждого ранга).
     */
    private List<Integer> buildDeck() {
        List<Integer> newDeck = new ArrayList<>();
        for (int rank = MIN_CARD_RANK; rank <= MAX_CARD_RANK; rank++) {
            for (int j = 0; j < 4; j++) {
                newDeck.add(rank);
            }
        }
        Collections.shuffle(newDeck);
        return newDeck;
    }

    // ========== Геттеры и вспомогательные методы ==========

    public boolean isPlaying() {
        return isPlaying;
    }

    public int getDealerTotal() {
        return dealerTotal;
    }

    public int getPlayerTotal() {
        return playerTotal;
    }

    public int getLastDrawnCard() {
        return lastDrawnCard;
    }

    /**
     * Возвращает первую (открытую) карту дилера, чтобы показать игроку при старте.
     */
    public int getDealerFirstCard() {
        return dealerTotal;
    }

    /**
     * Сообщение о результате игры (победа/проигрыш/ничья).
     */
    public String getResultMessage() {
        if (playerTotal > POINTS_TO_WIN) {
            return "Вы перебрали и проиграли!";
        } else if (dealerTotal > POINTS_TO_WIN) {
            return "Дилер перебрал. Вы победили!";
        } else if (playerTotal > dealerTotal) {
            return "Вы победили!";
        } else if (playerTotal < dealerTotal) {
            return "Дилер победил.";
        } else {
            return "Ничья!";
        }
    }
}
