package com.example.withub.data.network

import com.example.withub.BuildConfig
import com.example.withub.data.model.UserRepoData
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import retrofit2.http.Query as Query

object RetrofitClient {   //object 는 객체를 리턴해줄 수 있어 클래스보다 편리함
    fun initRetrofit(): Retrofit {
        val url = BuildConfig.URL  //서버주소
        val gson = Gson()                      //서버와 주고받을 데이터 형식
        val clientBuilder = OkHttpClient.Builder().build()  // http통신 규약을 사용하겠다
        val connection = Retrofit.Builder()
            .baseUrl(url)
            .client(clientBuilder)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return connection
    }
}


//------------------------------회원가입-------------------------------

//아이디 중복체크
data class IdValue(
    @SerializedName("id") val id: String
)

data class IdCheckData(val message: String, val success: Boolean)

//아이디 중복체크 API
interface IdCheckApi {
    @POST("/account/duplicate/id")
    fun idCheck(@Body requestData: IdValue): Call<IdCheckData>
}


//메일 보내기
data class EmailValue(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String
)

data class EmailCheckData(val message: String, val success: Boolean, val token: String)

//메일 보내는 API
interface SendEmailApi {
    @POST("/account/mail")
    fun emailCheck(@Body requestData: EmailValue): Call<EmailCheckData>
}


//메일로 보낸 인증번호 확인
data class tokenAuthEmailValue(
    @SerializedName("token") val token: String,
    @SerializedName("auth") val auth: String,
    @SerializedName("id") val id: String
)

data class CertiNumCheckData(val message: String, val success: Boolean)

//메일 보내는 API
interface CertiNumConfirmApi {
    @POST("/account/mail/auth")
    fun certiNumCheck(@Body requestData: tokenAuthEmailValue): Call<CertiNumCheckData>
}


//윗허브 닉네임 중복체크
data class NickNameValue(
    @SerializedName("nickname") val nickname: String
)

data class NickNameCheckData(val message: String, val success: Boolean)

//아이디 중복체크 API
interface NickNameCheckApi {
    @POST("/account/duplicate/nickname")
    fun nickNameCheck(@Body requestData: NickNameValue): Call<NickNameCheckData>
}


//회원가입
data class SignupValue(
    @SerializedName("id") val id: String,
    @SerializedName("pw") val pw: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("email") val email: String,
    @SerializedName("area") val area: Int,
    @SerializedName("committer") val committer: String,
    @SerializedName("repository") val repository: List<UserRepoData>
)

data class SignupCheckData(val message: String, val success: Boolean)

//회원가입 API
interface SignupApi {
    @POST("/account")
    fun signupCheck(@Body requestData: SignupValue): Call<SignupCheckData>
}


//----------------------아이디 찾기----------------------------
//아이디 찾기 메일전송
data class FindIdEmailValue(
    @SerializedName("email") val email: String
)

data class IdFindEmailCheckData(val message: String, val success: Boolean, val token: String)

//아이디찾기(메일전송) API
interface FindIdSendEmailApi {
    @POST("/account/id")
    fun emailCheck(@Body requestData: FindIdEmailValue): Call<IdFindEmailCheckData>
}


//메일로 보낸 인증번호 확인
data class FindIdAuthTokenEmailValue(
    @SerializedName("auth") val auth: String,
    @SerializedName("token") val token: String,
    @SerializedName("email") val id: String
)

data class FindIdCertiNumCheckData(val message: String, val success: Boolean, val id: String)

//메일 보내는 API
interface FindIdCertiNumConfirmApi {
    @POST("/account/id/auth")
    fun certiNumCheck(@Body requestData: FindIdAuthTokenEmailValue): Call<FindIdCertiNumCheckData>
}


//-----------------------비밀번호 찾기-------------------------------
//비밀번호 찾기 아이디, 이메일 전송 (로그인 전)
data class FindPwIdEmailValue(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String
)

data class FindPwIdEmailCheckData(val message: String, val success: Boolean, val token: String)

