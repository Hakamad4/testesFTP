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
            if (ftpClient.login("anonymous","")){
                FTPFile[] files = ftpClient.listFiles();
                printFiles(files);
                ftpClient.logout();
            }

            ftpClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void printFiles(FTPFile[] ftpFiles) {
        for (FTPFile file : ftpFiles){
            String details = file.getName();
            double size = file.getSize();
            if (file.isDirectory()){
                details = "["+details+"]";
            }else{
                double KB = (size / 1024);
                double MB = (size / 1024)/1024;
                size = KB;
            }
            details += "\t\t\t"+ size + "KB";
            System.out.println(details);
        }
    }
}
