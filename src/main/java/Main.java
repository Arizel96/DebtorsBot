import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Arizel on 20.11.2016.
 */
public class Main {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        initMap();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new MyBot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }


    public static void initMap() {
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        initArrayList(list);
        //System.out.println(list);
        MyBot.mapRus = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            ArrayList<String> underList = list.get(i);
            String key = underList.get(underList.size() - 1).toUpperCase();
            MyBot.mapRus.put(key, underList);
        }
    }

    public static void initArrayList(ArrayList<ArrayList<String>> list) {
        MyBot.others.add("dzd");
        MyBot.others.add("azn");
        MyBot.others.add("aud");
        MyBot.others.add("bdt");
        MyBot.others.add("cad");
        MyBot.others.add("clp");
        MyBot.others.add("cny");
        MyBot.others.add("hrk");
        MyBot.others.add("czk");
        MyBot.others.add("dkk");
        MyBot.others.add("hkd");

        ArrayList<String> listForMap = new ArrayList<>();
        listForMap.add("доллар");
        listForMap.add("долар");
        listForMap.add("сша");
        listForMap.add("dolar");
        listForMap.add("dollar");
        listForMap.add("бакс");
        listForMap.add("зелен");
        listForMap.add("usd");
        list.add(listForMap);

        listForMap = new ArrayList<>();
        listForMap.add("армянский драм");
        listForMap.add("армян");
        listForMap.add("драм");
        listForMap.add("dram");
        listForMap.add("amd");
        list.add(listForMap);

        listForMap = new ArrayList<>();
        listForMap.add("болгар");
        listForMap.add("лев");
        listForMap.add("lev");
        listForMap.add("bgn");
        list.add(listForMap);

        listForMap = new ArrayList<>();
        listForMap.add("евро");
        listForMap.add("euro");
        listForMap.add("eur");
        list.add(listForMap);

        listForMap = new ArrayList<>();
        listForMap.add("рубль");
        listForMap.add("руб");
        listForMap.add("rub");
        list.add(listForMap);
    }

}
