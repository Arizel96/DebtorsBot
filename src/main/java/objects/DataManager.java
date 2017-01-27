package objects;

import com.mysql.fabric.jdbc.FabricMySQLDriver;
import controllers.Controller;

import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Arizel on 19.01.2017.
 */
public class DataManager {
    private static final String CREATE_TABLE =  "CREATE TABLE `telegram_debtor`.`%s` (" +
                                                "`id` INT NOT NULL AUTO_INCREMENT," +
                                                "`Name` VARCHAR(45) NOT NULL," +
                                                "`Credit` VARCHAR(45) NOT NULL," +
                                                "UNIQUE INDEX `id_UNIQUE` (`id` ASC)," +
                                                "PRIMARY KEY (`id`, `Name`, `Credit`)," +
                                                "UNIQUE INDEX `Name_UNIQUE` (`Name` ASC))";

    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/telegram_debtor?useSSL=false";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    private static final String GET_MAX_ID = "SELECT max(id) FROM %s";
    private static final String SELECT = "SELECT * FROM %s WHERE Name = ?";
    private static final String INSERT = "INSERT INTO %s VALUES (?,?,?)";
    private static final String DELETE = "DELETE FROM %s WHERE Name = ?";
    private static final String UPDATE = "UPDATE %s set Credit = ? where Name = ?";

    private static DataManager dataManager;

    private Driver driver;

    private DataManager() {
        try {
            driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DataManager getInstance() {
        if (dataManager == null) dataManager = new DataManager();
        return dataManager;
    }

    public String actionOnTheData(HashMap<String, Debtor> data, String chatId) {

        String response = Controller.ERROR;

        try(Connection connection = DriverManager.getConnection(MYSQL_URL, USERNAME, PASSWORD);
        Statement statement = connection.createStatement()) {

            if (!tableExist(connection, chatId)) {
                String checkTable = String.format(CREATE_TABLE, chatId);
                statement.execute(checkTable);
            }

            Iterator<Map.Entry<String, Debtor>> iterator = data.entrySet().iterator();
            Map.Entry<String, Debtor> pair = iterator.next();

            switch (pair.getKey()) {
                case "delete":
                    response = deleteRecord(chatId, pair.getValue(), connection);
                    break;
                case "select":
                    response = getRecord(chatId, pair.getValue(), connection);
                    break;
                case "insert":
                    response = addRecord(chatId, pair.getValue(), connection);
                    break;
                case "update":
                    response = updateRecord(chatId, pair.getValue(), connection);
                    break;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return response;
    }

    private String deleteRecord(String chatId, Debtor debtor, Connection connection) {
        String tableName = "`" + chatId + "`";

        String sqlDelete = String.format(DELETE, tableName);
        boolean successful = true;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlDelete)) {

            preparedStatement.setString(1, debtor.getName());
            preparedStatement.execute();
        } catch (SQLException e) {
            successful = false;
        }

        return successful ? Controller.SUCCESS : Controller.ERROR;
    }

    private String getRecord(String chatId, Debtor debtor, Connection connection) {
        String tableName = "`" + chatId + "`";
        String selectQuery = String.format(SELECT, tableName);
        boolean successful = true;
        String response = "\n";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

            preparedStatement.setString(1, debtor.getName());
            ResultSet set = preparedStatement.executeQuery();
            set.next();
            response += set.getString(2) + " должен вам " + set.getString(3);
            set.close();
        } catch (SQLException e) {
            successful = false;
        }

        return successful ? Controller.SUCCESS + response : Controller.ERROR;
    }

    private String addRecord(String chatId, Debtor debtor, Connection connection) {
        String tableName = "`" + chatId + "`";
        String idQuery = String.format(GET_MAX_ID, tableName);
        String sqlInsert = String.format(INSERT, tableName);

        boolean successful = true;
        try (Statement statement = connection.createStatement();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert)) {

            ResultSet set = statement.executeQuery(idQuery);
            set.next();
            int id = set.getInt(1) + 1;
            set.close();

            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, debtor.getName());
            preparedStatement.setString(3, String.valueOf(debtor.getCredit()));
            preparedStatement.execute();
        } catch (SQLException e) {
            successful = false;
        }
        return successful ? Controller.SUCCESS : Controller.ERROR;
    }

    private String updateRecord(String chatId, Debtor debtor, Connection connection) {
        String tableName = "`" + chatId + "`";
        String sqlSelect = String.format(SELECT, tableName);
        String sqlUpdate = String.format(UPDATE, tableName);
        boolean successful = true;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate);
             PreparedStatement preparedStatement1 = connection.prepareStatement(sqlSelect)) {

            preparedStatement1.setString(1, debtor.getName());
            ResultSet set = preparedStatement1.executeQuery();
            if (set.next()) {
                preparedStatement.setString(1, String.valueOf(debtor.getCredit()));
                preparedStatement.setString(2, debtor.getName());
                preparedStatement.execute();
            } else {
                successful = false;
            }
        } catch (SQLException e) {
            successful = false;
        }

        return successful ? Controller.SUCCESS : Controller.ERROR;
    }

    public String getTable(String chatId) {
        String response = "";
        try(Connection connection = DriverManager.getConnection(MYSQL_URL, USERNAME, PASSWORD);
        Statement statement = connection.createStatement()) {

            ResultSet set = statement.executeQuery("SELECT * FROM telegram_debtor." + "`" + chatId + "`");
            set.next();
            response = getAllRecords(set);
            set.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return response;
    }

    private String getAllRecords(ResultSet set) throws SQLException {
        String response = "";

        while (set.next()) {
            response += set.getString(2) + " должен вам " + set.getString(3) + "\n";
        }

        return response;
    }

    private boolean tableExist(Connection conn, String tableName) throws SQLException {
        boolean tExists = false;
        try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName, null)) {
            while (rs.next()) {
                String tName = rs.getString("TABLE_NAME");
                if (tName != null && tName.equals(tableName)) {
                    tExists = true;
                    break;
                }
            }
        }
        return tExists;
    }



