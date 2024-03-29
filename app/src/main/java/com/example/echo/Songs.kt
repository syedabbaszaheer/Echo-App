package com.example.echo

import android.os.Parcel
import android.os.Parcelable

class Songs(var songId: Long,var songTitle: String,var artist: String,var songData: String,var dateAdded: Long): Parcelable
{
    override fun writeToParcel(dest: Parcel?, flags: Int) {

    }

    override fun describeContents(): Int {

   return  0
    }

    object Statified{
       var nameComparator: Comparator<Songs> =Comparator<Songs>{song1,song2  ->
           val songOne =song1.songTitle.toUpperCase()
           val songtwo =song2.songTitle.toUpperCase()
           songOne.compareTo(songtwo)

       }
        var dateComparator: Comparator<Songs> =Comparator<Songs>{song1,song2  ->
            val songOne =song1.dateAdded.toDouble()
            val songtwo =song2.dateAdded.toDouble()
            songtwo.compareTo(songOne)


        }
    }
}