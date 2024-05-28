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
public class ComandoGuarda implements Comando{
	private String cosaGuardare;
	
	public ComandoGuarda(String cosaGuardare) {
		this.cosaGuardare = cosaGuardare;
	}
	
	@Override
	public void esegui(Partita partita) {
		if(this.cosaGuardare == null) {
			partita.getIO().mostraMessaggio("Cosa vuoi guardare ? (Devi specificare se Stanza o Borsa)");
			return;
		}else if(this.cosaGuardare.equals("borsa") || this.cosaGuardare.equals("Borsa")) {
			partita.getIO().mostraMessaggio(partita.getPlayer().getZaino().getDescrizione());
		}else if(this.cosaGuardare.equals("stanza") || this.cosaGuardare.equals("Stanza")) {
			partita.getIO().mostraMessaggio(partita.getLabirinto().getStanzaCorrente().getDescrizione());
		} else partita.getIO().mostraMessaggio("COMANDO INESISTENTE");
	}
	
	@Override
	public void setParametro(String param) {
		this.cosaGuardare = param;
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
