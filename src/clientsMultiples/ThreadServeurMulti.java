package clientsMultiples;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

class ThreadServeurMulti extends Thread {
	private Socket client;
	
	private String mot = "";
	private String lettresRestantes = "";
	private String motMasque = "";
	private int essais = 0;
	
	public ThreadServeurMulti (Socket client) {
		this.client = client;
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
			
			lettresRestantes = "[abcdefghijklmnopqrstuvwxyz]";
			motMasque = mot.replaceAll(lettresRestantes, "_ ");
			essais = mot.length()+4;
		} finally {
			if (br != null)
				br.close();
		}
	}
	
	/* Renvoie vrai si le mot masqué est entièrement découvert, faux sinon */
	public boolean motDecouvert() {
		return motMasque.indexOf('_') == -1;
	}
	
	public void run () {
		BufferedWriter out = null;
		BufferedReader in = null;
		String bufReceived;
		char lettre;
			
		try {
			init ();
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			
			/* Envoi du mot masqué au joueur */
			out.write(motMasque+ " "+essais+" essais");
			out.newLine();
			out.flush();
			
			while ((bufReceived = in.readLine()) != null) {
				/* Mise a jour de l'avancée du joueur */
				essais--;

				if(bufReceived.length() > 0) {
					lettre = bufReceived.charAt(0);
					/* Ces caractères sont dans lettresRestantes
					 * et ne doivent pas être remplacés. */
					if (lettre != ']' && lettre != '[') {
						lettresRestantes = lettresRestantes.replace(String.valueOf(lettre), "");
						motMasque = mot.replaceAll(lettresRestantes, "_ ");
					}
				}
				
				if (motDecouvert()) {
					out.write(motMasque + " Gagne!");
					out.newLine();
					out.flush();
					break;
				}
				
				if (essais == 0) {
					out.write(motMasque+" Perdu!");
					out.newLine();
					out.flush();
					break;
				}
				
				out.write(motMasque+ " "+essais+" essais");	
				out.newLine();
				out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(in != null)
				try {in.close();} catch (IOException e) {e.printStackTrace();}
			if(out != null)
				try {out.close();} catch (IOException e) {e.printStackTrace();}
			if(client != null)
				try {client.close();} catch (IOException e) {e.printStackTrace();}
		}	
	}
}