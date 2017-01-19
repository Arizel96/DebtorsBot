package objects;

import com.mysql.fabric.jdbc.FabricMySQLDriver;
import controllers.Controller;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by Arizel on 19.01.2017.
 */
public class DataManager {
    private static final String INSERT = "";
    private static final String DELETE = "";
    private static final String UPDATE = "";

    private static DataManager dataManager;

    private Driver driver;

    public DataManager() {
        try {
            driver = new FabricMySQLDriver();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DataManager getInstance() {
        if (dataManager == null) dataManager = new DataManager();
        return dataManager;
    }

    public String actionOnTheData(HashMap<String, Debtor> data) {

        return Controller.SUCCESS;
    }

    private String deleteRecord() {
        return null;
    }

    private String getRecord() {
        return null;
    }

    private String addRecord() {
        return null;
    }



    //    CREATE TABLE `telegram_debtor`.`test` (
//            `id` INT NOT NULL AUTO_INCREMENT,
//  `Name` VARCHAR(45) NOT NULL,
//  `Credit` VARCHAR(45) NOT NULL,
//    PRIMARY KEY (`id`, `Name`, `Credit`));
}
