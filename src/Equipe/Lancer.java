package Equipe;

public class Lancer {
    public static int posHome[][];// une position est affiche de tel maniere=> Case 1 : posHome[0]( x => "1", y => "2");
    private int[] coord; //[0] = x et [1] = y et [2] = fruit (0 a 3 : c'est un fruit; 4 : c'est une chataigne; -1 : pas de fruit)


    public Lancer(int[] coord) {
        this.coord = coord;
    }

    public String getAction() {
        if (coord[2]>=0) {
            string x = homeThrow(coord);
            if (x != null) {
                return x;
            } else {
                return moveToHome();
            }
        } else {
            String x = collect();
            if (x != null) {
                return x;
            } else {
                return //TODO appeler la fonction A*
            }
        }
    }

    public String moveToHome() {
        //s'oriente sur l'axe soit vertical soit horizontal


    }


}
