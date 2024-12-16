package com.rafi.paymentgateway.utility;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class RazorpayUtility {

    public static boolean verifyWebhookSignature(String payload, String signature, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hashBytes = mac.doFinal(payload.getBytes());

            String generatedSignature = Base64.getEncoder().encodeToString(hashBytes);  // Razorpay signature
            System.out.println("Generated Signature: " + generatedSignature);
            System.out.println("Received Signature: " + signature);

            return generatedSignature.equals(signature);  // Return true if they match
        } catch (Exception e) {
            // Log the exception (optional)
            return false;
        }
    }
}