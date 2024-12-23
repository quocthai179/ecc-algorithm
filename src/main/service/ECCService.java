package main.service;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import javax.crypto.KeyAgreement;

public class ECCService {
    public static void main(String[] args) {
        try {
            // Step 1: Generate Key Pairs for two parties (Alice and Bob)
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
            keyPairGenerator.initialize(new ECGenParameterSpec("secp256r1")); // Using a standard curve

            KeyPair aliceKeyPair = keyPairGenerator.generateKeyPair();
            KeyPair bobKeyPair = keyPairGenerator.generateKeyPair();

            // Step 2: Perform Key Agreement
            KeyAgreement aliceKeyAgreement = KeyAgreement.getInstance("ECDH");
            aliceKeyAgreement.init(aliceKeyPair.getPrivate());
            aliceKeyAgreement.doPhase(bobKeyPair.getPublic(), true);

            KeyAgreement bobKeyAgreement = KeyAgreement.getInstance("ECDH");
            bobKeyAgreement.init(bobKeyPair.getPrivate());
            bobKeyAgreement.doPhase(aliceKeyPair.getPublic(), true);

            // Step 3: Generate Shared Secret
            byte[] aliceSharedSecret = aliceKeyAgreement.generateSecret();
            byte[] bobSharedSecret = bobKeyAgreement.generateSecret();

            // Verify that the shared secret is the same
            System.out.println("Alice's Shared Secret: " + bytesToHex(aliceSharedSecret));
            System.out.println("Bob's Shared Secret:   " + bytesToHex(bobSharedSecret));
            System.out.println("Shared secrets match:  " + MessageDigest.isEqual(aliceSharedSecret, bobSharedSecret));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Utility method to convert bytes to hex
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}