package src.Equipe;

import src.Client;

import java.util.ArrayList;
import java.util.Arrays;

public class Quarterback extends Lanceur {

	public Quarterback(int[] coord) {
		super(coord);
	}
	
	
	@Override
	public String getAction(){
		
		if(getCoord()[2] == 4){ //si il possede une chataigne
			String x = null;
			ArrayList<int[]> adversaires=new ArrayList<>();
			for(int[][] equipe : Client.autresEquipes){
				x = targetThrow(Arrays.copyOfRange(equipe, 1, 3)); //il la lance sur un des deux lanceurs si il est à distance car les QB interceptent
				adversaires.addAll(Arrays.asList(Arrays.copyOfRange(equipe, 1, 3))); //on ajoute les joueurs de l'equipe à la liste des joueurs adverses
			}
			if(x != null){ //si il lance a un des joueurs adverse
				return x;
			}else{
				String w=moveToClosestObjective(adversaires); //on va vers un adversaire
				return w;
			}
		}else{
			String c=super.getAction(); //sinon on joue normalement
			return c;
		}
		
	}
	
	
	@Override
	public String collectChataigne(int caseContent){
		if(getCoord()[2] >= 0){ //si on avait deja un fruit alors on le depose pour echanger
			Client.listeObjectifs.add(getCoord()); //on l'ajoute a la liste des objectifs pour les autres joueurs
		}
		int[] temp = null;
		for(int[] obj : Client.listeObjectifs){ //on parcours les objectifs pour retirer le fruit qui est a notre case
			if(obj[0] == getCoord()[0] && obj[1] == getCoord()[1]){ // pas obj.equals(coord) car la troisieme case du tableau n'est pas pareil
				temp = obj;
				break;
			}
		}
		if(temp != null){
			Client.listeObjectifs.remove(temp); //on le retire a l'exterieur de la boucle car dedans lance une ConcurrentModificationException car modifie la boucle foreach
		}
		getCoord()[2] = caseContent; //on redefinit le fruit actuel dans l'inventaire
		Client.map[getCoord()[0]][getCoord()[1]] = ".";
		move = "X";
		return "P"; //on retourne la commande pour prendre le fruit
	}
	
	
	@Override
	public boolean playable(int[] donnees){
		//si l'objectif se trouve sur la meme case que le joueur alors on l'ignore
		if(donnees[0] == getCoord()[0] && donnees[1] == getCoord()[1]){
			return false;
		}else{ //ici on accepte les chataignes
			if(Client.autresEquipes.size()>0){ //on autorise les chataignes uniquement si il y a d'autres equipes adverses
				return true;
			}else{
				if(donnees[2]==4){ //si c'est une chataqigne alors pas jouable
					return false;
				}else{
					return true;
				}
			}
		}
	}
}