//비밀번호 찾기(메일전송) API
interface FindPwSendEmailApi {
    @POST("/account/pw")
    fun idEmailCheck(@Body requestData: FindPwIdEmailValue): Call<FindPwIdEmailCheckData>
}


//메일로 보낸 인증번호 확인
data class FindPwTokenAuthEmailIdValue(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("auth") val auth: String,
    @SerializedName("token") val token: String
)

data class FindPwCertiNumCheckData(val message: String, val success: Boolean)

//메일 보내는 API
interface FindPwCertiNumConfirmApi {
    @POST("/account/pw/auth")
    fun certiNumCheck(@Body requestData: FindPwTokenAuthEmailIdValue): Call<FindPwCertiNumCheckData>
}


//비밀번호 변경
data class FindPwTokenPwValue(
    @SerializedName("token") val token: String,
    @SerializedName("pw") val pw: String
)

data class FindPwChangePwCheckData(val message: String, val success: Boolean)

//비밀번호 변경 API
interface FindPwChangePwApi {
    @PATCH("/account/pw")
    fun changePw(@Body requestData: FindPwTokenPwValue): Call<FindPwChangePwCheckData>
}

//-------------------------로그인----------------------------------
data class LoginIdPwValue(
    @SerializedName("id") val id: String,
    @SerializedName("pw") val pw: String
)

data class LoginCheckData(val message: String, val success: Boolean, val token: String)

//비밀번호 찾기(메일전송) API
interface LoginApi {
    @POST("/account/login")
    fun loginCheck(@Body requestData: LoginIdPwValue): Call<LoginCheckData>
}

//-----------------------10분에 한번 데이터 저장---------------------------
data class InfoData(
    val token: String,
    val commits: List<GitHubCommitDatasItem>
)

data class ResultData(
    val success: Boolean,
    val message: String
)

interface InfoApi {
    @POST("/info")
    suspend fun sendGithubDataToServer(@Body requestData: InfoData): ResultData
}

//---------------------유저 데이터 반환 API---------------------------
data class MyData(
    val success: Boolean,
    val message: String,
    val committer: String,
    val daily_commit: Int,
    val thirty_commit: List<MyThirtyCommits>,
    val friend_avg: Float,
    val area_avg: Float
)

data class MyThirtyCommits(
    val date: String,
    val commit: Int
)

data class MyNickNameData(
    val success: Boolean,
    val message: String,
    val avatar_url: String,
    val nickname: String
)

interface MyDataApi {
    @GET("/account")
    suspend fun getMyData(@Query("token") token: String): MyData

    @GET("/account/info")
    suspend fun getMyNickname(@Query("token") token: String): MyNickNameData
}
//---------------------내 레포 반환 API---------------------------

data class Repositories(
    val owner: String,
    val name: String
)

data class MyRepoData(
    val message: String,
    val success: Boolean,
    val committer: String,
    val repository: List<Repositories>
)

interface MyRepoDataApi {
    @GET("/account/repo")
    suspend fun getMyRepoData(@Query("token") token: String): MyRepoData
}

interface GetRepoDataApi {
    @GET("/account/repo")
    fun getMyRepoData(@Query("token") token: String): Call<MyRepoData>
}

//------------------------토큰 자동로그인-------------------------------

data class TokenCheckData(val message: String, val success: Boolean)

//자동로그인 토큰 API
interface TokenApi {
    @GET("/token")
    fun loginCheck(@Query("token") token: String): Call<TokenCheckData>
}

//------------------------친구 API-------------------------------

data class FriendListData(
    val success: Boolean,
    val message: String,
    val friends: List<FriendName>
)

data class FriendName(
    val nickname: String,
    val avatar_url: String
)

data class FriendNameData(
    val token: String,
    val nickname: String
)

data class FriendCommitInfo(
    val success: Boolean,
    val message: String,
    val committer: String,
    val friend_daily: Int,
    val thirty_commit: List<MyThirtyCommits>,
    val my_month_total: Int,
    val friend_month_total: Int,
    val repository: List<Repositories>
)


