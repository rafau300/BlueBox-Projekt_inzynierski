package bluebox;

/**
 * Klasa, ktora przechowuje ustawienia programu jak kolor tla czy tolerancja z jaka bedzie usuwany
 * kolor tla. Niektore klasy maja wspolne ustawienia, dlatego ta klasa jest zaimplementowana w projekcie.
 * @author Rafal
 *
 */
public class Ustawienia {
	private static int liczbaWatkow;
	private static int r,g,b;
	private static int tolerancja;
	private static int ileProcentKoloru;
	private static int minimalnaWartoscKoloru;
	
	/**
	 * Konstruktor ustanawia domyslne ustawienia, gdyby z jakis powodow nie dalo sie ich
	 * zaladowac do programu.
	 */
	public static final Ustawienia INSTANCE = new Ustawienia();
	public Ustawienia(){
		
		liczbaWatkow = 1;
		this.r = 0;
		this.g = 0;
		this.b = 255;
		tolerancja = 127;
		ileProcentKoloru = 50;
		minimalnaWartoscKoloru = 50;
	}
	
	
	public void ustawKolorTla(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
		System.out.println("Ustawiono kolor: (" + r + "," + g + "," + b + ")");
	}
	
	public void ustawLiczbeWatkow(int _liczbaWatkow) {
		liczbaWatkow = _liczbaWatkow;
	}
	
	public void ustawTolerancje(int _tolerancja) {
		tolerancja = _tolerancja;
		System.out.println("Ustawiono tolerancje na poziomie: " + tolerancja);
	}
	
	public int odczytajLiczbeWatkow() {
		return liczbaWatkow;
	}
	
	public Kolor odczytajKolorTla() {
		Kolor kolorTla = new Kolor();
		kolorTla.setR(r);
		kolorTla.setG(g);
		kolorTla.setB(b);
		return kolorTla;
	}
	
	public int odczytajTolerancje() {
		return tolerancja;
	}
	
	public static int odczytajProcentKoloru() {
		return ileProcentKoloru;
	}
	
	public int odczytajMinimalnaWartosKoloru() {
		return minimalnaWartoscKoloru;
	}
	
	public void ustawMinimalnaWartoscKoloru(int wartosc) {
		minimalnaWartoscKoloru = wartosc;
	}
}
