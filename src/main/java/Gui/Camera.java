package Gui;

import org.opencv.core.*;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import static javax.imageio.ImageIO.read;

public class Camera extends JFrame {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
    private CascadeClassifier faceCascade;
    CascadeClassifier eyesCascade;

    private JPanel MainPanel;
    private JButton But_Ident;
    private JPanel CameraPanel;

    private DaemonThread myThread = null;
    VideoCapture webSource = null;
    Mat frame = new Mat();
    Mat face = new Mat();
    MatOfByte mem = new MatOfByte();


    public static HashMap<Integer, String> names = new HashMap<Integer, String>();

    class DaemonThread implements Runnable{
        protected volatile boolean runnable = false;

        @Override
        public  void run()
        {
            synchronized(this)
            {
                while(runnable)
                {
                    if(webSource.grab())
                    {
                        try
                        {
                            webSource.retrieve(frame);
                            if ( frame.empty() ) continue;
                            Imgcodecs.imencode(".bmp", frame, mem);
                            Image im = read(new ByteArrayInputStream(mem.toArray()));

                            BufferedImage buff = (BufferedImage) im;
                            Graphics g = CameraPanel.getGraphics();
                            int w = getWidth();
                            if (g.drawImage(buff, 0, 0, CameraPanel.getWidth(), CameraPanel.getHeight() , 0, 0, buff.getWidth(), buff.getHeight(), null))

                                if(runnable == false)
                                {
                                    webSource.release();
                                    System.out.println("Going to wait()");
                                    this.wait();
                                }
                        }
                        catch(Exception ex)
                        {
                            System.out.println("Error");
                        }
                    }
                }
            }
        }
    }

    public Camera() {
        this.faceCascade = new CascadeClassifier();

        String fileFace = "data/haarcascades/haarcascade_frontalface_alt.xml";
        faceCascade.load(fileFace);

        eyesCascade = new CascadeClassifier();
        String fileEyes = "data/haarcascades/haarcascade_eye_tree_eyeglasses.xml";
        eyesCascade.load(fileEyes);

        webSource =new VideoCapture(0);
        myThread = new DaemonThread();
        Thread t = new Thread(myThread);
        t.setDaemon(true);
        myThread.runnable = true;
        t.start();
        But_Ident.setEnabled(true);  //start button

        But_Ident.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                File file = new File("./img/Unknow/What.png");
                Imgcodecs.imwrite(file.getPath(), frame);
                var test = detectFace(frame);

                myThread.runnable = false;
                But_Ident.setEnabled(true);
                Reconnize();
            }
        });
    }

    public boolean detectFace(Mat frame){
        MatOfRect faces = new MatOfRect();
        faceCascade.detectMultiScale(frame, faces);
        if(faces.toArray().length > 0) {
            Rect rectCrop = new Rect(faces.toArray()[0].tl(), faces.toArray()[0].br());
            face = new Mat(frame, rectCrop);
            File file = new File("./img/Unknow/What2.png");
            Imgcodecs.imwrite(file.getPath(), face);
            System.out.println(rectCrop.y);
            return true;
        }
        return false;
    }

    public static void Reconnize(){
        File root = new File("./img/Know");
        FilenameFilter imgFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                return name.endsWith(".png");
            }
        };
        File[] imageFiles = root.listFiles(imgFilter);
        List<Mat> images = new ArrayList<Mat>();
        System.out.println("THE NUMBER OF IMAGES READ IS: " + imageFiles.length);
        Mat labels = new Mat(imageFiles.length,1, CvType.CV_32SC1);
        int counter = 0;

        for (File image : imageFiles) {
            Mat img = Imgcodecs.imread(image.getAbsolutePath());
            Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
            Imgproc.equalizeHist(img, img);
            int label = Integer.parseInt(image.getName().split("\\-")[0]);
            String labnname = image.getName().split("\\_")[0];
            String name = labnname.split("\\-")[1];
            names.put(label, name);
            images.add(img);

            labels.put(counter, 0, label);
            counter++;
        }
        LBPHFaceRecognizer model = LBPHFaceRecognizer.create();
        model.train(images, labels);
        model.save("MyTrainnedData");

        Mat fileUnKnow = new Mat();
        fileUnKnow = Imgcodecs.imread("./img/Unknow/What2.jpg");
        Imgproc.cvtColor(fileUnKnow, fileUnKnow,Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(fileUnKnow, fileUnKnow);

        model.read("MyTrainnedData");
        int predict = model.predict_label(fileUnKnow);
        System.out.println("Le nom de la personne est : "+ names.get(predict));
    }
    private static String encodeFileToBase64Binary(File file) throws Exception{
        FileInputStream fileInputStreamReader = new FileInputStream(file);
        byte[] bytes = new byte[(int)file.length()];
        fileInputStreamReader.read(bytes);
        return new String(Base64.getEncoder().encode(bytes),"UTF-8");
    }
    public static void decodeFileFromBase64Binary(String fileStringed) throws IOException {
        byte[] bar = Base64.getDecoder().decode(fileStringed.getBytes());
        ByteArrayInputStream bis = new ByteArrayInputStream(bar);
        BufferedImage bImage2 = ImageIO.read(bis);
        ImageIO.write(bImage2, "jpg", new File("./output.jpg") );
        System.out.println("image created");

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("GoSecuriApp");
        frame.setContentPane(new Camera().MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 800));
        frame.pack();
        frame.setVisible(true);

    }
}
