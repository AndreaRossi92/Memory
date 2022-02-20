import java.io.*;
import java.util.*;

public class Griglia implements Serializable {
    public List<Casella> tiles = new ArrayList<>();
    public int scoperta = -1;
    public int coppieIndovinate = 0;
    
    public Griglia(int numCoppie){  //00
        char startingChar = 'A';
        for (int i = 0 ; i < numCoppie ; i++) {
            tiles.add(new Casella((String.valueOf((char)((int)(startingChar) + i))), Casella.Stato.scoperta));
            tiles.add(new Casella((String.valueOf((char)((int)(startingChar) + i))), Casella.Stato.scoperta));
        }
        Collections.shuffle(tiles);
    }
    
    public void copriNonEvidenziate() { //01
        for (Casella tile : tiles) {
            if(tile.stato != Casella.Stato.evidenziata)
                tile.stato = Casella.Stato.coperta;
        }
    }
}

/*
Note:

(00)
    Viene inizializzata una lista di oggetti Casella contenente
    numCoppie coppie di lettere a partire dalla lettera A. Le
    caselle vengono mischiate e risultano essere tutte scoperte

(01)
    Lo stato di ogni casella non evidenziata viene settato a: coperta
*/