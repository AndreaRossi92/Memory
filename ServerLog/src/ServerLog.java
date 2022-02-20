import com.thoughtworks.xstream.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

public class ServerLog {

    public static void main(String[] args) {
        try ( ServerSocket servs = new ServerSocket(8080, 7) ){ //00
            while(true) { 
                Socket s = servs.accept();
                Thread t = new Thread() {
                    public void run() {
                        try ( ObjectInputStream oin = new ObjectInputStream(s.getInputStream())) {  //01
                            String x = (String) oin.readObject();
                            ValidazioneXML.valida(x, "./myfiles/log.xsd", false);   //02
                            x += System.lineSeparator() + System.lineSeparator();
                            try { Files.write(Paths.get("./myfiles/log.xml"), x.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);  //03
                            } catch (Exception e) {}
                            XStream xs = new XStream();
                            Log l = (Log)xs.fromXML(x); //04
                            System.out.print(l.applicazione + ", " + l.timestamp + ", " + l.ipClient + ", " + l.evento);
                            System.out.println();
                        } catch (Exception e) {e.printStackTrace();}
                    } 
                };
            t.start();
            }
        } catch (IOException e) {e.printStackTrace();}
    }
} 

/*
Note:

(00)
    Il server multi-thread è in ascolto sulla porta 8080

(01)
    Viene letta la stringa ricevuta (in formato XML)
(02)
    La stringa in XML viene validata sul relativo XSD

(03)
    Si scive la stringa sul file di log: se il file non esiste viene creato
    altrimenti la stringa viene inserita in modalità append

(04)
    Si deserializza la stringa XML in un oggetto Log per poterne stampare
    i vari campi
*/