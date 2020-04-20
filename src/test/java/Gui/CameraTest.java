package Gui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class CameraTest {
    File directoryImg = new File("./img");

    @BeforeEach
    void setUp() {
        if(!directoryImg.exists()) {
            File newDirectory = new File("./img4");
            newDirectory.mkdir();
            directoryImg.renameTo(newDirectory);
        }
    }

    @AfterEach
    void tearDown() {
        if(!directoryImg.exists()) {
            File newDirectory = new File("./img4");
            newDirectory.renameTo(directoryImg);
            newDirectory.delete();
        }
    }

    @Test
    void init() {
        Camera cam = new Camera();
        cam.init();
        boolean directoryExist = directoryImg.exists();
        assertEquals(true, directoryExist);

    }
}