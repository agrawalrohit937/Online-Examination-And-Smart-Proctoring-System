import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List; 

public class ProctorWindow {

    private JFrame window;
    private Webcam webcam;

    public ProctorWindow(int webcamIndexToUse) {
        
        List<Webcam> webcams = Webcam.getWebcams();
        
        if (webcams.isEmpty()) {
            System.out.println("No webcam found.");
            return; 
        }

        if (webcamIndexToUse < 0 || webcamIndexToUse >= webcams.size()) {
            System.out.println("Invalid index " + webcamIndexToUse + ". Using default webcam (0).");
            webcamIndexToUse = 0; 
        }

        webcam = webcams.get(webcamIndexToUse);
        System.out.println("Using webcam: " + webcam.getName());

        Dimension[] sizes = webcam.getViewSizes();
        webcam.setViewSize(sizes[0]); 

        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setMirrored(true); 

        window = new JFrame("Proctoring Feed");
        window.add(panel);
        window.setResizable(true);
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // --- YEH HAI NAYA FIX ---
        // Is line se webcam window kabhi focus nahi legi
        window.setFocusableWindowState(false);
        // --- FIX ENDS ---

        window.pack();
        window.setLocation(0, 0); 
        window.setVisible(true);

        System.out.println("Webcam proctoring started.");
    }

    public void closeWindow() {
        if (webcam != null) {
            webcam.close();
            window.dispose();
            System.out.println("Webcam proctoring stopped.");
        }
    }
}