package trackerBT;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import simple.http.Request;
import simple.http.Response;
import simple.http.load.Service;
import simple.http.serve.Context;
import simple.http.upload.DiskFileUpload;
import simple.http.upload.FileUpload;
import simple.http.upload.FileUploadException;

/**
 *
 * @author wolfertdekraker
 */
public class TorrentService extends Service {

    TorrentFile torrent;

    public TorrentService(Context context) {
        super(context);
    }

    @Override
    protected void process(Request req, Response resp) throws Exception {
        System.out.println("proc");
//        HashMap hm = this.parseURI(req.getURI());
//        byte[] answer = null;
//        if (hm != null) {
//            TreeMap param = new TreeMap(hm);
//
//            if (param.get("ip") == null) {
//                param.put("ip", req.getInetAddress().toString().substring(1));
//            }
//
//            List peers = new ArrayList();
//            int message = this.processPeerList(param, peers);
//            answer = this.createAnswer(message, peers);
//        } else {
//            answer = this.createAnswer(10, null);
//        }
//        resp.set("Content-Type", "text/plain");
//        resp.setDate("Date", System.currentTimeMillis());
//        resp.set("Server", (String) Constants.get("servername"));
//        OutputStream out = resp.getOutputStream();
//        out.write(answer);
//        out.close();
    }

    private List processRequest(final Request req) {
        System.out.println("req");
        if (req != null) {
            if (FileUpload.isMultipartContent(req)) {
                try {
                    DiskFileUpload dfu = new DiskFileUpload();
                    return dfu.parseRequest(req);
                } catch (FileUploadException fue) {
                }
            }
            return null;
        }
        throw new IllegalArgumentException(
                "UploadService.processRequest: null Request argument");
    }

    private HashMap parseURI(String uri) throws UnsupportedEncodingException {
        String[] temp = uri.split("[?]");
        System.out.println(uri);
//        //String decURI = URLDecoder.decode(uri, Constants.BYTE_ENCODING);
//        HashMap<String, String> params = null;
//        if (temp.length >= 2) {
//            String[] param = temp[1].split("[&]");
//            params = new HashMap<String, String>(param.length);
//            for (int i = 0; i < param.length; i++) {
//                String[] splitParam = param[i].split("[=]");
//                if (splitParam.length == 1) {
//                    params.put(splitParam[0], "");
//                } else if (splitParam.length == 2) {
//                    params.put(splitParam[0], URLDecoder.decode(splitParam[1],
//                            Constants.BYTE_ENCODING));
//                }
//            }
//            return params;
//        }
        return null;
    }

    public TorrentFile getTorrent() {
        return torrent;
    }

    public void setTorrent(TorrentFile torrent) {
        this.torrent = torrent;
    }
}