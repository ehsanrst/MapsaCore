import com.mapsa.core.ReceiverSocket;
import com.mapsa.core.commits.CommitResponse;
import com.mapsa.core.commits.account.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ReceiverSocketTest {

    private ReceiverSocket<CommitResponse> receiver;
    NestedClientSocket sender;
    @Before
    public void beforeRun(){
        sender = new NestedClientSocket("localhost" ,8731);
    }

    @Test
    public void addItem_ifItemsAreSent(){
        //setup
        receiver = new ReceiverSocket<>(8731);
        BlockAccountCommit commit1 = new BlockAccountCommit();
        commit1.setAccountId("111");
        commit1.setCUID("222");
        WithdrawCommit commit2 = new WithdrawCommit();
        commit2.setCUID("444");
        DeactivateAccountCommit commit3 = new DeactivateAccountCommit("555");
        commit3.setCUID("666");
        BlockAccountCommitResponse response1 = new BlockAccountCommitResponse(commit1,true);
        WithdrawCommitResponse response2 = new WithdrawCommitResponse(commit2,true);
        DeactivateAccountCommitResponse response3 = new DeactivateAccountCommitResponse(commit3,false);
        //action
        sender.addObject(response1);
        sender.addObject(response2);
        sender.addObject(response3);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //test
        BlockAccountCommitResponse received_response1 =(BlockAccountCommitResponse) receiver.getItem();
        WithdrawCommitResponse received_response2 = (WithdrawCommitResponse) receiver.getItem();
        DeactivateAccountCommitResponse received_response3 = (DeactivateAccountCommitResponse) receiver.getItem();
        Assert.assertTrue(received_response1.isDone());
        Assert.assertTrue(received_response2.getDone());
        Assert.assertFalse(received_response3.isDone());
    }

    class NestedClientSocket extends Thread{
        private ArrayList<Object> objects;
        private int port;
        private Socket socket;
        private String IP;
        private ObjectOutputStream OOS;

        public NestedClientSocket(String IP , int port){
            this.port = port;
            objects = new ArrayList<>();
            this.IP = IP;
            this.start();
        }

        public void addObject(Object obj){
            objects.add(obj);
        }
        @Override
        public void run(){
            try {
                socket = new Socket(IP,port);
                OOS = new ObjectOutputStream(socket.getOutputStream());
                while (true){
                    while(objects.size()>0){
                        OOS.writeObject(objects.get(0));
                        objects.remove(0);
                    }
                    sleep(500);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    OOS.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
