package bluebox;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;

import javax.imageio.ImageIO;
import javax.security.auth.callback.Callback;
import javax.swing.JOptionPane;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;

/**
 * Klasa przetwarzajaca obrazy. Przetwarzanie odbywa sie w watku, zeby uplynnic dzialanie programu
 * @author Rafal Bebenek
 *
 */
public class Watek extends Thread{
	private boolean pracuj;
	private static int ileWykonano;
	private static int ileDoWykonania;
	private static File obrazZrodlowy;
	private static File obrazWTle;
	private static File obrazWynikowy;
	
	/**
	 * Domyslny konstruktor
	 */
	public Watek() {
		
	}
	
	/**
	 * Konstruktor dla pliku z obrazem
	 * @param obrazZrodlowy obraz z tlem do wyciecia
	 * @param obrazWTle obraz, ktorym jest zastepowane tlo
	 */
	public Watek(File obrazZrodlowy, File obrazWTle) {
		this.obrazZrodlowy = obrazZrodlowy;
		this.obrazWTle = obrazWTle;
		
		przetworzObrazy();
	}
	
	/**
	 * Konstruktor dla macierzy z obrazem, uzywany w przypadku przechwytywania obrazu z kamerki
	 * @param klatkaZKamerki pojedyncza klatka obrazu
	 * @param obrazWTle obraz do zastpienia tla
	 */
	/*public Watek(Mat klatkaZKamerki, File obrazWTle) {
		this.klatkaZKamerki = klatkaZKamerki;
		this.obrazWTle = obrazWTle;
		
		//przetworzObrazyZKamerki(klatkaZKamerki, obrazWTle);
	}*/
	
