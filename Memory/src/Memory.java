import java.io.*;
import java.text.*;
import java.util.*;
import javafx.animation.*;
import javafx.application.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.*;
import javafx.util.*;

public class Memory extends Application {

    //00
    private int minuti = 0;
    private int secondi = 0;
    private boolean finito = true;
    private Punteggio punteggio;
    
    private Archivio archivio;
    private Label titolo, score, classifica, testoSuggerimento, tempo, tempoLabel, scoreLabel, nome, fine;
    private TextField username;
    private Button casella[], inizia, suggerimento;
    private Rectangle rect;
    private Griglia grid;
    private Group root;
    private PauseTransition aspettaERicopri;
    private TableView<Archivio.Classifica> tabella;
    private Timeline timer;
    private ParametriIniziali parametri;
    private StatoDiGioco stato;
    private Log log;
    private EventHandler<ActionEvent> scopriEvent;
    
    public void start(Stage stage) {
        parametri = ParametriIniziali.deserializzaXML();    //01
        
        log = new Log("Memory", getTimestamp(), parametri.tecnologici.IpClient, "AVVIO");   //02    
        Log.SerializzaEInviaLog(log);
        
        punteggio = new Punteggio(parametri.gioco.puntiStandard, parametri.gioco.puntiBonusSerie,
            parametri.gioco.puntiPenalitaSuggerimento); //03
        
        //04
        rect = new Rectangle(20, 180, 370, 370); rect.setFill(Color.web(parametri.stile.coloreSfondo));
        casella = new Button[parametri.gioco.numCoppie*2];
        username = new TextField();
        username.setLayoutX(700); username.setLayoutY(95); username.setPrefWidth(150);
        inizializzaLabels(); posizionaLabels(); setLabelsStyle();
        inizializzaClassifica();
        inizializzaTimer(); 
        inizializzaPausa(parametri.gioco.tempoCopertura);
        inizializzaInizia();
        inizializzaSuggerimento();
        inizializzaScopriEvent();
        inizializzaOnCloseEvent(stage);
 
        root = new Group(titolo, rect, inizia, tabella, suggerimento, testoSuggerimento, tempo, tempoLabel, score, scoreLabel, classifica, username, nome, fine);
        
        inizializzaCache();
        
        Scene scene = new Scene(root, 870, 700);         
        stage.setTitle("Memory");          
        stage.setScene(scene);                          
        stage.show();
    }
    
    public void inizializzaGriglia() {  //05
        for(int i=0 ; i < parametri.gioco.numCoppie*2 ; i++) {
            casella[i] = new Button(grid.tiles.get(i).lettera);
            casella[i].setLayoutX((60*i)%360 + 25);
            casella[i].setLayoutY((i/6)*60 + 185);
            casella[i].setPrefSize(59, 59);
            casella[i].setStyle("-fx-background-color: #FFFFFF; -fx-border-width: 0; -fx-background-radius: 0; -fx-background-insets: 0; "
                    + "-fx-font-family:" + parametri.stile.fontCaselle + "; -fx-font-size:" + parametri.stile.dimensioneCaselle + "pt;");
            aggiornaCella(i);
            casella[i].setUserData(i);
            casella[i].setOnAction(scopriEvent);
            root.getChildren().add(casella[i]);
        }
    }
    
    public void inizializzaLabels() {   //06
        titolo = new Label("Memory");
        classifica = new Label("Classifica");
        score = new Label(Integer.toString(punteggio.punteggio));
        testoSuggerimento = new Label("");
        tempoLabel = new Label("Tempo:");
        scoreLabel = new Label("Punteggio:");
        nome = new Label("Nome:");
        fine = new Label("");
        tempo = new Label("00:00");
    }
    
    public void posizionaLabels() { //07
        testoSuggerimento.setLayoutX(20);
        testoSuggerimento.setLayoutY(95);
        titolo.setLayoutX(385);  
        tempoLabel.setLayoutX(290);
        tempoLabel.setLayoutY(145);
        tempo.setLayoutX(350);
        tempo.setLayoutY(145);
        scoreLabel.setLayoutX(20);
        scoreLabel.setLayoutY(145);
        score.setLayoutX(100);
        score.setLayoutY(145);
        fine.setLayoutX(600);
        fine.setLayoutY(570);
        classifica.setLayoutX(600);
        classifica.setLayoutY(145);
        nome.setLayoutX(600);
        nome.setLayoutY(95);
    }
    
