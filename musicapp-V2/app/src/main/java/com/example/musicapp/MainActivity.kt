package com.example.musicapp


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        btn_sign_up.setOnClickListener{
            startActivity(Intent(this,SignUpActivity::class.java))
            finish()
        }

        btn_login.setOnClickListener {
            doLogin()
        }


        loginLayout.setOnTouchListener(object : OnSwipeListener(this) {
            init {
                setDragHorizontal(true)
                setExitScreenOnSwipe(true)
                setAnimationDelay(500)
            }

            override fun onSwipeLeft(distance: Float) {
                val intent = Intent(this@MainActivity, ListActivity::class.java)
                // start your next activity
                startActivity(intent)
            }

            override fun onSwipeRight(distance: Float) {
                val intent = Intent(this@MainActivity, ListActivity::class.java)
                // start your next activity
                startActivity(intent)

            }
        }
        )


    }

    private fun doLogin() {
        if (editText_user.text.toString().isEmpty()) {
            editText_user.error = "Please enter email"
            editText_user.requestFocus()
            return
        }


        if (!Patterns.EMAIL_ADDRESS.matcher(editText_user.text.toString()).matches()) {
            editText_user.error = "Please enter vaild email"
            editText_user.requestFocus()
            return
        }

        if (editText_password.text.toString().isEmpty()) {
            editText_password.error = "Please enter password"
            editText_password.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(editText_user.text.toString(), editText_password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(
                        baseContext, "Login failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        //val currentUser = auth.currentUser
        //updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {

        if (currentUser != null) {
            startActivity(Intent(this, ListActivity::class.java))
        } else {
            Toast.makeText(
                baseContext, "Login failed.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}
