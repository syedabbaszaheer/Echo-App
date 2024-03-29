package com.example.echo.activites

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import com.example.echo.R
import com.example.echo.adapters.NavigationDrawerAdapter
import com.example.echo.fragments.MainScreenfragment
import com.example.echo.fragments.SongPlayingFragment

class MainActivity : AppCompatActivity() {

     var navigationDrawerIconsList: ArrayList<String> = arrayListOf()
     var images_for_navdrawer = intArrayOf(R.drawable.navigation_allsongs,R.drawable.navigation_favorites,
         R.drawable.navigation_settings,R.drawable.navigation_aboutus)
    var trackNotificationBuilder:Notification ?=null

    object Satified{
        var drawerLayout: DrawerLayout?=null
        var notificationManager: NotificationManager ?=null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar=findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        MainActivity.Satified.drawerLayout=findViewById(R.id.drawer_layout)

        navigationDrawerIconsList.add("All Songs")
        navigationDrawerIconsList.add("Favorites")
        navigationDrawerIconsList.add("Settings")
        navigationDrawerIconsList.add("About Us")

        val toggle=ActionBarDrawerToggle(this@MainActivity,MainActivity.Satified.drawerLayout,toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        MainActivity.Satified.drawerLayout?.setDrawerListener(toggle)
        toggle.syncState()
        val mainScreenFragment= MainScreenfragment()
        this.supportFragmentManager
            .beginTransaction()
            .add(R.id.details_fragment,mainScreenFragment, "MainScreenFragment")
            .commit()
        var _navigationAdapter=NavigationDrawerAdapter(navigationDrawerIconsList,images_for_navdrawer,this)
        _navigationAdapter.notifyDataSetChanged()

        var navigation_recycler_view=findViewById<RecyclerView>(R.id.navigation_recyler_view)
        navigation_recycler_view.layoutManager=LinearLayoutManager(this)
        navigation_recycler_view.itemAnimator=DefaultItemAnimator()
        navigation_recycler_view.adapter=_navigationAdapter
        navigation_recycler_view.setHasFixedSize(true)
        val intent= Intent(this@MainActivity,MainActivity::class.java)
        val pIntent=PendingIntent.getActivity(this@MainActivity,System.currentTimeMillis().toInt(),
            intent,0)
        trackNotificationBuilder=Notification.Builder(this)
            .setContentTitle("A track is playing in the background")
            .setSmallIcon(R.drawable.echo_logo)
            .setContentIntent(pIntent)
            .setOngoing(true)
            .setAutoCancel(true)
            .build()
        Satified.notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStart() {
        super.onStart()
        try {
           Satified.notificationManager?.cancel(1978)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            if(SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean){
                Satified.notificationManager?.notify(1978,trackNotificationBuilder)
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            Satified.notificationManager?.cancel(1978)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}
