package com.example.echo.fragments


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.echo.R
import com.example.echo.Songs
import com.example.echo.adapters.FavoriteAdapter
import com.example.echo.databases.EchoDatabase
import kotlinx.android.synthetic.main.fragment_favorite.*


/**
 * A simple [Fragment] subclass.
 *
 */
class FavoriteFragment : Fragment() {

    var myActivity: Activity?=null

    var noFavorites: TextView?=null
    var nowPlayingBottomBar:RelativeLayout?=null
    var playPauseButton:ImageButton?=null
    var songTile:TextView?=null
    var recyclerView:RecyclerView?=null
    var trackPosition: Int=0
    var favoriteContent: EchoDatabase?=null

    var refreshList:ArrayList<Songs>?=null
    var getListfromDatabase: ArrayList<Songs>?=null

    object Statified{
        var mediaPlayer: MediaPlayer?=null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_favorite, container, false)

        noFavorites=view?.findViewById(R.id.noFavorites)
        nowPlayingBottomBar=view.findViewById(R.id.hiddenBarFavScreen)
        songTile=view.findViewById(R.id.songTitle)
        playPauseButton=view.findViewById(R.id.playPauseButton)
        recyclerView=view.findViewById(R.id.favoriteRecycler)
        return view

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity=context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity= Activity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
          favoriteContent= EchoDatabase(myActivity)
          display_favorites_bysearching()
        bottombarSetup()

    }


    override fun onResume() {
        super.onResume()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item=menu?.findItem(R.id.action_sort)
        item?.isVisible=false
    }

    fun getSongsFromPhone(): ArrayList<Songs>{
        var arrayList=ArrayList<Songs>()
        var contentResolver=myActivity?.contentResolver
        var songUri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor=contentResolver?.query(songUri,null,null,null,null)
        if(songCursor !=null && songCursor.moveToFirst()){
            val songId=songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle= songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist= songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData=songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dataIndex=songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            while(songCursor.moveToNext()){
                var currentId=songCursor.getLong(songId)
                var currentTitle=songCursor.getString(songTitle)
                var currentArtist=songCursor.getString(songArtist)
                var currentData=songCursor.getString(songData)
                var currentDate=songCursor.getLong(dataIndex)
                arrayList.add(Songs(currentId,currentTitle,currentArtist,currentData,currentDate))
            }
        }
        return arrayList
    }
    fun bottombarSetup(){
        try{
            bottomBarClickHandler()
            songTile?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            SongPlayingFragment.Statified.mediaplayer?.setOnCompletionListener({
                songTile?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
                SongPlayingFragment.Staticated.onSongComplete()
            })
            if(SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean){
                nowPlayingBottomBar?.visibility=View.VISIBLE
            }else{
                nowPlayingBottomBar?.visibility=View.INVISIBLE
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun bottomBarClickHandler(){
        nowPlayingBottomBar?.setOnClickListener({

            Statified.mediaPlayer=SongPlayingFragment.Statified.mediaplayer
            val songPlayingFragment = SongPlayingFragment()
            val args = Bundle()
            args.putString("songArtist", SongPlayingFragment.Statified.currentSongHelper?.songArtist)
            args.putString("path", SongPlayingFragment.Statified.currentSongHelper?.songPath)
            args.putString("songTitle", SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            args.putInt("SongId", SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int)
            args.putInt("songPosition", SongPlayingFragment.Statified.currentSongHelper?.currentPosition?.toInt() as Int)
            args.putParcelableArrayList("songData", SongPlayingFragment.Statified.fetchSong)
            args.putString("FavBottomBar","success")
            songPlayingFragment.arguments=args
           fragmentManager?.beginTransaction()
               ?.replace(R.id.details_fragment,songPlayingFragment)
               ?.addToBackStack("SongPlayingFragment")
               ?.commit()
        })

        playPauseButton?.setOnClickListener({
            Statified.mediaPlayer=SongPlayingFragment.Statified.mediaplayer
            if(SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean) {
                SongPlayingFragment.Statified.mediaplayer?.pause()
                trackPosition = SongPlayingFragment.Statified.mediaplayer?.getCurrentPosition() as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            }else{
                SongPlayingFragment.Statified.mediaplayer?.seekTo(trackPosition)
                SongPlayingFragment.Statified.mediaplayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)

            }
        })

    }
    fun display_favorites_bysearching(){
      if(favoriteContent?.checkSize() as Int > 0){
          refreshList= ArrayList<Songs>()
          getListfromDatabase=favoriteContent?.queryDBList()
          var fetchListfromDevice=getSongsFromPhone()
          if(fetchListfromDevice != null){
              for(i in 0..fetchListfromDevice?.size -1){
                  for (j in 0..getListfromDatabase?.size as Int -1){
                      if((getListfromDatabase?.get(j)?.songId)==(fetchListfromDevice?.get(i)?.songId)){
                          refreshList?.add((getListfromDatabase as ArrayList<Songs>)[j])
                      }
                  }
              }
          }else{

          }
          if(refreshList == null){
              recyclerView?.visibility=View.INVISIBLE
              noFavorites?.visibility= View.VISIBLE
          }else{
              var favoriteAdapter=FavoriteAdapter(refreshList as ArrayList<Songs>,myActivity as Context)
              val mLayoutManager=LinearLayoutManager(activity)
              recyclerView?.layoutManager=mLayoutManager
              recyclerView?.itemAnimator=DefaultItemAnimator()
              recyclerView?.adapter=favoriteAdapter
              recyclerView?.setHasFixedSize(true)
          }
      }else{
          recyclerView?.visibility=View.INVISIBLE
          noFavorites?.visibility= View.VISIBLE
      }

      }

}
