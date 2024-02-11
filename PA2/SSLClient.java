import javax.net.ssl.*;
import java.io.IOException;
import java.io.FileInputStream;
import java.security.*;
import java.security.cert.CertificateException;

public class SSLClient {
    public static void main(String[] args)
            throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException,
            KeyManagementException {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("CA1.jks"), "changeit".toCharArray());
        tmf.init(ks);

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, tmf.getTrustManagers(), new SecureRandom());

        SSLSocketFactory sslFactory = sc.getSocketFactory();
        SSLSocket client = (SSLSocket) sslFactory.createSocket("www.secure-server.edu", 4433);

        // print cipher suites avaliable at the client
        String[] cipherSuites = sslFactory.getSupportedCipherSuites();
        for (int i = 0; i < cipherSuites.length; ++i) {
            System.out.println("option " + i + " " + cipherSuites[i]);
        }

        // establish connection
        client.startHandshake();
        SSLSession session = client.getSession();
        System.out.println("Cipher suite: " + session.getCipherSuite());
        System.out.println("Protocol version: " + session.getProtocol());
        System.out.println(session.getPeerCertificates()[0]);
    }
}