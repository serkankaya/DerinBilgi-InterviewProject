package derinbilgi.exampleproject.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by host on 17.07.2017.
 */

public class catalogFileArrayModel {
    @SerializedName("")
    @Expose
    private List<catalogFile> catalogFiles = new ArrayList<catalogFile>();

    public List<catalogFile> getCodes() {
        return catalogFiles;
    }
}
