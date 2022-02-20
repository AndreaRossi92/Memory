import com.thoughtworks.xstream.*;
import java.io.*;
import java.nio.file.*;

class Gioco implements Serializable {
    public int numCoppie;
    public int tempoCopertura;
    public int puntiStandard;
    public int puntiBonusSerie;
    public int puntiPenalitaSuggerimento;
    public int puntiBonusTempo;
}

class Stile implements Serializable {
    public int NRigheClassifica;
    public String fontTitolo;
    public String fontCaselle;
    public String fontClassificaENome;
    public String fontAltro;
    public int dimensioneTitolo;
    public int dimensioneCaselle;
    public int dimensioneClassificaENome;
    public int dimensioneAltro;
    public String coloreCaselle;
    public String coloreSfondo;
}

class Tecnologici implements Serializable {
    public String IpClient;
    public String IpServerLog;
    public int portaServerLog;
    public String IpDBMS;
    public int portaDBMS;
    public String passwordDBMS;
}

class ParametriIniziali implements Serializable{
    public Gioco gioco;
    public Stile stile;
    public Tecnologici tecnologici;
    
    public static ParametriIniziali deserializzaXML() { //00
        XStream xs = new XStream();
        String s = "";
        ValidazioneXML.valida("./myfiles/configurazione.xml", "./myfiles/configurazione.xsd", true);    //01
        try {s = new String(Files.readAllBytes(Paths.get("./myfiles/configurazione.xml")));
    } catch (Exception e) {}        
        ParametriIniziali p = (ParametriIniziali)xs.fromXML(s);
        return p;
    }
}

/*
Note:

(00)
    Deserializza la stringa contenente l'XML del file di configurazione
    e ritorna un oggetto ParametriIniziali contenente i parametri per
    l'inizializzazione del gioco

(01)
    Prima della deserializzazione viene validato il file XML sul relativo XSD
*/