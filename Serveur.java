package chat.proj;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;
import java.awt.*;
public class Serveur extends JFrame{
    // les sockets:
    private java.net.ServerSocket serveur;
    private java.net.Socket connexion;
    //les flux d'entr�es/sorties
    private java.io.ObjectInputStream entree ;
    private java.io.ObjectOutputStream sortie;
    // les composant de l'IHM
    private JTextField champEntree;
    private JTextArea zoneAffichage;
    
    private int compteur = 1;
    
    /** Creates a new instance of Main */
    public Serveur() {
        super("Serveur");
        Container c = getContentPane();
        
        // les composants !!!!
        //*****************************************************
        this.champEntree = new JTextField("");
        this.champEntree.setEnabled(false);
        this.champEntree.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                //envoyer au client le message
                System.err.println("envoyer:");
                envoyerDonnees(e.getActionCommand());
            }
        });
        //*******************************************************
        //******************************************************
        this.zoneAffichage = new JTextArea();
        //*****************************************************
        
        c.add(this.champEntree, BorderLayout.NORTH);
        c.add(new JScrollPane(this.zoneAffichage), BorderLayout.CENTER);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350,350);
        setVisible(true);
    }
    
//*****************************************************************************
    public void envoyerDonnees(String message){
        try{
            sortie.writeObject("SERVEUR>>> "+ message);
            sortie.flush();
            this.zoneAffichage.append("\nSERVEUR>>> "+message);
            this.champEntree.setText("");
        }
        catch(IOException io){
            zoneAffichage.append("\nErreur de l'ecriture d'un object");
        }
    }
//*****************************************************************************
    public void lancerServeur(){
        try{
            System.out.println("mila miandry kely o! hahahaha");
            //ETAPE 1:preparation d'un socket serveur
            serveur = new ServerSocket(5001,100);
            
            while(true){
                //ETAPE 2: attendre une connexion
                attendreUneConnexion();
                
                //ETAPE 3: obtenir les flux...
                obtenirLesFlux();
                
                //ETAPE 4:Traiter la connexion
                traiterConnexion();
                
                //ETAPE 5: fermer la connexion
                fermerConnexion();
            }
        }
        catch(EOFException eof){
            JOptionPane.showMessageDialog(this,eof.getMessage()
                                        +"\n lengthclient a interrompu la connexion");
                                        
        }
        catch(IOException io){
            // trate les eventuelles probl�mes d'E/S
            io.printStackTrace();
        }
    }
    public void attendreUneConnexion()throws IOException{
        this.zoneAffichage.setText("En attente de connexion......\n");
        //Faire en sorte que lengthserveur accepte une connexion.
        this.connexion = serveur.accept();
        
        this.zoneAffichage.append("connexion " + compteur + 
        "recue de : "+ connexion.getInetAddress().getHostName());        
    }
    
    public void obtenirLesFlux()throws IOException{
        // Avoir la sortie
        sortie = new ObjectOutputStream(connexion.getOutputStream());
        
        // vidanger lengthtampon se sorie pour envoyer les information
        //d'en-t�te.
        sortie.flush();
        
        //Avoir la sortie
        entree = new ObjectInputStream(connexion.getInputStream());
        
        this.zoneAffichage.append("\n J'ai recu les flux");
    }
    
    public void traiterConnexion()throws IOException{
        //envoyer message de la connexion reussie
        String message = "SERVEUR>>>  Connection";
        sortie.writeObject(message);
        sortie.flush();
        
        //Activer le champ d'Entree
        champEntree.setEnabled(true);
        
        //traitment des informations!!
        do{
            try{
                message = (String)entree.readObject();
                this.zoneAffichage.append("\n"+message);
                //place la zonne de texte sur le message
                this.zoneAffichage.setCaretPosition(this.zoneAffichage.getText().length());
            }
            catch(ClassNotFoundException e){
                this.zoneAffichage.append(e.getMessage()+"\nl'object recu de type inconnu");
            }
        }while (!message.equals("CLIENT>>> TERMINER"));
    }
    
    public void fermerConnexion()throws IOException{
        this.zoneAffichage.append("\nL'utilisateur a ferme la connexion.");
        this.champEntree.setEnabled(false);
        envoyerDonnees("TERMINER");
        sortie.close();
        entree.close();
        connexion.close();
        
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Serveur appli = new Serveur();
        appli.lancerServeur();
    }
    
}
