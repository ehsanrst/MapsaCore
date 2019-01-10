package com.mapsa.core;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SenderSocket<T> extends Thread{
    private Socket socket;
    private int port;
    private String IP;
    private List<T> queue;
    ObjectOutputStream OOS;

    public SenderSocket(String IP,int port) {
        this.port = port;
        this.IP = IP;
        queue = new ArrayList<>();
        this.start();
    }

    public void addResponse(T response){
        queue.add(response);
    }

    public void run(){
        try {
            socket = new Socket(IP, port);
            OOS = new ObjectOutputStream(socket.getOutputStream());
            while(true) {
                if (queue.size()>0) {
                    OOS.writeObject(queue.get(0));
                    queue.remove(0);
                } else {
                    sleep(1000);
                }
                if (queue ==null){
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void close() {
        if (socket!=null) {
            try {
                OOS.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
