package clientsDuels;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Random;

class ThreadServeurDuel extends Thread {
	private Socket clients[] = {null, null};	
	private String mot = "";
	private String lettresRestantes[] = {"", ""};
	private String motMasque[] = {"", ""};
	private int essais[] = {0, 0};
	
	public ThreadServeurDuel (Socket client1, Socket client2) throws FileNotFoundException, IOException {
		this.clients[0] = client1;
		this.clients[1] = client2;
	}
	
	public void init () throws FileNotFoundException, IOException {
		/*	Lecture d'un mot aléatoire dans le fichier mot.txt */
		Random rand = new Random();
		int ligneMot = rand.nextInt(20)+1;       
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(new File ("src/mots.txt")));
			/* Lecture des mots un par un jusqu'à celui qui a été choisi. */
			for (int i = 1; i <= ligneMot; i++)
				mot = br.readLine();
			
			/* Initialisation des données de jeu pour les deux joueurs. */
			for (int i = 0; i < clients.length; i++) {
				lettresRestantes[i] = "[abcdefghijklmnopqrstuvwxyz]";
				motMasque[i] = mot.replaceAll(lettresRestantes[i], "_ ");
				essais[i] = mot.length()+4;
			}
		} finally {
			if (br != null)
				br.close();
		}
	}
	
	/* Renvoie vrai si "mot" est entièrement découvert, faux sinon. */
	public boolean motDecouvert(String mot) {
		return mot.indexOf('_') == -1;
	}
	
	private void send(BufferedWriter out, String message) throws IOException {
		out.write(message);
		out.newLine();
		out.flush();
	}
	
	public void run () {
			BufferedWriter out[] = {null, null};
			BufferedReader in[] = {null, null};
			String bufReceived = null;
			char lettre = ' ';
			int client = 0, autreClient = 1;
			
			try {
				init();
				
				for (int i = 0; i < clients.length; i++) {
					in[i] = new BufferedReader(new InputStreamReader(clients[i].getInputStream()));
					out[i] = new BufferedWriter(new OutputStreamWriter(clients[i].getOutputStream()));
				}
				/* Envoi des mots masqués aux joueurs. */
				send(out[0], motMasque[0]+ " "+essais[0]+" essais");
				send(out[1], motMasque[1]+ " "+essais[1]+" essais");
				
				/* Le premier joueur débute la partie. */
				send(out[0], "Vous debutez la partie :");
				
				while ((bufReceived = in[client].readLine()) != null) {
					autreClient = 1 - client;
					
					/* Mise a jour de l'avancée du joueur courant. */
					essais[client]--;
					
					if(bufReceived.length() > 0) {
						lettre = bufReceived.charAt(0);
						/* Ces caractères sont dans lettresRestantes
						 * et ne doivent pas être remplacés. */
						if (lettre != ']' && lettre != '[') {
							lettresRestantes[client] = lettresRestantes[client].replace(String.valueOf(lettre), "");
							motMasque[client] = mot.replaceAll(lettresRestantes[client], "_ ");
						}
					}
					
					/* Affichage du nouveau mot masqué au joueur courant. */
					send(out[client], motMasque[client]+" "+ essais[client]+ " essais");
					
					/* Si le joueur a gagné */
					if (motDecouvert(motMasque[client])) {
						send(out[client], "Gagne!");
						essais[client] = 0;
						
						if (essais[autreClient] != 0) { /* Si l'autre joueur joue encore. */
							/* On offre une dernière chance au client ayant
							 * commencé en second. */
							if (autreClient == 1) {
								essais[autreClient] = 1;
								send(out[autreClient], "Derniere tentative :");
								client = autreClient;
								continue;
							}
							else { // Pas de dernière chance pour le premier joueur.
								send(out[autreClient], "Perdu!");
								break;
							}
						}
						else break; /* L'autre joueur avait déjà fini de jouer. */
					}
					/* Si le joueur a perdu */
					if (essais[client] == 0) {
						send(out[client], "Perdu!");
						
						if (essais[autreClient] == 0)
							break;
						else {
							client = autreClient;
							send(out[autreClient], "A votre tour :");
							continue;
						}
					}
					/* Sinon la partie continue */
					send(out[client], "L'autre joueur choisit une lettre.");
					send(out[autreClient], "A votre tour :");
	
					if (essais[autreClient] > 0)
						client = autreClient;
				}
		
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				for (int i = 0; i < clients.length; i++) {
					if(in[i] != null)
						try {in[i].close();} catch (IOException e) {e.printStackTrace();}
					if(out[i] != null)
						try {out[i].close();} catch (IOException e) {e.printStackTrace();}
					if(clients[i] != null)
						try {clients[i].close();} catch (IOException e) {e.printStackTrace();}
				}
			}	
	}
}