package clientUnique;
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

public class ServeurTcpPendu {
	private String hostname;
	private int port, maxClients;
	private String mot = "";
	private String lettresRestantes = "";
	private String motMasque = "";
	private int essais = 0;
	
	public ServeurTcpPendu (String hostname, int port, int maxClients) throws FileNotFoundException, IOException {
		this.hostname = hostname;
		this.port = port;
		this.maxClients = maxClients;
	}
	
	public void reset () throws FileNotFoundException, IOException {
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
	
	public boolean motDecouvert() {
		return motMasque.indexOf('_') == -1;
	}
	
	public void Echange () throws IOException {
		ServerSocket serveur = null;
		Socket client = null;
		BufferedWriter out = null;
		BufferedReader in = null;
		String bufReceived;
		char lettre;

		try {
			serveur = new ServerSocket(port);
			
			while(maxClients-- > 0) {
				client = serveur.accept();
				reset ();
				in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
				
				out.write(motMasque+ " "+essais+" essais");
				out.newLine();
				out.flush();
				
				while ((bufReceived = in.readLine()) != null) {
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
			}
		} finally {
			if (serveur != null)
				serveur.close();
			if (in != null)
				in.close();
			if (out != null)
				out.close();
			if (client != null)
				client.close();
		}
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		ServeurTcpPendu serveur = new ServeurTcpPendu("localhost", 50000, 2);
		serveur.Echange();
	}
}