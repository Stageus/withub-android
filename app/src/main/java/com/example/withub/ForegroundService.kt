package com.example.withub

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.ZoneId

class ForegroundService : Service() {

    val handler = CoroutineExceptionHandler{_,exception->
        Log.d("error",exception.toString())
        stopForeground(true)
    }
    val requestCommitApi= GithubClient.getApi().create(GitHubInfoApi::class.java)
    val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
    val myRepoApi= RetrofitClient.initRetrofit().create(MyRepoDataApi::class.java)
    val infoApi =  RetrofitClient.initRetrofit().create(InfoApi::class.java)

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 시간포멧
        val nowDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        val sinceTime =
            nowDateTime.minusDays(30L).withHour(0).withMinute(0).withSecond(0).withNano(0).toString()

        CoroutineScope(Dispatchers.IO).launch(handler){
            while (isActive){
                //레포지토리 호출
                val myRepoData : MyRepoData = withContext(Dispatchers.IO) {
                    myRepoApi.getMyRepoData(MyApp.prefs.accountToken!!)
                }
//                깃허브 정보 호출
                val committer = myRepoData.committer
                val commitsInAllRepo = arrayListOf<GitHubCommitDatasItem>()
                for (i in myRepoData.repository.indices) {
                    val commitsInOneRepo = ArrayList<GitHubCommitDatasItem>()
                    var count = 1
                    while (true){
                        val gitHubCommitDatas: Deferred<GitHubCommitDatas> = async(Dispatchers.IO) {
                            requestCommitApi.getInfo(
                                MyApp.prefs.githubToken!!,
                                myRepoData.repository[i].owner,
                                myRepoData.repository[i].name,
                                committer,
                                sinceTime,
                                nowDateTime.toString(),
                                100,
                                count
                            )
                        }
                        val a = gitHubCommitDatas.await()
                        if(a.isEmpty()){
                            break
                        }else{
                            commitsInOneRepo.addAll(a)
                            count++
                        }
                    }
                    commitsInAllRepo.addAll(commitsInOneRepo)
                }

                val result = gsonBuilder.toJson(commitsInAllRepo)
                Log.d("결과",result)
                val infoData = InfoData(MyApp.prefs.accountToken!!,commitsInAllRepo)
                val resultMessage = infoApi.sendGithubDataToServer(infoData).message
                Log.d("서버결과",resultMessage)
                delay(300000)
            }
        }
        return START_STICKY
    }


    fun createNotification(){
        //알림설정
        val builder = NotificationCompat.Builder(this, "default")
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContentTitle("WITHUB")
        builder.setContentText("Withub 실행중")
        builder.color = Color.RED
        val notificationIntent = Intent(this,MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, FLAG_IMMUTABLE)
        builder.setContentIntent(pendingIntent) // 알림 클릭 시 이동

        //알림표시
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "withub"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("default", name, importance)
            // Register the channel with the system

            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(1,builder.build())
        val notification = builder.build()
        startForeground(1,notification)

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("dd","파괴됨")
    }


}