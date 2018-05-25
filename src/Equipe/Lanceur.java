package Equipe;
import java.util.ArrayList;
import Algo.Noeud;
import java.util.HashMap;
public class Lanceur {
    public static HashMap<String, Noeud> listeOuverte = new HashMap<String, Noeud>();
    public static HashMap<String, Noeud> listeFermee = new HashMap<String, Noeud>();
    public ArrayList<Noeud>[] chemins;
    private int[] coord; //[0] = x et [1] = y et [2] = fruit (0 a 3 : c'est un fruit; 4 : c'est une chataigne; -1 : pas de fruit)

    public Lanceur(int[] coord) {
        this.coord = coord;
    }

    public String getAction() {
        if (coord[2] >= 0) {
            string x = homeThrow(coord);
            if (x != null) {
                return x;
            } else {
                return moveTowardHome();
            }
        } else {
            String x = collect();
            if (x != null) {
                return x;
            } else {
                return moveToClosestObjective();
            }
        }
    }

    public String moveTowardHome() {
        Noeud n1 = new Noeud();
        n1.setX(Client.home[0][0]);
        n1.setY(Client.home[0][1]);
        Noeud n2 = new Noeud();
        n2.setX(Client.home[1[0]);
        n2.setY(Client.home[1][1]);
        jouerTour(n1);
        jouerTour(n2);
        Noeud nDir = chemins[0].get(0);
        int[] dir = new int[2] {
            nDir.getX(), nDir.getY()
        };
        String r;
        if(coord[0] == dir[0]) {
            if(dir[1] > coord[1]) {
                r = new String("N");
            } else {
                r = new String("S");
            }
        } else {
            if(dir[0] > coord[0]) {
                r = new String("E");
            } else {
                r = new String("O");
            }
        }
        return r
    }

    public String moveToClosestObjective() {
        /* Parcourir les objectifs declare dans le client et les parcourir pour utiliser jouerTour(e)*/
        Client.listObjective.forEach(str -> jouerTour(str));
        int x = chemins.get(0).x;
        int y = chemins.get(0).y;
        //TODO
    }

    public String collect() {
        int caseContent = (int) Client.map[coord[0]][coord[1]] - '0';
        if (caseContent > 0 && caseContent < 4) {
            coord[2] = 2;
            return "P";
        } else {
            return null;
        }
    }

    /**
     * V1: algorithme a* qui joue un tour en prenant la cible la plus proche
     **/
    public static void jouerTourProche(Noeud objectifTemporaire) {
        //TODO: on peut mettre un compteur qui compare au fur et à mesure la construction du chemin si il dépasse pas un déjà existant, dans ce cas là on stopperait la recherche car ça ne servirait à rien (parcours partiel)
        /* initialisation de la case courante */
        String courant = position.getX() + ":" + position.getY();
        /* ajout de courant dans la liste ouverte */
        listeOuverte.put(courant, position);
        ajouter_liste_fermee(courant);
        ajouter_cases_adjacentes(position);
        /* tant que la destination n'a pas été atteinte et qu'il reste des noeuds à explorer dans la liste ouverte */
        while (!(courant.equals(objectifTemporaire.getX() + ":" + objectifTemporaire.getY()))
                &&
                !listeOuverte.isEmpty()
                ) {
            /* on cherche le meilleur noeud de la liste ouverte, on sait qu'elle n'est pas vide donc il existe */
            courant = meilleur_noeud(listeOuverte);
            /* on le passe dans la liste fermee, il ne peut pas déjà y être */
            ajouter_liste_fermee(courant);
            /* on recommence la recherche des noeuds adjacents */
            ajouter_cases_adjacentes(listeFermee.get(courant));
        }
        /* si la destination est atteinte, on remonte le chemin */
        if (courant.equals(objectifTemporaire.getX() + ":" + objectifTemporaire.getY())) {
            retrouver_chemin_proche(objectifTemporaire);
            //Fin
        } else {
            System.err.println("Pas de solution");
        }
    }

    /**
     * V2: algorithme a* qui joue un tour en prenant le chemin vers l'objectif qui rapportera le plus de points
     **/
    public static void jouerTour() {
        //TODO: tester le compromis entre proximité et points rapportés
        trouverCible(); //j'ai choisi celle qui rapporte le plus de points, même si elle est à l'opposé de la map
        /* initialisation de la case courante */
        String courant = position.getX() + ":" + position.getY();
        /* ajout de courant dans la liste ouverte */
        listeOuverte.put(courant, position);
        ajouter_liste_fermee(courant);
        ajouter_cases_adjacentes(position);
        /* tant que la destination n'a pas été atteinte et qu'il reste des noeuds à explorer dans la liste ouverte */
        while (!(courant.equals(objectif.getX() + ":" + objectif.getY()))
                &&
                !listeOuverte.isEmpty()
                ) {
            /* on cherche le meilleur noeud de la liste ouverte, on sait qu'elle n'est pas vide donc il existe */
            courant = meilleur_noeud(listeOuverte);
            /* on le passe dans la liste fermee, il ne peut pas déjà y être */
            ajouter_liste_fermee(courant);
            /* on recommence la recherche des noeuds adjacents */
            ajouter_cases_adjacentes(listeFermee.get(courant));
        }
        /* si la destination est atteinte, on remonte le chemin */
        if (courant.equals(objectif.getX() + ":" + objectif.getY())) {
            retrouver_chemin();
            //Fin
        } else {
            System.err.println("Pas de solution");
        }
    }

    /**
     * modifie les coordonnées de l'objectif (ici on prend la cible qui rapporte le plus de points)
     **/
    public static void trouverCible() {
        int x = 1;
        int y = 1;
        int points = 0;
        for (int j = 1; j < hauteur - 1; j++) { //on part de 1 et on enlève 1 car les bords sont des murs
            for (int i = 1; i < largeur - 1; i++) {
                try {
                    if (Integer.parseInt(lab[j][i]) > points) {
                        x = i;
                        y = j;
                        points = Integer.parseInt(lab[j][i]);
                    }
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        }
        objectif.setX(x);
        objectif.setY(y);
    }

    /**
     * parcours les cases adjacentes pour ajouter la bonne
     **/
    public static void ajouter_cases_adjacentes(Noeud current) {
        Noeud noeud;
        /* on met tous les noeud adjacents dans la liste ouverte (+vérif) */
        for (int i = current.getX() - 1; i <= current.getX() + 1; i++) {
            if ((i < 0) || (i >= largeur))  /* en dehors de l'image, on oublie */
                continue;
            for (int j = current.getY() - 1; j <= current.getY() + 1; j++) {
                if ((j < 0) || (j >= hauteur))   /* en dehors de l'image, on oublie */
                    continue;
                if ((i == current.getX()) && (j == current.getY()))  /* case actuelle current, on oublie */
                    continue;
                if ((i == current.getX() + 1 && (j == current.getY() + 1 || j == current.getY() - 1))
                        ||
                        (i == current.getX() - 1 && (j == current.getY() + 1 || j == current.getY() - 1)))
                    continue;
                if (lab[j][i].equals("D"))
                    /* obstace, terrain non franchissable, on oublie */
                    continue;
                String voisin = i + ":" + j; //on a un voisin valide
                if (!deja_present_dans_liste(voisin, listeFermee)) {
                    /* le noeud n'est pas déjà présent dans la liste fermée */
                    noeud = new Noeud();
                    /* calcul du cout G du noeud en cours d'étude : cout du parent + distance jusqu'au parent */
                    noeud.setCoutG(listeFermee.get(current.getX() + ":" + current.getY()).getCoutG() + distance(i, j, current.getX(), current.getY()));
                    /* calcul du cout H du noeud à la destination */
                    noeud.setCoutH(distance(i, j, objectif.getX(), objectif.getY()));
                    noeud.setCoutF(noeud.getCoutG() + noeud.getCoutH());
                    noeud.setParent(current);
                    noeud.setX(i);
                    noeud.setY(j);
                    if (deja_present_dans_liste(voisin, listeOuverte)) {
                        /* le noeud est déjà présent dans la liste ouverte, il faut comparer les couts */
                        if (noeud.getCoutF() < listeOuverte.get(voisin).getCoutF()) {
                            /* si le nouveau chemin est meilleur, on met à jour */
                            listeOuverte.put(voisin, noeud);
                        }
                        /* sinon le noeud courant a un moins bon chemin, on ne change rien */
                    } else {
                        /* le noeud n'est pas présent dans la liste ouverte, on l'y ajoute */
                        listeOuverte.put(voisin, noeud);
                    }
                }
            }
        }
    }

    /**
     * V1: fonction qui remonte de parents en parents pour retranscrire le chemin inverse de l'objectif jusqu'à nous
     * et assigne le meilleur chemin (plus proche)
     **/
    public static void retrouver_chemin_proche(Noeud objectifTemporaire) {
        //création du chemin à tester
        ArrayList<Noeud> cheminTemporaire = new ArrayList<Noeud>();
        /* l'arrivée est le dernier élément de la liste fermée */
        Noeud tmp = listeFermee.get(objectifTemporaire.getX() + ":" + objectifTemporaire.getY());
        cheminTemporaire.add(0, tmp); //on empile au début pour avoir le chemin dans l'ordre
        Noeud prec = tmp.getParent();
        while (!(prec.getX() + ":" + prec.getY()).equals(position.getX() + ":" + position.getY())) {
            tmp = listeFermee.get(prec.getX() + ":" + prec.getY());
            prec = tmp.getParent();
            cheminTemporaire.add(0, tmp);
        }
        //on n'ajoute pas le départ car on y est déjà
        if (chemin.isEmpty() || cheminTemporaire.size() < chemin.size()) { //si le chemin est mieux ou si aucun chemin n'a encore été trouvé, on l'assigne
            chemin = cheminTemporaire;
        }
    }

    /**
     * V2: fonction qui remonte de parents en parents pour retranscrire le chemin inverse
     **/
    public static void retrouver_chemin() {
        /* l'arrivée est le dernier élément de la liste fermée */
        Noeud tmp = listeFermee.get(objectif.getX() + ":" + objectif.getY());
        chemin.add(0, tmp); //on empile au début pour avoir le chemin dans l'ordre
        Noeud prec = tmp.getParent();
        while (!(prec.getX() + ":" + prec.getY()).equals(position.getX() + ":" + position.getY())) {
            tmp = listeFermee.get(prec.getX() + ":" + prec.getY());
            prec = tmp.getParent();
            chemin.add(0, tmp);
        }
        //on n'ajoute pas le départ car on y est déjà
    }

    /**
     * passe la case et son noeud de la liste ouverte à la liste fermée
     **/
    public static void ajouter_liste_fermee(String p) {
        Noeud n = listeOuverte.get(p);
        listeFermee.put(p, n);
        /* il faut le supprimer de la liste ouverte, ce n'est plus une solution explorable */
        listeOuverte.remove(p);
    }

    /**
     * recupère le meilleur noeud de la liste ouverte
     **/
    public static String meilleur_noeud(HashMap<String, Noeud> liste) {
        //TODO: passer à des collections ordonnés et triées : accélère la recherche (les HashMap sont des collections qui ne garantissent pas l'ordre
        HashMap.Entry<String, Noeud> entry = liste.entrySet().iterator().next(); //premier élément
        double curCoutf = entry.getValue().getCoutF();
        String cleNoeud = entry.getKey();
        for (HashMap.Entry<String, Noeud> entry2 : liste.entrySet()) { //on parcours pour récupérer le meilleur
            if (entry2.getValue().getCoutF() < curCoutf) {
                curCoutf = entry2.getValue().getCoutF();
                cleNoeud = entry2.getKey();
            }
        }
        return cleNoeud;
    }

    /**
     * calcule la distance entre les points (x1,y1) et (x2,y2)
     **/
    public static double distance(int x1, int y1, int x2, int y2) {
        /* distance euclidienne */
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        /* carré de la distance euclidienne pour avoir un nombre rond(passer à des int), les nombres ronds sont moins lourds à manipuler */
        /* return (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2); */
    }

    /**
     * test si la case fait déjà partie de la liste
     **/
    public static boolean deja_present_dans_liste(String c, HashMap<String, Noeud> l) {
        return l.containsKey(c);
    }
}
