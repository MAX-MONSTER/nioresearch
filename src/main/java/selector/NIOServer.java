package selector;

import util.HandleChannelData;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel sercerChannel = ServerSocketChannel.open();
        sercerChannel.configureBlocking(false);
        sercerChannel.bind(new InetSocketAddress(9090));
        Selector selector = Selector.open();
        sercerChannel.configureBlocking(false);
        sercerChannel.register(selector, SelectionKey.OP_ACCEPT);
        ByteBuffer fileMetaBu = ByteBuffer.allocateDirect(12);
        while (true) {
            int select = selector.select();
            if (select > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()){

                    try {
                        SelectionKey next = iterator.next();
                        if (next.isAcceptable()){
                            //
                            SocketChannel client = sercerChannel.accept();
                            if(null != client){
                                client.configureBlocking(false);
                                System.out.println("get new client : "+client.getRemoteAddress());
                                client.register(selector, SelectionKey.OP_READ);
                            }
                        }else if (next.isReadable()){
                            SocketChannel channel = (SocketChannel)next.channel();
                            channel.configureBlocking(false);
                            HandleChannelData.readData(channel,fileMetaBu,null);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        fileMetaBu.clear();
                    }
                   iterator.remove();
                }
            }

        }
    }
}
