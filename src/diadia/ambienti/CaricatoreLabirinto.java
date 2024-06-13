package diadia.ambienti;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import diadia.FormatoFileNonValidoException;
import diadia.attrezzi.Attrezzo;
import diadia.personaggi.AbstractPersonaggio;
import diadia.personaggi.Cane;
import diadia.personaggi.Mago;
import diadia.personaggi.Strega;

/**
 * Classe di inizializzazione di un labirinto tramite file esterno
 *
 * @author  Diego De Martino
 *          
 * @version base 4.0
 */
public class CaricatoreLabirinto {

	/* prefisso di una singola riga di testo contenente tutti i nomi delle stanze */
	private static final String STANZE_LABEL = "Stanze:";             

	/* prefisso di una singola riga contenente il nome della stanza iniziale */
	private static final String STANZA_INIZIALE_LABEL = "Inizio:";    

	/* prefisso della riga contenente il nome stanza vincente */
	private static final String STANZA_VINCENTE_LABEL = "Vincente:";  	
	
	/* prefisso della riga contenente il nome stanza buia */
	private static final String STANZE_BUIE_LABEL = "Buia:";  

	/* prefisso della riga contenente il nome stanza bloccata */
	private static final String STANZE_BLOCCATE_LABEL = "Bloccata:";  
	
	/* prefisso della riga contenente il nome stanza bloccata */
	private static final String STANZE_MAGICHE_LABEL = "Magica:";  
	
	/* prefisso della riga contenente le specifiche degli attrezzi da collocare nel formato <nomeMago> <presentazione> <attrezzo> */
	private static final String PERSONAGGI_LABEL_MAGO = "Mago:";
	
	/* prefisso della riga contenente le specifiche degli attrezzi da collocare nel formato <nomeStrega> <presentazione> */
	private static final String PERSONAGGI_LABEL_STREGA = "Strega:";
	
	/* prefisso della riga contenente le specifiche degli attrezzi da collocare nel formato <nomeCane> <presentazione> */
	private static final String PERSONAGGI_LABEL_CANE = "Cane:";
	
	/* prefisso della riga contenente le specifiche degli attrezzi da collocare nel formato <nomeAttrezzo> <peso> <nomeStanza> */
	private static final String ATTREZZI_LABEL = "Attrezzi:";

	/* prefisso della riga contenente le specifiche dei collegamenti tra stanza nel formato <nomeStanzaDa> <direzione> <nomeStanzaA> */
	private static final String USCITE_LABEL = "Uscite:";

	private BufferedReader reader;

	private Map<String, Stanza> nome2stanza;

	private Stanza stanzaIniziale;
	private Stanza stanzaVincente;


	public CaricatoreLabirinto(String nomeFile) throws FileNotFoundException {
		this.nome2stanza = new HashMap<String,Stanza>();
		this.reader = new LineNumberReader(new FileReader(nomeFile));
	}
	
	public CaricatoreLabirinto(StringReader reader) throws FileNotFoundException {
		this.nome2stanza = new HashMap<String,Stanza>();
		this.reader = new LineNumberReader(reader);
	}

