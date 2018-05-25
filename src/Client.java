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

	public static int PORT=1337; //port par default (indique dans l'enonce)
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
	
	public static HashMap<String, Noeud> listeOuverte=new HashMap<String, Noeud>();
	public static HashMap<String, Noeud> listeFermee=new HashMap<String, Noeud>();
	public static ArrayList<Noeud> chemin=new ArrayList<Noeud>();
	
	/** Main qui joue au jeu **/
	public static void main(String[] args) throws Exception {
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

			System.out.println("Map:");
			for(int i=0; i<hauteur; i++){
				for(int j=0; j<largeur; j++){
					System.out.print(map[j][i]);
				}
				System.out.print("\n");
			}
			System.out.println("Largeur:"+largeur+";Hauteur:"+hauteur);
			System.out.println("Num:"+num);
			System.out.println("NbEquipes:"+nbEquipes);
			System.out.println("Joueurs:");
			for(Lanceur j : equipe){
				System.out.println(j.getCoord()[0]+":"+j.getCoord()[1]+":"+j.getCoord()[2]);
			}
			System.out.println("Home:"+home[0][0]+":"+home[0][1]+"|"+home[1][0]+":"+home[1][1]);
			
			
			System.exit(1);
			
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
		for(int j=1; j<=hauteur; j++){ //pour chaque ligne (la premiere case : dimensions)
			ligne=new String[largeur];
			for(int i=0; i<largeur; i++){ //pour chaque case dans la ligne
				ligne[i]=infosMap[j].substring(i, i+1);
				//ligne[i]=cases[i+(j*largeur)];
				//version objectif le plus proche
				try{
					int type = Integer.parseInt(ligne[i]); //si ca ne leve pas d'exception alors c'est un objectif
					listeObjectifs.add(new int[]{i, j, type}); //on ajoute ses coordonnees a la liste des objectifs
				}catch(NumberFormatException e){
					//ce n'est pas un objectif
				}
				//fin version objectif le plus proche
			}
			map[j-1]=ligne;
		}
		
		//on creer notre equipe et les equipes adverses
		for(int i = 3; i < nbEquipes ; i++) {
			
			String[] parse = infos[i].split(","); //parse des equipes
			String[] joueur;
			int[] parametres = new int[3];
			
			if(num == i-3 ) { //c'est notre equipe
				
				joueur = parse[2].split(":");
				
				for(int j = 3 ; j < 5 ; i++) {
					joueur = parse[i].split(":");
					parametres[0] = Integer.parseInt(joueur[1]);
					parametres[1] = Integer.parseInt(joueur[2]);
					try{
						parametres[3] = Integer.parseInt(joueur[i]); //si ca ne leve pas d'exception alors c'est un objectif
						listeObjectifs.add(parametres);
					} catch(NumberFormatException e) {
						parametres[3]=-1;
					}
					if(j==3){
						Quarterback quarterBack = new Quarterback(parametres);
						equipe[j]=quarterBack;
					}else{
						Lanceur lanceur = new Lanceur(parametres);
						equipe[j]=lanceur;
					}
				}
				
			} else { //c'est une equipe adverse
				
				int[][] equipeAdverse=new int[3][3]; //tableau de 3 joueurs (un joueur : {x, y, fruit} )
				
				for(int j = 3 ; j<5 ; i++) {
					joueur = parse[i].split(":");
					parametres[0] = Integer.parseInt(joueur[1]);
					parametres[1] = Integer.parseInt(joueur[2]);
					try{
						parametres[3] = Integer.parseInt(joueur[3]); //si �a ne l�ve pas d'exception alors c'est un objectif
						listeObjectifs.add(parametres);
					} catch(NumberFormatException e) {
						parametres[3]=-1;
					}
					equipeAdverse[j]=parametres;
					autresEquipes.add(equipeAdverse);
				}
			}
		}
	}
	
}
