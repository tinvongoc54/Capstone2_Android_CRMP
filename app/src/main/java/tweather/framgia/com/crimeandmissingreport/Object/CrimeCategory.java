package tweather.framgia.com.crimeandmissingreport.Object;

import android.support.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CrimeCategory {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name_category")
    @Expose
    private String nameCategory;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNameCategory() {
        return nameCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }

    @NonNull
    @Override
    public String toString() {
        return nameCategory;
    }
}