interface FriendApi {
    @GET("/friend")
    suspend fun getFriendList(@Query("token") token: String): FriendListData

    @POST("/friend")
    suspend fun addFriend(@Body addedFriendData: FriendNameData): ResultData

    @HTTP(method = "DELETE", hasBody = true, path = "/friend")
    suspend fun deleteFriend(@Body deleteFriendData: FriendNameData): ResultData

    @GET("/friend/info")
    suspend fun getFriendInfo(
        @Query("token") token: String,
        @Query("nickname") nickname: String
    ): FriendCommitInfo
}


//------------------------커밋 API-------------------------------

data class MyCommitList(
    val success: Boolean,
    val message: String,
    val commits: List<CommitData>
)

data class CommitData(
    val date: String,
    val time: String,
    val repository: String,
    val commit_message: String,
    val sha: String
)

data class FriendRankData(
    val success: Boolean,
    val message: String,
    val daily_rank: List<RankData>,
    val weekly_rank: List<RankData>,
    val monthly_rank: List<RankData>,
    val continuous_rank: List<RankData>
)

data class AreaRankData(
    val success: Boolean,
    val message: String,
    val daily_rank: List<RankData>,
    val weekly_rank: List<RankData>,
    val monthly_rank: List<RankData>,
    val continuous_rank: List<RankData>
)

data class RankData(
    val nickname: String,
    val count: Int
)


interface CommitApi {
    @GET("/commit")
    suspend fun getMyCommitList(@Query("token") token: String): MyCommitList

    @GET("/commit/rank/friend")
    suspend fun getFriendRank(@Query("token") token: String): FriendRankData

    @GET("/commit/rank/area")
    suspend fun getAreaRank(@Query("token") token: String): AreaRankData
}

//-----------------------로그인 이후 새비밀번호---------------------------

//비밀번호 확인 및 탈퇴 비밀번호 확인
data class PwCheckData(val message: String, val success: Boolean)

//현재 비밀번호 확인 API
interface CurrentPwConfirmApi {
    @GET("/account/pw/after")
    fun tokenPwCheck(@Query("token") token: String, @Query("pw") pw: String): Call<PwCheckData>
}


//비밀번호 변경
data class PwTokenPwValue(
    @SerializedName("token") val token: String,
    @SerializedName("pw") val pw: String
)

data class ChangePwCheckData(val message: String, val success: Boolean)

//비밀번호 변경 API
interface ChangePwApi {
    @PATCH("/account/pw/after")
    fun changePw(@Body requestData: PwTokenPwValue): Call<ChangePwCheckData>
}


//------------------------------탈퇴하기-----------------------------------
//탈퇴
data class TokenValue(
    @SerializedName("token") val token: String
)

data class ResignTokenCheckData(val message: String, val success: Boolean)

//탈퇴하기 API
interface ResignApi {
    @DELETE("/account")
    fun tokenCheck(@Body requestData: TokenValue): Call<ResignTokenCheckData>
}

//-----------------------------로그인 후 닉네임 변경--------------------------
//닉네임 변경
data class TokenNickNameValue(
    @SerializedName("token") val token: String,
    @SerializedName("nickname") val nickname: String
)

data class TokenNickNameCheckData(val message: String, val success: Boolean)

//닉네임변경 API
interface ChangeNicknameApi {
    @PATCH("/account/nickname")
    fun tokenCheck(@Body requestData: TokenNickNameValue): Call<TokenNickNameCheckData>
}

//------------------------------레포지토리 수정-------------------------------

data class TokenRepositoryValue(
    @SerializedName("token") val token: String,
    @SerializedName("repository") val repository: ArrayList<Repositories>
)


data class TokenRepositoryCheckData(val message: String, val success: Boolean)

//레포지토리 변경 API
interface ChangeRepositoryApi {
    @PUT("/account/repo")
    fun tokenRepoCheck(@Body requestData: TokenRepositoryValue): Call<TokenRepositoryCheckData>
}
