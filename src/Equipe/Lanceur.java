package src.Equipe;

import java.util.ArrayList;
import java.util.HashMap;

import src.Client;
import src.Algo.Noeud;

public class Lanceur {
	
    public HashMap<String, Noeud> listeOuverte = new HashMap<String, Noeud>();
    public HashMap<String, Noeud> listeFermee = new HashMap<String, Noeud>();
    public ArrayList<Noeud> chemins=new ArrayList<Noeud>();
    public Noeud position;
    public String move=null;
    
    private int[] coord; //[0] = x et [1] = y et [2] = fruit (0 a 3 : c'est un fruit; 4 : c'est une chataigne; -1 : pas de fruit)

    public Lanceur(int[] coord) {
        this.coord = coord;
        position=new Noeud();
    }

    public String getAction() {
        if (coord[2] >= 0) { //on a un fruit
            String x = homeThrow();
            if (x != null) {
                return x;
            } else {
                return moveTowardHome();
            }
        } else { //on a rien
            String x = collect(); //si la case actuelle ne contient pas de fruit alors on move à la prochaine case du chemin
            
            if (x != null) {
                return x;
            } else {
                return moveToClosestObjective();
            }
        }
    }

    /** fonction qui permet de retourner a la maison **/
    public String moveTowardHome() {
    	//on creer les deux noeuds des extremitees de la maison
        Noeud n1 = new Noeud();
        n1.setX(Client.home[0][0]);
        n1.setY(Client.home[0][1]);
        Noeud n2 = new Noeud();
        n2.setX(Client.home[1][0]);
        n2.setY(Client.home[1][1]);
        //on recupere le chemin le plus court
        jouerTour(n1);
        jouerTour(n2);
        //on renvoie l'action a faire au tour
        int x = position.getX();
        int y = position.getY();
        if(chemins.get(0).getX()==x+1){ //on peut tester qu'une coordonnÃ©e parce que la prochaine case est forcÃ©ment collÃ©e Ã  nous
            move="E";
        	return "E";
        }else if(chemins.get(0).getX()==x-1){
        	move="O";
        	return "O";
        }else{
            if(chemins.get(0).getY()==y+1){
            	move="S";
            	return "S";
            }else if(chemins.get(0).getY()==y-1){
            	move="N";
            	return "N";
            }else{
            	move="X";
                System.err.println("Bug");
                return "X";
            }
        }
    }

    /** avancer sur la prochaine case du chemin le plus proche avec a* **/
    public String moveToClosestObjective() {
    	boolean pasBloque=false; //indique si le joueur est bloque dans un coin par les autres
    	for(int[] donnees : Client.listeObjectifs){ //Parcourir les objectifs(fruits) declares dans le client et les parcourir pour utiliser jouerTour(e)
    		if(donnees[0]==coord[0] && donnees[1]==coord[1]){ //si l'objectif se trouve sur la meme case que le joueur alors on l'ignore
    			continue;
    		}
    		//creation du noeud
    		Noeud noeud = new Noeud();
    		noeud.setX(donnees[0]);
    		noeud.setY(donnees[1]);
    		//calcul du chemin et attribution du meilleur chemin (fruit le plus proche)
    		pasBloque=jouerTour(noeud);
    		listeFermee=new HashMap<String,Noeud>(); //reinitialisation des listes
    		listeOuverte=new HashMap<String,Noeud>();
    	}

        int x = position.getX();
        int y = position.getY();
        if(!pasBloque){ //si le joueur est bloque, il bouge pas a ce tour
            System.err.println("Bloque");
            move="X";
        	return "X";
        }
        if(chemins.get(0).getX()==x+1){ //on peut tester qu'une coordonnee parce que la prochaine case est forcement collee a nous
        	move="E";
            return "E";
        }else if(chemins.get(0).getX()==x-1){
        	move="O";
        	return "O";
        }else{
            if(chemins.get(0).getY()==y+1){
            	move="S";
            	return "S";
            }else if(chemins.get(0).getY()==y-1){
            	move="N";
            	return "N";
            }else{
                System.err.println("Bug");
                move="X";
                return "X";
            }
        }
    }

