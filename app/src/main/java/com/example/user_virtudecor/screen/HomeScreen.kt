package com.example.user_virtudecor.screen


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.user_virtudecor.R
import com.example.user_virtudecor.model.Category
import com.example.user_virtudecor.model.PopularProducts
import com.example.user_virtudecor.model.Rooms
import com.example.user_virtudecor.model.categoryList
import com.example.user_virtudecor.model.popularProductList
import com.example.user_virtudecor.model.roomList
import com.example.user_virtudecor.ui.theme.DarkOrange
import com.example.user_virtudecor.ui.theme.LightGray_1
import com.example.user_virtudecor.ui.theme.TextColor_1
import com.google.firebase.auth.FirebaseAuth


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavController,

    ) {
    val auth = FirebaseAuth.getInstance()
    var text by remember { mutableStateOf("") }

    Spacer(Modifier.height(25.dp))
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)

    ) {
        item {
           Header {
               auth.signOut()
               navController.navigate("login") {
                   // Clear back stack to prevent back navigation after sign out
                   popUpTo(navController.graph.startDestinationId) { inclusive = true }
               }
           }
            CustomTextField(text = text, onValueChange = { text = it })
            Spacer(Modifier.height(20.dp))
            CategoryRow(navController)
            Spacer(Modifier.height(20.dp))
            PopularRow(navController)
            BannerRow()
            Rooms()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Rooms() {
    Column {
        Text(
            text = stringResource(id = R.string.rooms),
            style = TextStyle(
                fontWeight = FontWeight.W600,
                fontSize = 20.sp,
                color = Color.Black
            )
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text = stringResource(id = R.string.room_des),
            style = TextStyle(
                fontWeight = FontWeight.W400,
                fontSize = 14.sp,
                color = LightGray_1
            )
        )
        Spacer(Modifier.height(5.dp))
        LazyRow {
            items(roomList, key = { it.id }) {
                RoomSection(rooms = it)
            }
        }
    }
}

@Composable
fun RoomSection(rooms: Rooms) {
    Box(
        modifier = Modifier.padding(end = 15.dp)
    ) {
        Image(
            painter = painterResource(id = rooms.image),
            contentDescription = "",
            modifier = Modifier
                .width(127.dp)
                .height(195.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Text(
            text = rooms.title,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                color = TextColor_1
            ),
            modifier = Modifier
                .width(100.dp)
                .padding(20.dp)
        )
    }
}

@Composable
fun BannerRow() {
    Image(
        painter = painterResource(id = R.drawable.banner),
        contentDescription = "",
        modifier = Modifier
            .padding(vertical = 20.dp)
            .clip(RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .height(113.dp),
        contentScale = ContentScale.FillWidth
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PopularRow(navController: NavController) {
    Column {
        CommonTitle(title = stringResource(id = R.string.popular), navController = navController)
        Spacer(Modifier.height(10.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            popularProductList.forEach {
                PopularEachRow(data = it)
            }
        }
    }
}

@Composable
fun PopularEachRow(
    data: PopularProducts,
    modifier: Modifier = Modifier,

) {
    Column(
        modifier = modifier
            .padding(vertical = 5.dp)
            .width(141.dp)
            .clickable {  }
    ) {
        Box {
            Image(
                painter = painterResource(id = data.image),
                contentDescription = "",
                modifier = Modifier
                    .width(141.dp)
                    .height(149.dp)
            )
            Icon(
                painter = painterResource(id = R.drawable.wishlist),
                contentDescription = "",
                modifier = Modifier
                    .padding(15.dp)
                    .size(32.dp)
                    .align(TopEnd),
                tint = Color.Unspecified
            )
        }
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                Color.LightGray
            )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 15.dp),
                horizontalAlignment = CenterHorizontally
            ) {
                Text(
                    maxLines = 1,
                    text = data.title,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W400,
                        color = LightGray_1
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(

                    maxLines = 1,
                    text = data.price,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600,
                        color = Color.Black
                    )
                )
            }
        }
    }
}

@Composable
fun CategoryRow(navController: NavController) {
    Column {
        CommonTitle(
            title = stringResource(id = R.string.categories),
            navController = navController
        )
        Spacer(Modifier.height(20.dp))
        LazyRow {
            items(categoryList, key = { it.id }) {
                CategoryEachRow(category = it, navController = navController)
            }
        }
    }
}

@Composable
fun CategoryEachRow(category: Category, navController: NavController) {
    Box(
        modifier = Modifier
            .clickable(onClick = { navController.navigate("category_screen/${category.title}") })
            .padding(end = 15.dp)
            .background(category.color, RoundedCornerShape(8.dp))
            .width(140.dp)
            .height(80.dp)
    ) {
        Text(
            text = category.title,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                color = Color.Black
            ),
            modifier = Modifier
                .padding(start = 5.dp)
                .align(CenterStart)
        )
        Image(
            painter = painterResource(id = category.image),
            contentDescription = "",
            modifier = Modifier
                .size(60.dp)
                .padding(end = 5.dp)
                .align(BottomEnd)
        )
    }
}

@Composable
fun CommonTitle(
    title: String,
    onClick: () -> Unit = {}, navController: NavController,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.W600,
                color = Color.Black
            )
        )
        TextButton(onClick = onClick) {
            Text(
                text = stringResource(id = R.string.see_all),
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400,
                    color = DarkOrange
                ), modifier = Modifier.clickable(onClick = { navController.navigate("seeAll") })
            )
            Spacer(Modifier.width(5.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "",
                tint = DarkOrange,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    text: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
) {
    TextField(
        value = text,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(
                text = stringResource(id = R.string.placeholder),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.W400,
                    color = LightGray_1
                )
            )
        },
        shape = RoundedCornerShape(8.dp),
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.search),
                contentDescription = "",
                tint = LightGray_1
            )
        },
        modifier = modifier
            .padding(vertical = 20.dp)
            .fillMaxWidth()
            .border(1.dp, LightGray_1, RoundedCornerShape(8.dp))
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Header(onClick: () -> Unit = {}, onSignOut: () -> Unit) {
    Spacer(Modifier.height(25.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.heading_text),
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.W600,
                color = Color.Black
            )
        )

        TextButton(onClick = onSignOut) {
            Text(
                text = "Sign Out",
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}
