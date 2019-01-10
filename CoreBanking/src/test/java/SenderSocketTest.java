import com.mapsa.core.SenderSocket;
import com.mapsa.core.commits.CommitResponse;
import com.mapsa.core.commits.account.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SenderSocketTest {

    private SenderSocket<CommitResponse> sender;
    NestedServerSocket server;
    @Before
    public void beforeRun(){
        server = new NestedServerSocket(8731);
    }

    @Test
    public void addItem_ifItemsAreSent(){
        //setup
        sender = new SenderSocket<>("localhost",8731);
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
        sender.addResponse(response1);
        sender.addResponse(response2);
        sender.addResponse(response3);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //test
        ArrayList<Object> received_objects = server.getList();
        BlockAccountCommitResponse received_response1 = (BlockAccountCommitResponse) received_objects.get(0);
        WithdrawCommitResponse received_response2 = (WithdrawCommitResponse) received_objects.get(1);
        DeactivateAccountCommitResponse received_response3 = (DeactivateAccountCommitResponse) received_objects.get(2);
        Assert.assertTrue(received_response1.isDone());
        Assert.assertTrue(received_response2.getDone());
        Assert.assertFalse(received_response3.isDone());
    }
    
    class NestedServerSocket extends Thread{
        private ArrayList<Object> objects;
        private int port;
        private ServerSocket server;
        private Socket socket;
        private ObjectInputStream OIS;

        public NestedServerSocket (int port){
            this.port = port;
            objects = new ArrayList<>();
            this.start();
        }

        public ArrayList<Object> getList(){
            return objects;
        }
        @Override
        public void run(){
            try {
                server = new ServerSocket(port);
                socket = server.accept();
                OIS = new ObjectInputStream(socket.getInputStream());
                while (true){
                    objects.add(OIS.readObject());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    OIS.close();
                    socket.close();
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
