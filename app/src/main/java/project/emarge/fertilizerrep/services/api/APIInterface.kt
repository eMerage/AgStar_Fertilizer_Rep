package emarge.project.caloriecaffe.network.api





import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.reactivex.Observable
import project.emarge.fertilizerrep.models.datamodel.*
import retrofit2.http.*


import java.util.ArrayList
import okhttp3.MultipartBody
import retrofit2.http.POST
import retrofit2.http.Multipart




/**
 * Created by Himanshu on 9/6/19.
 */
interface APIInterface {

    @GET("User/ValidateUser")
    abstract fun validateUser(
        @Query("username") username: String, @Query("password") password: String, @Query("usertypeID") usertypeID: Int, @Query(
            "pushtokenid"
        ) pushtokenid: String
    ): Observable<Rep>


    @GET("Visit/GetVisitsByRep")
    fun getVisitsByRep(@Query("userID") userID: Int):  Observable<ArrayList<Visits>>


    @GET("Dealer/GetDealersByRepNearLocation")
    fun getDealersByRepNearLocation(@Query("userID") userID: Int,
                                    @Query("latitude") latitude: Double,
                                    @Query("longtitude") longtitude: Double):  Observable<ArrayList<Dealer>>




    @POST("Visit/SaveVisit")
    abstract fun saveVisit(@Body nfo: JsonObject): Observable<Visits>


    @GET("Product/GetProducts")
    fun getProducts(@Query("categoryID") categoryID: Int,@Query("userID") userID: Int):  Observable<ArrayList<Products>>

    @GET("Product/GetProductCategories")
    fun getProductCategories(@Query("TokenID") tokenID: Int):  Observable<ArrayList<ProductsCategory>>


    @POST("Order/SaveOrder")
    abstract fun saveOrder(@Body nfo: JsonObject): Observable<Orders>


    @POST("Order/ConfirmOrder")
    abstract fun confirmOrder(@Body nfo: JsonObject): Observable<Orders>





    @POST("Visit/SavePaymentVisitV2")
    abstract fun savePaymentVisitWithImageDetails(@Body nfo: JsonObject): Observable<Int>


    @POST("Visit/SaveStockVisitV2")
    abstract fun saveStockVisitWithImageDetails(@Body nfo: JsonObject): Observable<Int>


    @POST("Visit/SaveComplainVisitV2")
    abstract fun saveComplainVisitWithImageDetails(@Body nfo: JsonObject): Observable<Int>



       @Multipart
      @POST("Image/SaveImageFile")
     fun saveImageFile(@Part imageFile :MultipartBody.Part,@Query("imageCode") code: String): Observable<Image>


    @GET("Order/GetOrdersIncomplete")
    fun getOrdersIncomplete(@Query("userID") userID: Int):  Observable<ArrayList<Orders>>


    @POST("Order/ResendConfirmationCode")
    abstract fun resendConfirmationCode(@Query("orderID") orderID: Int): Observable<Orders>


    @GET("Image/GetMissingImagesByUser")
    fun getmissingImages(@Query("userID") userID: Int):  Observable<ArrayList<Image>>



}
