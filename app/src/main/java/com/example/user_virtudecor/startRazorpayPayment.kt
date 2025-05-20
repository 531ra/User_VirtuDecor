package com.example.user_virtudecor

import android.app.Activity
import android.widget.Toast
import com.razorpay.Checkout
import org.json.JSONObject

fun startRazorpayPayment(context: android.content.Context, amount: Double) {
    val activity = context as? android.app.Activity ?: return

    val checkout = Checkout()
    checkout.setKeyID(context.getString(R.string.api)) // Replace with your Razorpay API key

    try {
        val options = JSONObject()
        options.put("name", "VirtuDecor")
        options.put("description", "Shopping Payment")
        options.put("currency", "INR")
        options.put("amount", (amount * 100).toInt()) // Razorpay accepts amount in paise

        // Optional: prefill customer email or contact
        val prefill = JSONObject()
        prefill.put("email", "customer@example.com")
        prefill.put("contact", "9999999999")

        options.put("prefill", prefill)

        checkout.open(context as
                Activity, options)
    } catch (e: Exception) {
        Toast.makeText(context, "Error in starting payment: ${e.message}", Toast.LENGTH_LONG).show()
        e.printStackTrace()
    }
}
