package diadia;
import java.util.Scanner;

/**
 * Classe Input/Output gestisce tutti i System.in/System.out
 * 
 * @author Diego De Martino
 * @version base 3.1
 */
public class IOConsole implements IO{
	
	public void mostraMessaggio(String msg) {
		System.out.println(msg);
	}
	
	public void mostraMessaggioInLinea(String msg) {
		System.out.print(msg);
	}
	
	public String leggiRiga() {
		Scanner scannerDiLinee = new Scanner(System.in);
		String riga = scannerDiLinee.nextLine();
		//scannerDiLinee.close();
		return riga;
	}
}
