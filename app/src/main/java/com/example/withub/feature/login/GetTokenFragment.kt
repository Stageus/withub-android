package com.example.withub.feature.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.withub.feature.base.MyApp
import com.example.withub.R
import com.example.withub.feature.signUp.SignupActivity
import com.example.withub.feature.myInfo.NickNameAreaSelectFragment


class GetTokenFragment:Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.gettoken_fragment, container, false)
        val tokenReceiveBtn = view.findViewById<Button>(R.id.github_token_receive_btn_gettoken)
        val githubTokenText = view.findViewById<EditText>(R.id.github_token_paste_edittext_gettoken)
        val nextBtn = view.findViewById<Button>(R.id.next_btn_gettoken)

        val signupActivity = activity as SignupActivity
        val signupBackBtn = signupActivity.findViewById<ImageButton>(R.id.signup_back_btn)

        signupBackBtn.setOnClickListener{
            parentFragmentManager.beginTransaction().replace(R.id.fragmentArea, NickNameAreaSelectFragment()).commit()
        }

        tokenReceiveBtn.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://github.com/settings/tokens")
            startActivity(intent)
        }

        nextBtn.setOnClickListener{
            MyApp.prefs.githubToken = "token "+githubTokenText.text.toString()
            val fragmentManager = parentFragmentManager
            fragmentManager.commit {
                add(R.id.fragmentArea, GitHubRepositoryAddFragment(), "gitHubRepositoryAddFragment")
                addToBackStack(null)
            }
        }
        nextBtnActivate(githubTokenText,nextBtn)
        return view
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


}