package Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public abstract class Utils {
    public static String encodeFileToBase64Binary(File file) throws Exception{
        FileInputStream fileInputStreamReader = new FileInputStream(file);
        byte[] bytes = new byte[(int)file.length()];
        fileInputStreamReader.read(bytes);
        return new String(Base64.getEncoder().encode(bytes),"UTF-8");
    }
    public static void decodeFileFromBase64Binary(String fileStringed, String nameFile,String Path) throws IOException {
        byte[] bar = Base64.getDecoder().decode(fileStringed.getBytes());
        ByteArrayInputStream bis = new ByteArrayInputStream(bar);
        BufferedImage bImage2 = ImageIO.read(bis);
        ImageIO.write(bImage2, "png", new File(Path+"/"+nameFile) );
        System.out.println("image created");

    }
}
