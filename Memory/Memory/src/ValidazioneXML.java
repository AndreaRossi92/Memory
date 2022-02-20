import java.io.File;
import java.io.*;
import javax.xml.*;
import javax.xml.parsers.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.validation.*;
import org.w3c.dom.*;
import org.xml.sax.*;

public class ValidazioneXML {
    public static void valida(String xml, String xsd, boolean xmlIsFile) {  //00
    try {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema s = sf.newSchema(new StreamSource(new File(xsd)));
        if(xmlIsFile) { //01
            Document d = db.parse(new File(xml));
            s.newValidator().validate(new DOMSource(d));
        }
        else    //02
            s.newValidator().validate(new StreamSource(new StringReader(xml)));
    } catch (Exception e) {
        if (e instanceof SAXException)
            System.out.println("Errore di validazione: " + e.getMessage());
        else
            System.out.println(e.getMessage());
        }
    } 
}

/*
Note:

(00)
    La stringa xml può contenere il percorso di un file XML o una stringa
    in formato XML, la stringa xsd contiene il percorso del file XSD su
    cui validare l'XML. Il booleano xmlIsFile serve a distinguere se la 
    prima stringa è il percorso di un file XML o un stringa in XML

(01)
    Se la stringa xml è un file si valida il file sul relativo xsd

(02)
    Se la stringa xml è una stringa xml si valida sul relativo xsd
*/