package Gui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CameraTest {
    File directoryImg = new File("./img");
    Camera cam = new Camera();

    void setUp() {
        if(!directoryImg.exists()) {
            File newDirectory = new File("./img4");
            newDirectory.mkdir();
            directoryImg.renameTo(newDirectory);
        }
    }

    void tearDown() {
        if(!directoryImg.exists()) {
            File newDirectory = new File("./img4");
            newDirectory.renameTo(directoryImg);
            newDirectory.delete();
        }
    }

    @Test
    void init() {
        setUp();
        cam.init();
        boolean directoryExist = directoryImg.exists();
        assertTrue(directoryExist);
        tearDown();
    }
    @Test
    void addCheckboxes(){
       List<JCheckBox> listToAssert;
       listToAssert = cam.checkBoxes;
       assertEquals(14,listToAssert.size());
    }
}