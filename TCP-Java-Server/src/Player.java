import java.net.Socket;

public class Player {
    private Socket so;
    private int score = 0;
    private String proposition;
    private boolean etat = false;

    public Player(Socket s) {
        so = s;
        this.proposition = "default";
    }
    public void setSo(Socket soc) {
        so = soc;
    }
    public void setProposition(String p) {
        this.proposition = p;
    }
    public void setScore(int sc) {
        score = sc;
    }
    public void setEtat(boolean e) {
        etat = e;
    }
    public Socket getSo() {
        return so;
    }
    public String getProposition() {
        return proposition;
    }
    public int getScore() {
        return score;
    }
    public boolean getEtat() {
        return etat;
    }
}
