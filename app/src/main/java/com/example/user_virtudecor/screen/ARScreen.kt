import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.ar.core.Config
import com.google.firebase.storage.FirebaseStorage
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import dev.romainguy.kotlin.math.Float3
import kotlinx.coroutines.tasks.await

@Composable
fun ARScreen(
    modelId: String, // modelId is the path under "furniture_models" in Firebase Storage
    onBack: () -> Unit
) {
    val modelUrl = remember { mutableStateOf<String?>(null) }
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(modelId) {
        isLoading.value = true
        // Build storage reference path directly:
        val storageRef = FirebaseStorage.getInstance()
            .reference.child("furniture_models/$modelId")

        try {
            // Get download URL directly from Storage reference
            val url = storageRef.downloadUrl.await().toString()
            modelUrl.value = url
            Log.d("ARScreen", "Model download URL: $url")
        } catch (e: Exception) {
            Log.e("ARScreen", "Error fetching model download URL", e)
            modelUrl.value = null
        }
        isLoading.value = false
    }

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (modelUrl.value == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Failed to load model URL")
        }
    } else {
        ARSceneWithModelUrl(modelUrl = modelUrl.value!!, onBack = onBack)
    }
}

@Composable
fun ARSceneWithModelUrl(
    modelUrl: String,
    onBack: () -> Unit
) {
    val nodes = remember { mutableListOf<ArModelNode>() }
    val modelNode = remember { mutableStateOf<ArModelNode?>(null) }
    val isPlaced = remember { mutableStateOf(false) }
    val modelScale = rememberSaveable { mutableStateOf(1.0f) }  // Persist scale value on rotation
    val modelRotation = remember { mutableStateOf(0f) }

    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            nodes = nodes,
            planeRenderer = true,
            onCreate = { arSceneView ->
                // Adjusting light estimation to blend better with surroundings
                arSceneView.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR

                modelNode.value = ArModelNode(
                    engine = arSceneView.engine,
                    placementMode = PlacementMode.INSTANT
                ).apply {
                    loadModelGlbAsync(
                        glbFileLocation = modelUrl
                    ) { filamentInstance ->
                        if (filamentInstance != null) {
                            Log.d("ARScreen", "Model loaded successfully from $modelUrl")
                            scale = Float3(modelScale.value, modelScale.value, modelScale.value)
                        } else {
                            Log.e("ARScreen", "Failed to load model from: $modelUrl")
                        }
                    }
                    onAnchorChanged = { isPlaced.value = isAnchored }
                }

                nodes.add(modelNode.value!!)
            }
        )

        // Position the rotate buttons vertically centered with transparent backgrounds
        Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                onClick = {
                    modelRotation.value += 10f
                    modelNode.value?.rotation = Float3(0f, modelRotation.value, 0f)
                },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(16.dp)
            ) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Rotate Left",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = {
                    modelRotation.value -= 10f
                    modelNode.value?.rotation = Float3(0f, modelRotation.value, 0f)
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    Icons.Filled.ArrowForward,
                    contentDescription = "Rotate Right",
                    tint = Color.White
                )
            }
        }

        // Fix Position and Unfix Position buttons at the bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isPlaced.value) {
                // Fix Position button with a transparent background and rounded corners
                Button(
                    onClick = {
                        modelNode.value?.anchor()
                        isPlaced.value = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White)
                ) {
                    Text(text = "Fix Position", color = Color.White)
                }
            } else {
                // Unfix Position button with a transparent background and rounded corners
                Button(
                    onClick = {
                        modelNode.value?.detachAnchor()
                        isPlaced.value = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White)
                ) {
                    Text(text = "Unfix Position", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Button(
                    onClick = {
                        modelScale.value *= 1.1f
                        modelNode.value?.scale = Float3(modelScale.value, modelScale.value, modelScale.value)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White)
                ) {
                    Text(text = "Increase Size", color = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        modelScale.value *= 0.9f
                        modelNode.value?.scale = Float3(modelScale.value, modelScale.value, modelScale.value)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White)
                ) {
                    Text(text = "Decrease Size", color =
                        Color.White)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Text(text = "Back to Menu", color = Color.White)
            }
        }
    }
}
