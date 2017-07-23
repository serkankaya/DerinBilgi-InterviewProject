package derinbilgi.exampleproject;
import java.util.Map;
import derinbilgi.exampleproject.models.catalogCodeArrayModel;
import derinbilgi.exampleproject.models.aboutModel;
import derinbilgi.exampleproject.models.catalogFile;
import derinbilgi.exampleproject.models.shopArrayModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.QueryMap;

/**
 * Created by host on 16.07.2017.
 */

public interface MyApi {

    String baseUrl="http://derinkatalog.azurewebsites.net/api/";
    @GET("etkinkataloglar?musteriId=3")
    Call<catalogCodeArrayModel> getCatalogCode(@Header("Authorization") String headerValue);

    @GET("koddankatalog")
    Call<catalogFile> getCatalogFile(@Header("Authorization") String headerValue, @QueryMap Map<String, String> options);

    @GET("musteri?musteriId=3")
    Call<aboutModel> getAboutInformation(@Header("Authorization") String headerValue);

    @GET("log")
    Call<Void> sendLog(@Header("Authorization") String headerValue,@QueryMap Map<String, String> options);

    @GET("magazalar?musteriId=3")
    Call<shopArrayModel> getShopInformation(@Header("Authorization") String headerValue);





}
