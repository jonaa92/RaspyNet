package casasnovas.auger.raspynet;

import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;


public class serverInterface {

    public static String doPost (final String url, final String urlParameters) throws IOException {
        final String charset = "UTF-8";
        // Conexion

        Log.d("urlconnect", urlParameters);
        Log.d("urlconnect", "URL: "+url);

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
        int postDataLength = postData.length;
        // connection params (de POST)
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        connection.setUseCaches(false);

        // Hacer el post de postData (tiene que ser un byte[]. Arriba se ha definido el lenght
        OutputStream output = connection.getOutputStream();
        output.write(postData);
        output.close();

        // Errors
        InputStream inputStream = connection.getErrorStream();
        if (inputStream == null)
            inputStream = connection.getInputStream();

        // Llegim resposta
        BufferedReader responseReader = new BufferedReader(new InputStreamReader(inputStream, charset));

        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = responseReader.readLine()) != null) {
            response.append(inputLine);
        }
        responseReader.close();
        Log.d("urlconnect", "SERVER RESPONSE CODE: " + connection.getResponseCode());
        Log.d("urlconnect", "SERVER RESPONSE: " + response);

        return response.toString();

    }

    public static void downloadFTP (String user, String pass, String filePath, String savePath) throws IOException {
        /*TODO: Aqui se tiene que comprobar si el archivo que se ha descargado existe en el directorio savepath*/
        FTPClient f = new FTPClient();
        f.connect("raspynet.no-ip.org");
        f.login(user, pass);
        FileOutputStream os = new FileOutputStream(savePath);
        f.retrieveFile(filePath, os);
    }
    //UploadPath -> Directorio del server
    //SavePath -> Directorio del movil
    public static void uploadFTP (String user, String pass, String savePath, String uploadPath) throws IOException {
    FTPClient f = new FTPClient();
        f.connect("raspynet.no-ip.org");
        f.login(user, pass);
        FileInputStream is = new FileInputStream(savePath);
        f.storeFile(uploadPath, is);
    }

    public static FTPFile[] lsFTP(String directory, String user, String pass) throws IOException {
        FTPClient f = new FTPClient();
        f.connect("raspynet.no-ip.org");
        f.login(user, pass);
        return f.listFiles(directory);
    }
    public static void deletefile (String filePath, String user, String pass) throws IOException {
        FTPClient f = new FTPClient();
        f.connect("raspynet.no-ip.org");
        f.login(user, pass);
        f.deleteFile(filePath);
    }
}
