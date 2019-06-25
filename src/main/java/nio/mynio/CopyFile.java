package nio.mynio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class CopyFile {
    private static final String ROOT_PATH = System.getProperty("user.dir");

    public static void main(String[] args) throws Exception {
        String infile = ROOT_PATH + "/src/main/java/nio/mynio/CopyFile.java";
        String outfile = ROOT_PATH + "/CopyFile.txt";

        FileInputStream fIn = new FileInputStream(infile);
        FileOutputStream fOut = new FileOutputStream(outfile);

        FileChannel fcIn = fIn.getChannel();
        FileChannel fcOut = fOut.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int bytesRead = fcIn.read(buffer);
        while (-1 != bytesRead) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                fcOut.write(buffer);
            }
            buffer.clear();
            bytesRead = fcIn.read(buffer);
        }
        fcIn.close();
        fcOut.close();
        fIn.close();
        fOut.close();
    }
}