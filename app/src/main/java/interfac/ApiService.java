package interfac;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;

import model.MoveData;
import model.ParamData;
import model.ParamSchedule;
import model.ParamZone;
import model.TinyUser;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
 @Headers("Content-Type: application/json")
 @GET("api/myprofile")
 Call<JsonObject> getProfile(@Header("Authorization") String authorization);

 @Multipart
 @POST("api/profile/update")
 Call<JsonObject> updateProfile(@Header("Authorization") String authorization, @Part("name") RequestBody name,@Part MultipartBody.Part photo, @Part("mobile_no") RequestBody mobile_no, @Part("address") RequestBody address, @Part("gender") RequestBody gender, @Part("national_identification_num") RequestBody national_identification_num, @Part("description") RequestBody description);

 @Headers("Content-Type: application/json")
 @GET("api/doctorlist/{id}")
 Call<JsonArray> getAllDoctorList(@Header("Authorization") String authorization, @Path("id") String id);

 //@Headers("Content-Type: multipart/form-data")
 @Multipart
 @POST("api/VisitDoctor")
 Call<JsonObject> VisitDoctor(@Header("Authorization") String authorization,@Part MultipartBody.Part DoctorImage, @Part("DoctorId") RequestBody DoctorId, @Part("Latitude") RequestBody Latitude, @Part("Longitude") RequestBody Longitude, @Part("Location") RequestBody Location, @Part("EmployeeId") RequestBody EmployeeId,@Part("Comments") RequestBody Comments);

 @Headers("Content-Type: application/json")
 @POST("api/GetVisitedDoctorList")
 Call<JsonObject> getVisitedDoctorList(@Header("Authorization") String authorization,@Body JsonObject jsonBody);

 @Headers("Content-Type: application/json")
 @POST("api/AddDoctor")
 Call<JsonObject> addDoctor(@Header("Authorization") String authorization,@Body JsonObject jsonBody);

 @Headers("Content-Type: application/json")
 @POST("api/GetDashBoardInfo")
 Call<JsonObject> getSummary(@Header("Authorization") String authorization,@Body JsonObject jsonBody);

 /*@Headers("Content-Type: application/json")
 @POST("api/post_local_data")
 Call<JsonObject> postLocalData2Server(@Header("Authorization") String authorization, @Body List<Attendance> attendance);*/

 @Headers("Content-Type: application/json")
 @POST("api/{extra_url}")
 Call<JsonObject> postCheckInOut(@Header("Authorization") String authorization,@Path("extra_url") String extra_url,@Body JsonObject object);

 @Headers("Content-Type: application/json")
 @GET("api/current_attendance_summery")
 Call<JsonObject> getSummary(@Header("Authorization") String authorization);

 @Headers("Content-Type: application/json")
 @GET("api/AreaEmployeeList/{id}")
 Call<JsonArray> getAllEmpList(@Header("Authorization") String authorization, @Path("id") String id);

 //new API
 @Headers("Content-Type: application/json")
 @POST("api/LoginApp")
 Call<JsonObject> loginApp(@Body JsonObject jsonBody);

 @Headers("Content-Type: application/json")
 @GET("api/Doctor")
 Call<JsonObject> getDrList(@Header("auth_token") String authorization);

 @Headers("Content-Type: application/json")
 @GET("api/Chemist")
 Call<JsonObject> getChemistList(@Header("auth_token") String authorization);

 @Headers("Content-Type: application/json")
 @POST("api/Schedule/setPlanDoctor")
 Call<JsonObject> setPlanDoctor(@Header("auth_token") String authorization,@Body JsonObject object);

 @Headers("Content-Type: application/json")
 @POST("api/Schedule/setPlanChemist")
 Call<JsonObject> setPlanChemist(@Header("auth_token") String authorization,@Body JsonObject object);

 @Headers("Content-Type: application/json")
 @GET("api/Schedule/getDrListAfterSetPlan")
 Call<JsonObject> getDrListAfterSetPlan(@Header("auth_token") String authorization,@Query("visitDate") String visitDate,@Query("rosterID") int rosterID);

 @Headers("Content-Type: application/json")
 @GET("api/Schedule/getChListAfterSetPlan")
 Call<JsonObject> getChListAfterSetPlan(@Header("auth_token") String authorization,@Query("visitDate") String visitDate,@Query("rosterID") int rosterID);

 @Multipart
 @POST("api/Schedule/updatePlanDoctor")
 Call<JsonObject> updatePlanDoctor(@Header("auth_token") String authorization,@Part MultipartBody.Part imageUrl,@PartMap() Map<String, RequestBody> partMap);

 @Multipart
 @POST("api/Schedule/updatePlanChemist")
 Call<JsonObject> updatePlanChemist(@Header("auth_token") String authorization,@Part MultipartBody.Part imageUrl,@PartMap() Map<String, RequestBody> partMap);

 @Headers("Content-Type: application/json")
 @POST("api/Schedule/setDoctor")
 Call<JsonObject> setDoctor(@Header("auth_token") String authorization,@Body JsonObject object);

 @Headers("Content-Type: application/json")
 @POST("api/Schedule/setChemist")
 Call<JsonObject> setChemist(@Header("auth_token") String authorization,@Body JsonObject object);

 @Headers("Content-Type: application/json")
 @POST("api/Schedule/setLocationData")
 Call<JsonObject> setLocationData(@Header("auth_token") String authorization, @Body List<MoveData> users);

 @Headers("Content-Type: application/json")
 @GET("global/api/GetALLParameter")
 Call<ParamData>getALLParameter(@Header("auth_token") String authorization);

 @Headers("Content-Type: application/json")
 @GET("api/Schedule/GetMIODoctorVisitReport")
 Call<JsonObject> getMIODoctorVisitReport(@Header("auth_token") String authorization,@Query("ZoneCode") String zoneCode,@Query("DepotCode") String depotCode,@Query("RegionCode") String regionCode,@Query("AreaCode") String areaCode,@Query("TerritoryCode") String territoryCode,@Query("EmpCode") String empCode,@Query("FromDate") String fromDate,@Query("ToDate") String toDate);

 @Headers("Content-Type: application/json")
 @GET("api/Schedule/GetMIOChemistVisitReport")
 Call<JsonObject> getMIOChemistVisitReport(@Header("auth_token") String authorization,@Query("ZoneCode") String zoneCode,@Query("DepotCode") String depotCode,@Query("RegionCode") String regionCode,@Query("AreaCode") String areaCode,@Query("TerritoryCode") String territoryCode,@Query("EmpCode") String empCode,@Query("FromDate") String fromDate,@Query("ToDate") String toDate);

 @Headers("Content-Type: application/json")
 @GET("global/api/GetALLParameterDoctor")
 Call<ParamData>getALLParameterDoctor(@Header("auth_token") String authorization);

 @Headers("Content-Type: application/json")
 @GET("global/api/GetALLParameterChemist")
 Call<ParamData>getALLParameterChemist(@Header("auth_token") String authorization);

 @Headers("Content-Type: application/json")
 @GET("api/Schedule/GetDoctorWiseVisitReport")
 Call<JsonObject> getDoctorWiseVisitReport(@Header("auth_token") String authorization,@Query("ZoneCode") String zoneCode,@Query("DepotCode") String depotCode,@Query("RegionCode") String regionCode,@Query("AreaCode") String areaCode,@Query("TerritoryCode") String territoryCode,@Query("MarketCode") String marketCode,@Query("Id") int id,@Query("FromDate") String fromDate,@Query("ToDate") String toDate);

 @Headers("Content-Type: application/json")
 @GET("api/Schedule/GetChemistWiseVisitReport")
 Call<JsonObject> getChemistWiseVisitReport(@Header("auth_token") String authorization,@Query("ZoneCode") String zoneCode,@Query("DepotCode") String depotCode,@Query("RegionCode") String regionCode,@Query("AreaCode") String areaCode,@Query("TerritoryCode") String territoryCode,@Query("MarketCode") String marketCode,@Query("Id") int id,@Query("FromDate") String fromDate,@Query("ToDate") String toDate);

 @Headers("Content-Type: application/json")
 @GET("api/Schedule/GetMIOWiseTrackingReport")
 Call<JsonObject> getMIOWiseTrackingReport(@Header("auth_token") String authorization,@Query("ZoneCode") String zoneCode,@Query("DepotCode") String depotCode,@Query("RegionCode") String regionCode,@Query("AreaCode") String areaCode,@Query("TerritoryCode") String territoryCode,@Query("EmpCode") String eId,@Query("Date") String date);

 @Headers("Content-Type: application/json")
 @GET("api/Schedule/GetMIOWiseCurrentTrackingReport")
 Call<JsonObject> getMIOWiseCurrentTrackingReport(@Header("auth_token") String authorization,@Query("ZoneCode") String zoneCode,@Query("DepotCode") String depotCode,@Query("RegionCode") String regionCode,@Query("AreaCode") String areaCode,@Query("TerritoryCode") String territoryCode,@Query("EmpCode") String eId);

 @Headers("Content-Type: application/json")
 @POST("api/Auth/changePasswordAPI")
 Call<JsonObject> changePassword(@Header("auth_token") String authorization,@Body JsonObject jsonBody);

 @Headers("Content-Type: application/json")
 @GET("api/Market")
 Call<JsonObject> getMarketList(@Header("auth_token") String authorization);

 @Headers("Content-Type: application/json")
 @POST("api/Schedule/setMarket")
 Call<JsonObject> setMarket(@Header("auth_token") String authorization,@Body JsonObject object);

 @Headers("Content-Type: application/json")
 @POST("api/Schedule/setPlanMarket")
 Call<JsonObject> setPlanMarket(@Header("auth_token") String authorization,@Body JsonObject object);

 @Headers("Content-Type: application/json")
 @GET("api/Market/GetListMarketForPlan")
 Call<JsonObject> getListMarketForPlan(@Header("auth_token") String authorization,@Query("Date") String date);

 @Headers("Content-Type: application/json")
 @GET("api/Doctor/GetListDoctorMarketForPlan")
 Call<JsonObject> getListDoctorMarketForPlan(@Header("auth_token") String authorization,@Query("MarketCode") String mktCode);

 @Headers("Content-Type: application/json")
 @GET("api/Chemist/GetListChemistMarketForPlan")
 Call<JsonObject> getListChemistMarketForPlan(@Header("auth_token") String authorization,@Query("MarketCode") String mktCode);

 @Multipart
 @POST("api/Schedule/PlanSetExecuteDoctor")
 Call<JsonObject> planSetExecuteDoctor(@Header("auth_token") String authorization,@Part MultipartBody.Part imageUrl,@PartMap() Map<String, RequestBody> partMap);

 @Multipart
 @POST("api/Schedule/PlanSetExecuteChemist")
 Call<JsonObject> planSetExecuteChemist(@Header("auth_token") String authorization,@Part MultipartBody.Part imageUrl,@PartMap() Map<String, RequestBody> partMap);

 @Headers("Content-Type: application/json")
 @GET("api/Schedule/GetMarketScheduleData")
 Call<ParamData> getMarketScheduleData(@Header("auth_token") String authorization, @Query("Date") String date, @Query("RosterID") int rosterId);

 @Headers("Content-Type: application/json")
 @GET("api/Schedule/GetEmployeeDynamicData")
 Call<JsonObject> getEmployeeDynamicData(@Header("auth_token") String authorization,@Query("Code") String code,@Query("Type") String type);

 @Headers("Content-Type: application/json")
 @GET("api/Schedule/GetDoctorsDynamicData")
 Call<JsonObject> getDoctorsDynamicData(@Header("auth_token") String authorization,@Query("Code") String code,@Query("Type") String type);

 @Headers("Content-Type: application/json")
 @GET("api/Schedule/GetChemistsDynamicData")
 Call<JsonObject> getChemistsDynamicData(@Header("auth_token") String authorization,@Query("Code") String code,@Query("Type") String type);

 @Multipart
 @POST("api/Schedule/PlanSetExecuteEmp")
 Call<JsonObject> planSetExecuteEmp(@Header("auth_token") String authorization,@Part MultipartBody.Part imageUrl,@PartMap() Map<String, RequestBody> partMap);

 @Headers("Content-Type: application/json")
 @POST("api/LoginApp/LogoutUser")
 Call<JsonObject> logoutUser(@Header("auth_token") String authorization,@Body JsonObject jsonBody);

 @Headers("Content-Type: application/json")
 @POST("api/LoginApp/ConnectionUser")
 Call<JsonObject> connectionUser(@Header("auth_token") String authorization,@Body JsonObject jsonBody);

 @Headers("Content-Type: application/json")
 @GET("global/api/GetSearchList")
 Call<ParamData>getSearchList(@Header("auth_token") String authorization);

 @Headers("Content-Type: application/json")
 @GET("api/Schedule/GetEmployeeReportDynamicData")
 Call<JsonObject> getEmployeeReportDynamicData(@Header("auth_token") String authorization,@Query("Code") String code,@Query("Type") String type,@Query("CodeType") String codeType);

 @Headers("Content-Type: application/json")
 @GET("api/Schedule/GetEmpVisitReport")
 Call<JsonObject> getEmpVisitReport(@Header("auth_token") String authorization,@Query("ZoneCode") String zoneCode,@Query("DepotCode") String depotCode,@Query("RegionCode") String regionCode,@Query("AreaCode") String areaCode,@Query("TerritoryCode") String territoryCode,@Query("EmpCode") String empCode,@Query("FromDate") String fromDate,@Query("ToDate") String toDate);

 @Headers("Content-Type: application/json")
 @GET("global/api/GetCheckinOut")
 Call<JsonObject>getCheckInOut(@Header("auth_token") String authorization);

 @Headers("Content-Type: application/json")
 @POST("api/Schedule/setCheckInOut")
 Call<JsonObject> setCheckInOut(@Header("auth_token") String authorization,@Body JsonObject jsonBody);

 @Headers("Content-Type: application/json")
 @GET("global/api/VisitSummary")
 Call<JsonObject> getVisitSummary(@Header("auth_token") String authorization);

 @Headers("Content-Type: application/json")
 @GET("global/api/GetCheckinOutdetails")
 Call<JsonObject> getCheckInOutDetails(@Header("auth_token") String authorization,@Query("year") int year,@Query("month") int month);

}
