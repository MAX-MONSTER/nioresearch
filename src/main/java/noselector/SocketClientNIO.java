package noselector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class SocketClientNIO {
    public static void main(String[] args) {
        try {
            File file =new File("C:\\work\\jj.zip");
            nioClient(9090, file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }



    public static void nioClient(int port,File file) throws IOException, InterruptedException {
        consolePID();
        SocketChannel open = SocketChannel.open();
        open.connect(new InetSocketAddress(port));
        open.configureBlocking(false);
        try {
            FileChannel channel = new FileInputStream(file).getChannel();
            byte[] bytes = file.getName().getBytes();
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(12+bytes.length);
            byteBuffer.putLong(channel.size());
            byteBuffer.putInt(bytes.length);
            byteBuffer.put(bytes);
            byteBuffer.flip();
            open.write(byteBuffer);
            channel.transferTo(0, channel.size(), open);
            channel.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Thread.sleep(2000);
        open.close();
    }


    public static void consolePID() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        System.out.println(name);
        String pid = name.split("@")[0];
        System.out.println("Pid is:" + pid);
    }
}
