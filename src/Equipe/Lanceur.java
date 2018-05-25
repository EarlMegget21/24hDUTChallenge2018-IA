package src.Equipe;

import java.util.ArrayList;

public class Lanceur {


    public static int posHome[][];// une position est affiche de tel maniere=> Case 1 : posHome[0]( x => "1", y => "2");
    private int[] coord; //[0] = x et [1] = y et [2] = fruit (0 a 3 : c'est un fruit; 4 : c'est une chataigne; -1 : pas de fruit)


    public Lanceur(int[] coord) {
        this.coord = coord;
    }

    public String getAction() {
        if (coord[2] >= 0) {
            String x = homeThrow(coord);
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


    public String collect() {

        int caseContent=(int)Client.map[coord[0]][coord[1]]-'0';

        if (caseContent>0&&caseContent<4) {
            coord[2]=2;
            return "P";
        }
        else {
            return null;
        }

    }

    public static void jouerTour(){
        //TODO: tester le compromis entre proximit� et points rapport�s
        trouverCible(); //j'ai choisi celle qui rapporte le plus de points, m�me si elle est � l'oppos� de la map

        /* initialisation de la case courante */
        String courant=position.getX()+":"+position.getY();

        /* ajout de courant dans la liste ouverte */
        listeOuverte.put(courant, position);
        ajouter_liste_fermee(courant);
        ajouter_cases_adjacentes(position);

        /* tant que la destination n'a pas �t� atteinte et qu'il reste des noeuds � explorer dans la liste ouverte */
        while( !(courant.equals(objectif.getX()+":"+objectif.getY()))
                &&
                !listeOuverte.isEmpty()
                ){

            /* on cherche le meilleur noeud de la liste ouverte, on sait qu'elle n'est pas vide donc il existe */
            courant = meilleur_noeud(listeOuverte);

            /* on le passe dans la liste fermee, il ne peut pas d�j� y �tre */
            ajouter_liste_fermee(courant);

            /* on recommence la recherche des noeuds adjacents */
            ajouter_cases_adjacentes(listeFermee.get(courant));
        }

        /* si la destination est atteinte, on remonte le chemin */
        if (courant.equals(objectif.getX()+":"+objectif.getY())){
            retrouver_chemin();
            //Fin
        }else{
            System.err.println("Pas de solution");
        }
    }

    public static void trouverCible(){
        int x=1;
        int y=1;
        int points=0;
        for(int j=1; j<hauteur-1; j++){ //on part de 1 et on enl�ve 1 car les bords sont des murs
            for(int i=1; i<largeur-1; i++){
                try {
                    if(Integer.parseInt(lab[j][i])>points){
                        x=i;
                        y=j;
                        points=Integer.parseInt(lab[j][i]);
                    }
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        }
        objectif.setX(x);
        objectif.setY(y);
    }
}