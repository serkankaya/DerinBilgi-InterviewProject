package derinbilgi.exampleproject.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by host on 19.07.2017.
 */

public class shopsModel {
    @SerializedName("Magaza")
    @Expose
    private String shopName;

    @SerializedName("Lat")
    @Expose
    private String lat;

    @SerializedName("Lng")
    @Expose
    private String lng;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