    public void setLabelsStyle() {  //08
        testoSuggerimento.setStyle("-fx-font-family:" + parametri.stile.fontAltro + "; -fx-font-size:" + parametri.stile.dimensioneAltro + "pt;");
        titolo.setStyle("-fx-font-size:" + parametri.stile.dimensioneTitolo + "pt; -fx-font-weight: bold; -fx-font-family:" + parametri.stile.fontTitolo + ";");
        tempoLabel.setStyle("-fx-font-family:" + parametri.stile.fontAltro + "; -fx-font-size:" + parametri.stile.dimensioneAltro + "pt;");    
        tempo.setStyle("-fx-font-family:" + parametri.stile.fontAltro + "; -fx-font-size:" + parametri.stile.dimensioneAltro + "pt;"); 
        scoreLabel.setStyle("-fx-font-family:" + parametri.stile.fontAltro + "; -fx-font-size:" + parametri.stile.dimensioneAltro + "pt;");
        score.setStyle("-fx-font-family:" + parametri.stile.fontAltro + "; -fx-font-size:" + parametri.stile.dimensioneAltro + "pt;");        
        fine.setStyle("-fx-font-family:" + parametri.stile.fontAltro + "; -fx-font-size:" + parametri.stile.dimensioneAltro + "pt;");       
        classifica.setStyle("-fx-font-family:" + parametri.stile.fontClassificaENome + "; -fx-font-size:" + parametri.stile.dimensioneClassificaENome + "pt; -fx-font-weight: bold;");        
        nome.setStyle("-fx-font-family:" + parametri.stile.fontClassificaENome + "; -fx-font-size:" + parametri.stile.dimensioneClassificaENome + "pt; -fx-font-weight: bold;");        
    }
    
