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

import simple.http.*;
import simple.http.load.*;
import simple.http.serve.*;
import simple.util.net.*;

/**
 * Service called to process any request but upload and tracking ones. Simply serves the desired file
 * @author Bat
 *
 */
public class FileService extends Service {
    String percentage = "";

    public FileService(Context context) {
        super(context);
    }

    public void process(Request req, Response resp) throws IOException {
        String target = req.getURI();
        Parameters p = req.getParameters();
        System.out.println("test");

        if (target.matches("/favicon.ico")) {
            return;
        }
        if (targetIsActiveTorrent(target)) {
            resp.set("Content-Type", "text/html");
            resp.setDate("Date", System.currentTimeMillis());
            resp.set("Server", (String) Constants.get("servername"));
            OutputStream out = resp.getOutputStream();
            out.write(getPercentage());
            out.flush();
            out.close();
        } else {
            if (target.matches("/") || target.startsWith("/?")) {
                target = "/index.html" + target.replaceFirst("/", "");
            }
            resp.set("Content-Type", context.getContentType(target));
            resp.setDate("Date", System.currentTimeMillis());
            resp.set("Server", "Extending Bittorrent Tracker");
            OutputStream out = resp.getOutputStream();
            context.getContent(target).write(out);
            out.close();
        }
    }

    private byte[] getPercentage() {
        return percentage.getBytes();
    }

    private boolean targetIsActiveTorrent(String target) {
        target = target.substring(1, target.length());
        System.out.println("substring  target = " + target);
        for (TorrentFile t : ActiveDownloads.getTorList().keySet()) {
            System.out.println("comparing: " + target + " to: " + t.saveAs);
            if (t.saveAs.equals(target)) {
                percentage = ActiveDownloads.getTorList().get(t);
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }
}
