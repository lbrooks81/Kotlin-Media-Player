package com.example.audiomediaplayer

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.audiomediaplayer.ui.theme.AudioMediaPlayerTheme


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

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "select_song/placeholder") {
        composable("main"
        ) {
            MainScreen(
                mediaPlayer = mediaPlayer,
                currentSong = song,
                navController = navController
            )
        }

        composable("select_song/{currentSong}",
            arguments = listOf(navArgument("currentSong") {
                type = NavType.StringType
                nullable = true
            })
        ) {
            SelectSongScreen(navController, song, mediaPlayer)
        }
    }
}



