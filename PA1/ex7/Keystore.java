import java.io.FileInputStream;
import java.security.KeyStore;

import java.security.PrivateKey;
import java.util.Enumeration;

public class Keystore {

    public static PrivateKey getPrivateKey(String pathKeystore, String passwordKeystore) {
        try {
            // Load the keystore from the given file
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(new FileInputStream(pathKeystore), passwordKeystore.toCharArray());

            // Iterate through the aliases and return first private key found
            Enumeration<String> aliases = ks.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                if (ks.isKeyEntry(alias)) {
                    PrivateKey privateKey = (PrivateKey) ks.getKey(alias, passwordKeystore.toCharArray());
                    return privateKey;
                }
            }

            System.out.println("No private key found in the keystore.");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
