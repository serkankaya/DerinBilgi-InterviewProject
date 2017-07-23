package derinbilgi.exampleproject.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by host on 18.07.2017.
 */

public class aboutModel {
    @SerializedName("FirmaAdi")
    @Expose
    private String companyName;

    @SerializedName("Tanitim")
    @Expose
    private String promotion;

    @SerializedName("Eposta")
    @Expose
    private String email;
    @SerializedName("Tel")
    @Expose
    private String phoneNumber;
    @SerializedName("Resim")
    @Expose
    private String photo;

    public String getCompanyName() {
        return companyName;
    }

    public String getPromotion() {
        return promotion;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPhoto() {
        return photo;
    }
}
