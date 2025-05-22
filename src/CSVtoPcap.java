import java.io.BufferedReader;
import java.io.FileReader;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.math.BigDecimal;

public class CSVtoPcap {

    public static void main(String[] args) throws IOException {

        List<GooseData> pacotes = converteDatasetEmPacotesGOOSE( "ERENO-2.0.csv"); //ERENO-2.0.csv //teste.csv
        pacotesGooseToArqPcap(pacotes, "dataset.pcap");

        //String outputBin = "gooseFromGerandoCsv.bin";
    }

    public static List<GooseData> converteDatasetEmPacotesGOOSE(String filename) {

        List<GooseData> listaPacotesGoose = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String header = br.readLine(); // pula cabeçalho
            String linha;
            int linhaNumero = 1;

            while ((linha = br.readLine()) != null) {
                linhaNumero++;
                String[] campos = linha.split(",");

                if (campos.length < 52) {
                    System.err.println("Linha " + linhaNumero + " com dados insuficientes: " + campos.length);
                    continue;
                }

                try {
                    GooseData pacote = new GooseData();

                    pacote.dstMac = macToBytes(campos[25].trim());
                    pacote.srcMac = macToBytes(campos[26].trim());
                    pacote.ethType = setGOOSEHeaderFields(2, 0x88B8);
                    pacote.APPID = setGOOSEHeaderFields(2, Integer.decode(campos[29].trim()));
                    pacote.gpduLength = setGOOSEHeaderFields(2, Integer.parseInt(campos[30].trim()));
                    pacote.reserved1 = setGOOSEHeaderFields(2, 0);
                    pacote.reserved2 = setGOOSEHeaderFields(2, 0);
                    pacote.gooseTag = setGOOSEHeaderFields(1, 0x61);
                    pacote.length = setGOOSEHeaderFields(1, 0x81);
                    pacote.pduLength = setGOOSEHeaderFields(1, 0xb6);

                    // Goose PDU
                    pacote.vargocbRef = campos[32].trim();
                    pacote.gocbRef = pacote.vargocbRef.getBytes();
                    pacote.timeAllowedToLive =  setGOOSEHeaderFields(3, Integer.parseInt(campos[28].trim()));
                    pacote.vardatSet = campos[25].trim();
                    pacote.datSet = pacote.vardatSet.getBytes();
                    pacote.vargoID = campos[34].trim();

                    //Consertando, ate aqui as impressoes estao corretas, o problema esta na conversao do bigdecimal para byte, truncamento
                    long nanos = new BigDecimal(campos[20].trim()).setScale(9, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(1_000_000_000L)).longValue();

                    pacote.timestamp = setGOOSEHeaderFields(8, nanos);
                    pacote.stNum = setGOOSEHeaderFields(1, Integer.parseInt(campos[22].trim()));
                    pacote.sqNum = setGOOSEHeaderFields(1, Integer.parseInt(campos[21].trim()));
                    pacote.simulation = setGOOSEHeaderFields(1, campos[35].trim().equalsIgnoreCase("TRUE") ? 1 : 0);
                    pacote.confRev = setGOOSEHeaderFields(1, Integer.parseInt(campos[36].trim()));
                    pacote.ndsCom = setGOOSEHeaderFields(1, campos[37].trim().equalsIgnoreCase("TRUE") ? 1 : 0);
                    pacote.nDSEntries = setGOOSEHeaderFields(1, Integer.parseInt(campos[38].trim()));

                    listaPacotesGoose.add(pacote);
                    //System.out.println("Linha " + linhaNumero + " processada com sucesso.");

                } catch (Exception e) {
                    System.err.println("Erro ao processar a linha " + linhaNumero + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("\nTotal de pacotes GOOSE criados: " + listaPacotesGoose.size());

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
        return listaPacotesGoose;
    }

    public static void pacotesGooseToArqPcap(List<GooseData> listaPacotesGoose, String saidaFileName){

        // Global header
        final int MAGIC_NUMBER = 0xA1B2C3D4;  // Número mágico para o cabeçalho global
        final short VERSION_MAJOR = 0x0002;   // Versão maior personalizada
        final short VERSION_MINOR = 0x0004;   // Versão menor padrão
        final int RESERVED1 = 0;
        final int RESERVED2 = 0;
        final int SNAPLEN = 65535;            // Tamanho máximo de captura
        final int LINKTYPE = 1;               // Tipo de link (Ethernet por padrão)

        try (FileOutputStream fos = new FileOutputStream(saidaFileName)) {

            // Cabeçalho global do PCAP
            ByteBuffer globalHeader = ByteBuffer.allocate(24);
            globalHeader.order(ByteOrder.LITTLE_ENDIAN);
            globalHeader.putInt(MAGIC_NUMBER);
            globalHeader.putShort(VERSION_MAJOR);
            globalHeader.putShort(VERSION_MINOR);
            globalHeader.putInt(RESERVED1);
            globalHeader.putInt(RESERVED2);
            globalHeader.putInt(SNAPLEN);
            globalHeader.putInt(LINKTYPE);

            fos.write(globalHeader.array());

            // Para cada pacote GOOSE
            for (GooseData pacote : listaPacotesGoose) {

                byte[] packetData = pacote.toBytes();

                // checar aqui  a parte que esta truncando
                long timestampSeconds = System.currentTimeMillis() / 1000;
                long timestampMicroseconds = (System.currentTimeMillis() % 1000) * 1000;

                // Cabeçalho do pcap (16 bytes)
                ByteBuffer packetHeader = ByteBuffer.allocate(16);
                packetHeader.order(ByteOrder.LITTLE_ENDIAN);
                packetHeader.putInt((int) timestampSeconds);           // Timestamp em segundos
                packetHeader.putInt((int) timestampMicroseconds);
                packetHeader.putInt(packetData.length); // Captured length
                packetHeader.putInt(packetData.length); // Original length

                fos.write(packetHeader.array());
                fos.write(packetData);
            }

            System.out.println("Arquivo PCAP criado com sucesso!");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao criar o arquivo PCAP.");
        }
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

    //Funcao esta desatualizada, teria que modificar a data e anexar junto com o do pcap
    public static byte[] timestampToLong() {
        final Calendar cal = Calendar.getInstance();
        cal.set(2019, Calendar.AUGUST, 12, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        final Date date = cal.getTime();
        final long valorData = date.getTime();
        return setGOOSEHeaderFields(8, valorData);
    }
}
