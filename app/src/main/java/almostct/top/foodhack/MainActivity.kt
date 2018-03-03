package almostct.top.foodhack

import almostct.top.foodhack.api.Client
import almostct.top.foodhack.ui.OtherFragment
import almostct.top.foodhack.ui.feed.FeedFragment
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var mTextMessage: TextView
    private lateinit var cli: Client

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val selectedFragment = when (item.itemId) {
            R.id.navigation_home -> FeedFragment()
            R.id.navigation_dashboard -> OtherFragment()
            else -> return@OnNavigationItemSelectedListener false
        }
        supportFragmentManager.beginTransaction().replace(R.id.content, selectedFragment).commit()
        true

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // DI
        cli = (applicationContext as App).client

        // Set up bottom bar
        val navigation = findViewById(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.setOnNavigationItemReselectedListener { /* no-op*/ }

        //initial fragment
        supportFragmentManager.beginTransaction().replace(R.id.content, FeedFragment()).commit()
    }
}
