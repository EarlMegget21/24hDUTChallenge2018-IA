package src.Equipe;

import java.util.ArrayList;
import java.util.HashMap;

import src.Client;
import src.Algo.Noeud;

public class Lanceur {
	
    public HashMap<String, Noeud> listeOuverte = new HashMap<String, Noeud>();
    public HashMap<String, Noeud> listeFermee = new HashMap<String, Noeud>();
    public ArrayList<Noeud>[] chemins;
    public Noeud objectif;
    public Noeud position;
    
    private int[] coord; //[0] = x et [1] = y et [2] = fruit (0 a 3 : c'est un fruit; 4 : c'est une chataigne; -1 : pas de fruit)

    public Lanceur(int[] coord) {
        this.coord = coord;
        objectif=new Noeud();
        position=new Noeud();
    }

    public String getAction() {
        if (coord[2] >= 0) {
            String x = homeThrow();
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
        n2.setX(Client.home[1][0]);
        n2.setY(Client.home[1][1]);
        jouerTour(n1);
        jouerTour(n2);
        Noeud nDir = chemins[0].get(0);
        int[] dir = new int[] {
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
        return r;
    }

    public String moveToClosestObjective() {
        /* Parcourir les objectifs declare dans le client et les parcourir pour utiliser jouerTour(e) */
    	for(int[] donnees : Client.listeObjectifs){
    		Noeud noeud = new Noeud();
    		noeud.setX(donnees[0]);
    		noeud.setY(donnees[1]);
    		jouerTour(noeud);
    	}
        int x = chemins[0].get(0).getX();
        int y = chemins[0].get(0).getY();
        
        if(chemins[0].get(0).getX()==x+1){ //on peut tester qu'une coordonn√©e parce que la prochaine case est forc√©ment coll√©e √† nous
            return "E";
        }else if(chemins[0].get(0).getX()==x-1){
        	return "O";
        }else{
            if(chemins[0].get(0).getY()==y+1){
            	return "S";
            }else if(chemins[0].get(0).getY()==y-1){
            	return "N";
            }else{
                System.err.println("Bug");
                return "X";
            }
        }
    }

    public String collect() {
    	try{ //il y a un element
    		int caseContent = Integer.parseInt(Client.map[coord[0]][coord[1]]);
    		if (caseContent < 4) {
                coord[2] = caseContent;
                return "P";
            } else {
                return null;
            }
    	}catch(NumberFormatException e){ //si c'est vide "."
			return null;
		}
        
    }

    /**
     * V1: algorithme a* qui joue un tour en prenant la cible la plus proche
     **/
    public void jouerTour(Noeud objectifTemporaire) {
        //TODO: on peut mettre un compteur qui compare au fur et √† mesure la construction du chemin si il depasse pas un deja† existant, dans ce cas l√† on stopperait la recherche car √ßa ne servirait √† rien (parcours partiel)
        /* initialisation de la case courante */
    	position.setX(coord[0]);
    	position.setX(coord[1]);
        String courant = position.getX() + ":" + position.getY();
        /* ajout de courant dans la liste ouverte */
        listeOuverte.put(courant, position);
        ajouter_liste_fermee(courant);
        ajouter_cases_adjacentes(position);
        /* tant que la destination n'a pas √©t√© atteinte et qu'il reste des noeuds √† explorer dans la liste ouverte */
        while (!(courant.equals(objectifTemporaire.getX() + ":" + objectifTemporaire.getY()))
                &&
                !listeOuverte.isEmpty()
                ) {
            /* on cherche le meilleur noeud de la liste ouverte, on sait qu'elle n'est pas vide donc il existe */
            courant = meilleur_noeud(listeOuverte);
            /* on le passe dans la liste fermee, il ne peut pas d√©j√† y √™tre */
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
     * modifie les coordonn√©es de l'objectif (ici on prend la cible qui rapporte le plus de points)
     **/
    public void trouverCible() {
        int x = 1;
        int y = 1;
        int points = 0;
        for (int j = 1; j < Client.hauteur - 1; j++) { //on part de 1 et on enl√®ve 1 car les bords sont des murs
            for (int i = 1; i < Client.largeur - 1; i++) {
                try {
                    if (Integer.parseInt(Client.map[j][i]) > points) {
                        x = i;
                        y = j;
                        points = Integer.parseInt(Client.map[j][i]);
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
    public void ajouter_cases_adjacentes(Noeud current) {
        Noeud noeud;
        /* on met tous les noeud adjacents dans la liste ouverte (+v√©rif) */
        for (int i = current.getX() - 1; i <= current.getX() + 1; i++) {
            if ((i < 0) || (i >= Client.largeur))  /* en dehors de l'image, on oublie */
                continue;
            for (int j = current.getY() - 1; j <= current.getY() + 1; j++) {
                if ((j < 0) || (j >= Client.hauteur))   /* en dehors de l'image, on oublie */
                    continue;
                if ((i == current.getX()) && (j == current.getY()))  /* case actuelle current, on oublie */
                    continue;
                if ((i == current.getX() + 1 && (j == current.getY() + 1 || j == current.getY() - 1))
                        ||
                        (i == current.getX() - 1 && (j == current.getY() + 1 || j == current.getY() - 1)))
                    continue;
                if (Client.map[j][i].equals("D"))
                    /* obstace, terrain non franchissable, on oublie */
                    continue;
                String voisin = i + ":" + j; //on a un voisin valide
                if (!deja_present_dans_liste(voisin, listeFermee)) {
                    /* le noeud n'est pas d√©j√† pr√©sent dans la liste ferm√©e */
                    noeud = new Noeud();
                    /* calcul du cout G du noeud en cours d'√©tude : cout du parent + distance jusqu'au parent */
                    noeud.setCoutG(listeFermee.get(current.getX() + ":" + current.getY()).getCoutG() + distance(i, j, current.getX(), current.getY()));
                    /* calcul du cout H du noeud √† la destination */
                    noeud.setCoutH(distance(i, j, objectif.getX(), objectif.getY()));
                    noeud.setCoutF(noeud.getCoutG() + noeud.getCoutH());
                    noeud.setParent(current);
                    noeud.setX(i);
                    noeud.setY(j);
                    if (deja_present_dans_liste(voisin, listeOuverte)) {
                        /* le noeud est d√©j√† pr√©sent dans la liste ouverte, il faut comparer les couts */
                        if (noeud.getCoutF() < listeOuverte.get(voisin).getCoutF()) {
                            /* si le nouveau chemin est meilleur, on met √† jour */
                            listeOuverte.put(voisin, noeud);
                        }
                        /* sinon le noeud courant a un moins bon chemin, on ne change rien */
                    } else {
                        /* le noeud n'est pas pr√©sent dans la liste ouverte, on l'y ajoute */
                        listeOuverte.put(voisin, noeud);
                    }
                }
            }
        }
    }

    /**
     * V1: fonction qui remonte de parents en parents pour retranscrire le chemin inverse de l'objectif jusqu'√† nous
     * et assigne le meilleur chemin (plus proche)
     **/
    public void retrouver_chemin_proche(Noeud objectifTemporaire) {
        //creation du chemin a tester
        ArrayList<Noeud> cheminTemporaire = new ArrayList<Noeud>();
        /* l'arrivee est le dernier element de la liste fermee */
        Noeud tmp = listeFermee.get(objectifTemporaire.getX() + ":" + objectifTemporaire.getY());
        cheminTemporaire.add(0, tmp); //on empile au debut pour avoir le chemin dans l'ordre
        Noeud prec = tmp.getParent();
        while (!(prec.getX() + ":" + prec.getY()).equals(position.getX() + ":" + position.getY())) {
            tmp = listeFermee.get(prec.getX() + ":" + prec.getY());
            prec = tmp.getParent();
            cheminTemporaire.add(0, tmp);
        }
        //on n'ajoute pas le d√©part car on y est deja
        if (chemins[0].isEmpty() || cheminTemporaire.size() < chemins[0].size()) { //si le chemin est mieux ou si aucun chemin n'a encore ete trouve, on l'assigne
            chemins[0] = cheminTemporaire;
        }
    }

    /**
     * passe la case et son noeud de la liste ouverte a la liste fermee
     **/
    public void ajouter_liste_fermee(String p) {
        Noeud n = listeOuverte.get(p);
        listeFermee.put(p, n);
        /* il faut le supprimer de la liste ouverte, ce n'est plus une solution explorable */
        listeOuverte.remove(p);
    }

    /**
     * recupere le meilleur noeud de la liste ouverte
     **/
    public static String meilleur_noeud(HashMap<String, Noeud> liste) {
        //TODO: passer √† des collections ordonn√©s et tri√©es : acc√©l√®re la recherche (les HashMap sont des collections qui ne garantissent pas l'ordre
        HashMap.Entry<String, Noeud> entry = liste.entrySet().iterator().next(); //premier √©l√©ment
        double curCoutf = entry.getValue().getCoutF();
        String cleNoeud = entry.getKey();
        for (HashMap.Entry<String, Noeud> entry2 : liste.entrySet()) { //on parcours pour r√©cup√©rer le meilleur
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
        /* carr√© de la distance euclidienne pour avoir un nombre rond(passer √† des int), les nombres ronds sont moins lourds √† manipuler */
        /* return (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2); */
    }

    /**
     * test si la case fait deja partie de la liste
     **/
    public static boolean deja_present_dans_liste(String c, HashMap<String, Noeud> l) {
        return l.containsKey(c);
    }
    
 // posHome doit Ítre de la forme :  ((1,2),(2,1))
  //posJoueur : (1,2);
    private String homeThrow() {
    	
      int[] case1 = new int[2]; //on part du principe que posHome est un tableau comportant 2 cases, chaque case Ètant un tableau de deux index : x et y
      int[] case2 = new int[2];
      case1[0]= Client.home[0][0];// le x de la case 1 est le premier index du premier index
      case1[1]= Client.home[0][1];//le y de la case 1 est le deuxiËme index du premier index
      case2[0]= Client.home[1][0];//le x de la case 2 est le premier index du deuxiËme index
      case2[1]= Client.home[1][1];//le y de la case 2 est le deuxiËme index du deuxiËme index

      int distance; //permet de vÈrifier si la distance est <=4

      if(coord[0] == case1[0] || coord[1]==case1[1]){ // si le joueur est alignÈ ‡ la case 1
    	  
        if(coord[0]==case1[0]) { // si le joueur est alignÈ ‡ case 1 et dans la mÍme colonne que la case 1
        	
          distance =  coord[1]-case1[1];
          
          if(Math.abs(distance) <= 4) {
        	  
            boolean obstacle = obstacle(case1);
            
            if(obstacle) {
            	
              if(distance<0){ //si la distance est nÈgative, cad le joueur est placÈ plus haut(x moins grand) que la case maison
                return "S"; // alors on lance vers le bas ( vu qu'on est plus haut)
              }
              if(distance>0){ //si la distance est positive, cad le joueur est placÈ plus bas(x plus grand) que la case maison
                return "N";//alors on lance vers le haut(vu qu'on est plus bas)
              }
            }
          }
        }
        
        if(coord[1]==case1[1]){ // si le joueur est alignÈ ‡ case 1 et dans la mÍme ligne que la case 1
          distance =  coord[0]-case1[0];
          if(Math.abs(distance) <= 4){
            boolean obstacle = obstacle(case1);
            if(obstacle){
              if(distance<0){ //si la distance est nÈgative, cad le joueur est placÈ plus a gauche(x moins grand) que la case maison
                return "E"; // alors on lance vers la droite ( vu qu'on est plus a gauche)
              }
              if(distance>0){ //si la distance est positive, cad le joueur est placÈ plus ‡ droite(x plus grand) que la case maison
                return "O";//alors on lance vers la gauche(vu qu'on est plus ‡)
              }
            }
          }
        }
        
      } else if(coord[0] == case2[0] || coord[1]==case2[1]) { // si le joueur est alignÈ ‡ la case 2
    	  
        if(coord[0]==case2[0]) { // si le joueur est alignÈ ‡ case 2 et dans la mÍme colonne que la case 2
        	
          distance =  coord[1]-case2[1];
          if(Math.abs(distance) <= 4) {
        	  
            boolean obstacle = obstacle(case2);
            if(obstacle) {
              if(distance<0) { //si la distance est nÈgative, cad le joueur est placÈ plus haut(x moins grand) que la case maison
            	  
                return "S"; // alors on lance vers le bas ( vu qu'on est plus haut)
              }
              
              if(distance>0) { //si la distance est positive, cad le joueur est placÈ plus bas(x plus grand) que la case maison
            	  
                return "N";//alors on lance vers le haut(vu qu'on est plus bas)
              }
            }
          }
        }
        
        if(coord[1]==case2[1]) {// si le joueur est alignÈ ‡ case 1 et dans la mÍme ligne que la case 1
        	
          distance =  coord[0]-case2[0];
          
          if(Math.abs(distance) <= 4) {
        	  
            boolean obstacle = obstacle(case2);
            if(obstacle){
              if(distance<0){ //si la distance est nÈgative, cad le joueur est placÈ plus a gauche(x moins grand) que la case maison
                return "E"; // alors on lance vers la droite ( vu qu'on est plus a gauche)
              }
              if(distance>0){ //si la distance est positive, cad le joueur est placÈ plus ‡ droite(x plus grand) que la case maison
                return "O";//alors on lance vers la gauche(vu qu'on est plus ‡)
              }
            }
          }
        }
        
      }
      return null; //par default
    }

  //necessite l'import de CLient.java
    public boolean obstacle(int[] box) { //retourne un boolean, true si le chemin ne comporte pas d'obstacle et false sinon
    	
      if(coord[0]==box[0]) {
    	  
        int i = coord[0];
        if(coord[1]-box[1] <0){
            for(int j=coord[1]+1; j<=box[1];j++){
              if(!Client.map[i][j].equals(".")){
                return false;
              }
            }
            return true;
            
        } else if(coord[1]-box[1] >0) {
        	
            for(int j=coord[1]-1; j<=box[1];j--){
              if(!Client.map[i][j].equals(".")){
                return false;
              }
            }
            return true;
            
        } else {
        	
        	return true; //par default
        }
        
      } else if(coord[1]==box[1]) {
    	  
        int i = coord[1];
        if(coord[0]-box[0] <0) {
        	
            for(int j=coord[0]+1; j<=box[0];j++){
              if(!Client.map[j][i].equals(".")){
                return false;
              }
            }
            return true;
            
        } else if(coord[0]-box[0] >0) {
        	
            for(int j=coord[0]-1; j<=box[0];j--){
              if(!Client.map[j][i].equals(".")){
                return false;
              }
            }
            return true;
            
        } else {
        	
        	return true; //par default
        }
        
      } else {
    	  
    	  return true; //par default
      }
    }

	public HashMap<String, Noeud> getListeOuverte() {
		return listeOuverte;
	}

	public void setListeOuverte(HashMap<String, Noeud> listeOuverte) {
		listeOuverte = listeOuverte;
	}

	public HashMap<String, Noeud> getListeFermee() {
		return listeFermee;
	}

	public void setListeFermee(HashMap<String, Noeud> listeFermee) {
		listeFermee = listeFermee;
	}

	public ArrayList<Noeud>[] getChemins() {
		return chemins;
	}

	public void setChemins(ArrayList<Noeud>[] chemins) {
		this.chemins = chemins;
	}

	public Noeud getObjectif() {
		return objectif;
	}

	public void setObjectif(Noeud objectif) {
		this.objectif = objectif;
	}

	public Noeud getPosition() {
		return position;
	}

	public void setPosition(Noeud position) {
		this.position = position;
	}

	public int[] getCoord() {
		return coord;
	}

	public void setCoord(int[] coord) {
		this.coord = coord;
	}
    
    
}
