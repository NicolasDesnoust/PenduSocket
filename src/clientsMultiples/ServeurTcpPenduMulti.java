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

public class ServeurTcpPenduMulti {
	private String hostname;
	private int port, maxClients;
	
	public ServeurTcpPenduMulti (String hostname, int port, int maxClients) {
		this.hostname = hostname;
		this.port = port;
		this.maxClients = maxClients;
	}
	
	public void Echange () throws IOException {
		ServerSocket serveur = null;
		Socket client = null;
		
		try {
			serveur = new ServerSocket(port);
			
			while (maxClients-- > 0) {
				/* Lancement d'une partie en solitaire via un thread. */
				client = serveur.accept();
				ThreadServeurMulti thread = new ThreadServeurMulti(client);
				thread.start();
			}
			
		} finally {
			if (serveur != null)
				serveur.close();
		}
	}

	public static void main(String[] args) throws IOException {
		ServeurTcpPenduMulti serveur = new ServeurTcpPenduMulti("localhost", 50001, 2);
		serveur.Echange();
	}
}