	public void carica() throws FormatoFileNonValidoException {
		try {
			this.leggiECreaStanze();
			this.leggiECreaStanzeMagiche();
			this.leggiECreaStanzeBuie();
			this.leggiECreaStanzeBloccate();
			this.leggiInizialeEvincente();
			this.leggiECreaMaghi();
			this.leggiECreaCani();
			this.leggiECreaStreghe();
			this.leggiECollocaAttrezzi();
			this.leggiEImpostaUscite();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

	}


	private String leggiRigaCheCominciaPer(String LABEL) throws FormatoFileNonValidoException {
		try {
			String riga = this.reader.readLine();
			check(riga.startsWith(LABEL),"era attesa una riga che cominciasse per "+LABEL);
			return riga.substring(LABEL.length());
		} catch (IOException e) {
			throw new FormatoFileNonValidoException(e.getMessage());
		}
	}

	private void leggiECreaStanze() throws FormatoFileNonValidoException  {
		String nomiStanze = this.leggiRigaCheCominciaPer(STANZE_LABEL);
		for(String nomeStanza : separaStringheAlleVirgole(nomiStanze)) {
			Stanza stanza = new Stanza(nomeStanza);
			this.nome2stanza.put(nomeStanza, stanza);
		}
	}
	
	private void leggiECreaStanzeMagiche() throws FormatoFileNonValidoException  {
		String nomiStanze = this.leggiRigaCheCominciaPer(STANZE_MAGICHE_LABEL);
		for(String nomeStanza : separaStringheAlleVirgole(nomiStanze)) {
			Stanza stanza = new StanzaMagica(nomeStanza);
			this.nome2stanza.put(nomeStanza, stanza);
		}
	}
	

	private void leggiECreaStanzeBuie() throws FormatoFileNonValidoException {
		String specificheStanze = this.leggiRigaCheCominciaPer(STANZE_BUIE_LABEL);
		for(String specifica : separaStringheAlleVirgole(specificheStanze)) {
			
			try (Scanner scannerDiLinea = new Scanner(specifica)) 	{	
				while (scannerDiLinea.hasNext()) {
					
					check(scannerDiLinea.hasNext(),msgTerminazionePrecoce("la  stanza "+ specifica+" non esiste\n"));
					String nomeStanza = scannerDiLinea.next();
					check(scannerDiLinea.hasNext(),msgTerminazionePrecoce("vi è stato qualche problema nella creazione dell'attrezzo per vedere la stanza "+specifica+"\n"));
					String attrezzoPerVedere = scannerDiLinea.next();

					Stanza stanza = new StanzaBuia(nomeStanza, attrezzoPerVedere);
					this.nome2stanza.put(nomeStanza, stanza);
				}
			}
		} 

	}
	
	private void leggiECreaStanzeBloccate() throws FormatoFileNonValidoException {
		String specificheStanze = this.leggiRigaCheCominciaPer(STANZE_BLOCCATE_LABEL);
		for(String specifica : separaStringheAlleVirgole(specificheStanze)) {
			
			try (Scanner scannerDiLinea = new Scanner(specifica)) 	{	
				while (scannerDiLinea.hasNext()) {
					
					check(scannerDiLinea.hasNext(),msgTerminazionePrecoce("la  stanza "+ specifica+" non esiste\n"));
					String nomeStanza = scannerDiLinea.next();
					check(scannerDiLinea.hasNext(),msgTerminazionePrecoce("la  direzione della stanza"+ specifica+" non esiste\n"));
					Direzione direzione = Direzione.valueOf(scannerDiLinea.next());
					check(scannerDiLinea.hasNext(),msgTerminazionePrecoce("vi è stato qualche problema nella creazione dell'attrezzo per sbloccare la stanza "+specifica+"\n"));
					String attrezzoSbloccante = scannerDiLinea.next();

					Stanza stanza = new StanzaBloccata(nomeStanza, attrezzoSbloccante, direzione);
					this.nome2stanza.put(nomeStanza, stanza);
				}
			}
		} 
	}
	
	private void leggiECreaMaghi() throws FormatoFileNonValidoException {
		String specificheStanze = this.leggiRigaCheCominciaPer(PERSONAGGI_LABEL_MAGO);
		for(String specifica : separaStringheAlleVirgole(specificheStanze)) {
			
			try (Scanner scannerDiLinea = new Scanner(specifica)) 	{	
				while (scannerDiLinea.hasNext()) {
					
					check(scannerDiLinea.hasNext(),msgTerminazionePrecoce("la  stanza "+ specifica+"per aggiungere il mago non esiste\n"));
					String nomeStanza = scannerDiLinea.next();
					check(scannerDiLinea.hasNext(),msgTerminazionePrecoce("problemini nella creazione del mago ...\n"));
					String mago = scannerDiLinea.next();
					check(scannerDiLinea.hasNext(),msgTerminazionePrecoce("specifica la presentazione del mago\n"));
					String presentazione = scannerDiLinea.next();					
					check(scannerDiLinea.hasNext(),msgTerminazionePrecoce("vi è stato qualche problema nella creazione dell'attrezzo per il mago della stanza "+specifica+"\n"));
					String attrezzo = scannerDiLinea.next();

					AbstractPersonaggio personaggio = new Mago(mago, presentazione, new Attrezzo(attrezzo, 4));
					this.nome2stanza.get(nomeStanza).setPersonaggio(personaggio);
				}
			}
		} 
	}
	
	private void leggiECreaStreghe() throws FormatoFileNonValidoException {
		String specificheStanze = this.leggiRigaCheCominciaPer(PERSONAGGI_LABEL_STREGA);
		for(String specifica : separaStringheAlleVirgole(specificheStanze)) {
			
			try (Scanner scannerDiLinea = new Scanner(specifica)) 	{	
				while (scannerDiLinea.hasNext()) {
					
					check(scannerDiLinea.hasNext(),msgTerminazionePrecoce("la  stanza "+ specifica+"per aggiungere la strega non esiste\n"));
					String nomeStanza = scannerDiLinea.next();
					check(scannerDiLinea.hasNext(),msgTerminazionePrecoce("problemini nella creazione della strega ...\n"));
					String strega = scannerDiLinea.next();
					check(scannerDiLinea.hasNext(),msgTerminazionePrecoce("specifica la presentazione della strega\n"));
					String presentazione = scannerDiLinea.next();					


					AbstractPersonaggio personaggio = new Strega(strega, presentazione);
					this.nome2stanza.get(nomeStanza).setPersonaggio(personaggio);
				}
			}
		} 
	}
	
	private void leggiECreaCani() throws FormatoFileNonValidoException {
		String specificheStanze = this.leggiRigaCheCominciaPer(PERSONAGGI_LABEL_CANE);
		for(String specifica : separaStringheAlleVirgole(specificheStanze)) {
			
			try (Scanner scannerDiLinea = new Scanner(specifica)) 	{	
				while (scannerDiLinea.hasNext()) {
					
					check(scannerDiLinea.hasNext(),msgTerminazionePrecoce("la  stanza "+ specifica+"per aggiungere il cane non esiste\n"));
					String nomeStanza = scannerDiLinea.next();
					check(scannerDiLinea.hasNext(),msgTerminazionePrecoce("problemini nella creazione del cane ...\n"));
					String cane = scannerDiLinea.next();
					check(scannerDiLinea.hasNext(),msgTerminazionePrecoce("specifica la presentazione del cane\n"));
					String presentazione = scannerDiLinea.next();					


					AbstractPersonaggio personaggio = new Cane(cane, presentazione);
					this.nome2stanza.get(nomeStanza).setPersonaggio(personaggio);
				}
			}
		} 
	}

	private List<String> separaStringheAlleVirgole(String string) {
		List<String> result = new LinkedList<>();
		Scanner scanner = new Scanner(string);
		scanner.useDelimiter(",");
		try (Scanner scannerDiParole = scanner) {
			while(scannerDiParole.hasNext()) {
				result.add(scannerDiParole.next());
			}
		}
		return result;
	}


	private void leggiInizialeEvincente() throws FormatoFileNonValidoException {
		String nomeStanzaIniziale = null;
		nomeStanzaIniziale = this.leggiRigaCheCominciaPer(STANZA_INIZIALE_LABEL);
		check(this.isStanzaValida(nomeStanzaIniziale), nomeStanzaIniziale +" non definita");
		String nomeStanzaVincente = this.leggiRigaCheCominciaPer(STANZA_VINCENTE_LABEL);
		check(this.isStanzaValida(nomeStanzaVincente), nomeStanzaVincente + " non definita");
		this.stanzaIniziale = this.nome2stanza.get(nomeStanzaIniziale);
		this.stanzaVincente = this.nome2stanza.get(nomeStanzaVincente);
	}

	private void leggiECollocaAttrezzi() throws FormatoFileNonValidoException {
		String specificheAttrezzi = this.leggiRigaCheCominciaPer(ATTREZZI_LABEL);

		for(String specificaAttrezzo : separaStringheAlleVirgole(specificheAttrezzi)) {
			String nomeAttrezzo = null;
			String pesoAttrezzo = null;
			String nomeStanza = null; 
			try (Scanner scannerLinea = new Scanner(specificaAttrezzo)) {
				check(scannerLinea.hasNext(),msgTerminazionePrecoce("il nome di un attrezzo."));
				nomeAttrezzo = scannerLinea.next();
				check(scannerLinea.hasNext(),msgTerminazionePrecoce("il peso dell'attrezzo "+nomeAttrezzo+"."));
				pesoAttrezzo = scannerLinea.next();
				check(scannerLinea.hasNext(),msgTerminazionePrecoce("il nome della stanza in cui collocare l'attrezzo "+nomeAttrezzo+"."));
				nomeStanza = scannerLinea.next();
			}				
			posaAttrezzo(nomeAttrezzo, pesoAttrezzo, nomeStanza);
		}
	}

	private void posaAttrezzo(String nomeAttrezzo, String pesoAttrezzo, String nomeStanza) throws FormatoFileNonValidoException {
		int peso;
		try {
			peso = Integer.parseInt(pesoAttrezzo);
			Attrezzo attrezzo = new Attrezzo(nomeAttrezzo, peso);
			check(isStanzaValida(nomeStanza),"Attrezzo "+ nomeAttrezzo+" non collocabile: stanza " +nomeStanza+" inesistente");
			this.nome2stanza.get(nomeStanza).addAttrezzo(attrezzo);
		}
		catch (NumberFormatException e) {
			check(false, "Peso attrezzo "+nomeAttrezzo+" non valido");
		}
	}


	private boolean isStanzaValida(String nomeStanza) {
		return this.nome2stanza.containsKey(nomeStanza);
	}

	private void leggiEImpostaUscite() throws FormatoFileNonValidoException {
		String specificheUscite = this.leggiRigaCheCominciaPer(USCITE_LABEL);
		for(String specifiche : separaStringheAlleVirgole(specificheUscite)) {
			try (Scanner scannerDiLinea = new Scanner(specifiche)) 	{	
				while (scannerDiLinea.hasNext()) {
					check(scannerDiLinea.hasNext(),msgTerminazionePrecoce("le uscite di una stanza."));
					String stanzaPartenza = scannerDiLinea.next();
					check(scannerDiLinea.hasNext(),msgTerminazionePrecoce("la direzione di una uscita della stanza "+stanzaPartenza));
					String dir = scannerDiLinea.next();
					check(scannerDiLinea.hasNext(),msgTerminazionePrecoce("la destinazione di una uscita della stanza "+stanzaPartenza+" nella direzione "+dir));
					String stanzaDestinazione = scannerDiLinea.next();

					impostaUscita(stanzaPartenza, dir, stanzaDestinazione);
				}
			}
		} 
	}

	private String msgTerminazionePrecoce(String msg) {
		return "Terminazione precoce del file prima di leggere "+msg;
	}

	private void impostaUscita(String stanzaDa, String dir, String nomeA) throws FormatoFileNonValidoException {
		check(isStanzaValida(stanzaDa),"Stanza di partenza sconosciuta "+dir);
		check(isStanzaValida(nomeA),"Stanza di destinazione sconosciuta "+ dir);
		Stanza partenzaDa = this.nome2stanza.get(stanzaDa);
		Stanza arrivoA = this.nome2stanza.get(nomeA);
		partenzaDa.impostaStanzaAdiacente(Direzione.valueOf(dir), arrivoA);
	}


	final private void check(boolean condizioneCheDeveEsseraVera, String messaggioErrore) throws FormatoFileNonValidoException {
		if (!condizioneCheDeveEsseraVera)
			throw new FormatoFileNonValidoException("Formato file non valido [" + ((LineNumberReader) this.reader).getLineNumber() + "] "+messaggioErrore);		
	}

	public Stanza getStanzaIniziale() {
		return this.stanzaIniziale;
	}

	public Stanza getStanzaVincente() {
		return this.stanzaVincente;
	}
}
