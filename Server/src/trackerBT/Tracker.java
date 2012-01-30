/*
 * Java Bittorrent API as its name indicates is a JAVA API that implements the Bittorrent Protocol
 * This project contains two packages:
 * 1. jBittorrentAPI is the "client" part, i.e. it implements all classes needed to publish
 *    files, share them and download them.
 *    This package also contains example classes on how a developer could create new applications.
 * 2. trackerBT is the "tracker" part, i.e. it implements a all classes needed to run
 *    a Bittorrent tracker that coordinates peers exchanges. *
 *
 * Copyright (C) 2007 Baptiste Dubuis, Artificial Intelligence Laboratory, EPFL
 *
 * This file is part of jbittorrentapi-v1.0.zip
 *
 * Java Bittorrent API is free software and a free user study set-up;
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Java Bittorrent API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Java Bittorrent API; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * @version 1.0
 * @author Baptiste Dubuis
 * To contact the author:
 * email: baptiste.dubuis@gmail.com
 *
 * More information about Java Bittorrent API:
 *    http://sourceforge.net/projects/bitext/
 */

package trackerBT;

import java.io.*;
import java.net.*;

import simple.http.connect.*;
import simple.http.load.*;
import simple.http.serve.*;

/**
 * Simple HTTP server. It can process certain request according to a given service.
 * This servers acts even as a torrent file depository or a tracker for bittorrent
 * client
 *
 * <p>Titre : TrackerBT</p>
 *
 * <p>Description : Bittorrent Tracker</p>
 *
 * <p>Copyright : Copyright (c) 2007</p>
 *
 * @author Baptiste Dubuis
 * @version 1.0
 */
public class Tracker {

    public static void main(String[] args) {
        if (args.length > 0) {
            try {
                Constants.loadConfig(args[0]);
            } catch (Exception e) {
                System.err.println(
                        "Configuration file not found");
                System.exit(0);
            }
        }
        else {
            System.err.println(
                    "Please specify the location of the configuration file");
            System.exit(0);
        }
        new File((String) Constants.get("context")).mkdirs();
        try {
            FileWriter fw = new FileWriter((String) Constants.get("context") +
                                           "Mapper.xml");
            fw.write("<?xml version=\"1.0\"?>\r\n<mapper>\r\n<lookup>\r\n" +
                     "<service name=\"file\" type=\"trackerBT.FileService\"/>\r\n" +
                     "<service name=\"tracker\" type=\"trackerBT.TrackerService\"/>\r\n" +
                     "<service name=\"upload\" type=\"trackerBT.UploadService\"/>\r\n" +
                     "</lookup>\r\n<resolve>\r\n" +
                     "<match path=\"/*\" name=\"file\"/>\r\n" +
                     "<match path=\"/announce*\" name=\"tracker\"/>\r\n" +
                     "<match path=\"/upload*\" name=\"upload\"/>\r\n" +
                     "</resolve>\r\n</mapper>");
            fw.flush();
            fw.close();
        } catch (IOException ioe) {
            System.err.println("Could not create 'Mapper.xml'");
            System.exit(0);
        }
        Context context = new FileContext(new File((String) Constants.get(
                "context")));

        try {
            MapperEngine engine = new MapperEngine(context);

            ConnectionFactory.getConnection(engine).connect(new ServerSocket(
                    Integer.parseInt((String)Constants.get("listeningPort"))));
            System.out.println(
            "Tracker started! Listening on port " + Constants.get("listeningPort") +
            "\r\n\r\n\t********************************************\r\n" +
            "\t*   Press enter to shut down the server    *\r\n" +
            "\t********************************************\r\n");
            // Setup the I/O buffered stream, to read user input from the command line
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
            String s = null;
            try {
                s = br.readLine();
            } catch (IOException ioe) {
            }
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
