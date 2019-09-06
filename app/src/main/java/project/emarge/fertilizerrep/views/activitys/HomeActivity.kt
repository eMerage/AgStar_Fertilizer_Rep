package project.emarge.fertilizerrep.views.activitys

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.pddstudio.preferences.encrypted.EncryptedPreferences
import kotlinx.android.synthetic.main.activity_home.*

import project.emarge.fertilizerrep.R

import project.emarge.fertilizerrep.views.adaptors.home.PagerHomeAdapter

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {


    var visitID : Int = 0
    lateinit var visitCode : String
    lateinit var dealerNumber : String

    lateinit var pagerAdapter: PagerHomeAdapter
    lateinit var encryptedPreferences: EncryptedPreferences
    private val USER_REMEMBER = "userRemember"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar_home)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout_home, toolbar_home, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout_home.addDrawerListener(toggle)
        toggle.syncState()
        nav_home.setNavigationItemSelectedListener(this)

        encryptedPreferences = EncryptedPreferences.Builder(application).withEncryptionPassword("122547895511").build()


        visitID= getIntent().getIntExtra("VISITID",0)
        visitCode= getIntent().getStringExtra("VISITCODE")

        dealerNumber = getIntent().getStringExtra("DEALERNUMBER")

        pagerAdapter = PagerHomeAdapter(this, supportFragmentManager,visitID,visitCode,dealerNumber)

        view_pager_home.adapter = pagerAdapter
        tabs_home.setupWithViewPager(view_pager_home)



    }



    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout_home)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Exit!")
            alertDialogBuilder.setMessage("Do you really want to exit ?")
            alertDialogBuilder.setPositiveButton("YES"
            ) { _, _ -> super.onBackPressed() }
            alertDialogBuilder.setNegativeButton("NO", DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
            alertDialogBuilder.show()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_visits -> {
                val intent = Intent(this, VisitsActivity::class.java)
                val bndlanimation = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out).toBundle()
                startActivity(intent, bndlanimation)
                super.onBackPressed()
            }

            R.id.nav_order -> {
                val intent = Intent(this, OrderConfirmationActivity::class.java)
                val bndlanimation = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out).toBundle()
                startActivity(intent, bndlanimation)
                super.onBackPressed()
            }
            R.id.nav_logout -> {
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle("Logout!")
                alertDialogBuilder.setMessage("Do you really want to Logout ?")
                alertDialogBuilder.setPositiveButton("YES"
                ) { _, _ ->

                    encryptedPreferences.edit().putBoolean(USER_REMEMBER, false).apply()
                    val intent = Intent(this, LoginActivity::class.java)
                    val bndlanimation = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out).toBundle()
                    startActivity(intent, bndlanimation)
                    super.onBackPressed()

                }
                alertDialogBuilder.setNegativeButton("NO", DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
                alertDialogBuilder.show()
            }

        }

        drawer_layout_home.closeDrawer(GravityCompat.START)
        return true
    }
}
