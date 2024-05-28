package diadia.comandi;

import diadia.Partita;
import diadia.ambienti.Labirinto;

/**
 * Questa classe modella un comando di gioco.
 * Richiamando il metodo ESEGUI vengono riportati in output
 * all'utente i dati relativi alla STANZA.
 * 
 * @author Diego De Martino
 * @see Labirinto
 * @see Borsa
 * @version base 3.1
 */
public class ComandoPosa implements Comando{
	
	private String attrezzo;
	
	public ComandoPosa(String param) {
		this.attrezzo = param;
	}

	/**
	* esecuzione del comando
	* 
	* Mette l'oggetto presente nella stanza nella borsa del giocatore
	*/
	@Override
	public void esegui(Partita partita) {
		
		if(this.attrezzo==null) {
			partita.getIO().mostraMessaggio("Quale Attrezzo vuoi posare ? (Devi specificare un Attrezzo)");
			return;
		}	
		if(partita.getPlayer().getZaino().getAttrezzi().size() != 0) {
			if(partita.getPlayer().getZaino().hasAttrezzo(this.attrezzo)==true) {
				
				partita.getLabirinto().getStanzaCorrente().addAttrezzo(partita.getPlayer().getZaino().getAttrezzo(this.attrezzo));
				partita.getPlayer().getZaino().removeAttrezzo(this.attrezzo);
			  
				partita.getIO().mostraMessaggio("\n" + partita.getPlayer().getZaino().getDescrizione());
			} else {
				partita.getIO().mostraMessaggio("ATTREZZO INESISTENTE" + partita.getPlayer().getZaino().getDescrizione());
		    }
		} else partita.getIO().mostraMessaggio("Non ci sono attrezzi nella borsa");
	}
	
	@Override
	public void setParametro(String param) {
		this.attrezzo = param;
	}
	
	@Override
	public String getParametro() {
		return null;
	}
	
	@Override
	public String getNome() {
		return null;
	}

}

