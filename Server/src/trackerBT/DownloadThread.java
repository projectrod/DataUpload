package trackerBT;

import jBittorrentAPI.DownloadManager;
import java.util.ArrayList;
import simple.http.serve.Context;

/**
 *
 * @author wolfertdekraker
 */
public class DownloadThread extends Thread {

    private String torPath;
    private Context context;
    private TorrentService ts;
    
    public DownloadThread() {
        
    }

    public DownloadThread(Context context) {
    }

    void registerDownload(String torDirAndTorName) {
        this.torPath = torDirAndTorName;
        

    }

    public void run() {
        try {
            TorrentProcessor tp = new TorrentProcessor();
            TorrentFile t = tp.getTorrentFile(tp.parseTorrent(torPath));
             
            Constants.SAVEPATH = "downloads";
            if (t != null) {
                DownloadManager dm = new DownloadManager(t, Utils.generateID());

                ts = new TorrentService(context);
                ts.setTorrent(t);
                dm.startListening(6881, 6889);
                dm.startTrackerUpdate();

                dm.blockUntilCompletion();
                dm.stopTrackerUpdate();
                dm.closeTempFiles();
            } else {
                System.err.println(
                        "Provided file is not a valid torrent file");
                System.err.flush();
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println("Error while processing torrent file. Please restart the client");
            System.exit(1);
        }
    }

    void setContext(Context context) {
        this.context = context;
    }
    
}
