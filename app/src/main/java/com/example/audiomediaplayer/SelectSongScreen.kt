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
    Button(
        onClick = {
            navController.popBackStack()
            /*navController.navigate("main/${currentSong.value}")*/
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()

    ) {
        Text("Home Page")
    }

    val files = LocalContext.current.assets.list("") ?: emptyArray()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Button(
            onClick = {
                navController.navigate("main") {
                    // Clear the back stack to prevent going back to this screen
                    popUpTo("select_song") { inclusive = true }
                }
            }
        ) {
            Text("Return to Main Screen")
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (file in files) {
                if (file.endsWith(".mp3") || file.endsWith(".wav")) {
                    SongCard(
                        file = file,
                        mediaPlayer = mediaPlayer,
                        updateSong = {
                            currentSong.value = file
                        },
                        selectedSongFile = currentSong.value
                    )
                }
            }
        }
    }
}

@Composable
fun SongCard(file: String, mediaPlayer: MediaPlayer, updateSong: () -> Unit, selectedSongFile: String ?= null) {
    val context = LocalContext.current


    val metaDataRetriever = MediaMetadataRetriever().apply{
        context.assets.openFd(file).use { descriptor ->
            setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
        }
    }

    val artist = metaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
    val album = metaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
    val title = metaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
    val image = metaDataRetriever.embeddedPicture


    val isSelected = remember { mutableStateOf(file == selectedSongFile) }
    // Placing the isSelected state and selectedSongFile in the remember () means that they will act as dependencies
    // Any time either of them changes, the button color will be recomposed
    val buttonColor = remember(isSelected.value, selectedSongFile) {
        if (!isSelected.value || file != selectedSongFile) Color.Gray else Color(30, 160, 20)
    }
    val buttonBorderColor = remember(isSelected.value, selectedSongFile) {
        if (!isSelected.value || file != selectedSongFile) Color.DarkGray else Color(169, 173, 174)
    }

    val shape = RoundedCornerShape(16.dp)

    Card(
        modifier = Modifier
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),

            ) {


            Text(text = artist?: "Unknown", fontSize = 18.sp)

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = album ?: title!!, fontSize = 18.sp)

                Text(
                    text = "Select",
                    color = Color.White,
                    fontSize = 26.sp,
                    modifier = Modifier
                        .background(buttonColor, shape)
                        .border(4.dp, buttonBorderColor, shape)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable(
                            onClick = {
                                mediaPlayer.reset()
                                mediaPlayer.apply {
                                    context.assets.openFd(file).use { descriptor ->
                                        setDataSource(
                                            descriptor.fileDescriptor,
                                            descriptor.startOffset,
                                            descriptor.length
                                        )
                                        prepare()
                                    }
                                }
                                Log.d("SongSelection", "Before: ${isSelected.value} $buttonColor, $buttonBorderColor")

                                updateSong()
                                isSelected.value = !isSelected.value
                                Log.d("SongSelection", "After: ${isSelected.value} $buttonColor, $buttonBorderColor")

                            },
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple()
                        )
                )


                /*Image(
                    painter = painterResource(iconType),
                    contentDescription = "icon",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .scale(0.5f)
                        .pointerInput(Unit) {
                            detectTapGestures {

                            }
                        }
                )*/
            }
            Text(text = title!!, fontSize = 20.sp)


            if (image != null) {
                /*Image(
                    bitmap = image.decodeByteArray(image, 0, image.size).asImageBitmap(),
                )*/
            }
        }
    }
}