    public void inizializzaClassifica() {   //09
        tabella = new TableView<>();
        TableColumn usernameCol = new TableColumn("Giocatore");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn scoreCol = new TableColumn("Punteggio");
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));       
        tabella.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabella.setPrefWidth(250);
        tabella.setPrefHeight(370);
        tabella.getColumns().addAll(usernameCol, scoreCol);
        tabella.setLayoutX(600);
        tabella.setLayoutY(180);
        tabella.setFixedCellSize((double)337/(double)parametri.stile.NRigheClassifica);
        usernameCol.setStyle("-fx-alignment: center;");
        scoreCol.setStyle("-fx-alignment: center;");
        archivio = new Archivio(parametri.tecnologici.IpDBMS, parametri.tecnologici.portaDBMS, parametri.tecnologici.passwordDBMS); //10
        archivio.caricaClassificaDB(parametri.stile.NRigheClassifica);
        tabella.setItems(archivio.list);    //11
    }
    
    public void inizializzaInizia() {   //12
        inizia = new Button("Inizia");
        inizia.setLayoutX(20);
        inizia.setLayoutY(570);
        inizia.setPrefSize(150, 50);
        inizia.setStyle("-fx-font-family:" + parametri.stile.fontAltro + "; -fx-font-size:" + parametri.stile.dimensioneAltro + "pt;");
        inizia.setOnAction((ActionEvent event)->{   //13
            finito = false;
            timer.stop();
            punteggio.resetPunteggio();
            log = new Log("Memory", getTimestamp(), parametri.tecnologici.IpClient, "INIZIA");    
            Log.SerializzaEInviaLog(log);
            grid = new Griglia(parametri.gioco.numCoppie);
            stato = new StatoDiGioco(username.getText(), punteggio, grid, "", 0 ,0);
            inizializzaOggettiConStato(stato);
            inizializzaGriglia();
            inizializzaPausa(parametri.gioco.tempoCopertura);
            aspettaERicopri.play();
            inizializzaPausa(1);     
        });
    }
    
    public void inizializzaSuggerimento() { //14
        suggerimento = new Button("Suggerimento");
        suggerimento.setLayoutX(240);
        suggerimento.setLayoutY(570);
        suggerimento.setPrefSize(150, 50);
        suggerimento.setStyle("-fx-font-family:" + parametri.stile.fontAltro + "; -fx-font-size:" + parametri.stile.dimensioneAltro + "pt;");       
        suggerimento.setOnAction((ActionEvent event)->{ //15
            suggerisci();
            log = new Log("Memory", getTimestamp(), parametri.tecnologici.IpClient, "SUGGERIMENTO");      
            Log.SerializzaEInviaLog(log);
        });
    }
    
    public void inizializzaPausa(int tempo) {   //16
        aspettaERicopri = new PauseTransition(Duration.seconds(tempo));              
        aspettaERicopri.setOnFinished(event -> {
            grid.copriNonEvidenziate();
            for(Button tile: casella)
                aggiornaCella(Arrays.asList(casella).indexOf(tile));
            timer.play();
        });
    }
    
    public void inizializzaCache() {    //17
        File f = new File("./myfiles/cache.bin");
        if(f.isFile()){
            stato = StatoDiGioco.caricaBin();
            inizializzaOggettiConStato(stato);
            inizializzaGriglia();
            inizializzaPausa(1);
            finito = false;
            timer.play();
        }
    }
    
    public void inizializzaTimer() {    //18
        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            secondi++;
            if(secondi == 60){
                minuti++;
                secondi = 0;
            }
            tempo.setText(String.format("%02d", minuti) + ":" + String.format("%02d", secondi));
        }));        
        timer.setCycleCount(Animation.INDEFINITE);
    }
    
    public void inizializzaScopriEvent() {  //19
        scopriEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Button b = (Button)e.getSource();
                int i = (Integer)b.getUserData();
                log = new Log("Memory", getTimestamp(), parametri.tecnologici.IpClient, "CLICK");        
                Log.SerializzaEInviaLog(log);
                scopri(i);                
            }
        };
    }
    
    public void inizializzaOnCloseEvent(Stage stage) {  //20
        stage.setOnCloseRequest((WindowEvent we) ->{
            log = new Log("Memory", getTimestamp(), parametri.tecnologici.IpClient, "TERMINA");        
            Log.SerializzaEInviaLog(log);
            if(!finito) {
                stato = new StatoDiGioco(username.getText(), punteggio, grid, testoSuggerimento.getText(), minuti, secondi);
                StatoDiGioco.salvaBin(stato);
            }
        });
    }
    
    public void inizializzaOggettiConStato(StatoDiGioco s) {    //21
        testoSuggerimento.setText(s.suggerimento);
        punteggio = s.punteggio;
        minuti = s.minuti;
        secondi = s.secondi;
        username.setText(s.nome);
        score.setText(Integer.toString(punteggio.punteggio));
        tempo.setText(String.format("%02d", minuti) + ":" + String.format("%02d", secondi));
        fine.setText("");
        grid = s.griglia;
    }
    
    public void scopri(int idCasella) { //22
        grid.tiles.get(idCasella).stato = Casella.Stato.scoperta;
        aggiornaCella(idCasella);
        if(grid.scoperta != -1) {   //23
            if(grid.tiles.get(idCasella).lettera.equals(grid.tiles.get(grid.scoperta).lettera)) {   //24
                grid.tiles.get(idCasella).stato = Casella.Stato.evidenziata; grid.tiles.get(grid.scoperta).stato = Casella.Stato.evidenziata;
                aggiornaCella(idCasella); aggiornaCella(grid.scoperta);
                punteggio.aggiornaPunteggio();
                score.setText(Integer.toString(punteggio.punteggio));
                grid.coppieIndovinate++;
                if(grid.coppieIndovinate == parametri.gioco.numCoppie){ //25
                    fineGioco();
                    log = new Log("Memory", getTimestamp(), parametri.tecnologici.IpClient, "FINE");        
                    Log.SerializzaEInviaLog(log);
                }
                else {  //26
                    punteggio.serie++;
                    grid.scoperta = -1;
                }
            }
            else {  //27
                aspettaERicopri.play();
                punteggio.serie = 0;
                grid.scoperta = -1;
            }
            testoSuggerimento.setText("");
        }   //28
        else
            grid.scoperta = idCasella;
    }
    
    public void aggiornaCella(int i) { //29
        Casella.Stato s = grid.tiles.get(i).stato;
        if(s == Casella.Stato.coperta) {
            casella[i].setText("");
        }
        else if(s == Casella.Stato.scoperta) {
            casella[i].setText(grid.tiles.get(i).lettera);
        }
        else {
            casella[i].setStyle("-fx-background-color: " + parametri.stile.coloreCaselle + "; -fx-border-width: 0; -fx-background-radius: 0; -fx-background-insets: 0; "
                    + "-fx-font-family:" + parametri.stile.fontCaselle + "; -fx-font-size:" + parametri.stile.dimensioneCaselle + "pt; ");
        }
    }
    
    public void suggerisci() {  //30
        for(int i = 0 ; i < parametri.gioco.numCoppie*2 ; i++) {
            if((grid.tiles.get(i).lettera.equals(grid.tiles.get(grid.scoperta).lettera)) && (i != grid.scoperta)) {                
                String direzioneY = i/parametri.gioco.numCoppie == 0 ? "alto" : "basso";
                String direzioneX = i%6 < 3 ? "sinistra" : "destra";
                testoSuggerimento.setText("L'altra lettera " + grid.tiles.get(grid.scoperta).lettera + " è nel quadrante in " + direzioneY + " a " + direzioneX);
                punteggio.chiestoAiuto++; punteggio.serie = 0;
            }
        }
    }
    
    public void fineGioco() {   //31
        timer.stop();
        int bonusTempo = parametri.gioco.puntiBonusTempo - minuti*60 - secondi;
        bonusTempo = bonusTempo < 0 ? 0 : bonusTempo;
        punteggio.punteggio += bonusTempo;
        score.setText(Integer.toString(punteggio.punteggio));
        archivio.registraPunteggio(username.getText(), punteggio.punteggio);
        archivio.caricaClassificaDB(parametri.stile.NRigheClassifica);
        tabella.setItems(archivio.list);
        fine.setText("Ben fatto!    Bonus tempo: " + bonusTempo);
        File f = new File("./myfiles/cache.bin");
        f.delete();
        finito = true;
    }
    
    public String getTimestamp() {  //32
        SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd - HH:mm:ss");
        String timeStamp = date.format(new Date());
        return timeStamp;
    }
}

