package me.martiii.databasedatainserter;

import me.martiii.databasedatainserter.database.Database;
import me.martiii.databasedatainserter.database.type.MySQLDatabase;
import me.martiii.databasedatainserter.database.utils.Table;
import me.martiii.databasedatainserter.network.NetworkManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.Scanner;

public class DatabaseDataInserter {
    public static void main(String[] args) {
        new DatabaseDataInserter();
    }

    private Database database;

    public DatabaseDataInserter() {
        Properties prop = new Properties();
        File file = new File("config.properties");
        try {
            if (!file.exists()) {
                Files.copy(DatabaseDataInserter.class.getResourceAsStream("/config.properties"), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            InputStream is = new FileInputStream(file);
            prop.load(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String databaseType = prop.getProperty("databaseType", "mysql");
        if (databaseType.equals("mysql")) {
            String host = prop.getProperty("mysqlHost", "localhost");
            int port = Integer.parseInt(prop.getProperty("mysqlPort", "3306"));
            String username = prop.getProperty("mysqlUsername", "username");
            String password = prop.getProperty("mysqlPassword", "password");
            String db = prop.getProperty("mysqlDatabase", "database");
            String table = prop.getProperty("mysqlTable", "table");
            String data = prop.getProperty("mysqlData", "%auto_increment%:%current_millis%:%data0%");

            database = new MySQLDatabase(host, port, username, password, db, new Table(table, data.split(":")));
        }

        int networkPort = Integer.parseInt(prop.getProperty("networkPort", "5782"));
        NetworkManager networkManager = new NetworkManager(this, networkPort);
        networkManager.start();

        Scanner scan = new Scanner(System.in);
        boolean running = true;
        while (running) {
            try {
                String cmd = scan.nextLine();
                String[] parts = cmd.split(" ");
                if (parts[0].equals("")) continue;
                if (parts[0].equals("stop")) {
                    running = false;
                    System.out.println("Stopping this service...");
                    networkManager.close();
                    database.close();
                } else {
                    System.out.println("Unknown command. Use stop to stop this service.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.exit(0);
    }

    public Database getDatabase() {
        return database;
    }
}
