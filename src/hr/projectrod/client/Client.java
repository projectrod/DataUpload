package hr.projectrod.client;

/**
 *
 * @author wolfertdekraker
 */
public class Client {
    
    public static void main(String args[]){
        ClientThread ct = new ClientThread();
        ct.run();
    }
}

class ClientThread implements Runnable{

    public ClientThread() {
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
