package project.emarge.fertilizerrep.views.activitys


import android.app.ActivityOptions
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.navigation.NavigationView
import com.pddstudio.preferences.encrypted.EncryptedPreferences
import kotlinx.android.synthetic.main.activity_order_confirmation.*

import project.emarge.fertilizerrep.R
import project.emarge.fertilizerrep.databinding.ActivityOrderConfirmationBinding
import project.emarge.fertilizerrep.models.datamodel.Orders
import project.emarge.fertilizerrep.viewModels.orderconfirmations.OrderConfirmationViewModel
import project.emarge.fertilizerrep.views.adaptor.visits.OrderConfirmationAdaptor

class OrderConfirmationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {



    lateinit var bindingOrderConfirmation: ActivityOrderConfirmationBinding


    lateinit var orderConfirmationAdaptor: OrderConfirmationAdaptor
    lateinit var encryptedPreferences: EncryptedPreferences
    private val USER_REMEMBER = "userRemember"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingOrderConfirmation = DataBindingUtil.setContentView<ActivityOrderConfirmationBinding>(this, R.layout.activity_order_confirmation)
        bindingOrderConfirmation.orderconfirmation = ViewModelProviders.of(this).get(OrderConfirmationViewModel::class.java)



        val toolbar: Toolbar = findViewById(R.id.toolbar_orderconfirmation)
        setSupportActionBar(toolbar)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout_orderconfirmation)
        val navView: NavigationView = findViewById(R.id.nav_view_orderconfirmation)
        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        encryptedPreferences = EncryptedPreferences.Builder(application).withEncryptionPassword("122547895511").build()

        navView.setNavigationItemSelectedListener(this)


        getOrdes()


        swiperefresh_order_confirmation.setOnRefreshListener {
            getOrdes()
        }

        bindingOrderConfirmation.orderconfirmation!!.ordersRespons.observe(this, Observer<Orders> {
            it?.let { result ->
               if(result.status){
                   Toast.makeText(this, "Order confirmation complete", Toast.LENGTH_LONG).show()
                   getOrdes()
               }else{
                   Toast.makeText(this, result.networkError.errorMessage, Toast.LENGTH_LONG).show()

               }

            }
        })




        bindingOrderConfirmation.orderconfirmation!!.reSendRespons.observe(this, Observer<Orders> {
            it?.let { result ->
                if(result.status){
                    Toast.makeText(this, "Order confirmation code resend successfully", Toast.LENGTH_LONG).show()
                    getOrdes()
                }else{
                    Toast.makeText(this, result.networkError.errorMessage, Toast.LENGTH_LONG).show()

                }

            }
        })

    }



    fun getOrdes(){
        bindingOrderConfirmation.orderconfirmation!!.getOrdersFromServer().observe(this, Observer<ArrayList<Orders>> {
            it?.let { result ->
                swiperefresh_order_confirmation.isRefreshing = false

                if(result.isEmpty()){
                    textview_notconfrimorders.visibility = View.VISIBLE
                    textview_notconfrimorders.text = "No incomplete orders available"
                }else{
                    textview_notconfrimorders.visibility = View.GONE
                }

                recyclerView_notconfrim_orders.adapter =  OrderConfirmationAdaptor(result, this, bindingOrderConfirmation.orderconfirmation!!)
            }
        })

    }


    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout_orderconfirmation)
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

            }
            R.id.nav_logout -> {
                val alertDialogBuilder = android.app.AlertDialog.Builder(this)
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
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout_orderconfirmation)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
