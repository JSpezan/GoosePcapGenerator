import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GooseData {

    //dados do GOOSE HEADER
    byte[] dstMac;
    byte[] srcMac;
    byte[] ethType;
    byte[] APPID;
    byte[] gpduLength;
    byte[] reserved1;
    byte[] reserved2;
    byte[] gooseTag;       //gooseTag padrao
    byte[] length;
    byte[] pduLength;

    //dados do goosePDU
    byte[] gocbRef;
    String vargocbRef;
    byte[] timeAllowedToLive;
    String vardatSet;
    byte[] datSet;
    String vargoID;
    byte[] goID;
    byte[] timestamp;
    byte[] stNum;
    byte[] sqNum;
    byte[] simulation;
    byte[] confRev;
    byte[] ndsCom;
    byte[] nDSEntries;
    //public List<byte[]> allData;



    public byte[] toBytes() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            output.write(srcMac);
            output.write(dstMac);
            output.write(ethType);
            output.write(APPID);
            output.write(gpduLength);
            output.write(reserved1);
            output.write(reserved2);
            output.write(gooseTag);
            output.write(length);
            output.write(pduLength);

            //gocbref
            output.write(setGOOSEHeaderFields(1,128));
            //gocbRef = vargocbRef.getBytes();
            output.write(setGOOSEHeaderFields(1, vargocbRef.getBytes().length));
            output.write(gocbRef);

            //timeAllowedToLive
            output.write(setGOOSEHeaderFields(1,129));
            output.write(setGOOSEHeaderFields(1, 3));
            output.write(timeAllowedToLive);

            //datSet
            output.write(setGOOSEHeaderFields(1,130));
            datSet = vardatSet.getBytes();
            output.write(setGOOSEHeaderFields(1, datSet.length));
            output.write(datSet);

            //goID
            output.write(setGOOSEHeaderFields(1,131));
            goID = vargoID.getBytes();
            output.write(setGOOSEHeaderFields(1, goID.length));
            output.write(goID);

            //timestamp
            output.write(setGOOSEHeaderFields(1,132));
            output.write(setGOOSEHeaderFields(1, timestamp.length));
            output.write(timestamp);

            //stNum
            output.write(setGOOSEHeaderFields(1,133));
            output.write(setGOOSEHeaderFields(1, stNum.length));
            output.write(stNum);

            //sqNum
            output.write(setGOOSEHeaderFields(1,134));
            output.write(setGOOSEHeaderFields(1, sqNum.length));
            output.write(sqNum);

            //simulation
            output.write(setGOOSEHeaderFields(1,135));
            output.write(setGOOSEHeaderFields(1, simulation.length));
            output.write(simulation);

            //confRev
            output.write(setGOOSEHeaderFields(1,136));
            output.write(setGOOSEHeaderFields(1, confRev.length));
            output.write(confRev);

            //ndsCom
            output.write(setGOOSEHeaderFields(1,137));
            output.write(setGOOSEHeaderFields(1, ndsCom.length));
            output.write(ndsCom);

            //nDSEntries
            output.write(setGOOSEHeaderFields(1,138));
            output.write(setGOOSEHeaderFields(1, nDSEntries.length));
            output.write(nDSEntries);

            //Data
            output.write(setGOOSEHeaderFields(1, 0xab)); //allData
            output.write(setGOOSEHeaderFields(1,100));   //tamanho

            int cont;
            for(cont = 0; cont < 25; ++cont){
                output.write(setGOOSEHeaderFields(1, 131));
                output.write(setGOOSEHeaderFields(1, 2));
                output.write(setGOOSEHeaderFields(1,0));
                output.write(setGOOSEHeaderFields(1,0));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return output.toByteArray();
    }



    public static byte[] macToBytes(String mac) {
        String[] hex = mac.replace(" ", "").split(":");
        byte[] bytes = new byte[6];
        for (int i = 0; i < 6; i++) {
            bytes[i] = (byte) Integer.parseInt(hex[i], 16);
        }
        return bytes;
    }

    public static byte[] setGOOSEHeaderFields(int len, int value) {
        byte[] array = new byte[len];
        for (int i = array.length - 1; i >= 0; i--) {
            array[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return array;
    }

    public static byte[] setGOOSEHeaderFields(int len, long value) {
        byte[] array = new byte[len];
        for (int i = array.length - 1; i >= 0; i--) {
            array[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return array;
    }

    public static byte[] timestampToLong() {
        final Calendar cal = Calendar.getInstance();
        cal.set(2019, Calendar.AUGUST, 12, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        final Date date = cal.getTime();
        final long valorData = date.getTime();
        return setGOOSEHeaderFields(8, valorData);
    }
}