    /** fonction qui recupere le fruit sur la case courante **/
    public String collect() {
    	try{ //il y a un element sur notre case
    		int caseContent = Integer.parseInt(Client.map[coord[1]][coord[0]]); //on recupere le fruit
    		if (caseContent < 4) { //si ce n'est pas un chataigne on le prend
                if(coord[2]>=0){ //si on avait deja un fruit alors on le depose pour echanger
					Client.listeObjectifs.add(coord); //on l'ajoute a la liste des objectifs pour les autres joueurs
                }
                int[] temp=null;
                for(int[] obj : Client.listeObjectifs){ //on parcours les objectifs pour retirer le fruit qui est a notre case
                	if(obj[0]==coord[0] && obj[1]==coord[1]){ // pas obj.equals(coord) car la troisieme case du tableau n'est pas pareil
                		temp=obj;
                		break;
                	}
                }
                if(temp!=null){
                	Client.listeObjectifs.remove(temp); //on le retire a l'exterieur de la boucle car dedans lance une ConcurrentModificationException car modifie la boucle foreach
                }
                coord[2] = caseContent; //on redefinit le fruit actuel dans l'inventaire
                Client.map[coord[0]][coord[1]]=".";
                move="X";
                return "P"; //on retourne la commande pour prendre le fruit
            } else { //si c'est une chataign on la prend pas
                return null; //TODO: retourner une fontion chataigne qui retourne null et qui est redefinit dans la classe QuarterBack pour la prendre et retourner une commande
            }
    	}catch(NumberFormatException e){ //si c'est vide "."
			return null;
		}
        
    }

    /** algorithme a* qui joue un tour en prenant la cible la plus proche **/
    public boolean jouerTour(Noeud objectifTemporaire) {
        /* initialisation de la case courante */
    	position.setX(coord[0]);
    	position.setY(coord[1]);
        String courant = coord[0] + ":" + coord[1];
        
        /* ajout de courant dans la liste ouverte */
        listeOuverte.put(courant, position);
        
        /* passage listeOuverte -> listeFermee */
        ajouter_liste_fermee(courant);
        
        /* ajoute les voisins a listeOuverte pour traitement */
        ajouter_cases_adjacentes(position, objectifTemporaire);
        
        /* tant que la destination n'a pas ete atteinte et qu'il reste des noeuds a explorer dans la liste ouverte */
        while (!(courant.equals(objectifTemporaire.getX() + ":" + objectifTemporaire.getY()))
                &&
                !listeOuverte.isEmpty()
                ) {
            /* on cherche le meilleur noeud de la liste ouverte, on sait qu'elle n'est pas vide donc il existe */
            courant = meilleur_noeud(listeOuverte);
            /* on le passe dans la liste fermee, il ne peut pas dÃ©jÃ  y Ãªtre */
            ajouter_liste_fermee(courant);
            /* on recommence la recherche des noeuds adjacents */
            ajouter_cases_adjacentes(listeFermee.get(courant), objectifTemporaire);
        }
        
        /* si la destination est atteinte, on remonte le chemin */
        if (courant.equals(objectifTemporaire.getX() + ":" + objectifTemporaire.getY())) {
        	/* si une NullPointerException se leve c'est que un des noeuds n'a pas de parent 
        	 (generalement le noeud de depart, on essaie donc de recuperer un chemin contenant que la case ou on est deja sauf 
        	 que cette case est censee etre exclu du chemin de base */
            retrouver_chemin_proche(objectifTemporaire);
            
            return true; //on a bien un chemin
        } else {
            System.err.println("Pas de solution");
            return false; //on a parcouru tous les noeuds(cases) et on n'arrive jamais au point de depart
        }
    }

