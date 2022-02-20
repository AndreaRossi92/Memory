import java.io.*;

public class StatoDiGioco implements Serializable{
    public String nome;
    public Punteggio punteggio;
    public Griglia griglia;
    public String suggerimento;
    public int minuti;
    public int secondi;
    
    public StatoDiGioco(String nome, Punteggio punteggio, Griglia griglia, String suggerimento, int minuti, int secondi) { 
        this.nome = nome; this.punteggio = punteggio; this.griglia = griglia;
        this.suggerimento = suggerimento; this.minuti = minuti; this.secondi = secondi;
    }

    public static void salvaBin(StatoDiGioco s) {   //00
        try ( ObjectOutputStream ooutf = 
           new ObjectOutputStream( new FileOutputStream("./myfiles/cache.bin") );
        ) { ooutf.writeObject(s);} catch (Exception e) {}
    }
    
    public static StatoDiGioco caricaBin() {    //01
        StatoDiGioco s = null;
        try ( ObjectInputStream oinf = 
           new ObjectInputStream( new FileInputStream("./myfiles/cache.bin") );
        ) {s = (StatoDiGioco)oinf.readObject();} catch (Exception e) {e.printStackTrace();}
        return s;
    }
}

/*
Note:

(00)
    Metodo che serializza su file binario un oggetto StatoDiGioco contenente
    i dati necessari al ripristino dello stato di gioco al momento
    della ciusura dell'applicazione

(01)
    Deserializza da file binario a oggetto StatoDiGioco contenenti i dati di
    stato di gioco da ripristinare all'avvio dell'applicazione
*/