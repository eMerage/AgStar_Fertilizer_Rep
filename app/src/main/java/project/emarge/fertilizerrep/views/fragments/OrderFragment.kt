package project.emarge.fertilizerrep.views.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_order.*
import kotlinx.android.synthetic.main.fragment_order.view.*
import project.emarge.fertilizerrep.R

import project.emarge.fertilizerrep.models.datamodel.Orders
import project.emarge.fertilizerrep.models.datamodel.Products
import project.emarge.fertilizerrep.models.datamodel.ProductsCategory
import project.emarge.fertilizerrep.viewModels.home.OrderViewModel
import project.emarge.fertilizerrep.viewModels.visits.VisitsViewModel
import project.emarge.fertilizerrep.views.adaptors.home.order.*
import java.util.*

import kotlin.collections.ArrayList


/**
 * A placeholder fragment containing a simple view.
 */
class OrderFragment : Fragment(), DatePickerDialog.OnDateSetListener {


    lateinit var productsAdaptor: ProductsAdaptor
    lateinit var root: View


    lateinit var autoCompleteProductsAdapter: AutoCompleteProductsAdapter
    lateinit var addedProductsAdaptor: AddedProductsAdaptor

    lateinit var productsForOrderConfimationAdaptor: ProductsForOrderConfimationAdaptor


    lateinit var dialogProductQty: Dialog
    lateinit var dialogOrderConfirmation: Dialog


    var selectedProCategoryID: Int = 0
    var addedProducts = ArrayList<Products>()

    var allProducts = ArrayList<Products>()

    lateinit var visitsCode: String


    private lateinit var pageViewModel: OrderViewModel


    private var selectedDeliveryType: String = "Dealer"
    private var selectedPaymentType: String = "Cheque"


