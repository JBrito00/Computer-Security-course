import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;
import java.security.cert.*;
import java.util.Base64;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/* 
1. Geração chave simetrica(ks)
2. Geração de iv
3. AAD -> Header 
4. Obtenção de chave publica(kpub)
5. Cifra de ks com kpub -> "RSA/ECB/OAEPPadding" (cipher.MODE_WRAP  /n ...Wrap(Key) /n ... /n Cypher.MODE_UNWRAP /n key = ...unwrap())
6. Cifra de msg com ks -> "AES/GCM/NoPadding" -> updateADD()
7. Codificação BASE64 -> CLASSE Base64 (getUrlEncoder() /n withoutPAdding() /n ... /n getUrlDecoder())
8. Concatenação
*/

public class ConsoleApp {

    public static void main(String args[]) throws Exception {

        if (args.length != 3) {
            System.out.println("Invalid number of arguments");
            System.out.println("Usage: java ConsoleApp <mode> <string> <recipient>");
            return;
        }

        String command = args[0];
        String inputString = args[1];
        String recipient = args[2];

        // CIFRA
        if (command.equalsIgnoreCase("enc")) {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");

            // Opcional, se não passar um SecureRandom ao método init de keyGen
            SecureRandom secRandom = new SecureRandom();

            // Opcional
            keyGen.init(secRandom);

            SecretKey ks = keyGen.generateKey();

            // Gera o objeto da cifra simetrica com um modo de operação que precisa de IV
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            // Associa a chave key a cifra
            byte[] iv = new byte[12];
            secRandom.nextBytes(iv);
            GCMParameterSpec params = new GCMParameterSpec(128, iv);
            cipher.init(cipher.ENCRYPT_MODE, ks, params);

            // Obtém o IV gerado aleatoriamente durante o init()
            // byte[] iv = cipher.getIV();

            // 3. AAD -> Header
            String header = "{\"alg\":\"RSA-OAEP\",\"enc\":\"A256GCM\"}";
            System.out.println("Header: " + header);
            // byte[] aad = header.getBytes();
            String encodedHeader = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(header.getBytes(StandardCharsets.UTF_8));
            System.out.println("Encoded Header: " + encodedHeader);
            String decodedHeader = new String(Base64.getUrlDecoder().decode(encodedHeader));
            System.out.println("Decoded Header: " + decodedHeader);

            // 4. Obtenção de chave publica(kpub)
            // Instancia uma factory de certificados X.509
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            // Obtém o certificado a partir do ficheiro .cer
            InputStream fis = new FileInputStream(recipient);
            X509Certificate cert = (X509Certificate) factory.generateCertificate(fis);
            fis.close();
            PublicKey kpub = cert.getPublicKey();

            // 5. Cifra de ks com kpub
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
            rsaCipher.init(Cipher.WRAP_MODE, kpub);
            byte[] wrappedKey = rsaCipher.wrap(ks);
            String encodedWrappedKey = Base64.getUrlEncoder().withoutPadding().encodeToString(wrappedKey);
            System.out.println("Encoded Wrapped Key: " + encodedWrappedKey);

            // 6. Cifra de msg com ks
            Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
            aesCipher.init(Cipher.ENCRYPT_MODE, ks, params);
            aesCipher.updateAAD(encodedHeader.getBytes());
            byte[] ciphertextPlusTag = aesCipher.doFinal(inputString.getBytes());
            // separa o ciphertext da tag de autenticacao (16 bytes)
            byte[] ciphertext = new byte[ciphertextPlusTag.length - 16];
            ciphertext = Arrays.copyOfRange(ciphertextPlusTag, 0, ciphertextPlusTag.length - 16);
            byte[] tag = Arrays.copyOfRange(ciphertextPlusTag, ciphertextPlusTag.length - 16, ciphertextPlusTag.length);

            String encodedCiphertext = Base64.getUrlEncoder().withoutPadding().encodeToString(ciphertext);
            String encodedTag = Base64.getUrlEncoder().withoutPadding().encodeToString(tag);

            // 7. Codificação BASE64
            String encodedIv = Base64.getUrlEncoder().withoutPadding().encodeToString(iv);
            String finalMessage = encodedHeader + "." + encodedWrappedKey + "." + encodedIv + "." + encodedCiphertext
                    + "." + encodedTag;

            System.out.println("Final Message v");
            System.out.println(finalMessage);

            // DECIFRA
        } else if (command.equalsIgnoreCase("dec")) {
            // Parse the inputString into parts
            String[] parts = inputString.split("\\.");
            if (parts.length != 5) {
                System.err.println("Invalid JWE token: Incorrect number of parts");
                return;
            }
            // Extract encoded components
            String encodedHeader = parts[0];
            String encodedWrappedKey = parts[1];
            String encodedIv = parts[2];
            String encodedCiphertext = parts[3];
            String encodedTag = parts[4];

            // Decode the Base64 encoded parts
            byte[] iv = Base64.getUrlDecoder().decode(encodedIv);
            byte[] ciphertext = Base64.getUrlDecoder().decode(encodedCiphertext);
            byte[] tag = Base64.getUrlDecoder().decode(encodedTag);

            // Load recipient's private key from PFX keystore
            PrivateKey privateKey = Keystore.getPrivateKey(recipient, "changeit");

            // Decrypt wrapped AES key using RSA private key
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
            rsaCipher.init(Cipher.UNWRAP_MODE, privateKey);
            SecretKey unwrappedKey = (SecretKey) rsaCipher.unwrap(Base64.getUrlDecoder().decode(encodedWrappedKey),
                    "AES", Cipher.SECRET_KEY);

            // Decrypt the ciphertext using AES-GCM
            Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
            aesCipher.init(Cipher.DECRYPT_MODE, unwrappedKey, gcmSpec);
            aesCipher.updateAAD(encodedHeader.getBytes());
            byte[] decryptedMessageBytes = aesCipher.doFinal(concatArrays(ciphertext, tag)); // ???
            String decryptedMessage = new String(decryptedMessageBytes, StandardCharsets.UTF_8);
            System.out.println("Decrypted message: " + decryptedMessage);

            // INVALID COMMAND
        } else {
            System.out.println("Invalid command");
            System.out.println("Usage: java ConsoleApp <mode> <string> <recipient>");
            return;
        }

        /*
         * ...
         *
         * restantes operações de cifra
         */

        // Decifra com mesma chave e iv usado na cifra
        // cipher.init(cipher.DECRYPT_MODE, ks, new IvParameterSpec(iv));

        /*
         * ...
         *
         * restantes operações de decifra
         */
    }

    // Helper function to concatenate byte arrays
    public static byte[] concatArrays(byte[] first, byte[] second) {
        byte[] result = new byte[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

}