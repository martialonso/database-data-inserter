package me.martiii.databasedatainserter.network;

import me.martiii.databasedatainserter.DatabaseDataInserter;
import me.martiii.databasedatainserter.database.Database;

import javax.net.ServerSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkManager extends Thread {
    private Database database;
    private int port;
    private boolean closed = false;

    public NetworkManager(DatabaseDataInserter databaseDataInserter, int port) {
        this.database = databaseDataInserter.getDatabase();
        this.port = port;
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        ServerSocketFactory factory = ServerSocketFactory.getDefault();
        try {
            serverSocket = factory.createServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (serverSocket != null) {
            System.out.println("\u001B[32mListening on port " + port + "\u001B[0m");
            while (!closed) {
                try {
                    Socket socket = serverSocket.accept();
                    if (socket != null) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        if (!socket.isClosed()) {
                            String received = in.readLine();
                            System.out.println("Adding data: " + received);
                            database.addData(received.split(":"));
                            socket.close();
                        } else {
                            System.out.println("\u001B[31mSocket closed when trying to read.\u001B[0m");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void close() {
        closed = true;
    }
}
