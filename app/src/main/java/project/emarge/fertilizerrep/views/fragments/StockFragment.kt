package project.emarge.fertilizerrep.views.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
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
import android.view.Window
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_stock.*
import project.emarge.fertilizerrep.R
import project.emarge.fertilizerrep.models.datamodel.Products
import project.emarge.fertilizerrep.models.datamodel.ProductsCategory

import project.emarge.fertilizerrep.viewModels.home.StockViewModel
import project.emarge.fertilizerrep.views.adaptors.home.order.AddedProductsAdaptor
import project.emarge.fertilizerrep.views.adaptors.home.order.AutoCompleteProductsAdapter
import project.emarge.fertilizerrep.views.adaptors.home.order.ProductCatSpinnerAdapter
import project.emarge.fertilizerrep.views.adaptors.home.order.ProductsAdaptor
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * A placeholder fragment containing a simple view.
 */
class StockFragment : Fragment() {


    lateinit var pageViewModel: StockViewModel

    lateinit var root: View
    val IMAGE_PERMISSION_REQUEST = 702
    val PICK_IMAGE_REQUEST = 701
    val CAM_PERMISSION_REQUEST = 703


    val PICK_CAM_REQUEST = 705
    val STORAGE_PERMISSION_REQUEST = 706

    lateinit var filePath: Uri
    var selectedProCategoryID: Int = 0
    var selectedImagefilePath: Uri = Uri.EMPTY

    lateinit var productsAdaptor: ProductsAdaptor
    lateinit var autoCompleteProductsAdapter: AutoCompleteProductsAdapter

    lateinit var imageViewSelectedImage: ImageView
    lateinit var dialogStockProductQty: Dialog

    lateinit var addedProductsAdaptor: AddedProductsAdaptor

    var addedProducts = ArrayList<Products>()
    var allProducts = ArrayList<Products>()

    var saveButtonEnable: Boolean = true

