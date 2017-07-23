package derinbilgi.exampleproject.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by host on 17.07.2017.
 */

public class catalogFile {
    @SerializedName("Eskisi")
    @Expose
    private Boolean catalogOld;

    @SerializedName("Kod")
    @Expose
    private String catalogCode;

    @SerializedName("Dosya")
    @Expose
    private String catalogFile;

    public Boolean getCatalogOld() {
        return catalogOld;
    }

    public String getCatalogCode() {
        return catalogCode;
    }

    public String getCatalogFile() {
        return catalogFile;
    }
}
