
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import simple.http.connect.ConnectionFactory;
import simple.http.load.MapperEngine;
import simple.http.serve.Context;
import simple.http.serve.FileContext;
import trackerBT.*;

/**
 *
 * @author wolfertdekraker
 */
public class RunServer {

    private Context context;
    private FileService fs;
    private TrackerService ts;
    private UploadService us;
    private DownloadThread ds;

    public static void main(String[] args) {
        new RunServer();
    }

    public RunServer() {
        // check config file:
        if (!configFound()) {
            createDefaultConfig();
        }
        
        // ip + poort printen. check of t klopt.
        
        
       System.out.println("c = "+ Constants.get("context"));

        // Start de socket verbinding:
        context = getFileContext(Constants.get("context"));
        OpenSocketConnection(context);


        fs = new FileService(context);
        ts = new TrackerService(context);
        us = new UploadService(context);
        TorrentService ts = new TorrentService(context);
        
        us.setDs(ds);

        System.out.println("Server gestart op "+getIPAdress()+":"+Constants.get("listeningPort"));
    }

    private void OpenSocketConnection(Context context) {
        try {
            MapperEngine engine = new MapperEngine(context);
            ConnectionFactory.getConnection(engine).connect(new ServerSocket(Integer.parseInt((String) Constants.get("listeningPort"))));
        } catch (IOException ex) {
            System.out.println("Fout bij het verbinden aan de serverSocket: " + ex.toString());
        }
    }

    private FileContext getFileContext(Object context) {
        return new FileContext(new File((String) context));
    }
    
    private String getIPAdress() {
        String ip = "";
        try {
            InetAddress addr = InetAddress.getLocalHost();
            ip =  addr.getCanonicalHostName();
            System.out.println("todo tracker ip: "+ip);

        } catch (Exception e) {
            System.out.println("Kon IP adres niet opvragen: " + e);
        }
        return ip;
    }
    
    private boolean configFound() {
        try {
            Constants.loadConfig("config.xml");
            return Boolean.TRUE;
        } catch (Exception e) {
            System.err.println("Configuratie bestand niet gevonden: " + e.toString());
            return Boolean.FALSE;
        }
    }

    private void createDefaultConfig() {
        System.out.println("Default configuratie bestand aangemaakt");

        new File((String) Constants.get("context")).mkdirs();
        try {
            FileWriter fw = new FileWriter((String) Constants.get("context")
                    + "Mapper.xml");
            fw.write("<?xml version=\"1.0\"?>\r\n<mapper>\r\n<lookup>\r\n"
                    + "<service name=\"file\" type=\"trackerBT.FileService\"/>\r\n"
                    + "<service name=\"tracker\" type=\"trackerBT.TrackerService\"/>\r\n"
                    + "<service name=\"upload\" type=\"trackerBT.UploadService\"/>\r\n"
                    + "</lookup>\r\n<resolve>\r\n"
                    + "<match path=\"/*\" name=\"file\"/>\r\n"
                    + "<match path=\"/announce*\" name=\"OpenDataTracker\"/>\r\n"
                    + "<match path=\"/upload*\" name=\"upload\"/>\r\n"
                    + "</resolve>\r\n</mapper>");
            fw.flush();
            fw.close();
        } catch (IOException ioe) {
            System.err.println("Kon het configuratie bestant niet aanmaken: " + ioe);
            System.exit(0);
        }
    }
}
