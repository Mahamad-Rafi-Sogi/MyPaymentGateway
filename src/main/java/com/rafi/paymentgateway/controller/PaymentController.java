package com.rafi.paymentgateway.controller;

import com.rafi.paymentgateway.service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.Payment;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private RazorpayService razorpayService;

    @PostMapping(value = "/create-order", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createOrder(@RequestParam int amount) {
        try {
            Order order = razorpayService.createOrder(amount);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating order: " + e.getMessage());
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody String payload, @RequestHeader("X-Razorpay-Signature") String signature) {
        try {
            boolean isSignatureValid = razorpayService.verifySignature(payload, signature);
            if (!isSignatureValid) {
                return ResponseEntity.status(400).body("Invalid signature");
            }

            JSONObject event = new JSONObject(payload);
            String eventType = event.getString("event");

            if ("payment.captured".equals(eventType)) {
                // Handle successful payment
                JSONObject paymentDetails = event.getJSONObject("payload").getJSONObject("payment");
                String paymentId = paymentDetails.getString("id");
                Payment payment = razorpayService.fetchPayment(paymentId);
                // Do something with the payment details, such as updating your database
            } else if ("payment.failed".equals(eventType)) {
                // Handle payment failure
            }

            return ResponseEntity.ok("Webhook received successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error processing webhook: " + e.getMessage());
        }
    }
}