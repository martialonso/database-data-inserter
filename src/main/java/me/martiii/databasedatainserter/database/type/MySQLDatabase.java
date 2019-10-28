package me.martiii.databasedatainserter.database.type;

import com.zaxxer.hikari.HikariDataSource;
import me.martiii.databasedatainserter.database.Database;
import me.martiii.databasedatainserter.database.utils.Table;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.function.Consumer;

public class MySQLDatabase implements Database {
    private HikariDataSource ds;
    private Table table;

    public MySQLDatabase(String host, int port, String username, String password, String database, Table table) {
        this.table = table;
        try {
            ds = new HikariDataSource();
            ds.setDriverClassName("com.mysql.jdbc.Driver");
            ds.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
            ds.setUsername(username);
            ds.setPassword(password);

            execute(connection -> System.out.println("\u001B[32mConnection with the database successfully established.\u001B[0m"));
        } catch (Exception e) {
            System.out.println("\u001B[31mError when trying to connect to the database.\u001B[0m");
            e.printStackTrace();
        }
    }

    public void execute(Consumer<Connection> consumer) {
        new Thread(() -> {
            try {
                Connection connection = ds.getConnection();
                if (connection != null) {
                    consumer.accept(connection);
                    connection.close();
                } else {
                    System.out.println("\u001B[31mError when trying to connect to the database.\u001B[0m");
                }
            } catch (SQLException e) {
                System.out.println("\u001B[31mError when trying to connect to the database.\u001B[0m");
                e.printStackTrace();
            }
        }).start();
    }

    public void addData(Object[] data) {
        execute(connection -> {
            try {
                String sql = "INSERT INTO " + table.getTable() + " VALUES(" + StringUtils.repeat("?, ", table.getColumns()).substring(0, table.getColumns() * 3 - 2) + ")";
                PreparedStatement statement = connection.prepareStatement(sql);
                String[] tData = table.getData();
                for (int i = 0; i < table.getColumns(); i++) {
                    String type = tData[i];
                    if (type.equals("%auto_increment%")) {
                        statement.setNull(i + 1, Types.INTEGER);
                    } else if (type.equals("%current_millis%")) {
                        statement.setLong(i + 1, System.currentTimeMillis());
                    } else if (type.matches("%data[0-9]+%")){
                        int j = Integer.parseInt(type.substring(5, 6));
                        statement.setObject(i + 1, data[j]);
                    } else {
                        System.out.println("Unknown mysql data structure type: " + type);
                    }
                }
                statement.execute();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    public void close() {
        if (ds != null) {
            if (!ds.isClosed()) {
                ds.close();
            }
        }
    }
}