    var saveButtonEnable: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            pageViewModel = activity?.run { ViewModelProviders.of(this)[OrderViewModel::class.java] }!!

        }catch (ex:Exception){

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_order, container, false)

        return root

    }



    override fun onStart() {
        super.onStart()

        visitscode_order.text = arguments?.getString(VISITSCODE)
        visitsCode = arguments?.getString(VISITSCODE).toString()
        arguments?.getInt(VISITSID)?.let { pageViewModel.setVisistID(it) }



        getProductsCategorys()
        getProducts(selectedProCategoryID)






        autoCompleteTextView_order_products.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                var selectedPro: Products = parent.getItemAtPosition(position) as Products
                getSearchProducts(selectedPro.productsCode.toString())
                autoCompleteTextView_order_products.setText("")
            }


        imageView_pro_search_order.setOnClickListener {
            getSearchProducts(autoCompleteTextView_order_products.text.toString())
        }



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



        val myStrings = arrayOf("Dealer", "Company")
        spinner_delivery_type.adapter = ArrayAdapter(context as Activity, R.layout.list_bg_spinner, myStrings) as SpinnerAdapter?


        spinner_delivery_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedDeliveryType = parent.getItemAtPosition(position).toString()
            }
        }




        val myStringspayment_type = arrayOf("Cheque", "Cash")
        spinner_order_payment_type.adapter = ArrayAdapter(context as Activity, R.layout.list_bg_spinner, myStringspayment_type)


        spinner_order_payment_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedPaymentType = parent.getItemAtPosition(position).toString()
            }
        }


        button_order_save.setOnClickListener {
            if (saveButtonEnable) {
                pageViewModel.saveOrderToServer(
                    selectedDeliveryType,
                    selectedPaymentType,
                    textview_dispatchdate.text.toString(),
                    root.progressBar_order
                ).observe(this, Observer<Orders> {
                    it?.let { result ->
                        if (!result.status) {
                            Toast.makeText(context as Activity, "Order Adding fail,Please Try again", Toast.LENGTH_LONG)
                                .show()
                        } else {
                            openDialogOrderConfirmation(addedProducts, result)
                            Toast.makeText(context as Activity, "Order Adding Success", Toast.LENGTH_LONG).show()
                            saveButtonEnable = false

                            addedProductsAdaptor = AddedProductsAdaptor(ArrayList<Products>(), context as Activity)
                            root.recyclerView_added_products.adapter = addedProductsAdaptor

                        }
                    }
                })
            } else {
                Toast.makeText(context as Activity, "Order added already to this visits", Toast.LENGTH_LONG).show()
            }

        }

    }


    private fun getSearchProducts(input: String) {
        pageViewModel.getSearchProducts(input, allProducts).observe(this, Observer<ArrayList<Products>> {
            it?.let { result ->
                productsAdaptor = ProductsAdaptor(result, context as Activity)
                recyclerView_products.adapter = productsAdaptor
                productsAdaptor.setOnItemClickListener(object : ProductsAdaptor.ClickListener {
                    override fun onClick(products: Products, aView: View) {
                        openDialogProductQty(products)
                    }
                })

            }
        })

    }


    private fun getProductsCategorys() {
        progressBar_order.visibility = View.VISIBLE
        pageViewModel.getProductCategory().observe(this, Observer<ArrayList<ProductsCategory>> {
            it?.let { result ->
                progressBar_order.visibility = View.GONE
                var listProCat = ArrayList<ProductsCategory>()
                listProCat.add(ProductsCategory(0, "All"))
                listProCat.addAll(result)


                val adapter = ProductCatSpinnerAdapter(
                    context as Activity,
                    R.layout.item_spinner, listProCat
                )
                spinner_order_pro_cat.adapter = adapter

                spinner_order_pro_cat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        var selectedProductsCategory: ProductsCategory = parent.getItemAtPosition(position) as ProductsCategory
                        selectedProCategoryID = selectedProductsCategory.productsID!!
                        getProducts(selectedProCategoryID)

                    }

                }
            }
        })

    }


    private fun getProducts(proCat: Int) {
        progressBar_order.visibility = View.VISIBLE


        pageViewModel.getProducts(proCat).observe(this, Observer<ArrayList<Products>> {
            it?.let { result ->
                allProducts = result

                progressBar_order.visibility = View.GONE

                progressBar_recyclerView_products_order.visibility = View.GONE

                if (result.isEmpty()) {
                    relativeLayout_recyclerView_products_order.visibility = View.VISIBLE
                    recyclerView_products.visibility = View.INVISIBLE
                    textview_recyclerView_products_order.visibility = View.VISIBLE
                    textview_recyclerView_products_order.text = "No Products available"

                } else {
                    relativeLayout_recyclerView_products_order.visibility = View.GONE
                    recyclerView_products.visibility = View.VISIBLE
                    textview_recyclerView_products_order.visibility = View.GONE
                }


                productsAdaptor = ProductsAdaptor(result, context as Activity)
                recyclerView_products.adapter = productsAdaptor

                autoCompleteProductsAdapter = AutoCompleteProductsAdapter(
                    context as Activity,
                    R.layout.fragment_order,
                    R.id.lbl_name,
                    result
                )
                autoCompleteTextView_order_products.setAdapter(autoCompleteProductsAdapter)


                productsAdaptor.setOnItemClickListener(object : ProductsAdaptor.ClickListener {
                    override fun onClick(products: Products, aView: View) {
                        openDialogProductQty(products)
                    }
                })


            }
        })

    }


    private fun addedProduct(products: Products) {
        pageViewModel.addProducts(products, addedProducts).observe(this, Observer<ArrayList<Products>> {
            it?.let { result ->
                if (!card_view_added_products.isVisible) {
                    card_view_added_products.visibility = View.VISIBLE
                }

                addedProducts = result
                addedProductsAdaptor = AddedProductsAdaptor(addedProducts, context as Activity)
                root.recyclerView_added_products.adapter = addedProductsAdaptor


                addedProductsAdaptor.setOnItemClickListener(object : AddedProductsAdaptor.ClickListener {
                    override fun onClick(products: Products, aView: View) {

                        val alertDialogBuilder = AlertDialog.Builder(context)
                        alertDialogBuilder.setTitle("Warning!")
                        alertDialogBuilder.setMessage("Do you really want to delete this product ?")
                        alertDialogBuilder.setPositiveButton(
                            "YES"
                        ) { _, _ ->
                            addedProducts.remove(products)
                            addedProductsAdaptor.notifyDataSetChanged()

                            if (addedProducts.isEmpty()) {
                                card_view_added_products.visibility = View.GONE
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


    private fun openDialogOrderConfirmation(addedProducts: ArrayList<Products>, orders: Orders) {
        dialogOrderConfirmation = Dialog(context as Activity)
        dialogOrderConfirmation.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogOrderConfirmation.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialogOrderConfirmation.setContentView(R.layout.dialog_order_confirmation)
        dialogOrderConfirmation.setCancelable(true)


        var textviewOrderID =
            dialogOrderConfirmation.findViewById<TextView>(R.id.textview_dialogorderconfimations_orderid)
        var textviewVisitCode =
            dialogOrderConfirmation.findViewById<TextView>(R.id.textview_dialogorderconfimations_visitsid)
        var recyclerOrdersList =
            dialogOrderConfirmation.findViewById<RecyclerView>(R.id.recyclerView_dialogorderconfimations_orders)
        var editTextCode =
            dialogOrderConfirmation.findViewById<EditText>(R.id.editText_dialogorderconfimations_confirmation_code)


        var textviewDealerNum =
            dialogOrderConfirmation.findViewById<TextView>(R.id.textview_dialogorderconfimations_dealernumber)





        var btnConfirm = dialogOrderConfirmation.findViewById<Button>(R.id.button_dialogorderconfimations_confirm)
        var btnResend = dialogOrderConfirmation.findViewById<Button>(R.id.button_dialogorderconfimations_resend)

        textviewOrderID.text = orders.orderCode
        textviewVisitCode.text = visitsCode


        productsForOrderConfimationAdaptor = ProductsForOrderConfimationAdaptor(addedProducts, context as Activity)
        recyclerOrdersList.adapter = productsForOrderConfimationAdaptor



        textviewDealerNum.text=arguments?.getString(DELEARNUM)



        btnConfirm.setOnClickListener {
            pageViewModel.setOrderComfirmation(orders.orderID, editTextCode.text.toString()).observe(this, Observer<Orders> {
                it?.let { result ->
                    if (result.status) {
                        Toast.makeText(context as Activity, "Order Confirmation Success", Toast.LENGTH_LONG).show()
                        addedProducts.clear()
                        addedProductsAdaptor = AddedProductsAdaptor(addedProducts, context as Activity)
                        root.recyclerView_added_products.adapter = addedProductsAdaptor
                        dialogOrderConfirmation.dismiss()
                    } else {
                        Toast.makeText(context as Activity, result.networkError.errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            })
        }

        dialogOrderConfirmation.show()


    }


    private fun openDialogProductQty(products: Products) {
        dialogProductQty = Dialog(context as Activity)
        dialogProductQty.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogProductQty.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialogProductQty.setContentView(R.layout.dialog_order_product_qty)
        dialogProductQty.setCancelable(true)

        var textviewProductName = dialogProductQty.findViewById<TextView>(R.id.textview_product_qty_productname)
        var btnProductQty = dialogProductQty.findViewById<Button>(R.id.button_product_qty)
        var editTextProductQty = dialogProductQty.findViewById<EditText>(R.id.editText_product_qty)

        textviewProductName.text = products.productsName

        btnProductQty.setOnClickListener {
            var proQty = 0
            proQty = try {
                editTextProductQty.text.toString().toInt()
            } catch (num: NumberFormatException) {
                0
            }
            products.productsQTy = proQty
            addedProduct(products)
            dialogProductQty.dismiss()
        }

        dialogProductQty.show()

    }


    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val date = (monthOfYear + 1).toString() + "/" + dayOfMonth.toString() + "/" + year
        root.textview_dispatchdate.text = date

    }


    companion object {
        private const val VISITSID = "visitID"
        private const val VISITSCODE = "visitCode"
        private const val DELEARNUM = "dealerNumber"
        private val ARG_SECTION_NUMBER = "section_number"

        fun newInstance(index: Int, visitsid: Int, visitsCode: String,dnumber: String): OrderFragment {
            val fragment = OrderFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_SECTION_NUMBER, index)
            bundle.putInt(VISITSID, visitsid)
            bundle.putString(VISITSCODE, visitsCode)
            bundle.putString(DELEARNUM, dnumber)
            fragment.arguments = bundle
            return fragment
        }
    }
}