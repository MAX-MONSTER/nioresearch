package noselector;

import beans.FileMeta;
import util.HandleChannelData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;

public class SocketServerNIO {


    public static void main(String[] args) throws IOException {
        ServerSocketChannel open = ServerSocketChannel.open();
        open.configureBlocking(false);
        open.bind(new InetSocketAddress(9090));
        LinkedList<SocketChannel> clients = new LinkedList<>();

        while (true) {
            try {

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                SocketChannel accept = open.accept();
                if (null != accept) {
                    accept.configureBlocking(false);
                    clients.add(accept);
                } else {
                    System.out.println("no_client");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteBuffer fileMetaBu = ByteBuffer.allocateDirect(12);
            Iterator<SocketChannel> iterator = clients.iterator();
            System.out.println("size : " + clients.size());
            while (iterator.hasNext()) {
                SocketChannel client = iterator.next();
                try {
                    if (client.isConnected()) {
                        HandleChannelData.readData(client,fileMetaBu,iterator);
                    }
                    fileMetaBu.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
