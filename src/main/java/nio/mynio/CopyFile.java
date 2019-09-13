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

        FileInputStream fis = new FileInputStream(infile);
        FileOutputStream fos = new FileOutputStream(outfile);

        FileChannel inChannel = fis.getChannel();
        FileChannel outChannel = fos.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int bytesRead = inChannel.read(buffer);
        while (-1 != bytesRead) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                outChannel.write(buffer);
            }
            buffer.clear();
            bytesRead = inChannel.read(buffer);
        }

        inChannel.close();
        outChannel.close();
        fis.close();
        fos.close();
    }
}