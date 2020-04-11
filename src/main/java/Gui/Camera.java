package Gui;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.face.EigenFaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.imageio.ImageIO.read;

public class Camera extends JFrame {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    private JPanel MainPanel;
    private JButton startButtonTEST;
    private JButton button2;
    private JPanel CameraPanel;

    private DaemonThread myThread = null;
    int count = 0;
    VideoCapture webSource = null;

    Mat frame = new Mat();
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
        /*startButtonTEST.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                webSource =new VideoCapture(0);
                myThread = new DaemonThread();
                Thread t = new Thread(myThread);
                t.setDaemon(true);
                myThread.runnable = true;
                t.start();
                startButtonTEST.setEnabled(false);  //start button
                button2.setEnabled(true);  // stop button
            }
        });*/

        //Run on lunch
        webSource =new VideoCapture(0);
        myThread = new DaemonThread();
        Thread t = new Thread(myThread);
        t.setDaemon(true);
        myThread.runnable = true;
        t.start();
        startButtonTEST.setEnabled(false);  //start button
        button2.setEnabled(true);  // stop button

        button2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                File file = new File("/Users/maxencerouget/Desktop/OpencvPython/img/Unknow/What.jpg");
                Imgcodecs.imwrite(file.getPath(), frame);

                myThread.runnable = false;
                button2.setEnabled(false);
                startButtonTEST.setEnabled(true);

                try {
                    //var test = encodeFileToBase64Binary(file);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                myThread.runnable = true;
                button2.setEnabled(true);

            }
        });
    }

    public static List<String> ListFile(String pathToFolder){
        List<String> filesKnow = new ArrayList<String>();

        try (Stream<Path> walk = Files.walk(Paths.get(pathToFolder))) {

            filesKnow = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filesKnow;
    }
    public static void MyTest(){
        File root = new File("./img/Know");


        FilenameFilter imgFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                return name.endsWith(".jpg");
            }
        };

        File[] imageFiles = root.listFiles(imgFilter);

        List<Mat> images = new ArrayList<Mat>();

        System.out.println("THE NUMBER OF IMAGES READ IS: " + imageFiles.length);

        List<Integer> trainingLabels = new ArrayList<>();

        Mat labels = new Mat(imageFiles.length,1, CvType.CV_32SC1);

        int counter = 0;

        for (File image : imageFiles) {
            // Parse the training set folder files
            Mat img = Imgcodecs.imread(image.getAbsolutePath());
            // Change to Grayscale and equalize the histogram
            Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
            Imgproc.equalizeHist(img, img);
            // Extract label from the file name
            int label = Integer.parseInt(image.getName().split("\\-")[0]);
            // Extract name from the file name and add it to names HashMap
            String labnname = image.getName().split("\\_")[0];
            String name = labnname.split("\\-")[1];
            names.put(label, name);
            // Add training set images to images Mat
            images.add(img);

            labels.put(counter, 0, label);
            counter++;
        }
        LBPHFaceRecognizer model = LBPHFaceRecognizer.create();
        //EigenFaceRecognizer model = EigenFaceRecognizer.create();
        model.train(images, labels);
        model.save("MyTrainnedData");

        Mat fileUnKnow = new Mat();
        fileUnKnow = Imgcodecs.imread("./img/Unknow/What.jpg");
        Imgproc.cvtColor(fileUnKnow, fileUnKnow,Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(fileUnKnow, fileUnKnow);

        int predict = model.predict_label(fileUnKnow);
        System.out.println("Predict : " + predict);
    }

    public static void Reconizer(){
        List<String> filesKnow = ListFile("./img/Know");
        List<String> filesUnKnow = ListFile("./img/Unknow");
        List<Mat> MatUnKnow = new ArrayList<Mat>();
        Mat fileKnow = new Mat();
        Mat fileToCompare = new Mat();

        for(var path : filesKnow){
            fileKnow = Imgcodecs.imread(path);
        }
        for(var path : filesUnKnow){
            Mat matTemp = Imgcodecs.imread(path);
            MatUnKnow.add(matTemp);
        }

        EigenFaceRecognizer model = EigenFaceRecognizer.create();
        model.train(MatUnKnow, fileKnow);
        fileToCompare = Imgcodecs.imread("./img/Unknow/What.jpg");
        int predict = model.predict_label(fileToCompare);
        var test = "testy";
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
/*        JFrame frame = new JFrame("GoSecuriApp");
        frame.setContentPane(new Camera().MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 800));
        frame.pack();
        frame.setVisible(true);*/
        //Reconizer();
        MyTest();
    }
}
