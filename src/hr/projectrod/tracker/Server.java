package hr.projectrod.tracker;

/**
 *
 * @author wolfertdekraker
 */
public class Server {

    public static void main(String args[]){
        ServerThread st = new ServerThread(Boolean.TRUE);
        st.run();
    }
}

class ServerThread implements Runnable {

    private boolean seedOnly;
    
    public ServerThread(boolean seedOnly) {
        this.seedOnly = seedOnly;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}