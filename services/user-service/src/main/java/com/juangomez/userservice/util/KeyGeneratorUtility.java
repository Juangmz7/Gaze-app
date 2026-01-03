package com.juangomez.userservice.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.util.Base64;

public class KeyGeneratorUtility {

    public static void main(String[] args) {
        try {
            // Create RSA Key Pair Generator
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048); // 2048 bits is standard secure size

            // Generate the Pair
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            // Define paths (adjust relative path if needed)
            // This usually saves to the root of your project
            String privateKeyPath = "private.pem";
            String publicKeyPath = "public.pem";

            // Save Files
            saveKey(privateKeyPath, privateKey, "PRIVATE KEY");
            saveKey(publicKeyPath, publicKey, "PUBLIC KEY");

            System.out.println("Keys generated successfully!");
            System.out.println("Private Key: " + privateKeyPath);
            System.out.println("Public Key: " + publicKeyPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveKey(String fileName, Key key, String type) throws IOException {
        String encoded = Base64.getEncoder().encodeToString(key.getEncoded());

        // Format to standard PEM layout (64 chars per line)
        StringBuilder pem = new StringBuilder();
        pem.append("-----BEGIN ").append(type).append("-----\n");

        int i = 0;
        while (i < encoded.length()) {
            pem.append(encoded, i, Math.min(i + 64, encoded.length()));
            pem.append("\n");
            i += 64;
        }

        pem.append("-----END ").append(type).append("-----");

        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(pem.toString().getBytes());
        }
    }
}