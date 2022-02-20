import java.sql.*;
import javafx.beans.property.*;
import javafx.collections.*;

public class Archivio {
    public ObservableList<Classifica> list;
    public String ipDBMS;
    public int portaDBMS;
    public String passwordDBMS;
    
    public Archivio (String ipDBMS, int portaDBMS, String passwordDBMS){
        this.ipDBMS = ipDBMS;
        this.portaDBMS = portaDBMS;
        this.passwordDBMS = passwordDBMS;
    }
    
    public void caricaClassificaDB(int NRighe) {    //00
        list = FXCollections.observableArrayList();
        try (Connection co = DriverManager.getConnection("jdbc:mysql://" + ipDBMS +":" + portaDBMS +"/classifica", "root", passwordDBMS);
            PreparedStatement ps = co.prepareStatement("SELECT * FROM classifica ORDER BY score DESC LIMIT ?");
        ) {
            ps.setInt(1, NRighe);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(new Classifica(rs.getString("username"), rs.getInt("score")));
        } catch (SQLException e) {System.err.println(e.getMessage());}
    }
    
    public void registraPunteggio(String utente, int punti) {   //01
        list = FXCollections.observableArrayList();
        try ( Connection co = DriverManager.getConnection("jdbc:mysql://" + ipDBMS +":" + portaDBMS +"/classifica", "root", passwordDBMS);
            PreparedStatement ps = co.prepareStatement("INSERT INTO classifica (username, score) VALUES (?, ?) ON DUPLICATE KEY UPDATE score = GREATEST(score, VALUES(score))");
        ) {
            ps.setString(1, utente); ps.setInt(2, punti);
            System.out.println("rows affected: " + ps.executeUpdate());
     } catch (SQLException e) {System.err.println(e.getMessage());}
 } 
    
    public static class Classifica {    //02

        private final SimpleStringProperty username;
        private final SimpleIntegerProperty score;

        private Classifica(String username, int score) {
            this.username = new SimpleStringProperty(username);
            this.score = new SimpleIntegerProperty(score);
        }

        public String getUsername() { return username.get(); }
        public int getScore() {return score.get();}
    }
}

/*
Note:

(00)
    Seleziona al più i primi NRighe elementi del database dopo averli ordinati
    per score decrescente e li aggiunge alla observable list

(01)
    Inserisce l'username inserito nel TextField dell'interfaccia e il relativo
    score ottenuto nel database. Se l'username è già presente si
    modifica il solo valore di score e solo se si è ottenuto un punteggio
    maggiore del precedente

(02)
    Classe interna statica che fornisce i valori della classifica
*/