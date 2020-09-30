package edu.escuelaing.arep.securesparkapplive;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 *
 * @author Diego23p
 */
public class URLReader {
    
    public static void main(String[] args){
        
        try {

            // Create a file and a password representation
            File trustStoreFile = new File("keystores/myTrustStore");
            char[] trustStorePassword = "123456".toCharArray();
            
            // Load the trust store, the default type is "pkcs12", the alternative is "jks"
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(new FileInputStream(trustStoreFile), trustStorePassword);
            
            // Get the singleton instance of the TrustManagerFactory
            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());

            // Itit the TrustManagerFactory using the truststore object
            tmf.init(trustStore);
            
            //Set the default global SSLContext so all the connections will use it
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            SSLContext.setDefault(sslContext);

            readURL("https://ec2-52-91-92-161.compute-1.amazonaws.com:5000/login");
            
        } catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException | KeyManagementException ex) {
            Logger.getLogger(URLReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void readURL(String siteToRead){
        try {
            //objeto que reprtesenta URL
            URL siteURL = new URL(siteToRead);
            
            //Crea objeto URLConnection
            URLConnection urlConnection = siteURL.openConnection();
            
            //Obtiene los datos del encabezado y los alcena en un map
            Map<String, List<String>> headers = urlConnection.getHeaderFields();
            
            //Obtiene una vista del mnapa como conjunto de pares <k,v> para poder navergarlo
            Set<Map.Entry<String, List<String>>> entrySet = headers.entrySet();
            
            //recorre la lista de campos e imprime los valores
            for (Map.Entry<String, List<String>> entry : entrySet){
                String headerName = entry.getKey();
                
                //si el nombre es nulo significa que es la linea de estado
                if (headerName != null){
                    System.out.println(headerName+":");
                }
                List<String> headersValues = entry.getValue();
                for (String value : headersValues){
                    System.out.println(value);
                }
                System.out.println("");
            }
            
            System.out.println("---------Message Body---------");
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            
            String inputLine = null;
            while((inputLine = reader.readLine()) != null ){
                System.out.println(inputLine);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(URLReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
