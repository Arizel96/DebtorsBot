package controllers;

import objects.DataManager;
import objects.Debtor;
import org.apache.commons.lang.math.NumberUtils;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.HashMap;

/**
 * Created by Arizel on 20.11.2016.
 */
public class Controller extends TelegramLongPollingBot {
    public static final String ERROR = "Sorry, i cant understand you. Just try to write more correctly. Thank you :)";
    public static final String SUCCESS = "Операция прошла успешно!";

    DataManager dataManager = DataManager.getInstance();

    @Override
    public void onUpdateReceived(Update update) {
        Message msg = update.getMessage();
        HashMap<String, Debtor> data = getData(msg.getText().toLowerCase());
        if (data == null) {
            sendMsg(ERROR, msg);
        } else {
            sendMsg(dataManager.actionOnTheData(data), msg);
        }
    }


    @Override
    public String getBotUsername() {
        return "KianyRivs_Bot";
    }


    public void sendMsg(String text, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotToken() {
        return "260266090:AAH8IgdAI4g1KbzBuaIJ9YXXwvvvf11UZLs";
    }

    private HashMap getData(String msg) {
        String response = operationDefine(msg);
        Debtor debtor = dataDefine(msg);
        if (debtor == null || response.equals(ERROR)) {
            return null;
        }else {
            HashMap<String, Debtor> data = new HashMap<>();
            data.put(response, debtor);
            return data;
        }
    }

    private Debtor dataDefine(String msg) {
        String string = msg.replaceAll("[!,/@#%$^&*()+=_-~`'.]", " ");
        String[] strings = string.split(" ");
        String name = null;
        long credit = 0;
        for (String str : strings) {
            if (!str.isEmpty()) {
                if (NumberUtils.isNumber(str)) {
                    credit = Long.parseLong(str);
                } else {
                    name = str;
                }
            }
        }
        if (name != null && credit != 0) {
            return new Debtor(name, credit);
        } else {
            return null;
        }

    }

    private String operationDefine(String msg) {
        if (msg.contains("удал")) {
            return "delete";
        } else if (msg.contains("выб") || msg.contains("выт") || msg.contains("узнат")) {
            return "select";
        } else if (msg.contains("внес") || msg.contains("доба")) {
            return "insert";
        } else if (msg.contains("измен")) {
            return "update";
        } else return ERROR;
    }


}
