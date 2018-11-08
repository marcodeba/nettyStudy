package nio.mynio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class CopyFile {
    public static void main(String[] args) throws Exception {
        String infile = "CopyFile.java";
        String outfile = "CopyFile.txt";

        FileInputStream fin = new FileInputStream(infile);
        FileOutputStream fout = new FileOutputStream(outfile);

        FileChannel fcin = fin.getChannel();
        FileChannel fcout = fout.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int bytesRead = fcin.read(buffer);
        while (-1 != bytesRead) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                fcout.write(buffer);
            }
            buffer.clear();
            bytesRead = fcin.read(buffer);
        }
        fcin.close();
        fcout.close();
        fin.close();
        fout.close();
    }
}