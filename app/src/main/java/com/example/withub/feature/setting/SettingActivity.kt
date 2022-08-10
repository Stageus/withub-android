package com.example.withub.feature.setting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.withub.R
import com.example.withub.databinding.ActivitySettingBinding
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity

class SettingActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = getColor(R.color.point_color)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_setting
        )

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