	/**
	 * Metoda przetwarzajaca obraz z niebieskim lub zielonym tlem
	 * @return zwraca plik wynikowy, ale rowniez zapisuje w folderze z plikiem zrodlowym
	 */
	File przetworzObrazy() {
		//Ustawienia u = new Ustawienia();
		Ustawienia u = Ustawienia.INSTANCE;
		try {	   
		      //ladowanie biblioteki
			  System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		      //File input = new File("oryginal.jpg");
		      BufferedImage image = ImageIO.read(obrazZrodlowy);

		      //ladowanie pierwszego pliku
		      byte[] data = ((DataBufferByte) image.getRaster().
		      getDataBuffer()).getData();
		      Mat macierz = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		      macierz.put(0, 0, data);  
		      
		      //File input2 = new File("tlo.jpg");
		      BufferedImage image2 = ImageIO.read(obrazWTle);
		      
		      //ladowanie drugiego pliku
		      byte[] data2 = ((DataBufferByte) image2.getRaster().
		      getDataBuffer()).getData();
		      Mat macierzTla = new Mat(image2.getHeight(), image2.getWidth(), CvType.CV_8UC3);
		      macierzTla.put(0, 0, data2);   
		    	      
		    	      
		      Mat macierzWynikowa = macierz.clone();
		      Size size = macierzWynikowa.size();
		      
		      if (image.getHeight() < image2.getHeight() || image.getWidth() < image2.getWidth()) {
		    	  JOptionPane.showMessageDialog(null, "Obrazy maja inna rozdzielczosc!\nObraz w tle jest zostanie obciety", 
		    			  "Ostrzezenie", JOptionPane.WARNING_MESSAGE);
		      }
		      else if (image.getHeight() != image2.getHeight() || image.getWidth() != image2.getWidth()) {
		    	  JOptionPane.showMessageDialog(null, "Obrazy maja inna rozdzielczosc!\nPrzeskaluj obraz w tle", 
		    			  "Wystapil blad", JOptionPane.ERROR_MESSAGE);
		    	  return null;
		      }
		      
		      ileDoWykonania = (int) size.width;
		      
			   for (int i = 0; i < size.height; i++) {
				   ileWykonano = i;
				   Thread.yield(); //przelaczenie na inne watki, zeby nie zarzynac procesora na 100%
			       for (int j = 0; j < size.width; j++) {
			    	   
			           double[] kolor = macierzWynikowa.get(i, j); 
			           double[] kolorTla = macierzTla.get(i, j);
			           
			           Kolor ustawionyKolorTla = new Kolor();
			           ustawionyKolorTla = u.odczytajKolorTla();
			           
			         //Pojasnianie pikseli niebieskich
			           if (ustawionyKolorTla.b !=0)
			        	   if (czyPikselJestTlem(kolor)) {
			        		   kolor[0] *= 3;
			        		   kolor[1] /= 3;
			        		   kolor[2] /= 3;
			        	   }
			           
			          //Pojasnianie zielonych pikseli
			           if (ustawionyKolorTla.g !=0)
			        	   if (czyPikselJestTlem(kolor)) {
			        		   kolor[0] /= 3;
			        		   kolor[1] *= 3;
			        		   kolor[2] /= 3;
			        	   }
			           
			           //---Kolor niebieski jako kolor tla
			           if (ustawionyKolorTla.b !=0)
			           if (kolor[0] >= (ustawionyKolorTla.b-u.odczytajTolerancje() * 1.1)  
			           		&& kolor[1] <= u.odczytajTolerancje() * 0.9 
			           		&& kolor[2] <= u.odczytajTolerancje() * 0.9) {
			        	   kolor[0] = kolorTla[0];
			        	   kolor[1] = kolorTla[1];
			        	   kolor[2] = kolorTla[2];
			           }
			           
			           //---Kolor zielony jako kolor tla
			           if (ustawionyKolorTla.g !=0)
			           if (kolor[0] <= u.odczytajTolerancje() * 0.9 
			           		&& kolor[1] >= (ustawionyKolorTla.g-u.odczytajTolerancje() * 1.1) 
			           		&& kolor[2] <= u.odczytajTolerancje() * 0.9) {
			        	   kolor[0] = kolorTla[0];
			        	   kolor[1] = kolorTla[1];
			        	   kolor[2] = kolorTla[2];
			           }
			           
			           //zamiana kanalow czerwonego i niebieskiego, zeby kolory byly normalne
			           double pom = kolor[0];
			           kolor[0] = kolor[2];
			           kolor[2] = pom;
			           macierzWynikowa.put(i, j, kolor);
			       }
			   }
		      byte[] data1 = new byte[macierzWynikowa.rows()*macierzWynikowa.cols()*(int)(macierzWynikowa.elemSize())];
		      macierzWynikowa.get(0, 0, data1);
		      BufferedImage image1=new BufferedImage(macierzWynikowa.cols(),macierzWynikowa.rows()
		      ,BufferedImage.TYPE_3BYTE_BGR);
		      image1.getRaster().setDataElements(0,0,macierzWynikowa.cols(),macierzWynikowa.rows(),data1);

		      //zapisanie pliku wynikowego
		      String sciezka = obrazZrodlowy.getAbsolutePath();
		      obrazWynikowy = new File(sciezka.substring(0, sciezka.lastIndexOf(File.separator)) + File.separator + "obrazWynikowy.jpg");
		      ImageIO.write(image1, "jpg", obrazWynikowy);
		      } catch (Exception e) {
		         System.out.println("Wystapil blad: " + e.getMessage());
		      }
		return obrazWynikowy;
	}
	
	
	/**
	 * Matoda, ktora sprawdza ile procent w kolorze piksela zajmuje kolor tla (niebieski lub zielony)
	 * @param piksel Piksel "wyciagniety" z obrazu
	 * @return Zwraca true jesli piksel jest w wiekszosci niebieski lub zielony
	 */
	static boolean czyPikselJestTlem(double[] piksel) {
		boolean czyJest = false;
		Ustawienia u = Ustawienia.INSTANCE;
		double ileProcentKoloru = u.odczytajProcentKoloru();
		Kolor kolorTla = new Kolor();
		
		if (u.odczytajKolorTla().b != 0) {
			double sumaKolorow = piksel[0] + piksel[1] + piksel[2];
			double ileJestKoloru = piksel[0]/sumaKolorow;
			ileJestKoloru *= 100;
			if (ileJestKoloru >= ileProcentKoloru &&piksel[0] > u.odczytajMinimalnaWartosKoloru()) czyJest = true;
		}
		else if (u.odczytajKolorTla().g != 0) {
			double sumaKolorow = piksel[0] + piksel[1] + piksel[2];
			double ileJestKoloru = piksel[1]/sumaKolorow;
			ileJestKoloru *= 100;
			if (ileJestKoloru >= ileProcentKoloru &&piksel[1] > u.odczytajMinimalnaWartosKoloru()) czyJest = true;
		}
		
		return czyJest;
	}
	
