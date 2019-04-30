package tweather.framgia.com.crimeandmissingreport.Retrofit;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class APIUtils {
    public static final String BASE_URL = "https://capstone2.herokuapp.com/";
    public static final String API_URL = "https://capstone2-api.herokuapp.com/";
    public static final String API_GET_CRIMES_URL = API_URL + "crimes/";
    public static final String API_GET_MISSINGS_URL = API_URL + "missings/";
    public static final String API_GET_CRIME_CATEGORY_LIST_URL = API_URL + "categories";
    public static final String API_GET_COMMENT_OF_CRIME_REPORT_URL = API_URL + "crime/";
    public static final String API_GET_COMMENT_OF_MISSING_REPORT_URL = API_URL + "missing/";
    public static final String API_POST_COMMENT_CRIME_URL = API_URL + "commentcrimes";
    public static final String API_POST_COMMENT_MISSING_URL = API_URL + "commentmissings";
    public static final String API_USERS_URL = API_URL + "users";
    public static final String API_UPDATE_USER_URL = API_URL + "users/";
    public static final String API_CHECK_LOGIN_URL = API_URL + "login";
    public static final String API_IMGUR_URL = "https://api.imgur.com/3/";

    public static DataClient getData(String url) {
        return RetrofitClient.getClient(url).create(DataClient.class);
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertTime(String time) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss").parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }
}
