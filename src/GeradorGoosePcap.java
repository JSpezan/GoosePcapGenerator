import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GeradorGoosePcap {
    // Global header
    private static final int MAGIC_NUMBER = 0xA1B2C3D4;  // Número mágico para o cabeçalho global
    private static final short VERSION_MAJOR = 0x0002;            // Versão maior personalizada
    private static final short VERSION_MINOR = 0x0004;          // Versão menor padrão
    private static final int RESERVED1 = 0;
    private static final int RESERVED2 = 0;
    private static final int SNAPLEN = 65535;            // Tamanho máximo de captura
    private static final int LINKTYPE = 1;               // Tipo de link (Ethernet por padrão)

    public static void main(String[] args) {


        String binaryFilePath = "gooseBin.bin";
        String filename = "bin_to_goose.pcap";

        try (FileInputStream fin = new FileInputStream(binaryFilePath);
             FileOutputStream fout = new FileOutputStream(filename)) {

            // Criação do global header
            ByteBuffer buffer = ByteBuffer.allocate(24);
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            // Valores do global header
            buffer.putInt(MAGIC_NUMBER);        // Número mágico
            buffer.putShort(VERSION_MAJOR);     // Versão maior
            buffer.putShort(VERSION_MINOR);     // Versão menor
            buffer.putInt(RESERVED1);
            buffer.putInt(RESERVED2);
            buffer.putInt(SNAPLEN);             // Tamanho máximo de captura
            buffer.putInt(LINKTYPE);            // Tipo de link (Ethernet)

            fout.write(buffer.array());

            // Lê os dados do arquivo binário em blocos e cria pacotes para cada bloco
            byte[] bufferPacote = new byte[SNAPLEN];                                    //possivel que o tamanho do bloco esteja muito grande,
            // tem que mexer no snaplen especificamente
            int bytesLidos;

            while ((bytesLidos = fin.read(bufferPacote)) != -1) {
                // Se tiver dados, cria pacote

                // Dados do pacote
                byte[] packetData = new byte[bytesLidos];
                System.arraycopy(bufferPacote, 0, packetData, 0, bytesLidos);

                // Timestamp para cada pacote
                long timestampSeconds = System.currentTimeMillis() / 1000;
                long timestampMicroseconds = (System.currentTimeMillis() % 1000) * 1000;

                // Cabeçalho do pacote (16 bytes)
                ByteBuffer packetHeader = ByteBuffer.allocate(16);
                packetHeader.order(ByteOrder.LITTLE_ENDIAN);
                packetHeader.putInt((int) timestampSeconds);           // Timestamp em segundos
                packetHeader.putInt((int) timestampMicroseconds);     // Timestamp em microssegundos
                packetHeader.putInt(packetData.length);               // Tamanho total do pacote
                packetHeader.putInt(packetData.length);               // Tamanho capturado

                // Escreve o cabeçalho e os dados do pacote: aqui testamos se funcionava com um numero variavel de pacotes
                fout.write(packetHeader.array());
                fout.write(packetData);
                //fout.write(buffer.array());
                fout.write(packetHeader.array());
                fout.write(packetData);

            }

            System.out.println("Arquivo PCAP (gerado a partir do arq. binário) criado com sucesso: " + filename);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao criar o arquivo PCAP.");
        }
    }
}
