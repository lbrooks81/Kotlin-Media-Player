package com.example.audiomediaplayer

import android.graphics.BitmapFactory.decodeByteArray
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.ui.graphics.ImageBitmap.Companion
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.audiomediaplayer.ui.theme.AudioMediaPlayerTheme
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AudioMediaPlayerTheme {
                AppStart()
            }
        }
    }
}

@Composable
fun AppStart()
{
    // Initializes the mediaPlayer by opening the audio file and setting the data source
    val mediaPlayer = MediaPlayer()

    var artist: String? = null
    var album: String? = null
    var title: String? = null

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                mediaPlayer = mediaPlayer,
                artist = artist ?: "Unknown Artist",
                album = album ?: "Unknown Album",
                title = title ?: "Unknown Title",
                navController = navController
            )
        }
        composable("select_song") {
            SelectSongScreen()
        }
    }
}

@Composable
fun MainScreen(mediaPlayer: MediaPlayer, artist: String, album: String, title: String, navController: NavController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Button(
            onClick = {
                navController.navigate("select_song");
            },
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Text("Select a Song")
        }
        Card(
            modifier = Modifier
                .padding(top = 150.dp)
                .height(300.dp)
        ) {
            Column(
                modifier = Modifier.padding(top = 15.dp)
            ) {
                Text(
                    text = artist,
                    modifier = Modifier
                        .padding(start = 25.dp),
                    fontSize = 20.sp
                )
                Text(
                    text = title,
                    modifier = Modifier
                        .padding(start = 25.dp),
                    fontSize = 22.sp
                )
                Text(
                    text = album,
                    modifier = Modifier
                        .padding(start = 25.dp),
                    fontSize = 16.sp
                )
            }
            PlayCard(mediaPlayer = mediaPlayer)
        }
    }
}

@Composable
fun SelectSongScreen() {

    val files = LocalContext.current.assets.list("") ?: emptyArray()


    // TODO make a template for each song

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (file in files) {
                if (file.endsWith(".mp3") || file.endsWith(".wav")) {
                    SongCard(file = file, mediaPlayer = MediaPlayer()) {}
                }
            }
        }
    }
}

@Composable
fun SongCard(file: String, mediaPlayer: MediaPlayer, onClick: () -> Unit) {
    val context = LocalContext.current

    mediaPlayer.apply {
        context.assets.openFd(file).use { descriptor ->
            setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
            prepare()
        }
    }

    val metaDataRetriever = MediaMetadataRetriever().apply{
        context.assets.openFd(file).use { descriptor ->
            setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
        }
    }

    val artist = metaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
    val album = metaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
    val title = metaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
    val image = metaDataRetriever.embeddedPicture



    Card(
        modifier = Modifier
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),

        ) {
            Box(){
                Text(text = artist?: "Unknown", fontSize = 18.sp)
                Text(text = album?: title!!, fontSize = 18.sp)
                Text(text = title!!, fontSize = 20.sp)
            }
            if (image != null) {
                Image(
                    bitmap = image.decodeByteArray(image, 0, image.size).asImageBitmap(),
                )
            }

            Image(
                painter = painterResource(iconType),
                contentDescription = "icon",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures {
                            if (mediaPlayer.isPlaying) {
                                mediaPlayer.pause()
                                iconType = R.drawable.play
                            }
                            else {
                                mediaPlayer.start()
                                iconType = R.drawable.pause
                            }
                        }
                    }
            )
        }
    }
}

@Composable
fun PlayCard(mediaPlayer: MediaPlayer) {
    var currentTime by remember { mutableStateOf(mediaPlayer.currentPosition)}

    LaunchedEffect(Unit) {
        while (mediaPlayer.isPlaying) {
            currentTime = if (mediaPlayer.currentPosition >= mediaPlayer.duration) mediaPlayer.duration
            else mediaPlayer.currentPosition
            delay(100)
        }
    }

        Slider(
            modifier = Modifier
                .padding(horizontal = 50.dp),
            value = currentTime.toFloat(),
            onValueChange = {
                mediaPlayer.seekTo(it.toInt())
            },
            valueRange = 0f..mediaPlayer.duration.toFloat(),
            colors = SliderColors(
                thumbColor = Color(red=0, green=167, blue=100),
                activeTrackColor = Color.Green,
                inactiveTrackColor = Color.Gray,
                activeTickColor = Color.Black,
                inactiveTickColor = Color.Black,
                disabledActiveTrackColor = Color.Gray,
                disabledInactiveTrackColor = Color.Gray,
                disabledThumbColor = Color(red=0, green= 255, blue=100),
                disabledActiveTickColor = Color.Gray,
                disabledInactiveTickColor = Color.Gray,
            )
        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var iconType by remember { mutableStateOf(R.drawable.play) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f)
                    .height(300.dp)
                   ,
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .width(450.dp)
                        .height(50.dp)
                        ,
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text="${(currentTime / 1000) / 60}:${addZero((currentTime / 1000) % 60)}",
                        fontSize = 24.sp,
                    )

                    Image(
                        painter = painterResource(iconType),
                        contentDescription = "icon",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    if (mediaPlayer.isPlaying) {
                                        mediaPlayer.pause()
                                        iconType = R.drawable.play
                                    }
                                    else {
                                        mediaPlayer.start()
                                        iconType = R.drawable.pause
                                    }
                                }
                            }
                    )
                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text="${(mediaPlayer.duration / 1000) / 60}:${addZero((mediaPlayer.duration / 1000) % 60 )}",
                        fontSize = 24.sp
                    )
                }

                var loopType by remember { mutableStateOf(R.drawable.looptransparent) }

                Row(
                    modifier = Modifier
                        .width(450.dp)
                        .padding(bottom = 50.dp)
                        ,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    var volume by remember { mutableStateOf(0f) }
                    Slider(
                        modifier = Modifier
                            .padding(start = 50.dp)
                            .width(100.dp)
                            .height(1.dp),
                        value = volume,
                        onValueChange = {
                            mediaPlayer.setVolume(it,it)
                            volume = it
                        },
                        colors = SliderColors(
                            thumbColor = Color(red=0, green=167, blue=100),
                            activeTrackColor = Color.Green,
                            inactiveTrackColor = Color.Gray,
                            activeTickColor = Color.Black,
                            inactiveTickColor = Color.Black,
                            disabledActiveTrackColor = Color.Gray,
                            disabledInactiveTrackColor = Color.Gray,
                            disabledThumbColor = Color(red=0, green= 255, blue=100),
                            disabledActiveTickColor = Color.Gray,
                            disabledInactiveTickColor = Color.Gray,
                        ),
                        valueRange = 0f..1f
                    )
                    Image(
                        painter = painterResource(loopType),
                        contentDescription = "icon",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .padding(end = 20.dp)
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    if (mediaPlayer.isLooping) {
                                        mediaPlayer.isLooping = false
                                        loopType = R.drawable.loop
                                    } else {
                                        mediaPlayer.isLooping = true
                                        loopType = R.drawable.looptransparent
                                    }
                                }
                            }
                    )
                }
            }
        }
}
fun addZero(number: Int): String {
    return if (number < 10) {
        "0$number"
    } else {
        number.toString()
    }
}
