package me.martiii.databasedatainserter.database.utils;

public class Table {
    private String table;
    private String[] data;

    public Table(String table, String[] data) {
        this.table = table;
        this.data = data;
    }

    public String getTable() {
        return table;
    }

    public int getColumns() {
        return data.length;
    }

    public String[] getData() {
        return data;
    }
}
