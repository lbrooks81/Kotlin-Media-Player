# Audio Media Player
An Android application that supports audio playback for local files, metadata extraction, and song selection.

# Using the App
First, you will need to add your songs to the application. To do this, download a song. Ensure that the name of the file only consists of lowercase characters and underscores (ex: my_song.mp3). Next, move this file to app/src/main/assets. Do this with any number of files.

When launching the app, you will first be greeted with a song selection screen. Clicking on a song will highlight it, allowing you to click a button to navigate you to the main player page, which whill display information about the current and next song (if one exists), a pause/play button, a looping button, a volume slider, and a current position slider. By default, the player will automatically move to the next song once the end is reached. If the song you are on is the last in the list, it will loop back to the beginning. The looping button will instead loop back to the beginning of the song.

