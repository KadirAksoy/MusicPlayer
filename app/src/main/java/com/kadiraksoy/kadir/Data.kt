package com.kadiraksoy.kadir

import android.content.ContentValues
import android.provider.MediaStore
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit


//data class
data class MusicFile(
    val id: Long,
    val title: String,
    val path: String,
    val duration: Long,
    var isFavorite: Boolean = false
)
// localdeki müzikleri alma
fun getMusicList(context: android.content.Context): List<MusicFile> {
    val musicList = mutableListOf<MusicFile>()

    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.DURATION
    )

    val cursor = context.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        null
    )

    cursor?.use {
        while (it.moveToNext()) {
            val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
            val title = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
            val path = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
            val duration = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))

            val musicFile = MusicFile(id, title, path, duration)
            musicList.add(musicFile)
        }
    }
    return musicList
}





// SharedPreferences anahtarları
private const val PREFS_NAME = "MyMusicPrefs"
private const val FAVORITE_KEY_PREFIX = "favorite_"

// Favori durumu al
fun isFavorite(context: Context, musicFile: MusicFile): Boolean {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getBoolean(FAVORITE_KEY_PREFIX + musicFile.id, false)
}

// Favori durumu güncelle
fun updateFavoriteStatus(context: Context, musicFile: MusicFile) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Favori durumu güncelle
    prefs.edit {
        putBoolean(FAVORITE_KEY_PREFIX + musicFile.id, musicFile.isFavorite)
    }
}

// Tüm müzik dosyalarını al, favori durumları ekleyerek
fun getMusicListWithFavorites(context: Context): List<MusicFile> {
    val musicList = getMusicList(context)
    for (musicFile in musicList) {
        musicFile.isFavorite = isFavorite(context, musicFile)
    }
    return musicList
}

fun getFavoriteMusicList(context: Context): List<MusicFile> {
    val allMusicList = getMusicList(context)
    return allMusicList.filter { it.isFavorite }
}
