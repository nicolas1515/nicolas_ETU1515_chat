package chat.proj;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

import javax.swing.*;


public class Client extends JFrame{
    // interface IHM
    private Container c ;
    private JTextArea affichage ;
    private JTextField champEntree;
   
    
    // connexion au serveur.$
    private Socket client;
    private String serveurDeChat;
    private String message ;
   
    
    //les flux.....................
    private ObjectInputStream entree;
    private ObjectOutputStream sortie;
    
    
    /** Creates a new instance of Client */
    public Client(String hote) {
        super("Client");
        this.serveurDeChat = hote;
        
        //installation des composants graphique
        // sur le conteneur......
        c = getContentPane();        
        this.affichage = new JTextArea();
        this.champEntree = new JTextField();
        champEntree.setEnabled(false);  
        c.add(champEntree,BorderLayout.NORTH);
        c.add(new JScrollPane(affichage),BorderLayout.CENTER);
     
        
        
        //gestion des evenements
        champEntree.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    envoyerDonner(e.getActionCommand());
                }
        });

        
        setSize(300,300);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);                
    }
    
    public void lancerClient(){
        while(true){
        try{
            //Etape n:1;
            
            seConnecterAuServeur();
            
            //Etape n2;
            obtenirFlux();
            
            //Etape n3;
            traiterConnexion();
            
            //Etape n4;
            fermerConnexion();            
        }
        catch(EOFException eof){
            // le serveur a ferm� la connexion
            JOptionPane.showMessageDialog(this,eof.getMessage()+"","ERREUR"
                                        ,JOptionPane.ERROR);
        }
        catch(IOException e){
        //Traiter les problemes de communication avec lengthserveur 
            e.printStackTrace();
        }
        }
    }

    public void seConnecterAuServeur()throws IOException{
        this.affichage.setText("Essai de Connexion...\n"); 
         client = new Socket(InetAddress.getByName(this.serveurDeChat),5001);
        this.affichage.append("Connecte:" + client.getInetAddress().getHostName());
            
    }

    public void obtenirFlux()throws IOException{
        this.sortie = new ObjectOutputStream( this.client.getOutputStream());
        sortie.flush();
        this.entree = new ObjectInputStream(this.client.getInputStream());
        this.affichage.append("\nJ'ai eu les flux");    
    }

    public void traiterConnexion()throws IOException{
        this.champEntree.setEnabled(true);
        do{
            try{
                message=(String)entree.readObject();
                this.affichage.append("\n" +message);
                this.affichage.setCaretPosition(this.affichage.getText().length());
            }
            catch(ClassNotFoundException e){
                JOptionPane.showMessageDialog(this,e.getMessage());
            }
        }while(!message.equals("SERVEUR>>> TERMINER"));
            
    }

    public void fermerConnexion()throws IOException{
        this.affichage.append("\nUtilisateur a ferm� la connexion.");
        this.champEntree.setEnabled(false);
        sortie.close();
        entree.close();
        client.close();
            
    }

    
    public void envoyerDonner(String message){
        try{
        sortie.writeObject("CLIENT>>> " + message);
        sortie.flush();
        this.champEntree.setText("");
        }
        catch(IOException e){
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }
    
    public static void main(String[] args){
        Client client = new Client("127.0.0.1");
        client.lancerClient();
    }
    
}
