import com.thoughtworks.xstream.*;
import java.io.*;
import java.net.*;

class Log implements Serializable{
    public String applicazione;
    public String timestamp;
    public String ipClient;
    public String evento;
    
    public Log(String applicazione, String timestamp, String ipClient, String evento) {
        this.applicazione = applicazione; this.timestamp = timestamp; this.ipClient = ipClient; this. evento = evento;
    }
    
    public static void SerializzaEInviaLog(Log l) { //00
        XStream xs = new XStream();
        String x = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + System.lineSeparator() + xs.toXML(l);
        try ( Socket s = new Socket("localhost", 8080); //01
              ObjectOutputStream oout = new ObjectOutputStream (s.getOutputStream());
        ){ oout.writeObject(x);
        } catch (Exception e) {e.printStackTrace();} 
    }
}

/*
Note:

(00)
    Viene serializzato in XML un oggetto Log contenente i dati da inviare al
    server di log

(01)
    I dati vengono inviati attraverso questo socket
*/