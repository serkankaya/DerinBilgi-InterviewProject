package derinbilgi.exampleproject.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by host on 17.07.2017.
 */

public class catalogCodeArrayModel {
    @SerializedName("EtkinKatalogModels")
    @Expose
    private List<catalogCode> catalogCodes = new ArrayList<catalogCode>();

    public List<catalogCode> getCatalogCodes() {
        return catalogCodes;
    }

    public void setCatalogCodes(List<catalogCode> catalogCodes) {
        this.catalogCodes = catalogCodes;
    }
}
