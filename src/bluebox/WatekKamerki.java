package bluebox;

import com.atul.JavaOpenCV.Imshow;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

public class WatekKamerki extends Thread{
	
	private static boolean czyPrzechwytywanie3D;
	private static boolean czyPoprawianieKolorow;

	public WatekKamerki(boolean czy3D, boolean czyPoprawianieKolorow) {
		czyPrzechwytywanie3D = czy3D;
		this.czyPoprawianieKolorow = czyPoprawianieKolorow;
		przechwytujObraz();
	}
	
	
	private void przechwytujObraz() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		//VideoCapture vcam = new VideoCapture();
		//vcam.open(0);
		//Imshow im = new Imshow("Przechwytywanie obrazu z kamerki");
		//Mat m = new Mat();
		
		//vcam.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, 640);
		//vcam.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, 480); 
		
		
		// loop until VideoCamera is Available
		//while (vcam.isOpened() == false);
		
		//System.out.println("Found webcam: " + vcam.toString());

		// Bug Fix: Loop until initial image frames are empty
		//while (m.empty()) {
		//	vcam.retrieve(m);
		//}
		
		/*im.showImage(m);

		for (int i=0; i<10; i++) {
			vcam.retrieve(m);
			
			System.out.println("Displaying " + i + " frame");
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.interrupt();*/
		
		/////////////////////////////////////////////////////////////
		if (czyPrzechwytywanie3D == false) {
			MyFrame frame = new MyFrame(0,czyPoprawianieKolorow);
			frame.setTitle("Przechwytywanie obrazu");
			frame.setVisible(true);
		}
		else {
			MyFrame frame0 = new MyFrame(0,czyPoprawianieKolorow);
			frame0.setVisible(true);
			frame0.setTitle("Przechwytywanie obrazu - Pierwszy obraz");
			MyFrame frame1 = new MyFrame(1,czyPoprawianieKolorow);
			frame1.setTitle("Przechwytywanie obrazu - Drugi obraz");
			frame1.setVisible(true);
		}
	}
}
