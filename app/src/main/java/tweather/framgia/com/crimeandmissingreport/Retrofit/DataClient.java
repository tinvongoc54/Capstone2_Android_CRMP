package tweather.framgia.com.crimeandmissingreport.Retrofit;

import java.util.List;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;
import tweather.framgia.com.crimeandmissingreport.Object.CrimeCategory;
import tweather.framgia.com.crimeandmissingreport.Object.Report;
import tweather.framgia.com.crimeandmissingreport.Object.User;

public interface DataClient {
    @FormUrlEncoded
    @POST(APIUtils.API_CHECK_LOGIN_URL)
    Call<List<User>> RequestLogin(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST(APIUtils.API_CREATE_USER_URL)
    Call<JSONObject> CreateUser(@Field("email") String email,
                                @Field("password") String password,
                                @Field("password_confirmation") String password_confirmation,
                                @Field("fullname") String fullname,
                                @Field("phone_number") String phone_number,
                                @Field("address") String address,
                                @Field("role_id") int role_id);

    @GET
    Call<List<Report>> GetCrimeReportList(@Url String url);

    @GET
    Call<List<CrimeCategory>> GetCrimeCategoryList(@Url String url);

    @FormUrlEncoded
    @POST(APIUtils.API_GET_CRIME_REPORT_LIST_URL)
    Call<JSONObject> CreateCrimeReport(@Field("category_id") int categoryId,
            @Field("area") String area,
            @Field("title") String title,
            @Field("description") String description,
            @Field("user_id") int userId);

    @FormUrlEncoded
    @POST(APIUtils.API_GET_MISSING_PERSON_LIST_URL)
    Call<JSONObject> CreateMissingPerson(@Field("title") String title,
                                       @Field("description") String description,
                                       @Field("phone_number") String phone_number,
                                       @Field("user_id") int userId);
}
