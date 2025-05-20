package com.example.user_virtudecor

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.user_virtudecor.model.FurnitureDisplay
import com.example.user_virtudecor.model.OrderDetail
import com.example.user_virtudecor.model.OrderProduct
import com.example.user_virtudecor.model.User
import com.example.user_virtudecor.navigation.User_navigation
import com.example.user_virtudecor.ui.theme.User_VirtuDecorTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener

class MainActivity : ComponentActivity(), PaymentResultListener {
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Checkout.preload(applicationContext)
        setContent {
            User_navigation()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        Toast.makeText(this, "Payment Successful: $razorpayPaymentID", Toast.LENGTH_LONG).show()

        val userId = auth.currentUser?.uid ?: return

        val userRef = database.getReference("User_info").child(userId)
        val cartRef = database.getReference("Shopping_cart").child(userId)

        userRef.get().addOnSuccessListener { userSnapshot ->
            val user = userSnapshot.getValue(User::class.java) ?: User()

            cartRef.get().addOnSuccessListener { cartSnapshot ->
                val products = mutableListOf<OrderProduct>()
                var totalPrice = 0.0

                for (itemSnap in cartSnapshot.children) {
                    val furniture = itemSnap.getValue(FurnitureDisplay::class.java)
                    val quantity = itemSnap.child("quantity").getValue(Int::class.java) ?: 1

                    if (furniture != null) {
                        val priceDouble = furniture.price.toDoubleOrNull() ?: 0.0
                        products.add(OrderProduct(furniture.id, furniture.name, priceDouble, quantity))
                        totalPrice += priceDouble * quantity
                    }
                }

                val orderId = razorpayPaymentID ?: database.reference.push().key!!

                val orderDetail = OrderDetail(
                    orderId = orderId,
                    paymentId = razorpayPaymentID,
                    orderDate = System.currentTimeMillis(),
                    user = user,
                    products = products,
                    totalPrice = String.format("%.2f", totalPrice).toDouble()

                )

                val orderRef = database.getReference("Order_details").child(userId).child(orderId)
                orderRef.setValue(orderDetail).addOnCompleteListener { orderSaveTask ->
                    if (orderSaveTask.isSuccessful) {
                        cartRef.removeValue().addOnCompleteListener { cartClearTask ->
                            if (cartClearTask.isSuccessful) {
                                Toast.makeText(this, "Order saved and cart cleared!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Failed to clear cart: ${cartClearTask.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Failed to save order: ${orderSaveTask.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }

            }.addOnFailureListener {
                Toast.makeText(this, "Failed to get cart data: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to get user data: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }


    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment Failed: $response", Toast.LENGTH_LONG).show()
    }
}

