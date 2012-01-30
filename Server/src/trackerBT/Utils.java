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

import java.nio.ByteBuffer;
import java.security.*;
import java.util.Random;

/**
 * A set of utility methods used by several classes
 * @author Bat
 *
 */
public class Utils {

    /*
     * Convert a byte array into a URL encoded String
     */
    public static String byteArrayToURLString(byte in[]) {
        byte ch = 0x00;
        int i = 0;
        if (in == null || in.length <= 0) {
            return null;
        }

        String pseudo[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                          "A", "B", "C", "D", "E", "F"};
        StringBuffer out = new StringBuffer(in.length * 2);

        while (i < in.length) {
            // First check to see if we need ASCII or HEX
            if ((in[i] >= '0' && in[i] <= '9')
                || (in[i] >= 'a' && in[i] <= 'z')
                || (in[i] >= 'A' && in[i] <= 'Z') || in[i] == '$'
                || in[i] == '-' || in[i] == '_' || in[i] == '.'
                || in[i] == '+' || in[i] == '!') {
                out.append((char) in[i]);
                i++;
            } else {
                out.append('%');
                ch = (byte) (in[i] & 0xF0); // Strip off high nibble
                ch = (byte) (ch >>> 4); // shift the bits down
                ch = (byte) (ch & 0x0F); // must do this is high order bit is
                // on!
                out.append(pseudo[(int) ch]); // convert the nibble to a
                // String Character
                ch = (byte) (in[i] & 0x0F); // Strip off low nibble
                out.append(pseudo[(int) ch]); // convert the nibble to a
                // String Character
                i++;
            }
        }

        String rslt = new String(out);

        return rslt;
    }

    public static String byteStringToByteArray(String s){
        String ret = "";
        for(int i = 0; i < s.length(); i += 2)
            ret +="%" + (char)s.charAt(i) + (char)s.charAt(i+1);
        return ret;
    }

    /**
     *
     * Convert a byte[] array to readable string format. This makes the "hex"
     * readable!
     *
     * @author Jeff Boyle
     *
     * @return result String buffer in String format
     *
     * @param in
     *            byte[] buffer to convert to string format
     *
     */
    // Taken from http://www.devx.com/tips/Tip/13540
    public static String byteArrayToByteString(byte in[]) {
        byte ch = 0x00;
        int i = 0;
        if (in == null || in.length <= 0) {
            return null;
        }

        String pseudo[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                          "A", "B", "C", "D", "E", "F"};
        StringBuffer out = new StringBuffer(in.length * 2);

        while (i < in.length) {
            ch = (byte) (in[i] & 0xF0); // Strip off high nibble
            ch = (byte) (ch >>> 4); // shift the bits down
            ch = (byte) (ch & 0x0F); // must do this is high order bit is on!
            out.append(pseudo[(int) ch]); // convert the nibble to a String
            // Character
            ch = (byte) (in[i] & 0x0F); // Strip off low nibble
            out.append(pseudo[(int) ch]); // convert the nibble to a String
            // Character
            i++;
        }

        String rslt = new String(out);

        return rslt;
    }

    /**
     * Compute the SHA1 hash of the array in parameter
     * @param hashThis The array to be hashed
     * @return byte[] The SHA1 hash
     */
    public static byte[] hash(byte[] hashThis) {
        try {
            byte[] hash = new byte[20];
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            hash = md.digest(hashThis);
            return hash;
        } catch (NoSuchAlgorithmException nsae) {
            System.err.println("SHA-1 algorithm is not available...");
            System.exit(2);
        }
        return null;
    }
        /**
     * Generate the client id, which is a fixed string of length 8 concatenated with 12 random bytes
     * @return byte[]
     */
    public static byte[] generateID() {
        byte[] id = new byte[12];

        Random r = new Random(System.currentTimeMillis());
        r.nextBytes(id);
        return Utils.concat("-BE0001-".getBytes(),id);
    }

    /**
     * Concatenate the 2 byte arrays
     * @param a byte[]
     * @param b byte[]
     * @return byte[]
     */
    public static byte[] concat2(byte[] a, byte[] b) {
        ByteBuffer bb = ByteBuffer.allocate(a.length + b.length);
        bb.put(a);
        bb.put(b);
        return bb.array();
    }
    /**
     * Concatenate the 2 byte arrays
     * @param b1 byte[]
     * @param b2 byte[]
     * @return byte[]
     */
    public static byte[] concat(byte[] b1, byte[] b2) {
        byte[] b3 = new byte[b1.length + b2.length];
        System.arraycopy(b1, 0, b3, 0, b1.length);
        System.arraycopy(b2, 0, b3, b1.length, b2.length);
        return b3;
    }

    /**
     * Concatenate the byte array and the byte
     * @param b1 byte[]
     * @param b2 byte
     * @return byte[]
     */
    public static byte[] concat(byte[] b1, byte b2) {
        byte[] b3 = new byte[b1.length + 1];
        byte[] temp = new byte[] {b2};
        System.arraycopy(b1, 0, b3, 0, b1.length);
        System.arraycopy(temp, 0, b3, b1.length, 1);
        return b3;
    }
}
