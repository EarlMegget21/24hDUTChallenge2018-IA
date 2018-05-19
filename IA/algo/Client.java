package IA.algo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class Client {
//	public static boolean arret=false;
	public static int PORT=1337; //port par default (indiqué dans l'énoncé)
	
	public static void main(String[] args) throws Exception {
		/* Initialisation connexion Socket */
		Socket s = new Socket(args[0], PORT);
		System.out.println("STARTClient");
		BufferedReader ins = new BufferedReader(
				new InputStreamReader(s.getInputStream()) );
		PrintWriter outs = new PrintWriter( new BufferedWriter(
				new OutputStreamWriter(s.getOutputStream())), true);
		/*Fin connexion Socket */
		boolean arret=false;
		
		while(!arret){
			//TODO: algo ici
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
	
	//TODO: fonctions et classes ici
}
