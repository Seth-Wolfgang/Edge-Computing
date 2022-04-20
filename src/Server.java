import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.filesystem.nativefs.NativeFileSystemFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;

public class Server
{
    //initialize socket and input stream
    private Socket          socket   = null;
    private ServerSocket    server   = null;
    private DataInputStream in       =  null;

    // constructor with port
    public Server(int port) throws FtpException, EOFException {
        FtpServer ftpServer;
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory lFactory = new ListenerFactory();
        FileSystemFactory fsf = new NativeFileSystemFactory();
        ConnectionConfigFactory cFactory = new ConnectionConfigFactory();
        cFactory.setAnonymousLoginEnabled(true);

        lFactory.setPort(2221);
        serverFactory.addListener("default", lFactory.createListener());
        serverFactory.setConnectionConfig(cFactory.createConnectionConfig());


        // starts server and waits for a connection
        try
        {
            List<Authority> authorities = new ArrayList<Authority>();
            authorities.add(new WritePermission());
            BaseUser user = new BaseUser();
            user.setAuthorities(authorities);

            user.setName("user");
            user.setPassword("");
            user.setHomeDirectory("ftpResources");

            serverFactory.setFileSystem(fsf);
            serverFactory.getUserManager().save(user);
            ftpServer = serverFactory.createServer();
            ftpServer.start();

            server = new ServerSocket(port);
            System.out.println("Server started");
            System.out.println("Waiting for a client ...");
            socket = server.accept();
            System.out.println("Client accepted");

            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            String line = "";

            // reads message from client until "Over" is sent
            while (!line.equals("Over"))
            {
                try
                {
                    line = in.readUTF();
                    System.out.println(line);
                }
                catch(IOException e)
                {
                    System.out.println(e);
                }
            }
            System.out.println("Closing connection");

            // close connection
            ftpServer.stop();
            socket.close();
            in.close();
        } catch (EOFException e){
            throw new EOFException();
        } catch(IOException i)
        {
            System.out.println(i);
        } catch (FtpException e) {
            throw new FtpException();
        }
    }

    public static void main(String[] args) throws FtpException, EOFException {
        Server server = new Server(5000);
    }
}