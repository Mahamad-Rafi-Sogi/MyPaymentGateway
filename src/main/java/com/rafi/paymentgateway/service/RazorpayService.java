package com.rafi.paymentgateway.service;

import com.rafi.paymentgateway.utility.RazorpayUtility;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import jakarta.annotation.PostConstruct;

import com.razorpay.Payment;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;
    
    private final String webhookSecret = "Rafi@7829";

    private RazorpayClient razorpayClient;
    
    
    @PostConstruct
    public void init() throws Exception {
        System.out.println("keyID: " + razorpayKeyId + " keySecret: " + razorpayKeySecret);
        this.razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
    }

    public Order createOrder(int amount) throws Exception {
        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount * 100); // amount in paise
            orderRequest.put("currency", "INR");
            orderRequest.put("payment_capture", 1);  // auto-capture on payment

            // Log the orderRequest for debugging
            System.out.println("Creating order with request: " + orderRequest.toString());
            System.out.println("keyID: " + razorpayKeyId + "  keySecret: " + razorpayKeySecret);

            // Create the order using Razorpay client
            Order order = razorpayClient.orders.create(orderRequest);

            if (order != null && order.has("id")) {
                String orderId = order.get("id");
                System.out.println("Order created successfully. Order ID: " + orderId);
                return order;
            } else {
                System.out.println("Failed to create order. Response: " + order);
                throw new Exception("Order creation failed. No order ID in response.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error creating order: " + e.getMessage(), e);
        }
    }

    public Payment fetchPayment(String paymentId) throws Exception {
        Payment payment = razorpayClient.payments.fetch(paymentId);
        return payment;
    }

    public boolean verifySignature(String payload, String signature) throws Exception {
        return RazorpayUtility.verifyWebhookSignature(payload, signature, webhookSecret);
    }
}