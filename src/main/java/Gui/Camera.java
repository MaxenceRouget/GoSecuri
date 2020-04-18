package Gui;

import FireBase.FireBaseAccess;
import Model.Tools;
import Model.User;
import Utils.Utils;
import com.google.firebase.database.*;
import com.sun.tools.javac.Main;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.TileObserver;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import static javax.imageio.ImageIO.read;

public class Camera extends JFrame {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
    private static JFrame frame = new JFrame("GoSecuriApp");
    FireBaseAccess fireBaseAccess = FireBaseAccess.getInstance(); //Singleton
    private static User userAuth;
    private static long idUser;

    private List<JCheckBox> checkBoxes = new ArrayList<>();
    private List<Tools> tools = new ArrayList<>();
    private CascadeClassifier faceCascade;
    CascadeClassifier eyesCascade;

    private JPanel MainPanel;
    private JButton But_Ident;
    private JPanel CameraPanel;

    private DaemonThread myThread = null;
    VideoCapture webSource = null;
    Mat currentFrame = new Mat();
    Mat face = new Mat();
    MatOfByte mem = new MatOfByte();

    JButton back= new JButton("Finish");
    JCheckBox cb1 = new JCheckBox("Mousqueton");
    JCheckBox cb2 = new JCheckBox("Gants d'intervention");
    JCheckBox cb3 = new JCheckBox("Ceinture de sécurite tactique");
    JCheckBox cb4 = new JCheckBox("Detecteur de metaux");
    JCheckBox cb5 = new JCheckBox("Brassard de securite");
    JCheckBox cb6 = new JCheckBox("Lampe torche");
    JCheckBox cb7 = new JCheckBox("Bandeau Agent cynophile");
    JCheckBox cb8 = new JCheckBox("Gilet pare-balle");
    JCheckBox cb9 = new JCheckBox("Chemise manches courtes");
    JCheckBox cb10 = new JCheckBox("Blouson");
    JCheckBox cb11 = new JCheckBox("Coupe-vent");
    JCheckBox cb12 = new JCheckBox("Talkie-Walkie");
    JCheckBox cb13 = new JCheckBox("Kit oreillette");
    JCheckBox cb14 = new JCheckBox("Taser");

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
                            webSource.retrieve(currentFrame);
                            if ( currentFrame.empty() ) continue;
                            Imgcodecs.imencode(".bmp", currentFrame, mem);
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
    public void AddCheckBoxInList(){
        checkBoxes.add(cb1);
        checkBoxes.add(cb2);
        checkBoxes.add(cb3);
        checkBoxes.add(cb4);
        checkBoxes.add(cb5);
        checkBoxes.add(cb6);
        checkBoxes.add(cb7);
        checkBoxes.add(cb8);
        checkBoxes.add(cb9);
        checkBoxes.add(cb10);
        checkBoxes.add(cb11);
        checkBoxes.add(cb12);
        checkBoxes.add(cb13);
        checkBoxes.add(cb14);
    }
    public Camera() {
        AddCheckBoxInList();
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
        But_Ident.setEnabled(true);  //IndentificationButton button

        But_Ident.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                File file = new File("./img/Unknow/What.png");
                Imgcodecs.imwrite(file.getPath(), currentFrame);
                if(detectFace(currentFrame)){
                    myThread.runnable = false;
                    if(Reconnize())
                    {
                        MainPanel.setVisible(false);
                        InitManageForm();
                    }else{
                        But_Ident.setEnabled(true);
                        myThread.runnable = true;
                    }
                }
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
            return true;
        }
        return false;
    }
    public static boolean Reconnize(){
        try {
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
            Mat labels = new Mat(imageFiles.length, 1, CvType.CV_32SC1);
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
            fileUnKnow = Imgcodecs.imread("./img/Unknow/What2.png");
            Imgproc.cvtColor(fileUnKnow, fileUnKnow, Imgproc.COLOR_BGR2GRAY);
            Imgproc.equalizeHist(fileUnKnow, fileUnKnow);

            model.read("MyTrainnedData");
            int predict = model.predict_label(fileUnKnow);
            System.out.println("Le nom de la personne est : " + names.get(predict) + " Son id est " + predict);
            idUser = predict;
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public void InitManageForm(){
        JPanel ManagerFormPanel = new JPanel();
        ManagerFormPanel.setLayout(new GridLayout(5,3));
        frame.setContentPane(ManagerFormPanel);
        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                ManagerFormPanel.setVisible(false);
                frame.setContentPane(new Camera().MainPanel);
                But_Ident.setEnabled(true);
                CameraPanel.setVisible(true);
                myThread.runnable = true;
            }});

        try {
            var listData = fireBaseAccess.GetDataForTools();
            for (var document : listData){
                for (var checkBox : checkBoxes){
                    var test = document.getId();
                    var test2 = checkBox.getText();
                    int A =1;
                    if(document.getId().equals(checkBox.getText())){
                        tools.add(new Tools(checkBox.getText(),(ArrayList) document.get("Owner"),
                                (long)document.get("Total"),(long)document.get("Used")));
                    }
                }
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        var test = idUser;

        checkCheckBox();
        for(var checkBox : checkBoxes){
            ManagerFormPanel.add(checkBox);
        }
        ManagerFormPanel.setVisible(true);
    }
    private void checkCheckBox(){
        for(var box : checkBoxes) {
            int a = 1;
            for(var tool : tools){
                if(box.getText().equals(tool.getName())){
                    if(tool.getOwner().contains(idUser)){
                        box.setEnabled(true);
                        box.setSelected(true);
                    }
                    else if(!tool.getOwner().contains(idUser) && tool.getUsed() == tool.getTotal()){
                        box.setEnabled(false);
                    }
                    else if(!tool.getOwner().contains(idUser) && tool.getUsed() < tool.getTotal()){
                        box.setEnabled(true);
                        box.setSelected(false);
                    }
                }
            }
        }
    }
    public static void main(String[] args) throws IOException {
        frame.setContentPane(new Camera().MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 800));
        frame.pack();
        frame.setVisible(true);
    }
}
