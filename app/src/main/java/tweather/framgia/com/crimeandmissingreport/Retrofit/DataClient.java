package tweather.framgia.com.crimeandmissingreport.Retrofit;

import java.util.List;
import okhttp3.MultipartBody;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;
import tweather.framgia.com.crimeandmissingreport.Object.Comment;
import tweather.framgia.com.crimeandmissingreport.Object.CrimeCategory;
import tweather.framgia.com.crimeandmissingreport.Object.ImageResponse;
import tweather.framgia.com.crimeandmissingreport.Object.Report;
import tweather.framgia.com.crimeandmissingreport.Object.User;

public interface DataClient {
    @FormUrlEncoded
    @POST(APIUtils.API_CHECK_LOGIN_URL)
    Call<List<User>> RequestLogin(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST(APIUtils.API_USERS_URL)
    Call<JSONObject> CreateUser(@Field("email") String email, @Field("password") String password,
            @Field("password_confirmation") String password_confirmation,
            @Field("fullname") String fullname, @Field("phone_number") String phone_number,
            @Field("address") String address, @Field("role_id") int role_id);

    @GET
    Call<List<Report>> GetCrimeReportList(@Url String url);

    @GET
    Call<List<CrimeCategory>> GetCrimeCategoryList(@Url String url);

    @FormUrlEncoded
    @POST(APIUtils.API_GET_CRIMES_URL)
    Call<JSONObject> CreateCrimeReport(@Field("category_id") int categoryId,
            @Field("area") String area, @Field("title") String title,
            @Field("description") String description, @Field("image") String image, @Field("user_id") int userId);

    @FormUrlEncoded
    @POST(APIUtils.API_GET_MISSINGS_URL)
    Call<JSONObject> CreateMissingPerson(@Field("title") String title,
            @Field("description") String description, @Field("phone_number") String phone_number,
            @Field("image") String image,
            @Field("user_id") int userId);

    @GET
    Call<List<Report>> GetMissingReportList(@Url String url);

    @Multipart
    @Headers({
            "Authorization: Client-ID c0f7833ecaa0a0d"
    })
    @POST(APIUtils.API_IMGUR_URL + "image/")
    Call<ImageResponse> PostImageToImgur(@Part MultipartBody.Part file);

    @GET
    Call<List<Report>> GetReportListOfUser(@Url String url);

    @GET
    Call<List<Report>> GetMissingReportListOfUser(@Url String url);

    @FormUrlEncoded
    @PUT(APIUtils.API_UPDATE_USER_URL + "{user_id}")
    Call<User> UpdateUserProfile(@Path("user_id") int userId, @Field("email") String email,
            @Field("password") String password, @Field("fullname") String fullname,
            @Field("phone_number") String phone_number, @Field("address") String address);

    @DELETE(APIUtils.API_GET_CRIMES_URL + "{id}")
    Call<JSONObject> DeleteCrimeReport(@Path("id") int reportId);

    @DELETE(APIUtils.API_GET_MISSINGS_URL + "{id}")
    Call<JSONObject> DeleteMissingReport(@Path("id") int reportId);

    @GET
    Call<List<Comment>> GetCommentOfCrimeReport(@Url String url);

    @FormUrlEncoded
    @POST(APIUtils.API_POST_COMMENT_CRIME_URL)
    Call<JSONObject> PostCommentCrimeReport(@Field("crime_id") int crimeId,
            @Field("user_id") int userId, @Field("content") String content);

    @GET
    Call<List<Comment>> GetCommentOfMissingReport(@Url String url);

    @FormUrlEncoded
    @POST(APIUtils.API_POST_COMMENT_MISSING_URL)
    Call<JSONObject> PostCommentMissingReport(@Field("missing_id") int missingId,
            @Field("user_id") int userId, @Field("fullname") String fullName,
            @Field("content") String content);

    @DELETE(APIUtils.API_POST_COMMENT_CRIME_URL + "{id}")
    Call<JSONObject> DeleteCrimeComment(@Path("id") int idComment);

    @DELETE(APIUtils.API_POST_COMMENT_MISSING_URL + "{id}")
    Call<JSONObject> DeleteMissingComment(@Path("id") int idComment);

    @FormUrlEncoded
    @PUT(APIUtils.API_POST_COMMENT_CRIME_URL + "{id}")
    Call<JSONObject> UpdateCrimeComment(@Path("id") int id, @Field("content") String content);

    @FormUrlEncoded
    @PUT(APIUtils.API_POST_COMMENT_MISSING_URL + "{id}")
    Call<JSONObject> UpdateMissingComment(@Path("id") int id, @Field("content") String content);
}
