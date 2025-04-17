//Cópia do arquivo criado para o projeto do ERENO baseado nos modelos atuais
/*
package br.ufu.facom.ereno.dataExtractors;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;
import br.ufu.facom.ereno.messages.Sv;

import java.io.*;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.logging.Logger;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PcapWriter {
    private static FileOutputStream fout;
    private static final int SNAPLEN = 65535;
    public static String[] label = {
            "normal", "random_replay", "inverse_replay", "masquerade_fake_fault",
            "masquerade_fake_normal", "injection", "high_StNum",
            "poisoned_high_rate", "grayhole"
    };

    public static void startWriting(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            System.out.println("Directory created at: " + filename);
        }
        fout = new FileOutputStream(file);
        writeGlobalHeader();
    }

    private static void writeGlobalHeader() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(24).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(0xa1b2c3d4);       // Magic Number
        buffer.putShort((short) 2);      // Major Version
        buffer.putShort((short) 4);      // Minor Version
        buffer.putInt(0);                // Timezone
        buffer.putInt(0);
        buffer.putInt(SNAPLEN);          // Max packet length
        buffer.putInt(1);                // linkType (1 = Ethernet)
        fout.write(buffer.array());
    }

    public static void writeGoosePacket(Goose goose) throws IOException {
        byte[] ethernetPayload = GooseToBinaryConverter.convert(goose);

        long timestampMillis = System.currentTimeMillis();
        int tsSec = (int) (timestampMillis / 1000);
        int tsUsec = (int) ((timestampMillis % 1000) * 1000);

        ByteBuffer header = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
        header.putInt(tsSec);
        header.putInt(tsUsec);
        header.putInt(ethernetPayload.length); // Captured length
        header.putInt(ethernetPayload.length); // Original length

        fout.write(header.array());
        fout.write(ethernetPayload);
    }

    public static void finishWriting() throws IOException {
        if (fout != null) {
            fout.close();
        }
    }

    // PROCESSA TODAS AS MENSAGENS E ESCREVE EM FORMATO PCAP
    public static void processDataset(PriorityQueue<EthernetFrame> stationBusMessages, ArrayList<EthernetFrame> processBusMessages, String filename) throws IOException {
        startWriting(filename);

        Goose previousGoose = null;
        Logger.getLogger("PcapWriter").info(stationBusMessages.size() + " mensagens na fila.");

        while (!stationBusMessages.isEmpty()) {
            Goose goose = (Goose) stationBusMessages.poll();

            if (previousGoose != null) {
                // Rascunho para SV
                // Sv sv = ProtocolCorrelation.getCorrespondingSVFrame(processBusMessages, goose);
                // double delay = goose.getTimestamp() - sv.getTime(); (opcional)

                writeGoosePacket(goose);
            }

            previousGoose = goose.copy();
        }

        finishWriting();
        Logger.getLogger("PcapWriter").info("Arquivo PCAP finalizado com sucesso.");
    }
}

*/



/*Primeiro rascunho: ideias iniciais
import br.ufu.facom.ereno.messages.Goose;
import br.ufu.facom.ereno.messages.EthernetFrame;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.PriorityQueue;

public class PcapWriter {

    private static FileOutputStream fout;

    public static void startWriting(String filePath) throws IOException {
        fout = new FileOutputStream(filePath);
        writeGlobalHeader();
    }

    public static void processDataset(PriorityQueue<EthernetFrame> gooseMessages) throws IOException {
        while (!gooseMessages.isEmpty()) {
            Goose goose = (Goose) gooseMessages.poll();
            byte[] goosePacket = GooseToBinaryConverter.convert(goose);

            // Timestamp
            long timestampSeconds = (long) goose.getTimestamp();
            long timestampMicros = (long) ((goose.getTimestamp() - timestampSeconds) * 1_000_000);

            // Packet Header
            ByteBuffer header = ByteBuffer.allocate(16);
            header.order(ByteOrder.LITTLE_ENDIAN);
            header.putInt((int) timestampSeconds);
            header.putInt((int) timestampMicros);
            header.putInt(goosePacket.length);
            header.putInt(goosePacket.length);

            fout.write(header.array());
            fout.write(goosePacket);
        }
    }

    public static void finishWriting() throws IOException {
        if (fout != null) {
            fout.close();
        }
    }

    private static void writeGlobalHeader() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(24);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(0xa1b2c3d4);   // Magic Number
        buffer.putShort((short) 2);  // Version Major
        buffer.putShort((short) 4);  // Version Minor
        buffer.putInt(0);            // Reserved
        buffer.putInt(0);            // Reserved
        buffer.putInt(65535);        // SnapLen
        buffer.putInt(1);            // LinkType (Ethernet)
        fout.write(buffer.array());
    }
}

*/