    /** parcours les cases adjacentes pour ajouter la bonne **/
    public void ajouter_cases_adjacentes(Noeud current, Noeud objectifTemporaire) {
        Noeud noeud;
        /* on met tous les noeud adjacents dans la liste ouverte (+verif) */
        for (int i = current.getX() - 1; i <= current.getX() + 1; i++) {
            if ((i <= 0) || (i >= Client.largeur-1))  /* en dehors de l'image, on oublie */
                continue;
            for (int j = current.getY() - 1; j <= current.getY() + 1; j++) {
                if ((j <= 0) || (j >= Client.hauteur-1))   /* en dehors de l'image, on oublie */
                    continue;
                if ((i == current.getX()) && (j == current.getY()))  /* case actuelle current, on oublie */
                    continue;
                if ((i == current.getX() + 1 && (j == current.getY() + 1 || j == current.getY() - 1)) //cases en travers en haut
                        ||
                        (i == current.getX() - 1 && (j == current.getY() + 1 || j == current.getY() - 1))) //cases en travers en bas
                    continue;
                
                if (Client.map[j][i].equals("X"))
                    /* obstace, terrain non franchissable, on oublie */
                    continue;
                
                boolean jbloque=false;
                for(Lanceur l : Client.equipe){
                	if(l.getCoord()[0]==i && l.getCoord()[1]==j ){
                		/* joueur colle a nous nous empechant d'avancer on oublie */
                		jbloque=true;
                	}
                }
                if(jbloque){
                	continue;
                }
                
                String voisin = i + ":" + j; //on a un voisin valide
            	
                if (!deja_present_dans_liste(voisin, listeFermee)) {
                    /* le noeud n'est pas deja  present dans la liste fermee */
                    noeud = new Noeud();
                    
                    /* calcul du cout G du noeud en cours d'etude : cout du parent + distance jusqu'au parent */
                    noeud.setCoutG(listeFermee.get(current.getX() + ":" + current.getY()).getCoutG() + distance(i, j, objectifTemporaire.getX(), objectifTemporaire.getY()));
                    
                    /* calcul du cout H du noeud a la destination */
                    noeud.setCoutH(distance(i, j, objectifTemporaire.getX(), objectifTemporaire.getY()));
                    noeud.setCoutF(noeud.getCoutG() + noeud.getCoutH());
                    noeud.setParent(current);
                    noeud.setX(i);
                    noeud.setY(j);
                    if (deja_present_dans_liste(voisin, listeOuverte)) {
                        /* le noeud est deja  present dans la liste ouverte, il faut comparer les couts */
                        if (noeud.getCoutF() < listeOuverte.get(voisin).getCoutF()) {
                            /* si le nouveau chemin est meilleur, on met a jour */
                            listeOuverte.put(voisin, noeud);
                        }
                        /* sinon le noeud courant a un moins bon chemin, on ne change rien */
                    } else {
                        /* le noeud n'est pas present dans la liste ouverte, on l'y ajoute */
                        listeOuverte.put(voisin, noeud);
                    }
                }
            }
        }
    }

    /** fonction qui remonte de parents en parents pour retranscrire le chemin inverse de l'objectif jusqu'a
     * nous et assigne le meilleur chemin (plus proche) **/
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