/*
Note:

(00)
    Vengono dichiarate tutte le variabili

(01)
    Dopo aver deserializzato il file di configurazione in XML viene creato
    un oggetto contenente i parametri iniziali

(02)
    Viene inviato un messaggio di avvio al server di log

(03)
    Viene inizializzato un oggetto punteggio con i parametri di configurazione

(04)
    Vengono inizializzati e creati tutti gli elementi dell'interfaccia

(05)
    Viene inizializzata una griglia di Button e ad ogni Button viene
    assegnato un indice con SetUserId. L'oggetto Griglia è composto da
    una lista di Caselle. Ad ogni Button della griglia viene assegnata
    la lettera della Casella di indice corrispondente a quello del Button

(06)(07)(08)
    Vengono inizializzate tutte le Label, vengono posizionate e dimensionate
    e infine gli viene assegnato lo stile in base ai parametri di configurazione

(09)
    Viene generata, posizionata e dimensionata la TableView contenente la
    classifica. La dimensione delle celle è dinamica in base al parametro
    iniziale di utenti da visualizzare

(10)
    Viene generato un oggetto archivio che contiene i parametri per
    collegarsi al database e nella cui observable list vengono inseriti
    gli utenti che devono essere viusalizzati nella classifica

(11)
    La TableView viene sincronizzata con la observable list dell'oggetto
    Archivio

(12)
    Viene inizializzato il bottone Inizia

(13)
    Quuando viene premuto il tasto Inizia si resettano i valori di gioco,
    si inizializza una griglia con le caselle scoperte, si aspetta un certo
    tempo dato dai parametri di configurazione iniziale e poi si coprono
    tutte le caselle. Viene inviato l'evento corrispondente al server di log

(14)
    Viene inizializzato il bottone Suggerimento

(15)
    Quando viene premuto il tasto Suggerimento viene chaiamata la funzione
    che mostra nell'interfaccia il quadrante in cui si trova la lettera
    uguale a quella scoperta e viene inviato l'evento corrispondente
    al server di log

(16)
    Viene creato un evento di pausa dopo il quale vengono coperte tutte le
    caselle non evidenziate e viene fatto partire il timer (se il timer è già
    stato avviato, prosegue inalterato)

(17)
    Se è presente il file di cache si caricano i dati lì contenuti in un
    oggetto StatoDiGioco e si inizializzano tutti gli oggetti di gioco
    e l'interfaccia in accordo a tali dati. Viene fatto partire il timer

(18)
    Viene creato un oggetto Timeline che ogni secondo incrementa il tempo di
    gioco di un secondo. Svolge la funzione del timer

(19)
    Viene creato l'event handler che gestisce il click di una casella
    chiamando la funzione scopri sulla casella cliccata. Viene inviato
    il relativo evento al server di log

(20)
    Alla chiusra dell'applicazione se c'è una partita in corso viene salvato
    lo stato di gioco sulla cache. Viene inviato l'evento di chiusura
    dell'applicazione al server di log

(21)
    Vengono inizializzate le varie Label e gli oggetti Griglia e Punteggio
    con i valori contenuti nell'oggetto StatoDIGioco

(22)
    Metodo che scopre la casella idCasella

(23)
    Caso in cui ci sia già una casella scoperta (non evidenziata) al momento
    della scopertura di una casella

(24)
    Caso in cui la lettera della casella scoperta coincida con la lettera della
    casella scoperta in precedenza: le caselle vengono evidenziate, viene
    aggiornato il punteggio e si incrementa il contatore di coppie indovinate

(25)
    Caso in cui si sono indovinate tutte le coppie: viene chiamata la funzione
    di fine gioco e viene inviato l'evento di fine gioco al server di log

(26)
    Caso in cui, dopo aver indovinato una casella, restano ancora caselle da
    indovinare: si setta la variabile scoperta dell'oggetto Griglia a -1 ad
    indicare che non ci sono caselle scoperte (non evidenziate) e si incrementa
    il contatore di coppie indovinate consecutivamente

(27)
    Caso in cui la casella scoperta non coincide con quella scoperta in
    precedenza: si aspetta un secondo e si ricoprono le caselle
    (non evidenziate. Si setta la variabile scoperta dell'oggetto Griglia a -1
    ad indicare che non ci sono caselle scoperte (non evidenziate) e si
    azzera il contatore di coppie indovinate consecutivamente

(28)
    Caso in cui non ci sono altre caselle scoperte (non evidenziate).
    si setta la variabile scoperta dell'oggetto Griglia con l'id della
    casella appena scoperta per poterla confrontare con la successiva
    casella che verrà scoperta

(29)
    Si aggiorna l'interfaccia in base allo stato della casella i:
    se la casella è scoperta non si mostra nessuna lettera, se è scoperta
    si mostra la lettera corrispondente all'indice della casella, se è
    evidenziata si mostra il testo e si imposta lo sfondo in base al valore
    preso dal file di configurazione

(30)
    Si cerca l'indice dell'altra casella con la lettera corrispondente a quella
    scoperta. In base all'indice che ha si calcola in che quadrante si trova
    e si stampa a video il suggerimento con il quadrante corrispondente

(31)
    Finito il gioco viene fermato il timer, viene aggiunto l'eventuale bonus
    tempo al punteggio, viene (eventualmente) aggiornato il database e
    si ricarica la classifica con i valori aggiornati. Viene stampato un
    messaggio di fine gioco che include i punti bonus ricevuti.
    Poichè non ci sono più partite in corso viene cancellato il file di cache
    e settata la variabile finito a true

(32)
    Restituisce un timestamp nel formato indicato da inviare al server di log
    quando si deve inviare un evento
*/