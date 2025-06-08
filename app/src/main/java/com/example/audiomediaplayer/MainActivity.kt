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
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.scale
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import com.example.audiomediaplayer.ui.theme.AudioMediaPlayerTheme
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment


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
    val song = remember { mutableStateOf<String?>("placeholder") }

    var artist: String? = null
    var album: String? = null
    var title: String? = null

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main"/*,
            arguments = listOf(navArgument("currentSong") {
                type = NavType.StringType
            })*/
        ) { /*backStackEntry ->
            // Retrieve the current song from the back stack entry arguments
            song.value = backStackEntry.arguments?.getString("currentSong")

            // If a song is selected, retrieve its metadata
            if (song.value != null) {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(song.value ?: "")
                artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                retriever.release()
            }*/

            MainScreen(
                mediaPlayer = mediaPlayer,
                currentSong = song,
                artist = artist ?: "Unknown Artist",
                album = album ?: "Unknown Album",
                title = title ?: "Unknown Title",
                navController = navController
            )
        }

        composable("select_song/{currentSong}",
            arguments = listOf(navArgument("currentSong") {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            val currentSong = remember { mutableStateOf(backStackEntry.arguments?.getString("currentSong"))}
            SelectSongScreen(navController, currentSong, mediaPlayer)
        }
    }
}



