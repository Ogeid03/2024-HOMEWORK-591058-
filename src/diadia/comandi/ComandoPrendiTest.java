package diadia.comandi;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import diadia.IO;
import diadia.IOConsole;
import diadia.Partita;
import diadia.ambienti.Direzione;
import diadia.ambienti.Labirinto;
import diadia.attrezzi.Attrezzo;

class ComandoPrendiTest {

	IO io = new IOConsole(new Scanner(System.in));
	Labirinto labirinto;
	Partita partita;
	Comando comandoPrendi;
	FabbricaDiComandiFisarmonica factory = new FabbricaDiComandiFisarmonica();
	
	Attrezzo attrezzoTest = new Attrezzo("Vasetto_Nutella", 1);
	String istruzionePrendi = "prendi Vasetto_Nutella";
	
	
	@BeforeEach
	public void init() {
		this.labirinto = Labirinto.newLabBuilder()
				.addStanzaIniziale("LabCampusOne")
				.addStanzaVincente("Biblioteca")
				.addAdiacenza(Direzione.ovest, "LabCampusOne","Biblioteca")
				.getLabirinto();
		this.partita = new Partita(io, labirinto);
	}
	
	@Test
	public void testEseguiPrendi() {
		this.partita.getLabirinto().getStanzaCorrente().addAttrezzo(attrezzoTest);
		this.comandoPrendi = factory.costruisciComando(istruzionePrendi);
		this.comandoPrendi.esegui(this.partita);
		
		assertTrue(this.partita.getPlayer().getZaino().hasAttrezzo(this.attrezzoTest.getNome()));
	}
	
	@Test
	public void testEseguiPrendiNullParametro() {
		this.comandoPrendi = factory.costruisciComando("prendi");
		this.comandoPrendi.esegui(this.partita);
		assertNull(this.comandoPrendi.getParametro());
	}

}
