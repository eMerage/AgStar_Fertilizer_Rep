package project.emarge.fertilizerrep.views.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.fragment_complaint.*
import kotlinx.android.synthetic.main.fragment_complaint.view.*
import project.emarge.fertilizerrep.R
import project.emarge.fertilizerrep.models.datamodel.Complain
import project.emarge.fertilizerrep.models.datamodel.Products
import project.emarge.fertilizerrep.models.datamodel.ProductsCategory


import project.emarge.fertilizerrep.viewModels.home.ComplaintViewModel
import project.emarge.fertilizerrep.views.adaptors.home.complain.AddedComplainAdaptor
import project.emarge.fertilizerrep.views.adaptors.home.complain.ComplainProductsAdaptor
import project.emarge.fertilizerrep.views.adaptors.home.order.AutoCompleteProductsAdapter
import project.emarge.fertilizerrep.views.adaptors.home.order.ProductCatSpinnerAdapter
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * A placeholder fragment containing a simple view.
 */
class ComplaintFragment : Fragment() , DatePickerDialog.OnDateSetListener{


    val IMAGE_PERMISSION_REQUEST = 702
    val PICK_IMAGE_REQUEST = 701
    val CAM_PERMISSION_REQUEST = 703



    val PICK_CAM_REQUEST = 705
    val STORAGE_PERMISSION_REQUEST = 706

    lateinit var filePath: Uri
    var selectedProCategoryID: Int = 0
    var selectedImagefilePath: Uri = Uri.EMPTY

    lateinit var pageViewModel: ComplaintViewModel

    lateinit var root: View


    lateinit var productsAdaptor: ComplainProductsAdaptor
    lateinit var autoCompleteProductsAdapter: AutoCompleteProductsAdapter
    lateinit var addedComplainAdaptor: AddedComplainAdaptor

    var addedComplains = ArrayList<Complain>()
    var allProducts = ArrayList<Products>()
    var selectProduct = Products()


    var saveButtonEnable : Boolean = true