	/**
	 * Metoda usuwajaca niebieskie lub zielone tlo z obrazu dla obrazu przechwyconego
	 * z kamerki.
	 * @return Zwraca macierz z pikselami wchodzacymi w sklad ramki obrazu
	 */
	Mat przetworzObrazyZKamerki(Mat klatkaZKamerki, BufferedImage obrazWTle, boolean czyPoprawianieKolorow) {
		//Ustawienia u = new Ustawienia();
				Ustawienia u = Ustawienia.INSTANCE;
				Mat macierzWynikowa = null;
					   
				      //ladowanie biblioteki
					  System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
					  
					  //ladowanie macierzy otrzymanej z kamerki
					  Mat macierz = klatkaZKamerki;
				      
				  try {
					  //File input2 = new File("tlo.jpg");
				      BufferedImage image2 = obrazWTle;//ImageIO.read(obrazWTle);
				      
				      //ladowanie pliku z tlem
				      byte[] data2 = ((DataBufferByte) image2.getRaster().
				      getDataBuffer()).getData();
				      Mat macierzTla = new Mat(image2.getHeight(), image2.getWidth(), CvType.CV_8UC3);
				      macierzTla.put(0, 0, data2); 
				      
				    //Jezeli nie ma pliku z tlem - pominiecie przetwarzania
				      if (macierzTla.empty()) {
				    	  return macierz;
				      }
				    	      
				    	      
				      macierzWynikowa = macierz.clone();
				      Size size = macierzWynikowa.size();
				      Size size2 = macierzTla.size();
				      
				      if (size.height < size2.height || size.width < size2.width) {
				    	  //JOptionPane.showMessageDialog(null, "Obrazy maja inna rozdzielczosc!\nObraz w tle jest zostanie obciety", 
				    		//	  "Ostrzezenie", JOptionPane.WARNING_MESSAGE);
				    	  System.out.println("Obrazy maja inna rozdzielczosc!\nObraz w tle jest zostanie obciety");
				      }
				      else if (size.height != size2.height || size.width != size2.width) {
				    	  JOptionPane.showMessageDialog(null, "Obrazy maja inna rozdzielczosc!\nPrzeskaluj obraz w tle", 
				    			  "Wystapil blad", JOptionPane.ERROR_MESSAGE);
				    	  return null;
				      }
				      
				      ileDoWykonania = (int) size.width;
				      
				   for (int i = 0; i < size.height; i++) {
					   ileWykonano = i;
					   Thread.yield(); //przelaczenie na inne watki, zeby nie zarzynac procesora na 100%
				       for (int j = 0; j < size.width; j++) {
				    	   
				           double[] kolor = macierzWynikowa.get(i, j); 
				           double[] kolorTla = macierzTla.get(i, j);
					           
				           Kolor ustawionyKolorTla = new Kolor();
				           ustawionyKolorTla = u.odczytajKolorTla();
				           
				           //Poprawianie kolorow
				           if (czyPoprawianieKolorow) {
				           //Pojasnianie pikseli niebieskich
				           if (ustawionyKolorTla.b !=0)
				        	   if (czyPikselJestTlem(kolor)) {
				        		   kolor[0] *= 3;
				        		   kolor[1] /= 3;
				        		   kolor[2] /= 3;
				        	   }
				           
				          //Pojasnianie zielonych pikseli
				           if (ustawionyKolorTla.g !=0)
				        	   if (czyPikselJestTlem(kolor)) {
				        		   kolor[0] /= 3;
				        		   kolor[1] *= 3;
				        		   kolor[2] /= 3;
				        	   }
				           }
					           
					       //---Kolor niebieski jako kolor tla
				           if (ustawionyKolorTla.b !=0)
				           if (kolor[0] >= (ustawionyKolorTla.b-u.odczytajTolerancje() * 1.2) 
				           		&& kolor[1] < u.odczytajTolerancje() * 0.8 
				           		&& kolor[2] < u.odczytajTolerancje() *0.8) {
					        	   
				        	   kolor[0] = kolorTla[0];
				        	   kolor[1] = kolorTla[1];
				        	   kolor[2] = kolorTla[2];
				           }
				           
					          //---Kolor zielony jako kolor tla
				          if (ustawionyKolorTla.g !=0)
				          if (kolor[0] < u.odczytajTolerancje() * 0.8 
				           		&& kolor[1] >= (ustawionyKolorTla.g-u.odczytajTolerancje() * 1.2) 
				           		&& kolor[2] < u.odczytajTolerancje() * 0.8) {
					        	   
				        	   kolor[0] = kolorTla[0];
				        	   kolor[1] = kolorTla[1];
				        	   kolor[2] = kolorTla[2];
				           }
				          
					           
				           //zamiana kanalow czerwonego i niebieskiego, zeby kolory byly normalne
				           double pom = kolor[0];
				           kolor[0] = kolor[2];
				           kolor[2] = pom;
				           macierzWynikowa.put(i, j, kolor);
				       }
				   }
			      byte[] data1 = new byte[macierzWynikowa.rows()*macierzWynikowa.cols()*(int)(macierzWynikowa.elemSize())];
			      macierzWynikowa.get(0, 0, data1);
			} catch (Exception e) {
		         System.out.println("Wystapil blad: " + e.getMessage());
		    }

				
		return macierzWynikowa;		
	}
	
	
	/**
	 * Dodawanie szescianow z przodu obserwatora, żeby poglebic efekt trojwymiarowosci
	 */
	File dodajSzesciany(File obrazZrodlowy, File obrazZSzescianami, int ktoryObraz) {
		
		BufferedImage image = null;
		Mat macierz = null;
		Mat macierzTla = null;
		
		int przesuniecie;
	    if (ktoryObraz == 1)
	    	przesuniecie = 40;
	    else przesuniecie = 0;
		
		try {	   
			System.out.println("------Obraz nr " + ktoryObraz);
		      //ladowanie biblioteki
			  System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		      //File input = new File("oryginal.jpg");
		      image = ImageIO.read(obrazZrodlowy);

		
			//ladowanie pierwszego pliku
		      byte[] data = ((DataBufferByte) image.getRaster().
		      getDataBuffer()).getData();
		      macierz = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		      macierz.put(0, 0, data); 
		      
		  } catch (Exception e) {
	         System.out.println("Wystapil blad Buffered: " + e.getMessage() + " obrazu " + ktoryObraz);
	      }
		  
		  try {    
		      //File input2 = new File("tlo.jpg");
		      BufferedImage image2 = ImageIO.read(obrazZSzescianami);
		      
		      //ladowanie drugiego pliku
		      byte[] data2 = ((DataBufferByte) image2.getRaster().
		      getDataBuffer()).getData();
		      macierzTla = new Mat(image2.getHeight(), image2.getWidth(), CvType.CV_8UC3);
		      macierzTla.put(0, 0, data2); 
		      
		  } catch (Exception e) {
		         System.out.println("Wystapil blad Buffered: " + e.getMessage() + " obrazu " + ktoryObraz);
		  }
		    	      
		  	      
		      Mat macierzWynikowa = macierz.clone();
		      Size size = macierzWynikowa.size();
		      
		      ileDoWykonania = (int) size.width;
		  
		      
			   for (int i = 0; i < size.height; i++) {
				   ileWykonano = i;
				   Thread.yield(); //przelaczenie na inne watki, zeby nie zarzynac procesora na 100%
			       for (int j = przesuniecie; j < (size.width + przesuniecie); j++) {
			    	   
			           double[] kolor = macierzWynikowa.get(i, j-przesuniecie); 
			           double[] kolorTla = macierzTla.get(i, j);
			           
			           Kolor ustawionyKolorTla = new Kolor();
			           //ustawionyKolorTla = u.odczytajKolorTla();
			           ustawionyKolorTla.b = 0;
			           ustawionyKolorTla.g = 255;
			           ustawionyKolorTla.r = 0;
			        
			           
			           //---Kolor zielony jest wycinany, wklejane sa tylko szesciany
			           //if (ustawionyKolorTla.g !=0)
			           if (kolorTla != null)
			           if (kolorTla[0] >= 0 
			           		&& kolorTla[1] <= ustawionyKolorTla.g - 10 
			           		&& kolorTla[2] >= 0) {
			        	   kolor[0] = kolorTla[0];
			        	   kolor[1] = kolorTla[1];
			        	   kolor[2] = kolorTla[2];
			           }
			           
			           //zamiana kanalow czerwonego i niebieskiego, zeby kolory byly normalne
			           double pom = kolor[0];
			           kolor[0] = kolor[2];
			           kolor[2] = pom;
			           macierzWynikowa.put(i, j-przesuniecie, kolor);
			       }
			   }
		      byte[] data1 = new byte[macierzWynikowa.rows()*macierzWynikowa.cols()*(int)(macierzWynikowa.elemSize())];
		      macierzWynikowa.get(0, 0, data1);
		      BufferedImage image1=new BufferedImage(macierzWynikowa.cols(),macierzWynikowa.rows()
		      ,BufferedImage.TYPE_3BYTE_BGR);
		      image1.getRaster().setDataElements(0,0,macierzWynikowa.cols(),macierzWynikowa.rows(),data1);
		      
		      
		    try {
		      //zapisanie pliku wynikowego
		      String sciezka = obrazZrodlowy.getAbsolutePath();
		      obrazWynikowy = new File(sciezka.substring(0, sciezka.lastIndexOf(File.separator))
		    		  + File.separator + "obrazWynikowy" + ktoryObraz + ".jpg");
		      ImageIO.write(image1, "jpg", obrazWynikowy);
		      } catch (Exception e) {
		         System.out.println("Wystapil blad: " + e.getMessage());
		      }
		return obrazWynikowy;
	}

	public void zacznijPrace() {
		this.pracuj = true;
	}
	
	public void przerwijPrace() {
		this.pracuj = false;
	}
	
	synchronized static int ileWykonano() {
		return ileWykonano;
	}
	
	static int ileDoWykonania() {
		return ileDoWykonania;
	}
	
}
