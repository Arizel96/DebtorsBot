import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Arizel on 20.11.2016.
 */
public class MyBot extends TelegramLongPollingBot {
    public static HashMap<String, ArrayList<String>> mapRus;
    public static ArrayList<String> others = new ArrayList<>();
    private String toUser = "Благодарю за запрос, сударь, вот актуальнейший курс на сегодня по отношению к гривне : ";
    private String toUserIfError = "Простите, похоже, что сервис НБУ Украины не хочет сообщать нынешний курс на эту валюту";
    private String toUserRepeat = "Прошу прощения, сударь. Мне совершенно не ясны ваши намерения. Пожалуйста, будьте добры, сообщите мне валюту курс на которую вас интересует, спасибо.";

    @Override
    public void onUpdateReceived(Update update) {
        Message msg = update.getMessage();
        String msgText = msg.getText().toLowerCase();
        if (msg != null && msg.hasText()) {
            String currency = "";
            for (Map.Entry<String, ArrayList<String>> pair : mapRus.entrySet()) {
                ArrayList<String> list = pair.getValue();
                for (int i = 0; i < list.size(); i++) {
                    if (msgText.contains(list.get(i))) {
                        if (pair.getKey().equals("USD")) {
                            currency = getUSD();
                            break;
                        } else if (pair.getKey().equals("RUB")) {
                            currency = getRUB();
                            break;
                        } else if (pair.getKey().equals("EUR")) {
                            currency = getEUR();
                            break;
                        } else {
                            currency = getCurrency(pair.getKey());
                            break;
                        }
                    }
                }
                if (!currency.isEmpty()) {
                    break;
                }
            }

            if (currency.isEmpty()) {
                for (int i = 0; i < others.size(); i++) {
                    if (msgText.contains(others.get(i))) {
                        currency = getCurrency(others.get(i));
                        break;
                    }
                }
            }

            if (currency.equals("1")) {
                sendMsg(toUserIfError, msg);
            } else if (!currency.isEmpty()) {
                sendMsg((toUser + currency), msg);
            } else if (msgText.contains("фарту") || msgText.contains("масти") || msgText.contains("ауе")){
                sendMsg("Фарту масти мусорам по пасти, ауе! Всех любим, всех уважаем!", msg);
            } else {
                sendMsg(toUserRepeat, msg);
            }
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

    public String getCurrency(String code) {
        StringBuilder sb = new StringBuilder();
        String currency = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String date = dateFormat.format(new Date());
            URLConnection urlConnection = new URL("https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?valcode=" + code + "&date=" + date + "&json").openConnection();
            InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
            Scanner scanner = new Scanner(reader);
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
            }
            JSONArray array = (JSONArray) new JSONParser().parse(String.valueOf(sb));
            HashMap<String, Object> map = (HashMap<String, Object>) array.get(0);
            double rate = (double) map.get("rate");
            currency = String.valueOf(rate);
        } catch (IOException e) {
           //return "1";
            e.printStackTrace();
        } catch (ParseException e) {
            //return "1";
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            //return "1";
            e.printStackTrace();
        }
        return currency;
    }

    public String getUSD() {
        StringBuilder sb = new StringBuilder();
        String currency = "";
        try {
            URLConnection urlConnection = new URL("https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5").openConnection();
            InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
            Scanner scanner = new Scanner(reader);
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
            }
            JSONArray array = (JSONArray) new JSONParser().parse(String.valueOf(sb));
            HashMap<String, Object> map = (HashMap<String, Object>) array.get(2);
            String buy = (String) map.get("buy");
            String sale = (String) map.get("sale");
            currency = "\nПокупка : " + buy + "\n" + "Продажа: " + sale + "\n (Курс ПриватБанка)";
        } catch (IOException e) {
            return "1";
        } catch (ParseException e) {
            return "1";
        } catch (IndexOutOfBoundsException e) {
            return "1";
        }
        return currency;
    }

    public String getRUB() {
        StringBuilder sb = new StringBuilder();
        String currency = "";
        try {
            URLConnection urlConnection = new URL("https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5").openConnection();
            InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
            Scanner scanner = new Scanner(reader);
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
            }
            JSONArray array = (JSONArray) new JSONParser().parse(String.valueOf(sb));
            HashMap<String, Object> map = (HashMap<String, Object>) array.get(1);
            String buy = (String) map.get("buy");
            String sale = (String) map.get("sale");
            currency = "\nПокупка : " + buy + "\n" + "Продажа: " + sale + "\n (Курс ПриватБанка)";
        } catch (IOException e) {
            return "1";
        } catch (ParseException e) {
            return "1";
        } catch (IndexOutOfBoundsException e) {
            return "1";
        }
        return currency;
    }

    public String getEUR() {
        StringBuilder sb = new StringBuilder();
        String currency = "";
        try {
            URLConnection urlConnection = new URL("https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5").openConnection();
            InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
            Scanner scanner = new Scanner(reader);
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
            }
            JSONArray array = (JSONArray) new JSONParser().parse(String.valueOf(sb));
            HashMap<String, Object> map = (HashMap<String, Object>) array.get(0);
            String buy = (String) map.get("buy");
            String sale = (String) map.get("sale");
            currency = "\nПокупка : " + buy + "\n" + "Продажа: " + sale + "\n(Курс ПриватБанка)";
        } catch (IOException e) {
            return "1";
        } catch (ParseException e) {
            return "1";
        } catch (IndexOutOfBoundsException e) {
            return "1";
        }
        return currency;
    }



    @Override
    public String getBotToken() {
        return "260266090:AAH8IgdAI4g1KbzBuaIJ9YXXwvvvf11UZLs";
    }
}
