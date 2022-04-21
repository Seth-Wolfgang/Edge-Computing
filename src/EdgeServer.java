import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
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

import java.util.ArrayList;
import java.util.List;

public class EdgeServer {

    public EdgeServer(String ip, int port){

        FtpServer ftpServer = null;
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory lFactory = new ListenerFactory();
        FileSystemFactory fsf = new NativeFileSystemFactory();
        ConnectionConfigFactory cFactory = new ConnectionConfigFactory();
        cFactory.setAnonymousLoginEnabled(true);

        lFactory.setPort(2221); //static port
        serverFactory.addListener("default", lFactory.createListener());
        serverFactory.setConnectionConfig(cFactory.createConnectionConfig());

        try {
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
            
        } catch (FtpException e) {
            e.printStackTrace();
        }

    }

    static public void main(String[] args){

        EdgeServer edgeServer = new EdgeServer("35.40.254.5", 5001);

    }

}//end of class