    var isImageFromCamera: Boolean = false
    var currentPhotoPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            pageViewModel = activity?.run { ViewModelProviders.of(this)[StockViewModel::class.java] } !!
        }catch (ex:Exception){


        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater?.inflate(R.layout.fragment_stock, container, false)!!

        return root
    }



    override fun onStart() {
        super.onStart()

        visitscode_stock_fragment.text = arguments?.getString(VISITSCODE)
        arguments?.getInt(VISITSID)?.let { pageViewModel.setVisistID(it) }


        getProductsCategorys()
        getProducts(selectedProCategoryID)



        autoCompleteTextView_staock_products.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                var selectedPro: Products = parent.getItemAtPosition(position) as Products
                getSearchProducts(selectedPro.productsCode.toString())
            }


        imageView_pro_search_stock.setOnClickListener {
            getSearchProducts(autoCompleteTextView_staock_products.text.toString())
        }

        spinner_stock_pro_cat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                var selectedProductsCategory: ProductsCategory = parent.getItemAtPosition(position) as ProductsCategory
                selectedProCategoryID = selectedProductsCategory.productsID!!
                getProducts(selectedProCategoryID)
            }
        }

        button_stock_save.setOnClickListener {
            if (saveButtonEnable) {
                pageViewModel.saveStockToServer(progressBar_stock).observe(this, Observer<Int> {
                    it?.let { result ->
                        if (result == 0) {
                            Toast.makeText(
                                context as Activity,
                                "Stock Adding fail,Please try again ",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(context as Activity, "Stock Adding Success", Toast.LENGTH_LONG).show()
                            saveButtonEnable = false
                            addedProductsAdaptor = AddedProductsAdaptor(ArrayList<Products>(), context as Activity)
                            recyclerView_added_products_stock.adapter = addedProductsAdaptor
                            addedProducts.clear()

                            card_view_stock_added.visibility = View.GONE
                        }
                    }
                })
            } else {
                Toast.makeText(context as Activity, "Stock added already to this visits", Toast.LENGTH_LONG).show()
            }
        }

    }



    private fun getSearchProducts(input: String) {
        pageViewModel.getSearchProducts(input, allProducts).observe(this, Observer<ArrayList<Products>> {
            it?.let { result ->
                productsAdaptor = ProductsAdaptor(result, context as Activity)
                recyclerView_products_stoack.adapter = productsAdaptor
                productsAdaptor.setOnItemClickListener(object : ProductsAdaptor.ClickListener {
                    override fun onClick(products: Products, aView: View) {

                        if (saveButtonEnable) {
                            openDialogProductQty(products)
                        }else{
                            Toast.makeText(context as Activity, "Stock added already to this visits", Toast.LENGTH_LONG).show()
                        }



                    }
                })

            }
        })


    }


    private fun getProductsCategorys() {
        progressBar_stock.visibility = View.VISIBLE
        pageViewModel.getProductCategory().observe(this, Observer<ArrayList<ProductsCategory>> {
            it?.let { result ->
                progressBar_stock.visibility = View.GONE
                var listProCat = ArrayList<ProductsCategory>()
                listProCat.add(ProductsCategory(0, "All"))
                listProCat.addAll(result)
                val adapter = ProductCatSpinnerAdapter(
                    context as Activity,
                    R.layout.item_spinner, listProCat
                )
                spinner_stock_pro_cat.adapter = adapter
            }
        })

    }


    private fun getProducts(proCat: Int) {
        progressBar_stock.visibility = View.VISIBLE
        pageViewModel.getProducts(proCat).observe(this, Observer<ArrayList<Products>> {
            it?.let { result ->
                if (allProducts.isEmpty()) {
                    allProducts = result
                }


                progressBar_recyclerView_products_stock.visibility = View.GONE

                if (result.isEmpty()) {
                    relativeLayout_recyclerView_products_stock.visibility = View.VISIBLE
                    recyclerView_products_stoack.visibility = View.INVISIBLE
                    textview_recyclerView_products_stock.visibility = View.VISIBLE
                    textview_recyclerView_products_stock.text = "No Products available"

                } else {
                    relativeLayout_recyclerView_products_stock.visibility = View.GONE
                    recyclerView_products_stoack.visibility = View.VISIBLE
                    textview_recyclerView_products_stock.visibility = View.GONE
                }





                progressBar_stock.visibility = View.GONE
                productsAdaptor = ProductsAdaptor(result, context as Activity)
                recyclerView_products_stoack.adapter = productsAdaptor

                autoCompleteProductsAdapter = AutoCompleteProductsAdapter(
                    context as Activity,
                    R.layout.fragment_order,
                    R.id.lbl_name,
                    result
                )
                autoCompleteTextView_staock_products.setAdapter(autoCompleteProductsAdapter)
                productsAdaptor.setOnItemClickListener(object : ProductsAdaptor.ClickListener {
                    override fun onClick(products: Products, aView: View) {
                        if (saveButtonEnable) {
                            openDialogProductQty(products)
                        }else{
                            Toast.makeText(context as Activity, "Stock added already to this visits", Toast.LENGTH_LONG).show()
                        }


                    }
                })


            }
        })

    }


    private fun openDialogProductQty(products: Products) {
        dialogStockProductQty = Dialog(context as Activity)
        dialogStockProductQty.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogStockProductQty.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialogStockProductQty.setContentView(R.layout.dialog_stock)
        dialogStockProductQty.setCancelable(true)


        var textviewProductName =
            dialogStockProductQty.findViewById<TextView>(R.id.textview_stack_product_qty_productname)
        var editTextProductQty = dialogStockProductQty.findViewById<EditText>(R.id.editText_stack_product_qty)


        imageViewSelectedImage = dialogStockProductQty.findViewById<ImageView>(R.id.imageView_sock_selected_image)
        var btnProductQty = dialogStockProductQty.findViewById<Button>(R.id.button_stock_product_qty)


        textviewProductName.text = products.productsName


        var imageViewGallery = dialogStockProductQty.findViewById<ImageView>(R.id.imageView_stock_image_gallery_icon)

        var imageViewCam = dialogStockProductQty.findViewById<ImageView>(R.id.imageView_stock_image_cam_icon)






        imageViewGallery.setOnClickListener {
            val permission =
                ContextCompat.checkSelfPermission(context as Activity, Manifest.permission.READ_EXTERNAL_STORAGE)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                makeRequestImage()
            } else {
                chooseFile()
            }
        }



        imageViewCam.setOnClickListener {
            val permissionCam = ContextCompat.checkSelfPermission(context as Activity, Manifest.permission.CAMERA)
            val permission =
                ContextCompat.checkSelfPermission(context as Activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            when {
                permissionCam != PackageManager.PERMISSION_GRANTED -> makeRequestCamera()
                permission != PackageManager.PERMISSION_GRANTED -> makeRequestSTORAGE()
                else -> openCamera()
            }
        }



        btnProductQty.setOnClickListener {
            var proQty = 0
            proQty = try {
                editTextProductQty.text.toString().toInt()
            } catch (num: NumberFormatException) {
                0
            }

            addedProduct(proQty, selectedImagefilePath, products)

        }


        dialogStockProductQty.show()

    }


    private fun addedProduct(qty: Int, imagefilePath: Uri, pro: Products) {

        pageViewModel.addStockProducts(qty, imagefilePath, addedProducts, pro, isImageFromCamera)
            .observe(this, Observer<ArrayList<Products>> {
                it?.let { result ->

                    if(dialogStockProductQty.isShowing){
                        dialogStockProductQty.dismiss()
                    }else{

                    }


                    if (!card_view_stock_added.isVisible) {
                        card_view_stock_added.visibility = View.VISIBLE
                    }

                    addedProducts = result
                    addedProductsAdaptor = AddedProductsAdaptor(addedProducts, context as Activity)
                    recyclerView_added_products_stock.adapter = addedProductsAdaptor


                    selectedImagefilePath = Uri.EMPTY
                    imageViewSelectedImage.setImageURI(Uri.EMPTY)

                    addedProductsAdaptor.setOnItemClickListener(object : AddedProductsAdaptor.ClickListener {
                        override fun onClick(products: Products, aView: View) {

                            val alertDialogBuilder = AlertDialog.Builder(context)
                            alertDialogBuilder.setTitle("Warning!")
                            alertDialogBuilder.setMessage("Do you really want to delete this stock ?")
                            alertDialogBuilder.setPositiveButton(
                                "YES"
                            ) { _, _ ->
                                addedProducts.remove(products)
                                addedProductsAdaptor.notifyDataSetChanged()
                                if (addedProducts.isEmpty()) {
                                    card_view_stock_added.visibility = View.GONE
                                }
                            }
                            alertDialogBuilder.setNegativeButton(
                                "NO",
                                DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
                            alertDialogBuilder.show()


                        }
                    })


                }
            })
    }

    private fun openCamera() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dispatchTakePictureIntent()

        } else {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(context?.packageManager!!)?.also {
                    startActivityForResult(takePictureIntent, PICK_CAM_REQUEST)
                }
            }
        }


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
                        .into(imageViewSelectedImage)

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
                            .into(imageViewSelectedImage)
                        selectedImagefilePath = Uri.parse(currentPhotoPath)
                    } else {
                        filePath = data!!.data!!
                        Glide.with(context as Activity)
                            .asBitmap()
                            .load(filePath)
                            .into(imageViewSelectedImage)
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
                        context as Activity,
                        (activity?.packageName+".provider"),
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, PICK_CAM_REQUEST)
                }
            }
        }
    }


    override fun onDetach() {
        super.onDetach()

    }

    override fun onDestroy() {
        super.onDestroy()

    }


    companion object {
        private const val VISITSID = "visitID"
        private const val VISITSCODE = "visitCode"
        private val ARG_SECTION_NUMBER = "section_number"

        fun newInstance(index: Int, visitsid: Int, visitsCode: String): StockFragment {
            val fragment = StockFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_SECTION_NUMBER, index)
            bundle.putInt(VISITSID, visitsid)
            bundle.putString(VISITSCODE, visitsCode)
            fragment.arguments = bundle
            return fragment
        }
    }

}