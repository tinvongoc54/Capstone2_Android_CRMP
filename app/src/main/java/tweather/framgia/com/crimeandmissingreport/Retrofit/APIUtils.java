package tweather.framgia.com.crimeandmissingreport.Retrofit;

public class APIUtils {
    public static final String BASE_URL = "https://capstone2.herokuapp.com/";
    public static final String API_URL = "https://capstone2-api.herokuapp.com/";
    public static final String API_GET_CRIME_REPORT_LIST_URL = API_URL + "crimes";
    public static final String API_GET_MISSING_PERSON_LIST_URL = API_URL + "missings";
    public static final String API_GET_CRIME_CATEGORY_LIST_URL = API_URL + "categories";
    public static final String API_CREATE_USER_URL = API_URL + "users";
    public static final String API_CHECK_LOGIN_URL = API_URL + "login";
    public static final String API_IMGUR_URL = "https://api.imgur.com/3/";

    public static DataClient getData(String url) {
        return RetrofitClient.getClient(url).create(DataClient.class);
    }
}