    var isImageFromCamera: Boolean = false
    var currentPhotoPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            pageViewModel = activity?.run { ViewModelProviders.of(this)[ComplaintViewModel::class.java] }!!
        }catch (ex : Exception){

        }

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater?.inflate(R.layout.fragment_complaint, container, false)!!

        getProductsCategorys()
        getProducts(selectedProCategoryID)

        return root
    }





    override fun onStart() {
        super.onStart()

        visitscode_complain.text = arguments?.getString(VISITSCODE)
        arguments?.getInt(VISITSID)?.let { pageViewModel.setVisistID(it) }



        relativeLayout_date_icon.setOnClickListener {
            val c = Calendar.getInstance()
            val dpd = DatePickerDialog.newInstance(
                this,
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
            )
            dpd.show(fragmentManager, "Datepickerdialog")
        }



        autoCompleteTextView_complain_products.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            var selectedPro : Products = parent.getItemAtPosition(position) as Products
            getSearchProducts(selectedPro.productsCode.toString())
            autoCompleteTextView_complain_products.setText("")

        }



        imageView_pro_search_complaint.setOnClickListener {
            getSearchProducts(autoCompleteTextView_complain_products.text.toString())
        }




        imageView_complain_image_gallery_icon.setOnClickListener {
            val permission = ContextCompat.checkSelfPermission(context as Activity, Manifest.permission.READ_EXTERNAL_STORAGE)
            if(permission != PackageManager.PERMISSION_GRANTED){
                makeRequestImage()
            }else{
                chooseFile()
            }
        }



        imageView_complain_image_cam_icon.setOnClickListener {
            val permissionCam = ContextCompat.checkSelfPermission(context as Activity, Manifest.permission.CAMERA)
            val permission =
                ContextCompat.checkSelfPermission(context as Activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            when {
                permissionCam != PackageManager.PERMISSION_GRANTED -> makeRequestCamera()
                permission != PackageManager.PERMISSION_GRANTED -> makeRequestSTORAGE()
                else -> openCamera()
            }
        }



        spinner_complain_pro_cat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                var selectedProductsCategory: ProductsCategory = parent.getItemAtPosition(position) as ProductsCategory
                selectedProCategoryID = selectedProductsCategory.productsID!!
                getProducts(selectedProCategoryID)
            }

        }



        button_add_complain.setOnClickListener {
            if(saveButtonEnable){
                pageViewModel.addComplainProducts(addedComplains,
                    selectProduct,
                    editText_complain_description.text.toString(),
                    editText_complain_invoicenumber.text.toString(),
                    editText_complain_batchnumber.text.toString(),
                    textview_expdate.text.toString(),selectedImagefilePath,isImageFromCamera
                ).observe(this, Observer<ArrayList<Complain>> {
                    it?.let { result ->

                        if(!card_view_added_complain.isVisible){
                            card_view_added_complain.visibility = View.VISIBLE
                        }

                        imageView_complain_image.setImageURI(Uri.EMPTY)
                        selectedImagefilePath = Uri.EMPTY


                        addedComplainAdaptor = AddedComplainAdaptor(result, context as Activity)
                        recyclerView_added_complain.adapter = addedComplainAdaptor







                        addedComplainAdaptor.setOnItemClickListener(object : AddedComplainAdaptor.ClickListener {
                            override fun onClick(complain: Complain, aView: View) {

                                val alertDialogBuilder = AlertDialog.Builder(context)
                                alertDialogBuilder.setTitle("Warning!")
                                alertDialogBuilder.setMessage("Do you really want to delete this complaint ?")
                                alertDialogBuilder.setPositiveButton("YES"
                                ) { _, _ ->
                                    addedComplains.remove(complain)
                                    addedComplainAdaptor.notifyDataSetChanged()
                                    if(addedComplains.isEmpty()){
                                        card_view_added_complain.visibility = View.GONE
                                    }
                                }
                                alertDialogBuilder.setNegativeButton("NO", DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
                                alertDialogBuilder.show()



                            }
                        })

                    }
                })

            }else{
                Toast.makeText(context as Activity, "Complain added already to this visits", Toast.LENGTH_LONG).show()

            }



        }



        button_save_complain.setOnClickListener {


            if(saveButtonEnable){
                pageViewModel.saveComplainToServer(progress_bar_complaint).observe(this, Observer<Int> {
                    it?.let { result ->

                        if(result==0){
                            Toast.makeText(context as Activity, "Complain Adding fail,Please try again ", Toast.LENGTH_LONG).show()
                        }else{

                            saveButtonEnable = false

                            button_add_complain.isEnabled =  false
                            editText_complain_description.text.clear()
                            editText_complain_invoicenumber.text.clear()
                            textview_expdate.text = ""
                            imageView_complain_image.setImageURI(Uri.EMPTY)
                            editText_complain_batchnumber.text.clear()

                            addedComplainAdaptor = AddedComplainAdaptor(ArrayList<Complain>(), context as Activity)
                            recyclerView_added_complain.adapter = addedComplainAdaptor

                            card_view_added_complain.visibility = View.GONE

                            Toast.makeText(context as Activity, "Complain Adding Success", Toast.LENGTH_LONG).show()

                        }

                    }
                })
            }else{
                Toast.makeText(context as Activity, "Complain added already to this visits", Toast.LENGTH_LONG).show()
            }

        }



    }



    private fun getSearchProducts(input : String){
        pageViewModel.getSearchProducts(input,allProducts).observe(this, Observer<ArrayList<Products>> {
            it?.let { result ->
                productsAdaptor = ComplainProductsAdaptor(result, context as Activity)
                recyclerView_products_complain.adapter = productsAdaptor
                productsAdaptor.setOnItemClickListener(object : ComplainProductsAdaptor.ClickListener {
                    override fun onClick(products: Products, aView: View) {
                        selectProduct = products

                    }
                })
            }
        })

    }



    private fun getProducts(proCat: Int) {
        root.progress_bar_complaint.visibility = View.VISIBLE
        pageViewModel.getProducts(proCat).observe(this, Observer<ArrayList<Products>> {
            it?.let { result ->
                allProducts = result
                root.progress_bar_complaint.visibility = View.GONE



                root.progressBar_recyclerView_products_complain.visibility = View.GONE

                if (result.isEmpty()) {
                    root.relativeLayout_recyclerView_products_complain.visibility = View.VISIBLE
                    root.recyclerView_products_complain.visibility = View.INVISIBLE
                    root.textview_recyclerView_products_complain.visibility = View.VISIBLE
                    root.textview_recyclerView_products_complain.text = "No Products available"

                } else {
                    root.relativeLayout_recyclerView_products_complain.visibility = View.GONE
                    root.recyclerView_products_complain.visibility = View.VISIBLE
                    root. textview_recyclerView_products_complain.visibility = View.GONE
                }



                productsAdaptor = ComplainProductsAdaptor(result, context as Activity)
                root.recyclerView_products_complain.adapter = productsAdaptor

                autoCompleteProductsAdapter = AutoCompleteProductsAdapter(
                    context as Activity,
                    R.layout.fragment_order,
                    R.id.lbl_name,
                    result
                )
                root.autoCompleteTextView_complain_products.setAdapter(autoCompleteProductsAdapter)
                productsAdaptor.setOnItemClickListener(object : ComplainProductsAdaptor.ClickListener {
                    override fun onClick(products: Products, aView: View) {
                        selectProduct = products
                    }
                })
            }
        })

    }

    private fun getProductsCategorys() {
        root.progress_bar_complaint.visibility = View.VISIBLE
        pageViewModel.getProductCategory().observe(this, Observer<ArrayList<ProductsCategory>> {
            it?.let { result ->
                root.progress_bar_complaint.visibility = View.GONE
                var listProCat = ArrayList<ProductsCategory>()
                listProCat.add(ProductsCategory(0, "All"))
                listProCat.addAll(result)
                val adapter = ProductCatSpinnerAdapter(
                    context as Activity,
                    R.layout.item_spinner, listProCat
                )
                root.spinner_complain_pro_cat.adapter = adapter
            }
        })

    }

    private fun chooseFile() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE_REQUEST)
    }

    private fun makeRequestImage() {
     requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            IMAGE_PERMISSION_REQUEST
        )
    }

    private fun openCamera() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            dispatchTakePictureIntent()
        }else{
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(context?.packageManager!!)?.also {
                    startActivityForResult(takePictureIntent, PICK_CAM_REQUEST)
                }
            }
        }


    }


    private fun makeRequestSTORAGE() {
      requestPermissions(
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_REQUEST
        )
    }



    private fun makeRequestCamera() {
      requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAM_PERMISSION_REQUEST
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            IMAGE_PERMISSION_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    chooseFile()
                } else {
                    Toast.makeText(context, "Oops! Permission Denied!!", Toast.LENGTH_SHORT).show()
                }
                return
            }
            CAM_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val permission = ContextCompat.checkSelfPermission(
                        context as Activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )

                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        makeRequestSTORAGE()
                    } else {
                        openCamera()
                    }
                } else {
                    Toast.makeText(context as Activity, "Oops! Permission Denied!!", Toast.LENGTH_SHORT).show()
                }
                return
            }

            STORAGE_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val permission = ContextCompat.checkSelfPermission(context as Activity, Manifest.permission.CAMERA)

                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        makeRequestCamera()
                    } else {
                        openCamera()
                    }
                } else {
                    Toast.makeText(context as Activity, "Oops! Permission Denied!!", Toast.LENGTH_SHORT).show()
                }

                return
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_IMAGE_REQUEST -> when (resultCode) {
                Activity.RESULT_OK -> try {
                    isImageFromCamera = false
                    selectedImagefilePath = data?.data!!
                    Glide.with(context as Activity)
                        .asBitmap()
                        .load(selectedImagefilePath)
                        .into(imageView_complain_image)

                } catch (e: Exception) {
                    val alertDialogBuilder = AlertDialog.Builder(context as Activity)
                    alertDialogBuilder.setMessage("Image not selected properly, Please try again$e")
                    alertDialogBuilder.setPositiveButton(
                        "OK",
                        DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
                    alertDialogBuilder.show()
                }
                Activity.RESULT_CANCELED -> Toast.makeText(
                    context as Activity,
                    "Image not selected properly, Please try again",
                    Toast.LENGTH_SHORT
                ).show()
                else -> {
                }
            }
            PICK_CAM_REQUEST -> when (resultCode) {
                Activity.RESULT_OK -> try {
                    isImageFromCamera = true

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Glide.with(context as Activity)
                            .asBitmap()
                            .load(currentPhotoPath)
                            .into(imageView_complain_image)
                        selectedImagefilePath = Uri.parse(currentPhotoPath)
                    } else {
                        filePath = data!!.data!!
                        Glide.with(context as Activity)
                            .asBitmap()
                            .load(filePath)
                            .into(imageView_complain_image)
                        selectedImagefilePath = filePath

                    }


                } catch (e: Exception) {
                    val alertDialogBuilder = AlertDialog.Builder(context as Activity)
                    alertDialogBuilder.setMessage("Image not selected properly, Please try again")
                    alertDialogBuilder.setPositiveButton(
                        "OK",
                        DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
                    alertDialogBuilder.show()
                }
                Activity.RESULT_CANCELED -> Toast.makeText(
                    context as Activity, "Image not selected properly, Please try again", Toast.LENGTH_SHORT
                ).show()
                else -> {
                }
            }




        }

    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(context?.packageManager!!)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context as Activity,(activity?.packageName+".provider"),
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, PICK_CAM_REQUEST)
                }
            }
        }
    }


    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val date = (monthOfYear + 1).toString() + "/" + dayOfMonth.toString() + "/" + year
        textview_expdate.text = date
    }


    companion object {
        private const val VISITSID = "visitID"
        private const val VISITSCODE = "visitCode"
        private val ARG_SECTION_NUMBER = "section_number"

        fun newInstance(index: Int,visitsid: Int, visitsCode: String): ComplaintFragment {
            val fragment = ComplaintFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_SECTION_NUMBER, index)
            bundle.putInt(VISITSID, visitsid)
            bundle.putString(VISITSCODE, visitsCode)
            fragment.arguments = bundle
            return fragment
        }
    }
}