package clientUnique;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientTcpPendu {
	private String hostname;
	private int port;
	
	public ClientTcpPendu (String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}
	
	public void Echange() throws UnknownHostException, IOException {
		Socket soc = null;
		String bufSend, bufReceived;
		boolean connected;
		BufferedReader clavier = null, in = null;
		BufferedWriter out = null;
		
		try {
			soc = new Socket(hostname, port);
			clavier = new BufferedReader(new InputStreamReader(System.in));
			out = new BufferedWriter(new OutputStreamWriter(soc.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			
			connected = true;
			/* Reception et affichage du mot masqué */
			bufReceived = in.readLine();
			System.out.println(bufReceived);
			
			while (connected) {
				/* Lecture d'une ligne au clavier et envoi au serveur */
				bufSend = clavier.readLine();
				out.write(bufSend);
				out.newLine();
				out.flush();
				
				/* Reception et affichage du mot masqué, des essais ou de gagne/perdu  */
				bufReceived = in.readLine();
				System.out.println(bufReceived);
				
				/* On met fin a la connexion quand le joueur a gagné ou perdu */
				if (bufReceived.contains("Gagne!") || bufReceived.contains("Perdu!"))
					connected = false;
			}
		} finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
			if (soc != null)
				soc.close();
		}
	}
	
	public static void main (String[] args) throws UnknownHostException, IOException {
		ClientTcpPendu client = new ClientTcpPendu("localhost", 50000);
		client.Echange();
	}
}