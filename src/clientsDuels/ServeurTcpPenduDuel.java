package clientsDuels;
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

public class ServeurTcpPenduDuel {
	private String hostname;
	private int port, maxDuels;
	
	public ServeurTcpPenduDuel (String hostname, int port, int maxDuels) {
		this.hostname = hostname;
		this.port = port;
		this.maxDuels = maxDuels;
	}
	
	public void Echange () throws IOException {
		ServerSocket serveur = null;
		Socket client1 = null, client2 = null;
		boolean end = false;
		
		try {
			serveur = new ServerSocket(port);
			
			while (maxDuels-- > 0) {
				/* Lancement d'une partie via un thread entre deux clients */
				client1 = serveur.accept();
				client2 = serveur.accept();
				ThreadServeurDuel thread = new ThreadServeurDuel(client1, client2);
				thread.start();
			}
			
		} finally {
			if (serveur != null)
				serveur.close();
		}
	}

	public static void main(String[] args) throws IOException {
		ServeurTcpPenduDuel serveur = new ServeurTcpPenduDuel("localhost", 50002, 2);
		serveur.Echange();
	}
}