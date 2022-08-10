package com.example.withub.data.network


import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import kotlin.collections.ArrayList


object GithubClient {

    fun getApi(): Retrofit {
        val url = "https://api.github.com"
        val gson = Gson()
        val clientBuilder = OkHttpClient.Builder().build()  // http통신 규약을 사용하겠다
        return Retrofit.Builder()
            .baseUrl(url)
            .client(clientBuilder)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}

//깃허브 닉네임 중복체크
data class GithubNickNameData(
    @SerializedName("login") val login: String
)


//깃허브 아이디 유효성 체크 API
interface GithubNickNameCheckApi {
    @GET("users/{username}")
    fun githubNickNameCheck(@Path("username") username: String): retrofit2.Call<GithubNickNameData>
}


class GitHubCommitDatas : ArrayList<GitHubCommitDatasItem>()
data class GitHubCommitDatasItem(
    val author: Author,
    val comments_url: String,
    val commit: Commit,
    val committer: CommitterX,
    val html_url: String,
    val node_id: String,
    val parents: List<Parent>,
    val sha: String,
    val url: String
)

data class Author(
    val avatar_url: String,
    val events_url: String,
    val followers_url: String,
    val following_url: String,
    val gists_url: String,
    val gravatar_id: String,
    val html_url: String,
    val id: Int,
    val login: String,
    val node_id: String,
    val organizations_url: String,
    val received_events_url: String,
    val repos_url: String,
    val site_admin: Boolean,
    val starred_url: String,
    val subscriptions_url: String,
    val type: String,
    val url: String
)

data class AuthorX(
    val date: String,
    val email: String,
    val name: String
)

data class Commit(
    val author: AuthorX,
    val comment_count: Int,
    val committer: Committer,
    val message: String,
    val tree: Tree,
    val url: String,
    val verification: Verification
)

data class Committer(
    val date: String,
    val email: String,
    val name: String
)

data class CommitterX(
    val avatar_url: String,
    val events_url: String,
    val followers_url: String,
    val following_url: String,
    val gists_url: String,
    val gravatar_id: String,
    val html_url: String,
    val id: Int,
    val login: String,
    val node_id: String,
    val organizations_url: String,
    val received_events_url: String,
    val repos_url: String,
    val site_admin: Boolean,
    val starred_url: String,
    val subscriptions_url: String,
    val type: String,
    val url: String
)

data class Parent(
    val sha: String,
    val url: String
)

data class Tree(
    val sha: String,
    val url: String
)

data class Verification(
    val payload: Any,
    val reason: String,
    val signature: Any,
    val verified: Boolean
)

interface GitHubInfoApi {
    //커밋 가져오기
    @GET("/repos/{owner}/{repo}/commits")
    suspend fun getInfo(
        @Header("Authorization") github_token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("author") author: String,
        @Query("since") since: String,
        @Query("until") until: String,
        @Query("per_page") per_page: Int,
        @Query("page") page: Int
    ): GitHubCommitDatas
}


//깃허브 닉네임,오너,레포지토리 체크
data class LoginListData(
    @SerializedName("login") val login: String
)

//깃허브 레포 유효성 체크 API
interface GithubOwnerRepoCheckApi {
    @GET("repos/{owner}/{repo}/contributors")
    fun githubOwnerRepoCheck(@Path("owner") owner: String, @Path("repo") repo: String)
            : retrofit2.Call<List<LoginListData>>
}
