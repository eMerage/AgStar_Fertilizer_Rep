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
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_payment.*
import kotlinx.android.synthetic.main.fragment_payment.view.*

import project.emarge.fertilizerrep.R

import project.emarge.fertilizerrep.models.datamodel.Payment
import project.emarge.fertilizerrep.viewModels.home.PaymentViewModel
import project.emarge.fertilizerrep.views.adaptors.home.payment.AddedPaymentsAdaptor
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * A placeholder fragment containing a simple view.
 */
class PaymentFragment : Fragment() {
    val IMAGE_PERMISSION_REQUEST = 702
    val PICK_IMAGE_REQUEST = 701


    val CAM_PERMISSION_REQUEST = 703
    val PICK_CAM_REQUEST = 705


    val STORAGE_PERMISSION_REQUEST = 706





    lateinit var filePath: Uri


    lateinit var root: View


    var addedPayments = ArrayList<Payment>()

    lateinit var addedPaymentsAdaptor: AddedPaymentsAdaptor
    lateinit var pageViewModel: PaymentViewModel


    private var selectedPaymentType: String = "Cheque"
    var selectedImagefilePath: Uri = Uri.EMPTY


    var saveButtonEnable: Boolean = true

    var currentPhotoPath: String = ""

    var visitCode: String = ""

    var isImageFromCamera: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            pageViewModel = activity?.run { ViewModelProviders.of(this)[PaymentViewModel::class.java] }!!
        }catch (ex:Exception){ }

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_payment, container, false)




        val myStringspayment_type = arrayOf("Cheque", "Cash")
        root.spinner_payment_payment_type.adapter =
            ArrayAdapter(context as Activity, R.layout.list_bg_spinner, myStringspayment_type)



        root.spinner_payment_payment_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPaymentType = parent?.getItemAtPosition(position!!).toString()
            }
        }


        return root



    }



    override fun onStart() {
        super.onStart()


        visitCode = arguments?.getString(VISITSCODE).toString()
        visitscode_payment.text = visitCode


        arguments?.getInt(VISITSID)?.let { pageViewModel.setVisistID(it) }





        imageView_payment_image_gallery_icon.setOnClickListener {
            val permission =
                ContextCompat.checkSelfPermission(context as Activity, Manifest.permission.READ_EXTERNAL_STORAGE)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                makeRequestImage()
            } else {
                chooseFile()
            }
        }



        imageView_payment_image_cam_icon.setOnClickListener {
            val permissionCam = ContextCompat.checkSelfPermission(context as Activity, Manifest.permission.CAMERA)
            val permission =
                ContextCompat.checkSelfPermission(context as Activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            when {
                permissionCam != PackageManager.PERMISSION_GRANTED -> makeRequestCamera()
                permission != PackageManager.PERMISSION_GRANTED -> makeRequestSTORAGE()
                else -> openCamera()
            }
        }






        button_add_payment.setOnClickListener {

            pageViewModel.addPayment(
                addedPayments,
                editText_ordernumber.text.toString(),
                selectedPaymentType,
                editText_payment_value.text.toString(),
                selectedImagefilePath,isImageFromCamera,visitCode
            ).observe(this, Observer<ArrayList<Payment>> {
                it?.let { result ->
                    card_view_added_payment.visibility = View.VISIBLE


                    addedPayments = result
                    addedPaymentsAdaptor = AddedPaymentsAdaptor(addedPayments, context as Activity)
                    recyclerView_added_payment.adapter = addedPaymentsAdaptor

                    addedPaymentsAdaptor.setOnItemClickListener(object : AddedPaymentsAdaptor.ClickListener {
                        override fun onClick(payment: Payment, aView: View) {

                            val alertDialogBuilder = AlertDialog.Builder(context)
                            alertDialogBuilder.setTitle("Warning!")
                            alertDialogBuilder.setMessage("Do you really want to delete this payment ?")
                            alertDialogBuilder.setPositiveButton(
                                "YES"
                            ) { _, _ ->
                                addedPayments.remove(payment)
                                addedPaymentsAdaptor.notifyDataSetChanged()
                                if (addedPayments.isEmpty()) {
                                    card_view_added_payment.visibility = View.GONE
                                }
                            }
                            alertDialogBuilder.setNegativeButton(
                                "NO",
                                DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
                            alertDialogBuilder.show()


                        }
                    })
                    editText_ordernumber.setText("")
                    editText_payment_value.setText("")
                    selectedImagefilePath = Uri.EMPTY
                    imageView_payment_image.setImageURI(Uri.EMPTY)
                }
            })
        }


        button_save_payment.setOnClickListener {
            if (saveButtonEnable) {
                pageViewModel.savePaymentToServer(progressBar_payments).observe(this, Observer<Int> {
                    it?.let { result ->
                        if (result == 0) {
                            Toast.makeText(
                                context as Activity,
                                "Payment Adding fail,Please try again ",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            saveButtonEnable = false

                            Toast.makeText(context as Activity, "Payment Adding Success", Toast.LENGTH_LONG).show()
                            addedPaymentsAdaptor = AddedPaymentsAdaptor(ArrayList<Payment>(), context as Activity)
                            recyclerView_added_payment.adapter = addedPaymentsAdaptor
                            card_view_added_payment.visibility = View.GONE
                            button_add_payment.isEnabled = false
                        }

                    }
                })
            } else {
                Toast.makeText(context as Activity, "Payment collected already to this visits", Toast.LENGTH_LONG)
                    .show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent : Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (requestCode) {
            PICK_IMAGE_REQUEST -> when (resultCode) {
                Activity.RESULT_OK -> try {
                    isImageFromCamera = false
                    selectedImagefilePath = intent?.data!!

                    Glide.with(context as Activity)
                        .asBitmap()
                        .load(selectedImagefilePath)
                        .into(imageView_payment_image)
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
                            .into(imageView_payment_image)
                        selectedImagefilePath = Uri.parse(currentPhotoPath)
                    } else {
                        filePath = intent!!.data!!
                        Glide.with(context as Activity)
                            .asBitmap()
                            .load(filePath)
                            .into(imageView_payment_image)
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

        fun newInstance(index: Int, visitsid: Int, visitsCode: String): PaymentFragment {
            val fragment = PaymentFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_SECTION_NUMBER, index)
            bundle.putInt(VISITSID, visitsid)
            bundle.putString(VISITSCODE, visitsCode)
            fragment.arguments = bundle
            return fragment
        }
    }
}