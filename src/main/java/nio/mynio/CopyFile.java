package nio.mynio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class CopyFile {
    public static void main(String[] args) throws Exception {
        String infile = "CopyFile.java";
        String outfile = "CopyFile.txt";

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