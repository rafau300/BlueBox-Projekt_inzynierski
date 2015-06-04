package bluebox;
import java.awt.image.BufferedImage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;

public class VideoCap {

    VideoCapture cap;
    Mat2Image mat2Img = new Mat2Image();
    Mat mat = new Mat();
    
    private boolean czyPoprawianieKolorow;

    VideoCap(int ktoraKamerka,boolean czyPoprawianieKolorow){
    	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        cap = new VideoCapture();
        System.out.println("Uruchomiono kamerke o indeksie: " + ktoraKamerka);
        cap.open(ktoraKamerka);
        
        this.czyPoprawianieKolorow = czyPoprawianieKolorow;
    } 
 
    BufferedImage getOneFrame() {
        cap.retrieve(mat2Img.mat);
        Size rozmiar = new Size();
        rozmiar = mat2Img.mat.size();
        
        //new Thread(new Watek().przetworzObrazyZKamerki(mat2Img.mat,GlowneOkno.pobierzObrazWTle()));
        if (GlowneOkno.pobierzObrazWTleBuffered() != null) {
        	Watek watek = new Watek();
        	new Thread(watek).start();
        	mat2Img.mat = watek.przetworzObrazyZKamerki(mat2Img.mat,GlowneOkno.pobierzObrazWTleBuffered(),czyPoprawianieKolorow);
        	watek.interrupt();
        }
        else {
        	 //zamiana kanalow z BGR na RGB
            for (int i=0;i<rozmiar.height;i++) {
            	for (int j=0; j<rozmiar.width;j++) {
            		double[] kolor = mat2Img.mat.get(i, j); 
            		double pom = kolor[0];
    		        kolor[0] = kolor[2];
    		        kolor[2] = pom;
    		        mat2Img.mat.put(i, j, kolor);
            	}
            }
        }
        return mat2Img.getImage(mat2Img.mat);
    }
    
    public void zwolnijKamerke() {
    	cap.release();
    }
}