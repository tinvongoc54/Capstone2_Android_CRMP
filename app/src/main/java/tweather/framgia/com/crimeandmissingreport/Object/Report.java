package tweather.framgia.com.crimeandmissingreport.Object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Report {
  @SerializedName("id")
  @Expose
  private int mId;
  @SerializedName("title")
  @Expose
  private String mTitle;
  @SerializedName("description")
  @Expose
  private String mDescription;
  @SerializedName("category_id")
  @Expose
  private int mCrimeCategory;
  @SerializedName("area")
  @Expose
  private String mArea;
  @SerializedName("user_id")
  @Expose
  private int mUserId;
  @SerializedName("created_at")
  @Expose
  private String mTime;
  @SerializedName("image")
  @Expose
  private String mImage;
  private boolean mStatus;

  public Report (){}

  public Report(int id, String title, String description, int crimeCategory, String area, String time, String image, boolean status) {
    mId = id;
    mTitle = title;
    mDescription = description;
    mCrimeCategory = crimeCategory;
    mArea = area;
    mTime = time;
    mImage = image;
    mStatus = status;
  }

  public Report(CrimeReportBuilder crimeReportBuilder) {
    mId = crimeReportBuilder.mId;
    mTitle = crimeReportBuilder.mTitle;
    mDescription = crimeReportBuilder.mDescription;
    mCrimeCategory = crimeReportBuilder.mCrimeCategory;
    mUserId = crimeReportBuilder.mUserId;
    mArea = crimeReportBuilder.mArea;
    mTime = crimeReportBuilder.mTime;
    mImage = crimeReportBuilder.mImage;
    mStatus = crimeReportBuilder.mStatus;
  }

  public int getId() {
    return mId;
  }

  public void setId(int id) {
    mId = id;
  }

  public String getTitle() {
    return mTitle;
  }

  public void setTitle(String title) {
    mTitle = title;
  }

  public String getDescription() {
    return mDescription;
  }

  public void setDescription(String description) {
    mDescription = description;
  }

  public int getCrimeCategory() {
    return mCrimeCategory;
  }

  public void setCrimeCategory(int crimeCategory) {
    mCrimeCategory = crimeCategory;
  }

  public int getUserId() {
    return mUserId;
  }

  public void setUserId(int userId) {
    this.mUserId = userId;
  }

  public String getArea() {
    return mArea;
  }

  public void setArea(String area) {
    mArea = area;
  }

  public String getTime() {
    return mTime;
  }

  public void setTime(String time) {
    mTime = time;
  }

  public String getImage() {
    return mImage;
  }

  public void setImage(String image) {
    mImage = image;
  }

  public boolean isStatus() {
    return mStatus;
  }

  public void setStatus(boolean status) {
    mStatus = status;
  }

  public static class CrimeReportBuilder {
    private int mId;
    private String mTitle;
    private String mDescription;
    private int mCrimeCategory;
    private int mUserId;
    private String mArea;
    private String mTime;
    private String mImage;
    private boolean mStatus;

    public CrimeReportBuilder() {
    }

    public CrimeReportBuilder setId(int id) {
      mId = id;
      return this;
    }

    public CrimeReportBuilder setTitle(String title) {
      mTitle = title;
      return this;
    }

    public CrimeReportBuilder setDescription(String des) {
      mDescription = des;
      return this;
    }

    public CrimeReportBuilder setCrimeCategory(int category) {
      mCrimeCategory = category;
      return this;
    }

    public CrimeReportBuilder setUserId(int userId) {
      mUserId = userId;
      return this;
    }

    public CrimeReportBuilder setArea(String area) {
      mArea = area;
      return this;
    }

    public CrimeReportBuilder setTime(String time) {
      mTime = time;
      return this;
    }

    public CrimeReportBuilder setImage(String image) {
      mImage = image;
      return this;
    }

    public CrimeReportBuilder setStatus(boolean status) {
      mStatus = status;
      return this;
    }

    public Report build() {
      return new Report(this);
    }
  }
}