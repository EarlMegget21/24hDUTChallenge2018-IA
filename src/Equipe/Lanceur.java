package Equipe;

public class Lanceur {
    public Lanceur(){

    }

    public static void jouerTour(){
        //TODO: tester le compromis entre proximité et points rapportés
        trouverCible(); //j'ai choisi celle qui rapporte le plus de points, même si elle est à l'opposé de la map

        /* initialisation de la case courante */
        String courant=position.getX()+":"+position.getY();

        /* ajout de courant dans la liste ouverte */
        listeOuverte.put(courant, position);
        ajouter_liste_fermee(courant);
        ajouter_cases_adjacentes(position);

        /* tant que la destination n'a pas été atteinte et qu'il reste des noeuds à explorer dans la liste ouverte */
        while( !(courant.equals(objectif.getX()+":"+objectif.getY()))
                &&
                !listeOuverte.isEmpty()
                ){

            /* on cherche le meilleur noeud de la liste ouverte, on sait qu'elle n'est pas vide donc il existe */
            courant = meilleur_noeud(listeOuverte);

            /* on le passe dans la liste fermee, il ne peut pas déjà y être */
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
}
