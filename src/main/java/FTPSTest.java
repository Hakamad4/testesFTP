import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.*;
import org.apache.commons.net.util.TrustManagerUtils;


import java.io.*;

public class FTPSTest {
    private static String localFilename = "C:\\Users\\Erik Hakamada\\Desktop\\Nova\\testando.txt";

    public static void main(String[] args){
        String server = "opsfactor.com";
        int port = 21;
        String user = "hakamada";
        String pass = "ghj5678#";
        try {
//            System.setProperty("javax.net.debug", "ssl");

            SSLSessionReuseFTPSClient ftpClient = new SSLSessionReuseFTPSClient();
            System.setProperty("jdk.tls.useExtendedMasterSecret", "false");
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
                    ftpClient.enterLocalPassiveMode();

                    InputStream in = ftpClient.retrieveFileStream("/Erik/Teste.txt");
                    InputStreamReader isr = new InputStreamReader(in);
                    BufferedReader br = new BufferedReader(isr);
                    String linha;
                    while((linha = br.readLine()) != null ){
                        System.out.println("linha = " + linha);
                    }
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

    private static void printFiles(FTPFile[] ftpFiles) {
        for (FTPFile file : ftpFiles){
            String details = file.getName();
            if (file.isDirectory()){
                details = "["+details+"]";
            }
            details += "\t\t"+ file.getSize();
            System.out.println(details);
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
