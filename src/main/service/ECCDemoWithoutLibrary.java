package main.service;

public class ECCDemoWithoutLibrary {

    public static void main(String[] args) {
        // Define the elliptic curve parameters
        final int a = -1; // Coefficient for x
        final int b = 1;  // Coefficient for the constant
        final int p = 23; // Prime modulus

        // Define base point G
        final int[] G = {9, 17};

        // Private keys for Alice and Bob
        final int alicePrivateKey = 4; // Chosen randomly
        final int bobPrivateKey = 7;  // Chosen randomly

        // Calculate public keys
        int[] alicePublicKey = pointMultiply(G, alicePrivateKey, a, b, p);
        int[] bobPublicKey = pointMultiply(G, bobPrivateKey, a, b, p);

        // Key agreement
        int[] aliceSharedKey = pointMultiply(bobPublicKey, alicePrivateKey, a, b, p);
        int[] bobSharedKey = pointMultiply(alicePublicKey, bobPrivateKey, a, b, p);

        // Display results
        System.out.println("Alice's Public Key: " + pointToString(alicePublicKey));
        System.out.println("Bob's Public Key:   " + pointToString(bobPublicKey));
        System.out.println("Alice's Shared Key: " + pointToString(aliceSharedKey));
        System.out.println("Bob's Shared Key:   " + pointToString(bobSharedKey));

        System.out.println("Shared keys match:  " + pointsEqual(aliceSharedKey, bobSharedKey));
    }

    // Point addition on the elliptic curve
    public static int[] pointAdd(int[] P, int[] Q, int a, int b, int p) {
        if (P == null) return Q;
        if (Q == null) return P;

        int lambda;
        if (P[0] == Q[0] && P[1] == Q[1]) {
            // Point doubling
            lambda = (3 * P[0] * P[0] + a) * modInverse(2 * P[1], p) % p;
        } else {
            // Regular addition
            lambda = (Q[1] - P[1]) * modInverse(Q[0] - P[0], p) % p;
        }

        if (lambda < 0) lambda += p;

        int xR = (lambda * lambda - P[0] - Q[0]) % p;
        if (xR < 0) xR += p;

        int yR = (lambda * (P[0] - xR) - P[1]) % p;
        if (yR < 0) yR += p;

        return new int[]{xR, yR};
    }

    // Scalar multiplication
    public static int[] pointMultiply(int[] P, int k, int a, int b, int p) {
        int[] result = null;
        int[] temp = P;

        while (k > 0) {
            if ((k & 1) == 1) {
                result = pointAdd(result, temp, a, b, p);
            }
            temp = pointAdd(temp, temp, a, b, p);
            k >>= 1;
        }

        return result;
    }

    // Modular inverse
    public static int modInverse(int k, int p) {
        k = k % p;
        for (int x = 1; x < p; x++) {
            if ((k * x) % p == 1) return x;
        }
        throw new IllegalArgumentException("Modular inverse does not exist");
    }

    // Utility to compare points
    public static boolean pointsEqual(int[] P, int[] Q) {
        if (P == null || Q == null) return false;
        return P[0] == Q[0] && P[1] == Q[1];
    }

    // Utility to display points
    public static String pointToString(int[] point) {
        if (point == null) return "null";
        return "(" + point[0] + ", " + point[1] + ")";
    }
}