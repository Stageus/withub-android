package com.example.withub

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity


class GetTokenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_WITHUB)
        super.onCreate(savedInstanceState)
        window.statusBarColor = getColor(R.color.point_color)
        setContentView(R.layout.gettoken_activity)

        val backBtn = findViewById<ImageButton>(R.id.back_btn_get_token_ac)
        val tokenReceiveBtn = findViewById<Button>(R.id.github_token_receive_btn_get_token_ac)
        val confirmBtn = findViewById<Button>(R.id.next_btn_get_token_ac)
        val githubTokenText = findViewById<EditText>(R.id.github_token_paste_edittext_get_token_ad)

        backBtn.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            MyApp.prefs.accountToken = null
            finish()
        }

        tokenReceiveBtn.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://github.com/settings/tokens")
            startActivity(intent)
        }

        confirmBtn.setOnClickListener{
            MyApp.prefs.githubToken = "token "+githubTokenText.text.toString()
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

        nextBtnActivate(githubTokenText,confirmBtn)

    }

    private fun nextBtnActivate(githubTokenText:EditText, nextBtn:Button) {
        githubTokenText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (githubTokenText.text.toString().isNotEmpty()) {
                    nextBtn.setBackgroundResource(R.drawable.login_btn)
                    nextBtn.isEnabled = true
                } else {
                    nextBtn.setBackgroundResource(R.drawable.disabled_button)
                    nextBtn.isEnabled = false
                }
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    override fun onBackPressed() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        MyApp.prefs.accountToken = null
        startActivity(intent)
    }
}