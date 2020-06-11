package nio.mynio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class CopyFile {
    private static final String ROOT_PATH = System.getProperty("user.dir");

    public static void main(String[] args) {
        String infile = ROOT_PATH + "/src/main/java/nio/mynio/CopyFile.java";
        String outfile = ROOT_PATH + "/CopyFile.txt";

        FileInputStream fis = null;
        FileChannel inChannel = null;
        FileOutputStream fos = null;
        FileChannel outChannel = null;

        try {
            fis = new FileInputStream(infile);
            inChannel = fis.getChannel();
            fos = new FileOutputStream(outfile);
            outChannel = fos.getChannel();

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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inChannel != null) {
                try {
                    inChannel.close();
                    inChannel = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outChannel != null) {
                try {
                    outChannel.close();
                    outChannel = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                    fis = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                    fos = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}