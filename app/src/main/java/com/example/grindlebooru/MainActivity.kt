package com.example.grindlebooru

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grindlebooru.ui.theme.GrindleBooruTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GrindleBooruTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BooruApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooruApp(modifier: Modifier = Modifier) {
    val categoryFeed = stringResource(R.string.category_image_feed)
    val categoryFavorites = stringResource(R.string.category_favorites)

    var previousImageIndex by remember { mutableStateOf(0) }
    var imageIndexState by remember { mutableStateOf(0) }
    var scaleState by remember { mutableStateOf(1f) }
    var offsetState by remember { mutableStateOf(Offset.Zero) }
    var categoryState by remember { mutableStateOf(categoryFeed) }
    val favoriteListState = remember { mutableStateListOf<Map<String, Any>>() }
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    var favorited = false
    val colorByCategory = when (categoryState) {
        categoryFeed -> Color(0xFF29E6FF)
        categoryFavorites -> Color(0xFFff3333)
        else -> Color.Magenta
    }
    val colorFavorites = Color(0xFFff3333)
    val imageFeedList = listOf(
        mapOf(
            "url" to "8sOZJ8JF0S8",
            "title" to "canal between cherry blossom trees",
            "artist" to "Sora Sagano",
            "painter" to painterResource(R.drawable.sora_sagano_8sozj8jf0s8_unsplash)
        ),
        mapOf(
            "url" to "tKCd-IWc4gI",
            "title" to "men in black suits standing in the hallway",
            "artist" to "Yoav Aziz",
            "painter" to painterResource(R.drawable.yoav_aziz_tkcd_iwc4gi_unsplash)
        ),
        mapOf(
            "url" to "alY6_OpdwRQ",
            "title" to "people gathered outside buildings and vehicles",
            "artist" to "Jezael Melgoza",
            "painter" to painterResource(R.drawable.jezael_melgoza_aly6_opdwrq_unsplash)
        ),
        mapOf(
            "url" to "oCZHIa1D4EU",
            "title" to "Japanese lantern over city bike at nighttime",
            "artist" to "Jase Bloor",
            "painter" to painterResource(R.drawable.jase_bloor_oczhia1d4eu_unsplash)
        ),
        mapOf(
            "url" to "N4DbvTUDikw",
            "title" to "Mount Fuji, Japan",
            "artist" to "David Edelstein",
            "painter" to painterResource(R.drawable.david_edelstein_n4dbvtudikw_unsplash)
        ),
        mapOf(
            "url" to "hwLAI5lRhdM",
            "title" to "three bicycles parked in front of building",
            "artist" to "Clay Banks",
            "painter" to painterResource(R.drawable.clay_banks_hwlai5lrhdm_unsplash)
        ),
        mapOf(
            "url" to "SBK40fdKbAg",
            "title" to "Torii Gate, Japan",
            "artist" to "Tianshu Liu",
            "painter" to painterResource(R.drawable.tianshu_liu_sbk40fdkbag_unsplash)
        )
    )
    val currentUrl: String = when (categoryState) {
        categoryFeed -> imageFeedList[imageIndexState]["url"] as String
        categoryFavorites -> favoriteListState[imageIndexState]["url"] as String
        else -> "nothing"
    }

    fun changeCategory(category: String? = null, imageIndex: Int? = null) {
        if (category != null) {
            previousImageIndex = imageIndexState
            categoryState = category
        }
        if (imageIndex != null) imageIndexState = imageIndex
    }

    @Composable
    fun FavoriteButton(
        thumbnail: Painter,
        contentDescription: String,
        title: String,
        artist: String,
        onClick: () -> Unit
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Row {
                Image(
                    painter = thumbnail,
                    contentDescription = contentDescription,
                    modifier = Modifier
                        .weight(0.25f)
                        .padding(end = 10.dp)
                )
                Column(modifier = Modifier.weight(0.75f)) {
                    Text(
                        text = title,
                        color = Color.Black
                    )
                    Text(
                        text = artist,
                        color = Color.Black
                    )
                }

            }
        }
    }

    @Composable
    fun FavoritesList(
        favoriteListState: List<Map<String, Any>>
    ) {
        LazyColumn {
            items(favoriteListState.size) { index ->
                val onClick: () -> Unit = {
                    if (categoryState != categoryFavorites) {
                        changeCategory(categoryFavorites, index)
                    } else {
                        changeCategory(imageIndex = index)
                    }
                    offsetState = Offset.Zero
                    scaleState = 1f
                }

                FavoriteButton(
                    thumbnail = favoriteListState[index]["painter"] as Painter,
                    contentDescription = favoriteListState[index]["title"] as String,
                    title = favoriteListState[index]["title"] as String,
                    artist = favoriteListState[index]["artist"] as String,
                    onClick = onClick
                )
            }
        }
    }

    favorited = favoriteListState.any { it["url"] == currentUrl }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.6f)
                    .background(color = Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.10f)
                        .background(colorFavorites),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = categoryFavorites,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                FavoritesList(
                    favoriteListState = favoriteListState
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = categoryState,
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp) },
                    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = colorByCategory)
                )
            },
            bottomBar = {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = modifier
                        .fillMaxWidth()
                        .background(colorByCategory)
                ) {
                    NavBarButton( // Backwards button
                        painter = painterResource(R.drawable.icon_arrow_back),
                        contentDescription = stringResource(R.string.navigate_back_content_description)
                    ) {
                        when (categoryState) {
                            categoryFeed -> {
                                offsetState = Offset.Zero
                                scaleState = 1f
                                if (imageIndexState == 0) {
                                    imageIndexState = imageFeedList.size - 1
                                } else imageIndexState--
                            }
                            categoryFavorites -> {
                                offsetState = Offset.Zero
                                scaleState = 1f
                                if (imageIndexState == 0) {
                                    imageIndexState = favoriteListState.size - 1
                                } else imageIndexState--
                            }
                        }


                    }
                    NavBarButton( // Favorite button
                        painter = if (favorited) {
                            painterResource(R.drawable.icon_favorite_full)
                        } else {
                            painterResource(R.drawable.icon_favorite_empty)
                        },
                        contentDescription = stringResource(R.string.favorite_content_description)
                    ) {
                        when (categoryState) {
                            categoryFeed -> {
                                favorited = if (!favorited) {
                                    val currentDate = System.currentTimeMillis()
                                    val newDict = mutableMapOf<String, Any>("date" to currentDate)

                                    newDict.putAll(imageFeedList[imageIndexState])
                                    favoriteListState.add(newDict)
                                    true
                                } else {
                                    favoriteListState.removeAll { it["url"] == currentUrl }
                                    false
                                }
                            }
                            categoryFavorites -> {
                                if (favoriteListState.size == 1) {
                                    favoriteListState.removeAll { it["url"] == currentUrl }
                                    changeCategory(categoryFeed, previousImageIndex)
                                }
                                else if (imageIndexState == favoriteListState.size - 1) {
                                    imageIndexState--
                                    favoriteListState.removeAll { it["url"] == currentUrl }
                                }
                                else {
                                    favoriteListState.removeAll { it["url"] == currentUrl }
                                }
                            }
                        }
                    }
                    NavBarButton(  // Forwards button
                        painter = painterResource(R.drawable.icon_arrow_forward),
                        contentDescription = stringResource(R.string.navigate_forwards_content_description),
                    ) {
                        when (categoryState) {
                            categoryFeed -> {
                                offsetState = Offset.Zero
                                scaleState = 1f
                                if (imageIndexState == imageFeedList.size - 1) {
                                    imageIndexState = 0
                                } else imageIndexState++
                            }
                            categoryFavorites -> {
                                offsetState = Offset.Zero
                                scaleState = 1f
                                if (imageIndexState == favoriteListState.size - 1) {
                                    imageIndexState = 0
                                } else imageIndexState++
                            }
                        }
                    }
                }
            }
        ) { padding ->
            val currentPainter = when (categoryState) {
                categoryFeed -> imageFeedList[imageIndexState]["painter"] as Painter
                categoryFavorites -> favoriteListState[imageIndexState]["painter"] as Painter
                else -> painterResource(R.drawable.ic_launcher_background)
            }
            val activity = (LocalContext.current as? Activity)
            BackPressHandler {
                when (categoryState) {
                    categoryFeed -> activity?.finish()
                    categoryFavorites -> {
                        offsetState = Offset.Zero
                        scaleState = 1f
                        changeCategory(categoryFeed, previousImageIndex)
                    }
                }
            }
            Row(modifier = Modifier
                .fillMaxSize()
            ) {
                Box(modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.03f)
                )
                Box(modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.97f)
                    .pointerInput(Unit) {
                        detectTransformGestures { centroid, pan, zoom, _ ->
                            val oldScale = scaleState
                            val newScale = scaleState * zoom

                            offsetState =
                                (offsetState + centroid / oldScale) - (centroid / newScale + pan / oldScale)
                            scaleState = newScale
                        }
                    }
                )
            }
            Image(
                painter = currentPainter,
                contentDescription = stringResource(R.string.image_content_description),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scaleState,
                        scaleY = scaleState,
                        translationX = -offsetState.x * scaleState,
                        translationY = -offsetState.y * scaleState,
                        transformOrigin = TransformOrigin(0f, 0f)
                    )
            )
            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                val currentList = when (categoryState) {
                    categoryFeed -> imageFeedList
                    categoryFavorites -> favoriteListState
                    else -> imageFeedList
                }

                MetadataCard(
                    title = currentList[imageIndexState]["title"] as String,
                    artist = currentList[imageIndexState]["artist"] as String,
                    modifier = modifier
                        .fillMaxWidth()
                        .background(Color(0x7F000000))
                        .padding(
                            start = 10.dp,
                            bottom = 12.dp,
                            end = 10.dp,
                            top = 4.dp
                        )
                )
            }
        }
    }
}

