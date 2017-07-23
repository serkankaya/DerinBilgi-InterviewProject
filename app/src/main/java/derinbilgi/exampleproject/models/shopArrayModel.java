package derinbilgi.exampleproject.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import derinbilgi.exampleproject.models.shopsModel;

/**
 * Created by host on 19.07.2017.
 */

public class shopArrayModel {
    @SerializedName("MagazaModels")
    @Expose
    private List<shopsModel> shopsModelList = new ArrayList<shopsModel>();


    public List<shopsModel> getShopsModelList() {
        return shopsModelList;
    }

    public void setShopsModelList(List<shopsModel> shopsModelList) {
        this.shopsModelList = shopsModelList;
    }
}