//    String sql = "CREATE TABLE REGISTRATION " +
//            "(id INTEGER not NULL, " +
//            " Name VARCHAR(45), " +
//            " Credit VARCHAR(45), " +
//            " PRIMARY KEY ( id, Name, Credit ))";

//    UNIQUE INDEX `idnew_table_UNIQUE` (`idnew_table` ASC),
//    UNIQUE INDEX `new_tablecol_UNIQUE` (`new_tablecol` ASC)


//    "CREATE TABLE `telegram_debtor`.`%s` (" +
//            "`id` INT NOT NULL AUTO_INCREMENT," +
//            "`Name` VARCHAR(45) NOT NULL," +
//            "`Credit` VARCHAR(45) NOT NULL," +
//            "PRIMARY KEY (`id`, `Name`, `Credit`))";



//    PreparedStatement sql = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + db + ".blocks (`player` varchar(16) NOT NULL," +
//            " `itemid` text NOT NULL, `location` text NOT NULL, `action` text NOT NULL, " +
//            "`time` bigint(20) NOT NULL ) ENGINE=InnoDB DEFAULT CHARSET=latin1");

//        CREATE TABLE `telegram_debtor`.`test` (
//            `id` INT NOT NULL AUTO_INCREMENT,
//  `Name` VARCHAR(45) NOT NULL,
//  `Credit` VARCHAR(45) NOT NULL,
//    PRIMARY KEY (`id`, `Name`, `Credit`));

    //CREATE TABLE `telebot`.`koko` (
    // `id` INT NOT NULL AUTO_INCREMENT,
    // `Name` VARCHAR(45) NOT NULL,
    // PRIMARY KEY (`id`, `Name`, `Credit`));
}
