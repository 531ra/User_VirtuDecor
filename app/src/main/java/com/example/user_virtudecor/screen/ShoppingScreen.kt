package com.example.user_virtudecor.screen

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.user_virtudecor.model.CartItem
import com.example.user_virtudecor.model.User
import com.example.user_virtudecor.startRazorpayPayment
import com.example.user_virtudecor.ui.theme.DarkOrange
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(navController: NavHostController) {
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val cartDbRef = FirebaseDatabase.getInstance().getReference("Shopping_cart").child(userId ?: "")
    val userDbRef = FirebaseDatabase.getInstance().getReference("User_info").child(userId ?: "")

    var cartItems by remember { mutableStateOf(listOf<Pair<String, CartItem>>()) }
    var isLoading by remember { mutableStateOf(true) }
    var userInfo by remember { mutableStateOf(User()) }
    var userInfoLoading by remember { mutableStateOf(true) }
    var showOrderDialog by remember { mutableStateOf(false) }

    // Fetch cart items
    DisposableEffect(userId) {
        if (userId == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            isLoading = false
            return@DisposableEffect onDispose {}
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val itemsList = mutableListOf<Pair<String, CartItem>>()
                for (childSnap in snapshot.children) {
                    val key = childSnap.key ?: continue
                    val item = childSnap.getValue(CartItem::class.java) ?: continue
                    itemsList.add(key to item)
                }
                cartItems = itemsList
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load cart", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
        }

        cartDbRef.addValueEventListener(listener)
        onDispose { cartDbRef.removeEventListener(listener) }
    }

    // Fetch user info
    DisposableEffect(userId) {
        if (userId == null) {
            userInfoLoading = false
            return@DisposableEffect onDispose {}
        }

        val userListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(User::class.java)?.let { userInfo = it }
                userInfoLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load user info", Toast.LENGTH_SHORT).show()
                userInfoLoading = false
            }
        }

        userDbRef.addValueEventListener(userListener)
        onDispose { userDbRef.removeEventListener(userListener) }
    }

    val totalBill = cartItems.sumOf { (_, item) ->
        (item.price.toDoubleOrNull() ?: 0.0) * item.quantity
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    "Your Shopping Cart",
                    color = Color(0xFF212529),
                    fontWeight = FontWeight.Bold
                )
            })
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (isLoading || userInfoLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                return@Scaffold
            }

            if (cartItems.isEmpty()) {
                Text(
                    "Your cart is empty.",
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
                return@Scaffold
            }

            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems, key = { it.first }) { (key, item) ->
                        CartItemCard(
                            cartKey = key,
                            cartItem = item,
                            onUpdateQuantity = { newQty ->
                                if (newQty >= 1) {
                                    cartDbRef.child(key).child("quantity").setValue(newQty)
                                }
                            },
                            onDelete = {
                                cartDbRef.child(key).removeValue()
                            }
                        )
                    }
                }


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F3F5))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total: ₹${"%.2f".format(totalBill)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212529)
                    )
                    Button(
                        onClick = { showOrderDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D6EFD)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Order", color = Color.White)
                    }
                }
            }
        }
    }

    if (showOrderDialog) {
        OrderDialog(
            initialUserInfo = userInfo,
            totalPrice = totalBill,
            onDismiss = { showOrderDialog = false },
            onSubmit = { updatedUserInfo ->
                userDbRef.setValue(updatedUserInfo)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
                        showOrderDialog = false
                        startRazorpayPayment(context, totalBill)
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show()
                    }
            }
        )
    }
}

@Composable
fun CartItemCard(
    cartKey: String,
    cartItem: CartItem,
    onUpdateQuantity: (Int) -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            ) {
                AsyncImage(
                    model = cartItem.imageUrl,
                    contentDescription = cartItem.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop // You can also use .Fit or .FillBounds based on your preference
                )
            }


            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(cartItem.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Price: ₹${cartItem.price}", color = Color.DarkGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Quantity: ${cartItem.quantity}", color = Color.Gray, fontSize = 13.sp)
            }

            Row (horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                TextButton(
                    onClick = { if (cartItem.quantity > 1) onUpdateQuantity(cartItem.quantity - 1) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(35.dp),
                    border = BorderStroke(2.dp, DarkOrange),
                    colors = ButtonDefaults.textButtonColors(contentColor = DarkOrange)
                ) {
                    Text("-", fontSize = 20.sp)
                }

                TextButton(
                    onClick = { onUpdateQuantity(cartItem.quantity + 1) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(35.dp),
                    border = BorderStroke(2.dp, DarkOrange),
                    colors = ButtonDefaults.textButtonColors(contentColor = DarkOrange)
                ) {
                    Text("+", fontSize = 20.sp)
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete item", tint = Color.Red)
            }
        }
        }
    }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDialog(
    initialUserInfo: User,
    totalPrice: Double,
    onDismiss: () -> Unit,
    onSubmit: (User) -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf(initialUserInfo.name) }
    var address by remember { mutableStateOf(initialUserInfo.address) }
    var phone by remember { mutableStateOf(initialUserInfo.phone) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        text = {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Enter Order Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212529)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Shipping Address") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF8D7DA))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Total Price: ₹${"%.2f".format(totalPrice)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF721C24)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel", color = Color.Gray)
                        }

                        Button(
                            onClick = {
                                if (name.isBlank() || address.isBlank() || phone.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        "Please fill all fields",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }

                                val updatedUser = User(
                                    name = name,
                                    email = initialUserInfo.email,
                                    phone = phone,
                                    address = address
                                )
                                onSubmit(updatedUser)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D6EFD))
                        ) {
                            Text("Submit", color = Color.White)
                        }
                    }
                }
            }
        }
    )
}