        //on n'ajoute pas le depart car on y est deja
        if (chemins.isEmpty() || cheminTemporaire.size() < chemins.size()) { //si le chemin est mieux ou si aucun chemin n'a encore ete trouve, on l'assigne
            chemins = cheminTemporaire;
        }

    }

    /** passe la case et son noeud de la liste ouverte a la liste fermee **/
    public void ajouter_liste_fermee(String p) {
        Noeud n = listeOuverte.get(p);
        listeFermee.put(p, n);
        /* il faut le supprimer de la liste ouverte, ce n'est plus une solution explorable */
        listeOuverte.remove(p);
    }

    /** recupere le meilleur noeud de la liste ouverte **/
    public static String meilleur_noeud(HashMap<String, Noeud> liste) {
        HashMap.Entry<String, Noeud> entry = liste.entrySet().iterator().next(); //premier element
        double curCoutf = entry.getValue().getCoutF();
        String cleNoeud = entry.getKey();
        for (HashMap.Entry<String, Noeud> entry2 : liste.entrySet()) { //on parcours pour recuperer le meilleur
            if (entry2.getValue().getCoutF() < curCoutf) {
                curCoutf = entry2.getValue().getCoutF();
                cleNoeud = entry2.getKey();
            }
        }
        return cleNoeud;
    }

    /** calcule la distance entre les points (x1,y1) et (x2,y2) **/
    public static double distance(int x1, int y1, int x2, int y2) {
        /* distance euclidienne */
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        /* carre de la distance euclidienne pour avoir un nombre rond(passer a des int), les nombres ronds sont moins lourds aa manipuler */
        /* return (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2); */
    }

    /** test si la case fait deja partie de la liste **/
    public static boolean deja_present_dans_liste(String c, HashMap<String, Noeud> l) {
        return l.containsKey(c);
    }
    
    /** fonction qui envoie un fruit a la maison
  		Client.home doit être de la forme : ((1,2),(2,1))
  		on stock que les deux cases aux extremitees de la
  		maison car on va lancer les fruits de loin **/
    private String homeThrow() {

      int[] case1 = new int[2]; //on part du principe que posHome est un tableau comportant 2 cases, chaque case étant un tableau de deux index : x et y
      int[] case2 = new int[2];
      case1[0]= Client.home[0][0];// le x de la case 1 est le premier index du premier index
      case1[1]= Client.home[0][1];//le y de la case 1 est le deuxième index du premier index
      case2[0]= Client.home[1][0];//le x de la case 2 est le premier index du deuxième index
      case2[1]= Client.home[1][1];//le y de la case 2 est le deuxième index du deuxième index
  	
      int distance; //permet de vérifier si la distance est <=4
    	  
		if(coord[0]==case1[0]) { // si le joueur est aligné à case 1 verticalement
		
		  distance =  coord[1]-case1[1]; //distance positive si le joueur est en dessus de la case (maison en bas de la map) et vice versa
		  
		  if(Math.abs(distance) <= 4) { //si la maison est atteignable au lancer
			  
		    boolean obstacle = obstacle(case1); //on definit si un obstacle est present entre nous et la maison
		    
		    if(obstacle) {
		      if(distance<0){ //si la distance est négative, cad le joueur est placé plus haut(x moins grand) que la case maison
		        //apres le lancer on remet l'inventaire du joueur a l'inital (pas besoin normalement 
		        //car les variables sont reinitialisees a chaque tour donc apres chaque envoi d'un action au serveur
		    	  coord[2]=-1; 
		    	  return "LS"; // alors on lance vers le bas ( vu qu'on est plus haut)
		      }
		      if(distance>0){ //si la distance est positive, cad le joueur est placé plus bas(x plus grand) que la case maison
		    	  coord[2]=-1;
		    	  return "LN";//alors on lance vers le haut(vu qu'on est plus bas)
		      }
		    }
		  }
		}
		
		if(coord[1]==case1[1]){ // si le joueur est aligné à case 1 horizontalement

		  distance =  coord[0]-case1[0];
		  
		  if(Math.abs(distance) <= 4){
			  
		    boolean obstacle = obstacle(case1);
		    
		    if(obstacle) {
		    	
		      if(distance<0){ //si la distance est négative, cad le joueur est placé plus a gauche(x moins grand) que la case maison
		    	  coord[2]=-1;
		    	  move="X"; //signifie qu'au prochain tour le joueur sera sur la meme place
		    	  return "LE"; // alors on lance vers la droite ( vu qu'on est plus a gauche)
		      }
		      if(distance>0){ //si la distance est positive, cad le joueur est placé plus à droite(x plus grand) que la case maison
		    	  coord[2]=-1;
		    	  move="X";
		    	  return "LO";//alors on lance vers la gauche(vu qu'on est plus a droite)
		      }
		    }
		  }
		}
		  
		if(coord[0]==case2[0]) { // si le joueur est aligné à case 2 verticalement
			
		  distance =  coord[1]-case2[1];
		  if(Math.abs(distance) <= 4) {
			  
		    boolean obstacle = obstacle(case2);
		    if(obstacle) {
		      if(distance<0) { //si la distance est négative, cad le joueur est placé plus haut(x moins grand) que la case maison
		    	  coord[2]=-1;
		    	  move="X";
		        return "LS"; // alors on lance vers le bas ( vu qu'on est plus haut)
		      }
		      
		      if(distance>0) { //si la distance est positive, cad le joueur est placé plus bas(x plus grand) que la case maison
		    	  coord[2]=-1;
		    	  move="X";
		        return "LN";//alors on lance vers le haut (vu qu'on est plus bas)
		      }
		    }
		  }
		}
		
		if(coord[1]==case2[1]) {// si le joueur est aligné à case 1 horizontalement
			
		  distance =  coord[0]-case2[0];
		  
		  if(Math.abs(distance) <= 4) {
			  
		    boolean obstacle = obstacle(case2);
		    if(obstacle){
		      if(distance<0){ //si la distance est négative, cad le joueur est placé plus a gauche(x moins grand) que la case maison
		    	  coord[2]=-1;
		    	  move="X";
		    	  return "LE"; // alors on lance vers la droite ( vu qu'on est plus a gauche)
		      }
		      if(distance>0){ //si la distance est positive, cad le joueur est placé plus à droite(x plus grand) que la case maison
		    	  coord[2]=-1;
		    	  move="X";
		    	  return "LO";//alors on lance vers la gauche(vu qu'on est plus à)
		      }
		    }
		  }
		}
        
      return null; //par default
    }

    /** methode qui indique si il y a un obstacle entre le joueur et la case en parametre 
       retourne un boolean, true si le chemin ne comporte pas d'obstacle et false sinon **/
    public boolean obstacle(int[] box) {
    	
      if(coord[0]==box[0]) { //si les cases sont alignees verticalement (meme x)
    	  
        int i = coord[0];
        if(coord[1]-box[1] <0){ //si la distance est <0 alors la case est en dessous du joueur
            for(int j=coord[1]+1; j<=box[1];j++){ //pour chaque case entre les deux
              if(!Client.map[i][j].equals(".")){ //si ce n'est pas vide alors obstacle
                return false;
              }
            }
            return true; //pas d'obstacle
            
        } else if(coord[1]-box[1] >0) { //si case au dessus du joueur
        	
            for(int j=coord[1]-1; j<=box[1];j--){
              if(!Client.map[i][j].equals(".")){
                return false;
              }
            }
            return true;
            
        } else { //par default joueur sur case donc pas d'obstacle (peut etre renvoyer false pour pas faire de lancer
        	
        	return true; //par default
        }
        
      } else if(coord[1]==box[1]) { //si alignes horizontalement
    	  
        int i = coord[1];
        if(coord[0]-box[0] <0) { //si case a droite
        	
            for(int j=coord[0]+1; j<=box[0];j++){
              if(!Client.map[j][i].equals(".")){
                return false;
              }
            }
            return true;
            
        } else if(coord[0]-box[0] >0) { //si case a gauche
        	
            for(int j=coord[0]-1; j<=box[0];j--){
              if(!Client.map[j][i].equals(".")){
                return false;
              }
            }
            return true;
            
        } else {
        	
        	return true; //par default car veut dire sur meme case
        }
        
      } else {
    	  
    	  return false; //par default false car si x et y differents alors pas de lancer car lancer se fait en ligne droite
      }
    }

    /** getters and setters **/
    
	public HashMap<String, Noeud> getListeOuverte() {
		return listeOuverte;
	}

	public HashMap<String, Noeud> getListeFermee() {
		return listeFermee;
	}

	public ArrayList<Noeud> getChemins() {
		return chemins;
	}

	public void setChemins(ArrayList<Noeud> chemins) {
		this.chemins = chemins;
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
