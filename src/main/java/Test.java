import FireBase.FireBaseAccess;
import Gui.Camera;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.core.Scalar;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Test {
    // Compulsory
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        JFrame frame = new JFrame("toto");
        frame.setContentPane(new Camera().MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 800));
        frame.pack();
        frame.setVisible(true);

        FireBaseAccess FB = new FireBaseAccess();
        System.out.println("Begin ---");
        FB.GetDataFromDatabase();
        System.out.println("End ---");
    }
}