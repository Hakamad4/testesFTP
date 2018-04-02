import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.*;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.SocketException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class FTPSTest {
    private static String localFilename = "C:\\Users\\Erik Hakamada\\Desktop\\Nova\\testando.txt";

    public static void main(String[] args){
        String server = "opsfactor.com";
        int port = 21;
        String user = "hakamada";
        String pass = "ghj5678#";
        try {
//            System.setProperty("javax.net.debug", "ssl");

            FTPSClient ftpClient = new FTPSClient();
            // Connect to host
            ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));
            ftpClient.connect(server, port);
            int reply = ftpClient.getReplyCode();
            if (FTPReply.isPositiveCompletion(reply)) {
                ftpClient.enterLocalPassiveMode();
                // Login
                if (ftpClient.login(user, pass)) {
                    // Set protection buffer size
                    ftpClient.execPBSZ(0);
                    // Set data channel protection to private
                    ftpClient.execPROT("P");
                    // Enter local passive mode
                    ftpClient.type(FTP.BINARY_FILE_TYPE);

                    ftpClient.printWorkingDirectory();


                    ftpClient.logout();
                } else {
                    System.out.println("FTP login failed");
                }
                // Disconnect
                ftpClient.disconnect();

            } else {
                System.out.println("FTP connect to host failed");
            }
        } catch (IOException ioe) {
            System.out.println("FTP client received network error");
            ioe.printStackTrace();
        }
    }
    public static void downloadDirectory(FTPSClient ftpClient, String parentDir,
                                         String currentDir, String saveDir) throws IOException {
        String dirToList = parentDir;
        if (!currentDir.equals("")) {
            dirToList += "/" + currentDir;
        }

        FTPFile[] subFiles = ftpClient.listFiles(dirToList);

        if (subFiles != null && subFiles.length > 0) {
            for (FTPFile aFile : subFiles) {
                String currentFileName = aFile.getName();
                if (currentFileName.equals(".") || currentFileName.equals("..")) {
                    // skip parent directory and the directory itself
                    continue;
                }
                String filePath = parentDir + "/" + currentDir + "/"
                        + currentFileName;
                if (currentDir.equals("")) {
                    filePath = parentDir + "/" + currentFileName;
                }

                String newDirPath = saveDir + parentDir + File.separator
                        + currentDir + File.separator + currentFileName;
                if (currentDir.equals("")) {
                    newDirPath = saveDir + parentDir + File.separator
                            + currentFileName;
                }

                if (aFile.isDirectory()) {
                    // create the directory in saveDir
                    File newDir = new File(newDirPath);
                    boolean created = newDir.mkdirs();
                    if (created) {
                        System.out.println("CREATED the directory: " + newDirPath);
                    } else {
                        System.out.println("COULD NOT create the directory: " + newDirPath);
                    }

                    // download the sub directory
                    downloadDirectory(ftpClient, dirToList, currentFileName,
                            saveDir);
                } else {
                    // download the file
                    boolean success = downloadSingleFile(ftpClient, filePath,
                            newDirPath);
                    if (success) {
                        System.out.println("DOWNLOADED the file: " + filePath);
                    } else {
                        System.out.println("COULD NOT download the file: "
                                + filePath);
                    }
                }
            }
        }
    }

    public static boolean downloadSingleFile(FTPSClient ftpClient,
                                             String remoteFilePath, String savePath) throws IOException {
        File downloadFile = new File(savePath);

        File parentDir = downloadFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdir();
        }

        OutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream(downloadFile));
        try {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            return ftpClient.retrieveFile(remoteFilePath, outputStream);
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}