@Composable
private fun MetadataCard(
    title: String,
    artist: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = TextStyle(
                color = Color.White,
                fontSize = 40.sp,
                shadow = Shadow(
                    color = Color.Black,
                    offset = Offset(4f, 4f),
                    blurRadius = 8f
                )
            )
        )
        Text(
            text = artist,
            style = TextStyle(
                color = Color.White,
                fontSize = 30.sp,
                shadow = Shadow(
                    color = Color.Black,
                    offset = Offset(4f, 4f),
                    blurRadius = 8f
                )
            )
        )
    }
}

@Composable
private fun NavBarButton(
    painter: Painter,
    contentDescription: String,
    modifier: Modifier = Modifier,
    color: Color? = null,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        )
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            colorFilter = color?.let { ColorFilter.tint(it) },
            modifier = modifier
        )
    }
}

@Composable
fun BackPressHandler(
    backPressedDispatcher: OnBackPressedDispatcher? =
        LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher,
    onBackPressed: () -> Unit
) {
    val currentOnBackPressed by rememberUpdatedState(newValue = onBackPressed)

    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                currentOnBackPressed()
            }
        }
    }

    DisposableEffect(key1 = backPressedDispatcher) {
        backPressedDispatcher?.addCallback(backCallback)

        onDispose {
            backCallback.remove()
        }
    }
}
