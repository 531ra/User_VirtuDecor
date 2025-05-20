import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.user_virtudecor.model.CartItem
import com.example.user_virtudecor.model.FurnitureDisplay
import com.example.user_virtudecor.screen.Rooms
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ProductDetailScreen(navController: NavController, itemJson: String) {
    val item = Gson().fromJson(itemJson, FurnitureDisplay::class.java)
    var currentImageIndex by remember { mutableStateOf(0) }
    var quantity by remember { mutableStateOf(1) }
    val context = LocalContext.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val imageHeight = screenHeight * 0.35f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item.name, maxLines = 1, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val encodedUrl = Uri.encode(item.glbModelUrl)
                            navController.navigate("arscreen/$encodedUrl")
                        }
                    ) {
                        Icon(Icons.Filled.Info, contentDescription = "View in AR")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (item.imageUrls.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight)
                        .pointerInput(item.imageUrls.size) {
                            detectHorizontalDragGestures { change, dragAmount ->
                                change.consume()
                                currentImageIndex = when {
                                    dragAmount < -10 -> (currentImageIndex + 1).coerceAtMost(item.imageUrls.lastIndex)
                                    dragAmount > 10 -> (currentImageIndex - 1).coerceAtLeast(0)
                                    else -> currentImageIndex
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = item.imageUrls[currentImageIndex],
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) + slideInHorizontally { fullWidth -> fullWidth } with
                                    fadeOut(animationSpec = tween(300)) + slideOutHorizontally { fullWidth -> -fullWidth }
                        },
                        label = "ImageTransition"
                    ) { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Furniture Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                // Row for product name and quantity controls side-by-side
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.name,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(36.dp),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBBDEFB))
                        ) {
                            Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = quantity.toString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(28.dp),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = { quantity++ },
                            modifier = Modifier.size(36.dp),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBBDEFB))
                        ) {
                            Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Category: ${item.category}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Price: ₹${item.price}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4CAF50) // Greenish
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = item.description.ifEmpty { "No description available." },
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(36.dp))
                Rooms()
                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        val cartItem = CartItem(
                            id = item.id,
                            name = item.name,
                            price = item.price,
                            imageUrl = item.imageUrls.firstOrNull() ?: "",
                            quantity = quantity
                        )

                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        if (userId == null) {
                            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val dbRef = FirebaseDatabase.getInstance()
                            .getReference("Shopping_cart")
                            .child(userId)

                        dbRef.push().key?.let { key ->
                            dbRef.child(key).setValue(cartItem)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Added to Bag", Toast.LENGTH_SHORT).show()
                                    navController.navigate("shoppingscreen")
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Failed to add item", Toast.LENGTH_SHORT).show()
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        "Add to Bag",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }



            }
        }
    }
}
