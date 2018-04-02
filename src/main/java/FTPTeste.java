import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.PrintWriter;

public class FTPTeste {

    public static void main(String[] args) {
        FTPClient ftpClient = new FTPClient();
        ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));
        try {
            ftpClient.connect("speedtest.tele2.net");
            int reply = ftpClient.getReplyCode();
            if (FTPReply.isPositiveCompletion(reply)) {
                if (ftpClient.login("anonymous","")){
                    FTPFile[] files = ftpClient.listFiles();
                    for (FTPFile ftpFile: files){
                        System.out.println("ftpFile = " + ftpFile.getName());
                    }
                }

            }
            ftpClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
