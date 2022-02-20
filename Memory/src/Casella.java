import java.io.*;

public class Casella implements Serializable {  //00
    
    public enum Stato {coperta, scoperta, evidenziata}; //01
    
    public final String lettera;
    public Stato stato;
    
    public Casella(String lettera, Stato stato) {
        this.lettera = lettera;
        this.stato = stato;
    }
}

/*
Note:

(00)
    Ogni casella ha una lettera e uno stato

(01)
    Gli stati che può avere una casella sono: coperta, scoperta, evidenziata
*/