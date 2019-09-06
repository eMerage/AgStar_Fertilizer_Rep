package project.emarge.fertilizerrep.views.activitys

import android.Manifest
import android.app.ActivityOptions
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.navigation.NavigationView
import com.pddstudio.preferences.encrypted.EncryptedPreferences
import kotlinx.android.synthetic.main.activity_visits.*
import kotlinx.android.synthetic.main.dialog_new_visits.*
import project.emarge.fertilizerrep.R
import project.emarge.fertilizerrep.databinding.ActivityVisitsBinding
import project.emarge.fertilizerrep.databinding.DialogNewVisitsBinding
import project.emarge.fertilizerrep.models.datamodel.Dealer
import project.emarge.fertilizerrep.models.datamodel.Visits
import project.emarge.fertilizerrep.viewModels.visits.VisitsViewModel
import project.emarge.fertilizerrep.views.adaptor.visits.VisitsAdaptor
import project.emarge.fertilizerrep.views.adaptors.visits.VisitsDealerAdaptor

class VisitsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    lateinit var bindingVisits: ActivityVisitsBinding
    lateinit var vieModel: VisitsViewModel

 /*   private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    lateinit var locationRequest: LocationRequest

    val LOCATION_REQUEST = 900
    private val REQUEST_CHECK_SETTINGS = 2*/


    lateinit var encryptedPreferences: EncryptedPreferences
    private val USER_REMEMBER = "userRemember"

    private var currentLocation: LatLng = LatLng(0.00,0.00)

    val READ_STORAGE_PERMISSION_REQUEST = 701

    lateinit var dialogNewVisists: Dialog

    lateinit var visitsDealerAdaptor: VisitsDealerAdaptor



    var listDealers = ArrayList<Dealer>()

    var selectedDelear =  Dealer()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingVisits = DataBindingUtil.setContentView(this, R.layout.activity_visits)
        bindingVisits.lifecycleOwner = this
        vieModel = ViewModelProviders.of(this).get(VisitsViewModel::class.java)
        bindingVisits.visits =vieModel





        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        encryptedPreferences =EncryptedPreferences.Builder(application).withEncryptionPassword("122547895511").build()

       /* fusedLocationClient = let { LocationServices.getFusedLocationProviderClient(it) }!!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permission = let { ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) }
            if (permission != PackageManager.PERMISSION_GRANTED) {
                makeRequest()
            } else {
                createLocationRequest()
            }
        } else {
            createLocationRequest()
        }


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                onLocationChanged(locationResult!!.lastLocation)
            }
        }
*/

        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequestStorage()
        } else {
            bindingVisits.visits!!.uploadeMissingImages().observe(this, Observer<Int> {
                it?.let { result ->
                    textview_imagecount.text = "Total images to upload  $result"
                }
            })
        }

        bindingVisits.visits!!.getVisitsFromServer().observe(this, Observer<ArrayList<Visits>> {
            it?.let { result ->
                if(result.isEmpty()){
                   textview_novisits.visibility = View.VISIBLE
                   textview_novisits.text = "No Visits available"
               }else{
                   textview_novisits.visibility = View.GONE
               }
                recyclerView_visits.adapter= VisitsAdaptor(result, this)
            }
        })



        bindingVisits.visits!!.visitsRespons.observe(this, Observer<Visits> {
            it?.let { result ->


                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("VISITID", result.visitsID)
                intent.putExtra("VISITCODE", result.visitsCode)
                intent.putExtra("DEALERNUMBER", selectedDelear.dealerContactNumber)
                val bndlanimation = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out).toBundle()
                startActivity(intent, bndlanimation)
                super.onBackPressed()

            }
        })

    }



    fun onClickFabForNewVisitsDialog(view: View) {

        dialogNewVisists = Dialog(this)
        dialogNewVisists.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogNewVisists.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialogNewVisists.setCancelable(true)

        var bindingDialog: DialogNewVisitsBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_new_visits, null, false)
        dialogNewVisists.setContentView(bindingDialog.root)
        bindingDialog.newVisits = ViewModelProviders.of(this).get(VisitsViewModel::class.java)




        dialogNewVisists.imageView_rep_search.setOnClickListener {
            bindingVisits.visits!!.getSearchDealersToVisits(dialogNewVisists.editText_newvisits_dealer.text.toString(),listDealers).observe(this, Observer<ArrayList<Dealer>> {
                it?.let { result ->
                    visitsDealerAdaptor = VisitsDealerAdaptor(result, this)
                    dialogNewVisists.recyclerView_dealers.adapter = visitsDealerAdaptor
                    visitsDealerAdaptor.setOnItemClickListener(object : VisitsDealerAdaptor.ClickListener {
                        override fun onClick(dealer: Dealer, aView: View) {
                            selectedDelear = dealer
                            bindingDialog.newVisits?.setDealer(dealer)

                        }
                    })

                }
            })

        }



        bindingVisits.visits!!.getDealersToVisits(currentLocation).observe(this, Observer<ArrayList<Dealer>> {
            it?.let { result ->

                listDealers =result
                dialogNewVisists.progressBar_recyclerView_dealerstovisits.visibility = View.GONE

                if (result.isEmpty()) {
                    dialogNewVisists.relativeLayout_recyclerView_dealerstovisits.visibility = View.VISIBLE
                    dialogNewVisists.recyclerView_dealers.visibility = View.INVISIBLE
                    dialogNewVisists.textview_no_dealerstovisits.visibility = View.VISIBLE
                    dialogNewVisists. textview_no_dealerstovisits.text = "No Approved dealers"

                } else {
                    dialogNewVisists.relativeLayout_recyclerView_dealerstovisits.visibility = View.GONE
                    dialogNewVisists.recyclerView_dealers.visibility = View.VISIBLE
                    dialogNewVisists.textview_no_dealerstovisits.visibility = View.GONE
                }


                visitsDealerAdaptor = VisitsDealerAdaptor(result, this)
                dialogNewVisists.recyclerView_dealers.adapter = visitsDealerAdaptor
                visitsDealerAdaptor.setOnItemClickListener(object : VisitsDealerAdaptor.ClickListener {
                    override fun onClick(dealer: Dealer, aView: View) {
                        selectedDelear = dealer
                        bindingDialog.newVisits?.setDealer(dealer)
                    }
                })
                dialogNewVisists.show()

            }
        })


    }





