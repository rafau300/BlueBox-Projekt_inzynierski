package bluebox;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Interfejs po ktorym dziedzicza inne klasy z oknami. Istnieje po to, zeby
 * troche uproscic implementacje klas "okienkowych".
 * @author Rafal
 */
public interface Okno {
	
	public int wysokosc = 800;
	public int szerokosc = 600;
	String tytul = "BlueBox";
	
	JFrame f = new JFrame();
	JPanel p = new JPanel();
	
	public void ustawTytul(String tytul);

}
