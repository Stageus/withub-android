package com.example.withub

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.withub.databinding.ActivityMainBinding
import com.example.withub.mainFragments.CommitFragment
import com.example.withub.mainFragments.HomeFragment
import com.example.withub.mainFragments.MyInfoFragment
import com.example.withub.mainFragments.RankingFragment

class MainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var backKeyPressedTime : Long= 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_WITHUB)
        window.statusBarColor = getColor(R.color.point_color)

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )

        //첫 프래그먼트 설정
        if (savedInstanceState == null) {
            if (!isMyServiceRunning(ForegroundService::class.java)){
                val serviceIntent = Intent(this,ForegroundService::class.java)
                applicationContext.startForegroundService(serviceIntent)
            }
            setFragment(HomeFragment(),"Home")
        }

        //바텀 네비게이션 뷰
        binding.bottomNavView.setOnItemSelectedListener { item->
            when(item.itemId){
                R.id.tap_home ->{
                    setFragment(HomeFragment(),"Home")
                }
                R.id.tap_ranking ->{
                    setFragment(RankingFragment(),"Ranking")
                }
                R.id.tap_commit ->{
                    setFragment(CommitFragment(),"Commit")
                }
                R.id.tap_my_info->{
                    setFragment(MyInfoFragment(),"MyInfo")
                }
            }
            true
        }
    }

    // 뒤로가기 눌렀을 때 네비게이션 드로어 닫기
    override fun onBackPressed() {
            supportFragmentManager.fragments.forEach { fragment ->
                if (fragment != null && fragment.isVisible) {
                    with(fragment.childFragmentManager) {
                        if (backStackEntryCount > 0) {
                            popBackStack()
                            return
                        }
                    }
                }

                if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                    backKeyPressedTime = System.currentTimeMillis()
                    Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
                    return
                }

                if (System.currentTimeMillis() <= backKeyPressedTime + 2000){
                    finish()
                }
            }
    }

    private fun setFragment(fragment: Fragment, tag: String){
        val manager: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = manager.beginTransaction()

        //트랜잭션에 tag로 전달된 fragment가 없을 경우 add
        if(manager.findFragmentByTag(tag) == null){
            ft.add(R.id.main_frame_layout, fragment, tag)
        }

        //작업이 수월하도록 manager에 add되어있는 fragment들을 변수로 할당해둠
        val home = manager.findFragmentByTag("Home")
        val ranking = manager.findFragmentByTag("Ranking")
        val commit = manager.findFragmentByTag("Commit")
        val myInfo = manager.findFragmentByTag("MyInfo")
        //모든 프래그먼트 hide
        if(home!=null){
            ft.hide(home)
        }
        if(ranking!=null){
            ft.hide(ranking)
        }
        if(commit!=null){
            ft.hide(commit)
        }
        if(myInfo!=null){
            ft.hide(myInfo)
        }

        //선택한 항목에 따라 그에 맞는 프래그먼트만 show
        if(tag == "Home"){
            if(home!=null){
                ft.show(home)
            }
        }
        else if(tag == "Ranking"){
            if(ranking!=null){
                ft.show(ranking)
            }
        }
        else if(tag == "Commit"){
            if(commit!=null){
                ft.show(commit)
            }
        }
        else if(tag == "MyInfo"){
            if(myInfo!=null){
                ft.show(myInfo)
            }
        }

        //마무리
        ft.commitAllowingStateLoss()
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == serviceClass.name }
    }
}