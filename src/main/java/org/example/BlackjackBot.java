package org.example;


import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import static org.example.Logger.logger;
import static org.example.SecretKey.*;

import java.util.HashMap;
import java.util.Map;

public class BlackjackBot extends TelegramLongPollingBot {


    public final Map<Long, BlackjackGame> sessions = new HashMap<>();

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new BlackjackBot());
            logger.info("Бот успешно запущен.");
        } catch (TelegramApiException e) {
            logger.error("Ошибка при запуске бота: ", e);

        }
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String userText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            logger.info("Получен текст от пользователя {}: {}" + chatId, userText);

            BlackjackGame gameSession = sessions.get(chatId);

            if (userText.equals("/start")) {

                BlackjackGame newGame = new BlackjackGame();
                newGame.startGame();
                sessions.put(chatId, newGame);


                sendTextMessage(chatId, "Игра началась! " +
                        "\nВаш счёт: " + newGame.getPlayerTotal() +
                        "\nСчёт дилера: " + newGame.getDealerFirstCard() + " (одна карта скрыта)." +
                        "\n\nДоступные действия:\n" +
                        "\"д\" — взять карту\n" +
                        "\"н\" — остановиться (передать ход дилеру)");
                return;
            }

            if (gameSession == null) {
                sendTextMessage(chatId, "У вас ещё нет активной игры. Наберите /start для начала.");
                return;
            }

            if (gameSession.isPlaying()) {
                switch (userText.toLowerCase()) {
                    case "д":
                        gameSession.drawCardForPlayer();

                        if (gameSession.getPlayerTotal() > BlackjackGame.POINTS_TO_WIN) {
                            sendTextMessage(chatId,
                                    "Вы взяли карту: " + gameSession.getLastDrawnCard() +
                                            "\nВаш счёт: " + gameSession.getPlayerTotal() +
                                            "\nК сожалению, вы перебрали (больше " + BlackjackGame.POINTS_TO_WIN + "). Вы проиграли!" +
                                            "\nВведите /start, чтобы начать заново."
                            );
                            gameSession.endGame();
                        } else {
                            sendTextMessage(chatId,
                                    "Вы взяли карту: " + gameSession.getLastDrawnCard() +
                                            "\nВаш счёт: " + gameSession.getPlayerTotal() +
                                            "\n\nДоступные действия:\n" +
                                            "\"д\" — взять карту\n" +
                                            "\"н\" — остановиться (передать ход дилеру)"
                            );
                        }
                        return;

                    case "н":
                        gameSession.dealerPlay();
                        sendTextMessage(chatId,
                                "Дилер закончил ход.\n" +
                                        "Итоговый счёт дилера: " + gameSession.getDealerTotal() +
                                        "\nВаш счёт: " + gameSession.getPlayerTotal() +
                                        "\n\n" + gameSession.getResultMessage() +
                                        "\nВведите /start для новой игры."
                        );
                        gameSession.endGame();
                        return;

                    default:
                        sendTextMessage(chatId,
                                "Пожалуйста, введите:\n" +
                                        "\"д\" — взять карту\n" +
                                        "\"н\" — остановиться (передать ход дилеру)\n"
                        );
                }
            } else {
                sendTextMessage(chatId, "Игра завершена. Введите /start для новой игры.");
            }
        }
    }


    private void sendTextMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Ошибка при отправке сообщения пользователю {}: {}", chatId, e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return "test_21_o4ko_bot";
    }

    @Override
    public String getBotToken() {
        return Token;
    }
}