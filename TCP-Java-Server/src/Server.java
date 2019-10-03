import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;

// Server class => prend les connexions et l'assigne sur ClientHandler
public class Server
{
    private static int nbco = 0;

    public void set_Nbco(int c) {
        nbco = c;
    }
    public int get_Nbco() {
        return nbco;
    }

    public static void main(String[] args) throws IOException
    {
        ServerSocket ss = new ServerSocket(5056);
        while (true)
        {
            Socket s = null;

            try
            {
                s = ss.accept(); // fait un tableau des connexiosn entrantes

                System.out.println("A new client is connected : " + s);

                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                if(nbco < 2) {
                    Player p1 = new Player(s);
                    Thread t = new ClientHandler(s, dis, dos, p1);
                    t.start();
                    nbco++;
                } else {
                    dos.writeUTF("2 personnes sont déjà connectées sur ce serveur");
                    throw new Exception("Server full");
                }

            }
            catch (Exception e){
                s.close();
                e.printStackTrace();
            }
        }
    }
}

// ClientHandler class => traitement des inputs du client
class ClientHandler extends Thread
{
    DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd");
    DateFormat fortime = new SimpleDateFormat("hh:mm:ss");
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;
    private static int compteur = 0;
    Boolean gstatus = false;
    String received;
    String toreturn;
    Server server = new Server();
    private final static ArrayList<Player> plist = new ArrayList<>();

    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, Player p)
    {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        addList(p);
        try {
            dos.writeUTF("Tu es bien connecté");
            System.out.println(plist);
        } catch (Exception e) { System.out.println(e.getMessage()); }
    }

    private void addList(Player p) {
        synchronized (plist) {
            plist.add(p);
        }
    }

    private void removeList(Player p) {
        synchronized (plist) {
            plist.remove(p);
        }
    }

    @Override
    public void run()
    {
        while (true)
        {
            try {
                received = dis.readUTF().toLowerCase();
                System.out.println("Client " + s + received);
                dos.writeUTF("You're in Shi-Fu-Mi game !\n" +
                            "Type Exit to terminate connection.");



                if(received.equals("exit"))
                {
                    Player premove = null;
                    for(Player p: plist) {
                        if(p.getSo() == s) {
                            premove = p;
                        }
                    }
                    removeList(premove);
                    System.out.println(plist);
                    System.out.println("Closing this connection.");
                    this.s.close();
                    System.out.println("Connection closed");
                    server.set_Nbco(server.get_Nbco() - 1);
                    break;
                }
                else if(received.equals("time")) {
                    Date date = new Date();
                        toreturn = fortime.format(date);
                        dos.writeUTF(toreturn);
                }
                else if(received.equals("score")) {
                    for(Player ptemp : plist) {
                        if(ptemp.getSo() == s)
                            dos.writeUTF("Votre score est de " + ptemp.getScore());
                    }
                }
                else if(received.equals("pierre")) {
                    System.out.println("Pierre");
                    if(plist.size() == 1) {
                        whoWinBot(received, dos);
                    } else {
                        for(Player ptemp1: plist) {
                            if(ptemp1.getSo() == s)
                                ptemp1.setProposition(received);
                        }
                        int ctemp = 0;
                        for(Player ptemp2: plist) {
                            System.out.println("Player" + ptemp2.getSo() + " - " + ptemp2.getProposition());
                            if(!ptemp2.getProposition().equals("default")) {
                                ctemp++;
                            }
                        }
                        if(ctemp == 2) {
                            whoWin(plist.get(0), plist.get(1));
                        } else
                            dos.writeUTF("En attente de l'autre joueur");
                    }
                }
                else if(received.equals("feuille")) {
                    System.out.println("Feuille");
                    if(plist.size() == 1) {
                        whoWinBot(received, dos);
                    } else {
                        for(Player ptemp1: plist) {
                            if(ptemp1.getSo() == s)
                                ptemp1.setProposition(received);
                        }
                        int ctemp = 0;
                        for(Player ptemp2: plist) {
                            System.out.println("Player" + ptemp2.getSo() + " - " + ptemp2.getProposition());
                            if(!ptemp2.getProposition().equals("default")) {
                                ctemp++;
                            }
                        }
                        if(ctemp == 2) {
                            whoWin(plist.get(0), plist.get(1));
                        } else
                            dos.writeUTF("En attente de l'autre joueur");
                    }

                }
                else if(received.equals("ciseaux")) {
                    System.out.println("Ciseaux");
                    if(plist.size() == 1) {
                        whoWinBot(received, dos);
                    } {
                        for(Player ptemp1: plist) {
                            if(ptemp1.getSo() == s)
                                ptemp1.setProposition(received);
                        }
                        int ctemp = 0;
                        for(Player ptemp2: plist) {
                            System.out.println("Player" + ptemp2.getSo() + " - " + ptemp2.getProposition());
                            if(!ptemp2.getProposition().equals("default")) {
                                ctemp++;
                            }
                        }
                        if(ctemp == 2) {
                            whoWin(plist.get(0), plist.get(1));
                        } else
                            dos.writeUTF("En attente de l'autre joueur");
                    }

                    } else {
                        dos.writeUTF("Invalid input");
                    }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try
        {
            // Libération des resources
            this.dis.close();
            this.dos.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void whoWinBot(String s, DataOutputStream dos) {
        int c = new Random().nextInt(3);
        // 0 = Pierre / 1 = Feuille / 2 = Ciseaux
        String e = "C'est une égalité !";
        String l = "Vous avez perdu contre le bot !";
        String w = "Vous avez gagné contre le bot !";
        Player playerBot = plist.get(0);
        try {
            switch (s) {
                case "pierre":
                    switch (c) {
                        case 0:
                            dos.writeUTF(e + " Votre score est maintenant de " + playerBot.getScore());
                            break;
                        case 1:
                            playerBot.setScore(playerBot.getScore() - 1);
                            dos.writeUTF(l + " Votre score est maintenant de " + playerBot.getScore());
                            break;
                        case 2:
                            playerBot.setScore(playerBot.getScore() + 1);
                            dos.writeUTF(w + " Votre score est maintenant de " + playerBot.getScore());
                            break;

                    }
                    break;
                case "feuille":
                    switch (c) {
                        case 0:
                            playerBot.setScore(playerBot.getScore() + 1);
                            dos.writeUTF(w + " Votre score est maintenant de " + playerBot.getScore());
                            break;
                        case 1:
                            dos.writeUTF(e + " Votre score est maintenant de " + playerBot.getScore());
                            break;
                        case 2:
                            playerBot.setScore(playerBot.getScore() - 1);
                            dos.writeUTF(l + " Votre score est maintenant de " + playerBot.getScore());
                            break;

                    }
                    break;
                case "ciseaux":
                    switch (c) {
                        case 0:
                            playerBot.setScore(playerBot.getScore() - 1);
                            dos.writeUTF(l + " Votre score est maintenant de " + playerBot.getScore());
                            break;
                        case 1:
                            playerBot.setScore(playerBot.getScore() + 1);
                            dos.writeUTF(w + " Votre score est maintenant de " + playerBot.getScore());
                            break;
                        case 2:
                            dos.writeUTF(e + " Votre score est maintenant de " + playerBot.getScore());
                            break;

                    }
                    break;
            }
        } catch (IOException ie) { System.out.println(ie.getMessage()); }
    }
    private void whoWin(Player pl1, Player pl2) {
        try {
            String prop1 = pl1.getProposition();
            String prop2 = pl2.getProposition();
            DataOutputStream dos1 = new DataOutputStream(pl1.getSo().getOutputStream());
            DataOutputStream dos2 = new DataOutputStream(pl2.getSo().getOutputStream());
            String e = "C'est une égalité !";
            String l = "Vous avez perdu contre votre adversaire !";
            String w = "Vous avez gagné contre votre adversaire !";

            switch (prop1) {
                case "pierre":
                    switch (prop2) {
                        case "pierre":
                            dos1.writeUTF(e + "\nVotre score est maintenant de " + pl1.getScore() + ". Et celui de votre adversaire " + pl2.getScore());
                            dos2.writeUTF(e + "\nVotre score est maintenant de " + pl2.getScore() + ". Et celui de votre adversaire " + pl1.getScore());
                            break;
                        case "feuille":
                            pl1.setScore(pl1.getScore() - 1); // Defeat
                            pl2.setScore(pl2.getScore() + 1); // Victory
                            dos1.writeUTF(l + "\nVotre score est maintenant de " + pl1.getScore() + ". Et celui de votre adversaire " + pl2.getScore());
                            dos2.writeUTF(w + "\nVotre score est maintenant de " + pl2.getScore() + ". Et celui de votre adversaire " + pl1.getScore());
                            break;
                        case "ciseaux":
                            pl1.setScore(pl1.getScore() + 1); // Victory
                            pl2.setScore(pl2.getScore() - 1); // Defeat
                            dos1.writeUTF(w + "\nVotre score est maintenant de " + pl1.getScore() + ". Et celui de votre adversaire " + pl2.getScore());
                            dos2.writeUTF(l + "\nVotre score est maintenant de " + pl2.getScore() + ". Et celui de votre adversaire " + pl1.getScore());
                            break;

                    }
                    break;
                case "feuille":
                    switch (prop2) {
                        case "pierre":
                            pl1.setScore(pl1.getScore() + 1); // Victory
                            pl2.setScore(pl2.getScore() - 1); // Defeat
                            dos1.writeUTF(w + "\nVotre score est maintenant de " + pl1.getScore() + ". Et celui de votre adversaire " + pl2.getScore());
                            dos2.writeUTF(l + "\nVotre score est maintenant de " + pl2.getScore() + ". Et celui de votre adversaire " + pl1.getScore());
                            break;
                        case "feuille":
                            dos1.writeUTF(e + "\nVotre score est maintenant de " + pl1.getScore() + ". Et celui de votre adversaire " + pl2.getScore());
                            dos2.writeUTF(e + "\nVotre score est maintenant de " + pl2.getScore() + ". Et celui de votre adversaire " + pl1.getScore());
                            break;
                        case "ciseaux":
                            pl1.setScore(pl1.getScore() - 1); // Defeat
                            pl2.setScore(pl2.getScore() + 1); // Victory
                            dos1.writeUTF(l + "\nVotre score est maintenant de " + pl1.getScore() + ". Et celui de votre adversaire " + pl2.getScore());
                            dos2.writeUTF(w + "\nVotre score est maintenant de " + pl2.getScore() + ". Et celui de votre adversaire " + pl1.getScore());
                            break;

                    }
                    break;
                case "ciseaux":
                    switch (prop2) {
                        case "pierre":
                            pl1.setScore(pl1.getScore() - 1); // Defeat
                            pl2.setScore(pl2.getScore() + 1); // Victory
                            dos1.writeUTF(l + "\nVotre score est maintenant de " + pl1.getScore() + ". Et celui de votre adversaire " + pl2.getScore());
                            dos2.writeUTF(w + "\nVotre score est maintenant de " + pl2.getScore() + ". Et celui de votre adversaire " + pl1.getScore());
                            break;
                        case "feuille":
                            pl1.setScore(pl1.getScore() + 1); // Victory
                            pl2.setScore(pl2.getScore() - 1); // Defeat
                            dos1.writeUTF(w + "\nVotre score est maintenant de " + pl1.getScore() + ". Et celui de votre adversaire " + pl2.getScore());
                            dos2.writeUTF(l + "\nVotre score est maintenant de " + pl2.getScore() + ". Et celui de votre adversaire " + pl1.getScore());
                            break;
                        case "ciseaux":
                            dos1.writeUTF(e + "\nVotre score est maintenant de " + pl1.getScore() + ". Et celui de votre adversaire " + pl2.getScore());
                            dos2.writeUTF(e + "\nVotre score est maintenant de " + pl2.getScore() + ". Et celui de votre adversaire " + pl1.getScore());
                            break;

                    }
                    break;
            }
        } catch (IOException ie) { System.out.println(ie.getMessage()); }
        pl1.setProposition("default");
        pl2.setProposition("default");
    }
} 