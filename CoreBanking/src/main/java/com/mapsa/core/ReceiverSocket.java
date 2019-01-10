package com.mapsa.core;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ReceiverSocket<T> extends Thread{
    private int port;
    private List<T> queue;
    private ServerSocket server;
    private ObjectInputStream OIS;
    private Socket socket;

    public ReceiverSocket(int port){
        this.port = port;
        queue = new ArrayList<>();
        this.start();
    }
    public T getItem(){
        T item = queue.get(0);
        queue.remove(0);
        return item;
    }
    @Override
    public void run(){
        {
            try {
                server = new ServerSocket(port);
                socket = server.accept();
                OIS = new ObjectInputStream(socket.getInputStream());
                while (true) {
                    @SuppressWarnings("unchecked")
                    T item =(T) OIS.readObject();
                    if (item!=null)
                        queue.add(item);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            finally {
                closing();
            }
        }
    }

    private void closing() {
        try {
            OIS.close();
            socket.close();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
