package com.internshala.echo.activities
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import com.internshala.echo.fragments.MainScreenFragment
import com.internshala.echo.R
import com.internshala.echo.activities.MainActivity.Statified.drawerLayout
import com.internshala.echo.adapters.NavigationDrawerAdapter
import com.internshala.echo.fragments.SongPlayingFragment

class MainActivity : AppCompatActivity() {
    /*The line below is used to define the variable for drawer layout*/
    /*The list for storing the names of the items in list of navigation drawer*/
    var navigationDrawerIconsList: ArrayList<String> = arrayListOf()
    /*Images which will be used inside navigation drawer*/
    var images_for_navdrawer = intArrayOf(R.drawable.navigation_allsongs,
            R.drawable.navigation_favorites, R.drawable.navigation_settings,
            R.drawable.navigation_aboutus)
    var tracknNotificationBuilder : Notification? = null
    /*We made the drawer layout as an object of this class so that this object can also be used
as same inside the adapter class without any change in its value*/
    object Statified {
        var drawerLayout: DrawerLayout? = null
        var notificationManager:NotificationManager? = null
    }
    /*While choosing onCreate() method, you might see two options i.e.
    * override fun onCreate(savedInstanceState: Bundle?, persistentState:
    PersistableBundle?) {
    super.onCreate(savedInstanceState, persistentState)
    }
    * and the other one which we chose. You also choose the one we chose as the other one
    is not the one which is included in the lifecycle method*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
/*Here we define the variable for toolbar and by using findViewById, we told our
kotlin file that the toolbar which we made
* is present with the id as toolbar. This id is also present in the R file.*/
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
/*Now the code below is used to tell the compiler that we would be doing some
actions on our toolbar so therefore put our toolbar
* in the form of Action bar which can support multiple actions thus making it
interactive*/
        setSupportActionBar(toolbar)
/*Here we did the similar thing which we did for toolbar earlier, using the
findViewById() and telling the compiler that we will use the drawer layout which
* is present in our XML with the id "drawer_layout"*/
        /*This syntax is used to access the objects inside the class*/
        MainActivity.Statified.drawerLayout = findViewById(R.id.drawer_layout)
/*The icon having 3 parallel horizontal lines is known as the action bar drawer
toggle. The 4 parameters taken by the this can be understood as the:
* 1. Activity: In which activity the toggle is placed
* 2. Drawer Layout: For which drawer layout we are placing this toggle
* 3. Toolbar: In which toolbar are we placing it
* 4, 5: Action Commands: The function of drawer when toggle is click, which is open
and close the drawer*/
        /*Adding names of the titles using the add() function of ArrayList*/
        navigationDrawerIconsList.add("All Songs")
        navigationDrawerIconsList.add("Favorites")
        navigationDrawerIconsList.add("settings")
        navigationDrawerIconsList.add("About Us")
        val toggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
/*Earlier we only defined the toggle, now we place the listener using the
addDrawerListener() method
* Note: In the videos, we told to use setDrawerListener() but now this method cuts
itself when we use it which means that this method is now deprecated which
* can be removed from the future versions, hence here we have told the latest
version of the method i.e. addDrawerListener(). Although as of now the usage
* setDrawerListener() is also correct*/
        drawerLayout?.addDrawerListener(toggle)
/*When we click the toggle icon, we can see the shape of the icon changing its
shape. This may only be visible for a small amount of time as our drawer covers it up.
* But it does and this change happens because we sync the state of the toggle
accordingly using the function syncState()*/toggle.syncState()
/*Creating an object of the MainScreenFragment*/
        val mainScreenFragment = MainScreenFragment()
/*Since our app will contain different fragments and we want to display main screen
fragment whn the app launches, hence we add main screen fragment to the details fragment*/
        this.supportFragmentManager /*the support fragment manager helps us interact with
the fragment in the activity*/
                .beginTransaction() /*This is used for starting a series of functions
which are handled by fragment manager*/
                .add(R.id.details_fragment, mainScreenFragment, "MainScreenFragment")
/*Adding a new fragment in place of the details fragment*/
                .commit() /*This used to save the changes made to the app and the main
screen fragment is made visible to the user*/
        /*Now we create a variable of Navigation Drawer adapter and initialise it with the
params required. As you remember that while creating a class for the navigation drawer
adapter,
* we gave it some params which are required for initialising the class. These
params are the list, images and the context for the adapter file respectively*/
        val _navigationAdapter = NavigationDrawerAdapter(navigationDrawerIconsList,
                images_for_navdrawer, this)
/*Here the function notifyDataSetChanged() tells the adapter that the data you were holding
has been changed and thus you should refresh the list*/
        _navigationAdapter.notifyDataSetChanged()
/*Declaring the variable navigation_recycler_view for the list inside the navigation
drawer*/
        val navigation_recycler_view =
                findViewById<RecyclerView>(R.id.navigation_recycler_view)
/*Here we set a layout manager which aligns the items in a recycler view. As we want to set
the items vertically one below the other we use a linear layout manager.*/
        navigation_recycler_view.layoutManager = LinearLayoutManager(this)
/*As the name is suggesting the item animator is used to animate the way the items appear in
a recycler view. As we used the default item animator, here we will just see the items
* appear as they come without any transition */
        navigation_recycler_view.itemAnimator = DefaultItemAnimator()
/*Now we set the adapter to our recycler view to the adapter we created*/
        navigation_recycler_view.adapter = _navigationAdapter
/*As the code setHasFixedSize() suggests, the number of items present in the recycler view
are fixed and won't change any time*/
        navigation_recycler_view.setHasFixedSize(true)
        var intent = Intent(this@MainActivity,MainActivity::class.java)
        var pIntent = PendingIntent.getActivity(this@MainActivity, System.currentTimeMillis().toInt(),
                intent,0)
        tracknNotificationBuilder = Notification.Builder(this)
                .setContentTitle("A track is playing in background")
                .setSmallIcon(R.drawable.echo_logo)
                .setContentIntent(pIntent)
                .setOngoing(true)
                .setAutoCancel(true)
                .build()
        Statified.notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    override fun onStart() {
        super.onStart()
        try {
            Statified.notificationManager?.cancel(1978)

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            if(SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean){
                Statified.notificationManager?.notify(1978,tracknNotificationBuilder
                )
            }

        }catch (e:Exception){
            e.printStackTrace()

        }
    }

    override fun onResume() {
        super.onResume()
        try {
            Statified.notificationManager?.cancel(1978)

        }catch (e:Exception){
            e.printStackTrace()
        }

    }
}
