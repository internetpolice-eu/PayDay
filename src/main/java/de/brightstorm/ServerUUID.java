package de.brightstorm;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.net.UnknownHostException;
import java.net.SocketException;
import java.security.MessageDigest;
import java.net.NetworkInterface;
import java.net.InetAddress;

public class ServerUUID
{
    private String id;
    private String seed;
    
    public ServerUUID() {
        this.generate();
    }
    
    public String getID() {
        return this.id;
    }
    
    public void generate() {
        try {
            final NetworkInterface network = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            final byte[] macRaw = network.getHardwareAddress();
            StringBuffer sb = new StringBuffer();
            byte[] array;
            for (int length = (array = macRaw).length, i = 0; i < length; ++i) {
                final byte b = array[i];
                sb.append(Integer.toHexString(b & 0xFF));
            }
            final String mac = sb.toString();
            sb = new StringBuffer();
            final String location = ServerUUID.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            this.seed = String.valueOf(mac) + location;
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(this.seed.getBytes("UTF-16"));
            final byte[] digest = md.digest();
            byte[] array2;
            for (int length2 = (array2 = digest).length, j = 0; j < length2; ++j) {
                final byte b2 = array2[j];
                sb.append(Integer.toHexString(b2 & 0xFF));
            }
            this.id = sb.toString();
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
        catch (UnknownHostException e2) {
            e2.printStackTrace();
        }
        catch (NoSuchAlgorithmException e3) {
            e3.printStackTrace();
        }
        catch (UnsupportedEncodingException e4) {
            e4.printStackTrace();
        }
    }
}
