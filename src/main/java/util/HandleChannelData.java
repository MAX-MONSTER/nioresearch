package util;

import beans.FileMeta;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.UUID;

public class HandleChannelData {
    public static final String FILE_PATH = "C:\\work\\recv_file_";
    public static void readData(SocketChannel client, ByteBuffer fileMetaBu, Iterator<SocketChannel> iterator) throws IOException {
        while (true) {

            int read = client.read(fileMetaBu);
            if (read > 0) {
                FileMeta fileMeta = initFileData(fileMetaBu, client);
                if(null !=fileMeta){
                    FileOutputStream f = new FileOutputStream(new File(FILE_PATH+fileMeta.getName()));
                    buildFile(f, fileMeta.getFileSize(), 4096, client);
                }
                break;
            } else if (read == -1) {
                client.close();
                if(null != iterator){
                    iterator.remove();
                }
                break;
            } else {
                break;
            }
        }
    }


    public static FileMeta initFileData(ByteBuffer fileMetaBu, SocketChannel sc) throws IOException {

        FileMeta fileMeta = new FileMeta();
        fileMetaBu.flip();

        fileMeta.setFileSize(fileMetaBu.getLong());
        int nameSize = fileMetaBu.getInt();
        if(0 == nameSize){
            fileMeta.setName(UUID.randomUUID().toString());
            return fileMeta;
        }
        ByteBuffer nameBuffer = ByteBuffer.allocateDirect(nameSize);
        sc.read(nameBuffer);
        nameBuffer.flip();
        byte[] bytes = new byte[nameSize];
        nameBuffer.get(bytes);
        fileMeta.setName(new String(bytes));
        return fileMeta;
    }

    public static void buildFile(FileOutputStream fos, long len, int capcity, SocketChannel soc) throws IOException {
        ByteBuffer allocate = ByteBuffer.allocate(capcity);
        int read;
        FileChannel channel = fos.getChannel();
        while ((read = soc.read(allocate)) > 0) {
            allocate.flip();
            while (allocate.hasRemaining()) {
                channel.write(allocate);
            }

            fos.flush();
            allocate.clear();
            len -= read;
            if (len < 0) {
                return;
            }
            if (len < capcity) {
                capcity = (int) len;
                buildFile(fos, len, capcity, soc);
                channel.close();
                fos.close();
                return;
            }
        }
        //分包情况 由于是大文件传输 暂不考虑   如果是一般数据传输就可以走过一次  让数据暂存再buffer 下次出发读数据再读取出


    }
}
