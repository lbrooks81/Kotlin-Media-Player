package com.example.audiomediaplayer

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SelectSongScreen(navController: NavController, currentSong: MutableState<String?>, mediaPlayer: MediaPlayer) {


    val files = LocalContext.current.assets.list("") ?: emptyArray()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Music Library",
                fontSize = 32.sp,
                modifier = Modifier.padding(16.dp)
            )
            /*LazyColumn {
                items(files.filter { it.endsWith(".mp3") || it.endsWith(".wav") }.count()) { file ->
                    SongCard(
                        file = file.toString(),
                        mediaPlayer = mediaPlayer,
                        updateSong = {
                            currentSong.value = file.toString()
                        },
                        selectedSongFile = currentSong.value,
                        navController = navController
                    )
                }
            }*/
            for (file in files) {
                if (file.endsWith(".mp3") || file.endsWith(".wav")) {
                    SongCard(
                        file = file,
                        mediaPlayer = mediaPlayer,
                        updateSong = {
                            currentSong.value = file
                        },
                        selectedSongFile = currentSong.value,
                        navController = navController
                    )
                }
            }
            if (currentSong.value != null && currentSong.value != "placeholder") {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {

                        Text(
                            text = "Back to Player",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SongCard(file: String, mediaPlayer: MediaPlayer, updateSong: () -> Unit, selectedSongFile: String ?= null, navController: NavController) {
    val context = LocalContext.current


    val metaDataRetriever = MediaMetadataRetriever().apply{
        context.assets.openFd(file).use { descriptor ->
            setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
        }
    }

    val artist = metaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
    val album = metaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
    val title = metaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
            .clickable(
                onClick = {
                    mediaPlayer.reset()
                    context.assets.openFd(file).use { descriptor ->
                        mediaPlayer.setDataSource(
                            descriptor.fileDescriptor,
                            descriptor.startOffset,
                            descriptor.length
                        )
                        mediaPlayer.prepare()
                    }
                    updateSong()
                    navController.navigate("main") {
                        popUpTo("select_song") { inclusive = true }
                    }
                },
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple()
            )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title ?: "Unknown Title",
                    fontSize = 18.sp,
                    color = if (file == selectedSongFile) Color(0xFF1E88E5) else Color.Black
                )
                Text(
                    text = album ?: "Unknown Album",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = artist ?: "Unknown Artist",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

