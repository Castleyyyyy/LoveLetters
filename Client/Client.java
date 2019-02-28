/**
 * <p>Materialien zu den zentralen
 * Abiturpruefungen im Fach Informatik ab 2012 in
 * Nordrhein-Westfalen.</p>
 * <p>Klasse Client</p>
* Ein Client kann &uuml;ber das Netz die Verbindung zu einem Server herstellen.<br>
* Fehlermeldungen werden ausgegeben.<br>
* Die Eingaben werden nebenl&auml;ufig verarbeitet.
* <p>NW-Arbeitsgruppe: Materialentwicklung zum Zentralabitur
 * im Fach Informatik</p>
 *
 * @version 2010-10-24
 */
public abstract class Client
{

    //Objektbeziehungen
    private Connection hatVerbindung;
    private Clientempfaenger hatEmpfaenger;
     
    /**
    Hilfsklasse fuer den Client, die in einem eigenen Thread den Empfang einer Nachricht vom Server realisiert.
    @author Horst Hildebrecht
    @version 1.0 vom 15.06.2006
    */
    class Clientempfaenger extends Thread
    {
        // Objekte
        private Client kenntClient;
        private Connection kenntVerbindung;
        
        // Attribute
        private boolean zVerbindungAktiv;
    
        /**
        Der ClientEmpfaenger hat den zugeh&ouml;rigen Client und die zugeh&ouml;rige Connection kennen gelernt.<br>
        @param pClient zugeh&ouml;riger Client, der die einkommenden Nachrichten bearbeitet
        @param pConnection zugeh&ouml;rige Connection, die die einkommenden Nachrichten empfängt
        */
        public Clientempfaenger(Client pClient, Connection pConnection)
        {
            kenntClient = pClient;
            kenntVerbindung = pConnection;
            zVerbindungAktiv = true;
        }
        
        /**
        Solange der Server Nachrichten sendete, wurden diese empfangen und an die ClientVerbinedung weitergereicht.
        */
        public void run()
        {
            String lNachricht;
            boolean lNachrichtEmpfangen = true;
            
            do
                if (zVerbindungAktiv)
                {
                    lNachricht = kenntVerbindung.receive();
                    lNachrichtEmpfangen = (lNachricht != null);
                    if (lNachrichtEmpfangen)
                        kenntClient.processMessage(lNachricht); 
                }
            while (zVerbindungAktiv && lNachrichtEmpfangen);
        }
        
        /**
        Der ClientEmpfaenger arbeitet nicht mehr
        */
        public void gibFrei()
        {
            zVerbindungAktiv = false;
        }
        
    }
    
    /**
    Der Client ist mit Ein- und Ausgabestreams initialisiert.<br>
    @param pIPAdresse IP-Adresse bzw. Domain des Servers
    @param pPortNr Portnummer des Sockets
    */
    public Client(String pIPAdresse, int pPortNr)
    {
        hatVerbindung = new Connection(pIPAdresse, pPortNr); 
        
        try
        {
            hatEmpfaenger = new Clientempfaenger(this, hatVerbindung);
            hatEmpfaenger.start();
        }

        catch (Exception pFehler)
        {
            System.err.println("Fehler beim \u00D6ffnen des Clients: " + pFehler);
        }       
         
    }
    
    public void send(String pMessage)
    {
        hatVerbindung.send(pMessage);
    }

    public boolean istVerbunden()
    {  if (hatEmpfaenger != null)
         return hatEmpfaenger.zVerbindungAktiv;
       else
         return false;
    }
    public String toString()
    {
        return "Verbindung mit Socket: " + hatVerbindung.verbindungsSocket();
    }
    
    /**
     Eine Nachricht vom Server wurde bearbeitet.<br>
     Diese abstrakte Methode muss in Unterklassen &uuml;berschrieben werden.
     @param pMessage die empfangene Nachricht, die bearbeitet werden soll
    */
    public abstract void processMessage(String pMessage);

    /**
    Die Verbindung wurde mit Ein- und Ausgabestreams geschlossen.
    */
    public void close()
    {
        if (hatEmpfaenger != null)
            hatEmpfaenger.gibFrei();
        hatEmpfaenger = null;
        hatVerbindung.close();
    }

}
