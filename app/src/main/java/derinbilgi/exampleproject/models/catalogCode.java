package derinbilgi.exampleproject.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by host on 17.07.2017.
 */

public class catalogCode {
    @SerializedName("Kod")
    @Expose
    private String catalogCodes;


    public catalogCode(String catalogCodes) {
        this.catalogCodes = catalogCodes;
    }

    public void setCatalogCodes(String catalogCodes) {
        this.catalogCodes = catalogCodes;
    }

    public String getCatalogCodes() {
        return catalogCodes;
    }
}
