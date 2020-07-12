package com.example.musicapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()

        btn_sign_up.setOnClickListener {
            signUpUser()
        }
    }

    private fun signUpUser(){

        if(editText_user.text.toString().isEmpty()){
            editText_user.error = "Please enter email"
            editText_user.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(editText_user.text.toString()).matches()){
            editText_user.error = "Please enter vaild email"
            editText_user.requestFocus()
            return
        }

        if(editText_password.text.toString().isEmpty()){
            editText_password.error = "Please enter password"
            editText_password.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(editText_user.text.toString(), editText_password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful)
                {
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }
                else {
                    Toast.makeText(baseContext, "Sign up failed.Try again after some time",
                        Toast.LENGTH_SHORT).show()
                }
            }

    }
}
