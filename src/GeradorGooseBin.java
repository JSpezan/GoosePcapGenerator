import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class GeradorGooseBin {
    public static void main(String[] args) {
        //Campos do GOOSE_header
        int HEADER_GOOSE_SIZE = 40;    //atualizar: 40 bytes sem o payload do pacote

        byte[] destinationMac = setGOOSEHeaderFields(6, 691288944503L);  //padrao
        byte[] sourceMac = setGOOSEHeaderFields(6, 1790800572279L);      //endereço inventado
        //byte[] VLANtag = setGOOSEHeaderFields(4,);                                     //esse campo é opcional, entao nao vou implementar por agora
        byte[] Ethertype = setGOOSEHeaderFields(2, 35000);

        //dados do GPDU
        byte[] APPID = setGOOSEHeaderFields(2, 1);                                                     //Todos os valores "0" abaixo devem ser atualizados, como fazer isso?
        byte[] gpduLength = setGOOSEHeaderFields(2, 145);
        byte[] reserved1 = setGOOSEHeaderFields(2, 0);
        byte[] reserved2 = setGOOSEHeaderFields(2, 0);
        byte[] gooseTag = setGOOSEHeaderFields(1, 97);
        byte[] pduLength = setGOOSEHeaderFields(1, 129);

        //dados do gooseAPDU
        String vargocbRef = "GEDeviceF650/LLN0$GO$gcb01";                                                               //O padrao deveria ser de 6 bytes, mas como e uma string acaba utilizando mais espaco
        byte[] gocbRef = vargocbRef.getBytes();
        byte[] timeAllowedToLive = setGOOSEHeaderFields(3, 40000);//1000);                                      //um padrao comum e usar 1000 milissegundos = 1 segundo
        String datSet = "GEDeviceF650/LLN0$GOOSE1";  //none
        String goID = "F650_GOOSE1";                                                                                    //padrao recomenda 8 bytes
        byte[] timestamp = timestampToLong();
        byte[] stNum = setGOOSEHeaderFields(1, 1);                                                    //padrao recomenda entre 1, 2 ou 4 bytes. Inicia-se o frame no 0
        byte[] sqNum = setGOOSEHeaderFields(1, 1);                                                     //padrao recomenda 2 bytes
        byte[] simulation = setGOOSEHeaderFields(1, 0);                                                //indicar se a mensagem GOOSE está simulando um evento ou se e um evento real
        byte[] confRev = setGOOSEHeaderFields(1, 1);                                                  //valor incrementado que avisa caso ocorram mudancas no frame
        byte[] ndsCom = setGOOSEHeaderFields(1, 0);
        byte[] nDSEntries = setGOOSEHeaderFields(1, 8);                                                 //ajustar o valor para  qtd de dados do pacote


        //Para automatizar o codigo posteriormente, observe que para cada valor dentro do allData, precisaremos implementar os contadores e o tamanho, e definir os bytes

        //Criar o arquivo binario que vai receber os dados e definir a extensao .pcap
        String fileName = "gooseBin.bin";
        String filePath = System.getProperty("user.dir") + File.separator + fileName;

        //String filePath = "C:\\Users\\Julia\\Desktop\\GOOSEpcap\\outputTeste.bin";                                      //diretorio atual (modificar futuramente para receber inputs)
        try (FileOutputStream fout = new FileOutputStream(filePath)) {


            int contador = 128;

            //Escrita dos dados em array de bytes
            fout.write(sourceMac);
            fout.write(destinationMac);
            fout.write(Ethertype);

            //GPDU
            fout.write(APPID);
            fout.write(gpduLength);
            fout.write(reserved1);
            fout.write(reserved2);
            fout.write(gooseTag);  //goose pdu tag(61) e length(variavel) goose pdu data
            fout.write(pduLength);
            fout.write(setGOOSEHeaderFields(1, 134));

            //GPDU: => APDU (Application Protocol Data Unit)
            //gocbref
            fout.write(setGOOSEHeaderFields(1, 128));                                                  //tentativa de colocar o tamanho de contador temporario
            fout.write(setGOOSEHeaderFields(1, gocbRef.length));
            fout.write(gocbRef);

            //timeAllowedToLive
            fout.write(setGOOSEHeaderFields(1, 129));
            fout.write(setGOOSEHeaderFields(1, 3));
            fout.write(timeAllowedToLive);

            //datSet
            fout.write(setGOOSEHeaderFields(1, 130));
            fout.write(setGOOSEHeaderFields(1, datSet.length()));
            fout.write(datSet.getBytes());

            //goID
            fout.write(setGOOSEHeaderFields(1, 131));
            fout.write(setGOOSEHeaderFields(1, goID.length()));
            fout.write(goID.getBytes());

            //timestamp
            fout.write(setGOOSEHeaderFields(1, 132));
            fout.write(setGOOSEHeaderFields(1, timestamp.length));
            fout.write(timestamp);

            //stNum
            fout.write(setGOOSEHeaderFields(1, 133));
            fout.write(setGOOSEHeaderFields(1, stNum.length));
            fout.write(stNum);                                                                                          //vai ser alterado com a implementacao do frame

            //sqNum
            fout.write(setGOOSEHeaderFields(1, 134));
            fout.write(setGOOSEHeaderFields(1, sqNum.length));
            fout.write(sqNum);

            //simulation
            fout.write(setGOOSEHeaderFields(1, 135));
            fout.write(setGOOSEHeaderFields(1, simulation.length));
            fout.write(simulation);

            //confRev
            fout.write(setGOOSEHeaderFields(1, 136));
            fout.write(setGOOSEHeaderFields(1, confRev.length));
            fout.write(confRev);

            //ndsCom
            fout.write(setGOOSEHeaderFields(1, 137));
            fout.write(setGOOSEHeaderFields(1, ndsCom.length));
            fout.write(ndsCom);

            //nDSEntries
            fout.write(setGOOSEHeaderFields(1, 138));
            fout.write(setGOOSEHeaderFields(1, nDSEntries.length));
            fout.write(nDSEntries);

            //Data
            fout.write(setGOOSEHeaderFields(1, 171));
            fout.write(setGOOSEHeaderFields(1, 32));

            //GOOSE Data (depende do ndsEntries)
            int cont;
            for (cont = 0; cont < 4; ++cont) {
                fout.write(setGOOSEHeaderFields(1, 131));
                fout.write(setGOOSEHeaderFields(1, 1));
                fout.write(setGOOSEHeaderFields(1, 0));

                fout.write(setGOOSEHeaderFields(1, 132));
                fout.write(setGOOSEHeaderFields(1, 3));
                fout.write(setGOOSEHeaderFields(1, 3));
                fout.write(setGOOSEHeaderFields(2, 0));
            }

            fout.close();

            System.out.println("Arquivo de frame GOOSE criado com sucesso!\n");

        } catch (IOException e) {
            System.err.println("Erro: " + e.getMessage());
        }

        //Para testar o conteudo gerado em binario atraves da visualizacao do frame em hexadecimal
        try (FileInputStream fis = new FileInputStream("gooseBin.bin")) {
            int byteLido;
            System.out.println("Conteúdo do arquivo em formato hexadecimal:");

            int contador = 0;
            while ((byteLido = fis.read()) != -1) {
                System.out.printf("%02x ", byteLido);
                contador += 1;
                if (contador == 16) {
                    System.out.println("\n");
                    contador = 0;
                }
            }

            System.out.println();

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }

    }


    //Funcao de alocacao do tamanho do array de bytes e para converter os valores dados em bytes
    public static byte[] setGOOSEHeaderFields(int len, int valorFormatOrg) {
        byte[] array = new byte[len];
        int setValue = valorFormatOrg;

        for (int i = array.length - 1; i >= 0; i--) {
            array[i] = (byte) (setValue & 0xFF);
            setValue >>= 8;
        }
        return array;
    }

    public static byte[] setGOOSEHeaderFields(int len, long valorFormatOrg) {
        byte[] array = new byte[len];
        long setValue = valorFormatOrg;

        for (int i = array.length - 1; i >= 0; i--) {
            array[i] = (byte) (setValue & 0xFF);
            setValue >>= 8;
        }
        return array;
    }

    //Funcao de configuracao da data do timestamp (baseada no padrao do Silvio)
    public static byte[] timestampToLong() {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 12);
        cal.set(Calendar.MONTH, Calendar.AUGUST);
        cal.set(Calendar.YEAR, 2019);
        cal.set(Calendar.HOUR, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        cal.set(Calendar.MILLISECOND, 00);
        cal.setTimeZone(TimeZone.getDefault());
        final Date date = cal.getTime();
        final long valorData = date.getTime();
        //O timestamp tem tamanho de 8 bytes
        return setGOOSEHeaderFields(8, valorData);
    }


    public static byte[] timestampToLong(int milliseconds) {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 18);
        cal.set(Calendar.MONTH, Calendar.OCTOBER);
        cal.set(Calendar.YEAR, 2019);
        cal.set(Calendar.HOUR, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        cal.set(Calendar.MILLISECOND, milliseconds);
        cal.setTimeZone(TimeZone.getDefault());
        final Date date = cal.getTime();
        final long valorData = date.getTime();
        //O timestamp tem tamanho de 8 bytes
        return setGOOSEHeaderFields(8, valorData);


    }
}
