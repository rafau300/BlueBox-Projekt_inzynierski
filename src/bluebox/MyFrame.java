package bluebox;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MyFrame extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private BufferedImage obraz;
	
	//Wczytywane obrazy, ktore beda scalone w jeden obraz 3D
	private BufferedImage obraz1;
	private BufferedImage obraz2;
	
	private BufferedImage obrazZSzescianami;
	
	static int ramka = 0;
	private int ktoraKamerka;
	private boolean czyPoprawianieKolorow;
	
	VideoCap videoCap;
	

  /**
  * Create the frame.
  */
    public MyFrame(int _ktora, boolean _czyPoprawianieKolorow) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        if (_ktora == 0) setBounds(0, 100, 640, 480);
        else setBounds(700, 100, 640, 480);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        this.ktoraKamerka = _ktora;
        this.czyPoprawianieKolorow = _czyPoprawianieKolorow;
        
        videoCap = new VideoCap(ktoraKamerka,czyPoprawianieKolorow);
  
        new MyThread().start();
    }
    
 
    public void paint(Graphics g){
        g = contentPane.getGraphics();
        ramka++;
        System.out.println("Ramka nr: " + ramka);
        obraz = videoCap.getOneFrame();
        g.drawImage(obraz, 0, 0, this);
        
        //Zapisywanie 1 klatki obrazu z kamerki do pliku
        if (ramka == 10 || ramka == 11) {	//Zapisywanie 6 i 7 klatki obrazu, bo czasami program wyrzuca wyjatek przy 1 klatce
        	//dodatkowo w kolejnych klatkach obraz jest jakby lepiej naswietony
        	 
        	//Zapis do pliku
        	
        	File zapisanyObraz = new File("zrzut" + ktoraKamerka + ".jpg");
        	 try {
				ImageIO.write(obraz, "jpg", zapisanyObraz);
			} catch (IOException e) {
				System.err.println("Blad przy zapisie: " + e.toString());
			}
        	      	 
        	 //////////////////////////////////////////////////////////////
        	 if (ktoraKamerka /*== 0*/ ==1) {//Program 2 kamerki scala 2 obrazy jesli wlaczono 3D
            	
        		 //Uspienie watku, zeby plik zdarzyl sie zapisac przed odczytem
            	 try {
    				Thread.sleep(100);
    			} catch (InterruptedException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			} 
            	 
        		File plik1 = new File("zrzut0.jpg");
        		 try {
					obraz1 = ImageIO.read(plik1);
				} catch (IOException e) {
					System.err.println("Blad przy odczycie: " + e.toString());
				} 
        		 
        		 
        		 File plik2 = new File("zrzut1.jpg");
        		 try {
					obraz2 = ImageIO.read(plik2);
				} catch (IOException e) {
					System.err.println("Blad przy odczycie: " + e.toString());
				} 
        		 
        		 File plikZSzescianami = new File("szesciany.jpg");
        		 try {
        			obrazZSzescianami = ImageIO.read(plikZSzescianami);
				} catch (IOException e) {
					System.err.println("Blad przy odczycie pliku z szescianami: " + e.toString());
				} 
        		 
        		 File plikZSzescianami2 = plikZSzescianami;
        		 
        		 
        		 //*******************************************************************
        		 //dodawanie szescianow do obrazow
        		 Watek watek1 = new Watek();
        		 Watek watek2 = new Watek();
        		 plik1 = watek1.dodajSzesciany(plik1, plikZSzescianami, 0);
        		 plik2 = watek2.dodajSzesciany(plik2, plikZSzescianami2, 1);


        		 
        		 try {
					obraz1 = ImageIO.read(plik1);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        		 
        		 try {
 					Thread.sleep(100);
 				} catch (InterruptedException e2) {
 					// TODO Auto-generated catch block
 					e2.printStackTrace();
 				}
        		 
        		 try {
 					obraz2 = ImageIO.read(plik2);
 				} catch (IOException e1) {
 					// TODO Auto-generated catch block
 					e1.printStackTrace();
 				}
        		 
        		 //Laczenie dwoch obrazow w jeden o podwojnej szerokosci
        		 BufferedImage obrazWynikowy;
        		 obrazWynikowy = new BufferedImage (obraz1.getWidth()+obraz2.getWidth(), obraz1.getHeight(), BufferedImage.TYPE_3BYTE_BGR);    
        		  
        		 Graphics g1 = obrazWynikowy.getGraphics ();
        		 g1.drawImage (obraz1, 0, 0, null);
        		 g1.drawImage (obraz2, obraz1.getWidth (), 0, null);
        		 
        		 File zapisanyObraz3D = new File("zrzut3D.jps");
            	 try {
    				ImageIO.write(obrazWynikowy, "jpg", zapisanyObraz3D);
    			} catch (IOException e) {
    				System.err.println("Blad przy zapisie: " + e.toString());
    			}
            	 g.dispose ();
        	 }
        }
        g.dispose();
    }
 
    class MyThread extends Thread{
        @Override
        public void run() {
            for (;;){
                repaint();
                try { 
                	Thread.sleep(10);
                } catch (InterruptedException e) {    }
            }  
        } 
    }
}