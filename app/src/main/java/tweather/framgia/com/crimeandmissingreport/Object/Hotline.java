package tweather.framgia.com.crimeandmissingreport.Object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Hotline {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("area")
    @Expose
    private String area;
    @SerializedName("hotline_number")
    @Expose
    private String hotlineNumber;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getHotlineNumber() {
        return hotlineNumber;
    }

    public void setHotlineNumber(String hotlineNumber) {
        this.hotlineNumber = hotlineNumber;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
