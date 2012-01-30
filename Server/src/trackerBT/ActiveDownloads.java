package trackerBT;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author wolfertdekraker
 */
public class ActiveDownloads {


    private static HashMap<TorrentFile, String> torList;

    private static void checkExists() {
        if (torList == null) {
            torList = new HashMap<TorrentFile, String>();
        }

    }

    public ActiveDownloads() {
    }

    public static HashMap<TorrentFile, String>  getTorList() {
        checkExists();
        return torList;
    }

    public static void addActiveTorrent(TorrentFile t, String p) {
        checkExists();
        torList.put(t, p);
    }

    public static void removeTorrent(TorrentFile t) {
        if (torList.containsKey(t)) {
            torList.remove(t);
        }
    }
    
    public static void updatePercentageFromTorrent(TorrentFile t, String p) {
        torList.put(t, p);
        System.out.println("updated! "+t.saveAs + " to: "+p);
    }
}
