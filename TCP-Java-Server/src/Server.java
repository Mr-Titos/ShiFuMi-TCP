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
    int statusGame = 0;

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
                else if(received.equals("pierre")) {
                    System.out.println("Pierre");
                    if(plist.size() == 1) {
                        whoWinBot(received, dos);
                    }
                }
                else if(received.equals("feuille")) {
                    System.out.println("Feuille");
                    if(plist.size() == 1) {
                        whoWinBot(received, dos);
                    }

                }
                else if(received.equals("ciseaux")) {
                    System.out.println("Ciseaux");
                    if(plist.size() == 1) {
                        whoWinBot(received, dos);
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
        System.out.println(c);
        try {
        switch (s) {
            case "pierre":
                switch (c) {
                    case 0:
                        dos.writeUTF(e);
                        break;
                    case 1:
                        dos.writeUTF(l);
                        break;
                    case 2:
                        dos.writeUTF(w);
                        break;

                }
                break;
            case "feuille":
                switch (c) {
                    case 0:
                        dos.writeUTF(w);
                        break;
                    case 1:
                        dos.writeUTF(e);
                        break;
                    case 2:
                        dos.writeUTF(l);
                        break;

                }
                break;
            case "ciseaux":
                switch (c) {
                    case 0:
                        dos.writeUTF(l);
                        break;
                    case 1:
                        dos.writeUTF(w);
                        break;
                    case 2:
                        dos.writeUTF(e);
                        break;

                }
                break;
        }
    } catch (IOException ie) { System.out.println(ie.getMessage()); }
    }
    private Player whoWin(Player pl1, Player pl2){
        String prop1 = pl1.getProposition();
        String prop2 = pl2.getProposition();
        Player pwin = new Player("null");
        switch (prop1) {
            case "pierre":
                switch (prop2) {
                    case "feuille":
                        break;
                    case "ciseaux":
                        break;
                }
                break;
            case "feuille":
                break;
            case "ciseaux":
                break;
        }
        return pwin;
    }

    private void roundLogic() {
        try {
            Player pwin = whoWin(plist.get(0), plist.get(1));
            if (!pwin.getProposition().equals("null")) {
                DataOutputStream outputwin = new DataOutputStream(pwin.getSo().getOutputStream());
                outputwin.writeUTF("Victoire !");
                for (Player ptemp : plist) {
                    if (ptemp != pwin) {
                        DataOutputStream outputlose = new DataOutputStream(ptemp.getSo().getOutputStream());
                        outputlose.writeUTF("Défaite D:");
                    }
                }
            } else {
                for (Player ptemp : plist) {
                    DataOutputStream outputequal = new DataOutputStream(ptemp.getSo().getOutputStream());
                    outputequal.writeUTF("Egalité !");
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
} 