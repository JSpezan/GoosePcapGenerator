//Modelo do projeto do Ereno: adicionando um modelo inicial baseado nos arquivos que eu criei

/*
package br.ufu.facom.ereno.dataExtractors;

import br.ufu.facom.ereno.featureEngineering.IntermessageCorrelation;
import br.ufu.facom.ereno.featureEngineering.ProtocolCorrelation;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Sv;
import br.ufu.facom.ereno.messages.Goose;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GooseToBinaryConverter {

    public static byte[] convert(Goose goose) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();


        try {
            // Ethernet Header
            baos.write(hexToBytes(goose.getEthDst())); // Destination MAC
            baos.write(hexToBytes(goose.getEthSrc())); // Source MAC
            baos.write(shortToBytes((short) 0x88b8));  // Ethertype

            // GOOSE APPID + Dados de teste
            baos.write(shortToBytes((short) 0x0001)); // APPID
            baos.write(shortToBytes((short) 120));    // gpduLength
            baos.write(shortToBytes((short) 0));      // reserved1
            baos.write(shortToBytes((short) 0));      // reserved2

            // Simulação de payload - preciso adicionar o restante, essa parte é de teste
            baos.write(new byte[] {0x61});            // GoosePDU Tag
            baos.write(new byte[] {0x10});            // Length (simplificada)
            baos.write(goose.goID.getBytes());   // Parte do payload (GoID)


        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    private static byte[] hexToBytes(String mac) {
        String[] parts = mac.split(":");
        byte[] bytes = new byte[parts.length];
        for (int i = 0; i < parts.length; i++) {
            bytes[i] = (byte) Integer.parseInt(parts[i], 16);
        }
        return bytes;
    }

    private static byte[] shortToBytes(short val) {
        return ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN).putShort(val).array();
    }
}
*/