/*
    private fun onLocationChanged(location: Location) {
        currentLocation = LatLng(location.latitude, location.longitude)
    }


    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_REQUEST
        )
    }
*/


/*
    fun createLocationRequest() {

        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }


        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = let { LocationServices.getSettingsClient(it) }
        val task = client?.checkLocationSettings(builder.build())
        builder.setAlwaysShow(true)

        task?.addOnSuccessListener {
            startLocationUpdates()
        }
        task?.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                }

            }
        }


    }
*/


   /* override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    createLocationRequest()
                } else {
                    Toast.makeText(this, "Oops! Permission Denied!!", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }*/

    override fun onDestroy() {
       // stopLocationUpdates()
        super.onDestroy()
    }

    override fun onStop() {
      //  stopLocationUpdates()
        super.onStop()
    }

   /* private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null *//* Looper *//*)
    }
*/

    private fun makeRequestStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_STORAGE_PERMISSION_REQUEST
            )
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {

            READ_STORAGE_PERMISSION_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    bindingVisits.visits!!.uploadeMissingImages().observe(this, Observer<Int> {
                        it?.let { result ->
                            textview_imagecount.text = "Total images to upload  $result"
                        }
                    })
                } else {
                    Toast.makeText(this, "Oops! Permission Denied!!", Toast.LENGTH_SHORT).show()
                }
                return
            }

        }
    }


    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
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
                // Handle the camera action
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
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}



