import java.io.*;

public class Punteggio implements Serializable{
    public int punteggio;
    public int serie;
    public int chiestoAiuto;
    public int puntiStandard;
    public int puntiBonusSerie;
    public int puntiPenalitaSuggerimento;
    public int puntiBonusTempo;
    
    public Punteggio(int puntiStandard, int puntiBonusSerie, int puntiPenalitaSuggerimento) {
        this.puntiStandard = puntiStandard; this.puntiBonusSerie = puntiBonusSerie;
        this.puntiPenalitaSuggerimento = puntiPenalitaSuggerimento;
        resetPunteggio();
    }
    
    public void resetPunteggio() {  //00
        punteggio = serie = chiestoAiuto = 0;
    }
    
    public void aggiornaPunteggio() {   //01
        punteggio += puntiStandard + serie*puntiBonusSerie;
        if(chiestoAiuto > 0) {
            punteggio -= puntiPenalitaSuggerimento;
            chiestoAiuto --;
        }
    }
}

/*
Note:

(00)
    Setta i valori di punteggio ai valori iniziali

(01)
    Aggiorna il punteggio dopo aver indovinato una coppia
*/