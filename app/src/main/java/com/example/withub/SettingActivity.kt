package com.example.withub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = getColor(R.color.point_color)
        setContentView(R.layout.setting_activity)

        // 뒤로가기
        findViewById<ImageButton>(R.id.back_btn_setting).let {
            it.setOnClickListener{
                finish()
            }
        }

        // 오픈소스 라이센스
        findViewById<LinearLayout>(R.id.option_opensource_licenses).let {
            it.setOnClickListener {
                startActivity(Intent(this, OssLicensesMenuActivity::class.java))
                OssLicensesMenuActivity.setActivityTitle(getString(R.string.opensource_licenses))
            }
        }

        // 개발자 정보
        findViewById<LinearLayout>(R.id.option_develop_info).let{
            it.setOnClickListener {

            }
        }
    }


}