package me.martiii.databasedatainserter.database;

public interface Database extends AutoCloseable{
    void addData(Object[] data);
}
