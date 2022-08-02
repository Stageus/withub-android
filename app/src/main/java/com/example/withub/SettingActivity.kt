package com.example.withub

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.withub.databinding.ActivitySettingBinding
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity

class SettingActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        val view = binding.root
        window.statusBarColor = getColor(R.color.point_color)
        setContentView(view)

        // 뒤로가기
        binding.backBtnSetting.setOnClickListener{
            finish()
        }

        // 오픈소스 라이센스
        binding.optionOpensourceLicenses.setOnClickListener {
            startActivity(Intent(this, OssLicensesMenuActivity::class.java))
            OssLicensesMenuActivity.setActivityTitle(getString(R.string.opensource_licenses))
        }

        // 개발자 정보
        binding.optionDevelopInfo.setOnClickListener {
        }
    }


}