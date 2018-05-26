package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import src.Algo.Noeud;
import src.Equipe.Lanceur;
import src.Equipe.Quarterback;

public class Client {

	public static int PORT; //port par default (indique dans l'enonce)
	public static BufferedReader ins; //Flux d'entree
	public static PrintWriter outs; //Flux de sortie
	
	public static String data; // donnees recus par le serveur
	public static String[][] map; //tableau representant la ma	
	public static int largeur, hauteur; //dimensions
	public static boolean premierTour; //indiquant si c'est le premier tour ou non
	public static int num; // numero d'equipe
	public static int nbEquipes; // nombre d'equipes
	public static ArrayList<int[][]> autresEquipes= new ArrayList<int[][]>(); //liste car on connait pas le nombre d'equipe
	public static Lanceur[] equipe = new Lanceur[3]; //notre equipe
	public static int[][] home = new int[2][2]; //les deux cases extremite de la maison (chaque case : {x, y}
	
	public static ArrayList<int[]> listeObjectifs=new ArrayList<int[]>(); //stock les x et y de tous les objectifs
	
	/** Main qui joue au jeu **/
	public static void main(String[] args) throws Exception {
		PORT=Integer.parseInt(args[1]);
		/* Initialisation connexion Socket */
		Socket s = new Socket(args[0], PORT);
		System.out.println("STARTClient");
		ins = new BufferedReader(
				new InputStreamReader(s.getInputStream()) );
		outs = new PrintWriter( new BufferedWriter(
				new OutputStreamWriter(s.getOutputStream())), true);
		outs.println("L'equipe du sale");
		
		//lecture du code de retour qui indique le numero d'equipe
		try {
			num = Integer.parseInt(ins.readLine());
		} catch (NumberFormatException e1) {
			System.err.println("code de retour du serveur pas int");
			e1.printStackTrace();
			System.exit(1);
		} catch (IOException e1) {
			System.err.println("probleme de lecture");
			e1.printStackTrace();
			System.exit(1);
		}
		
		/* Fin connexion Socket */
		
		data=ins.readLine();
		
		while(!data.equals("FIN")){
			
			parseReponse(data); //on parse la reponse du serveur
			
			String action="";

			for(int index=0; index < equipe.length; index++){ //pour chaque joueur
				action+=equipe[index].getAction(); // on recupere son action
				if(index >= 3){ //si c'est le dernier joueur on met le "\n"
					action+="\n";
				}else{
					action+="-"; //sinon on met le separateur "-"
				}
			}
			
			outs.println(action);
			data=ins.readLine();
			
			for(int index=0; index < equipe.length; index++){ //pour chaque joueur
				equipe[index].listeOuverte = new HashMap<String, Noeud>();
				equipe[index].listeFermee = new HashMap<String, Noeud>();
				equipe[index].chemins=new ArrayList<Noeud>();
			}
			listeObjectifs=new ArrayList<int[]>();
		}
		
		/* Fermeture des flux */
		try {
			ins.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		outs.close();
		s.close();
		System.out.println("Fin du jeu!");
		/* Fin des fermeture des flux */
	}
	
	/** parse la reponse du serveur **/
	public static void parseReponse(String reponse){
		String[] infos; //apres le split
		String[] infosMap; //apres le split de chaque ligne de la map
		String[] ligne; //une ligne avec chaque case : String avec . ou X ou 0-4
		
		/* Recuperation des infos envoyees par le serveur */
		infos=data.split("_");
		
		//nombre d'equipes
		nbEquipes=Integer.parseInt(infos[1]);
		
		//infos de la map
		infosMap=infos[2].split(",");
		
		//dimensions
		largeur=Integer.parseInt(infosMap[0].split(":")[0]);
		hauteur=Integer.parseInt(infosMap[0].split(":")[1]);
		
		//convertion du String en tableau de String � deux dimensions pour le plateau de jeu (on aurait pu faire a une seule dimension avec String.toArray())
		if(premierTour){ //pour n'allouer un nouveau tableau qu'au premier tour car les dimensions ne changent pas
			map=new String[hauteur][];
			premierTour=false;
		}
		
		//on creer la map
		map=new String[hauteur][largeur];
		for(int j=1; j<=hauteur; j++){ //pour chaque ligne (la premiere case : dimensions)
			ligne=new String[largeur];
			for(int i=0; i<largeur; i++){ //pour chaque case dans la ligne
				ligne[i]=infosMap[j].substring(i, i+1);
				
				try{
					int type = Integer.parseInt(ligne[i]); //si ca ne leve pas d'exception alors c'est un objectif
					if(type<4)
						listeObjectifs.add(new int[]{i, j-1, type}); //on ajoute ses coordonnees a la liste des objectifs
				}catch(NumberFormatException e){
					//ce n'est pas un objectif
				}
				//fin version objectif le plus proche
			}
			map[j-1]=ligne;
		}
		
		//on creer notre equipe et les equipes adverses
		for(int i = 3; i < nbEquipes+3 ; i++) {
			
			String[] parse = infos[i].split(","); //parse des equipes
			String[] joueur;
			int[] parametres;
			Lanceur[] team=new Lanceur[3];
			if(num == i-3 ) { //c'est notre equipe
				for(int j=2;j<5;j++){
					parametres = new int[3];
					joueur = parse[j].split(":");
					parametres[0] = Integer.parseInt(joueur[1]);
					parametres[1] = Integer.parseInt(joueur[2]);
					if(joueur[3].equals("x")){
						parametres[2]=-1;
					}else{
						parametres[2]=Integer.parseInt(joueur[3]);
					}
					
					if(j==2){
						Quarterback quarterBack = new Quarterback(parametres);
						equipe[j-2]=quarterBack;
					}else{
						Lanceur lanceur = new Lanceur(parametres);
						equipe[j-2]=lanceur;
					}
					team=equipe;
				}
				for(int j=6;j<=8;j++){
					joueur = parse[j].split(":");
					if(j==6){
						home[0][0] = Integer.parseInt(joueur[1]);
						home[0][1] = Integer.parseInt(joueur[2]);
					}
					if(j==8){
						home[1][0] = Integer.parseInt(joueur[1]);
						home[1][1] = Integer.parseInt(joueur[2]);
					}
				}
				
			} else { //c'est une equipe adverse
				
				int[][] equipeAdverse=new int[3][3]; //tableau de 3 joueurs (un joueur : {x, y, fruit} )
				
				for(int j = 3 ; j<5 ; j++) {
					parametres = new int[3];
					joueur = parse[j].split(":");
					parametres[0] = Integer.parseInt(joueur[1]);
					parametres[1] = Integer.parseInt(joueur[2]);
					try{
						parametres[2] = Integer.parseInt(joueur[3]); //si ca ne leve pas d'exception alors c'est un objectif
					} catch(NumberFormatException e) {
						parametres[2]=-1;
					}
					equipeAdverse[j-2]=parametres;
					autresEquipes.add(equipeAdverse);
				}
			}
			
		}
	}
	
}
