package com.example.criptotienda

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.criptotienda.model.User
import com.example.criptotienda.network.Callback
import com.example.criptotienda.network.FirestoreService
import com.example.criptotienda.network.USERS_COLLECTION
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_trader.*

/**
 * @author Santiago Carrillo
 * github sancarbar
 * 1/29/19.
 */


const val USERNAME_KEY = "username_key"

class LoginActivity : AppCompatActivity() {


    private val TAG = "LoginActivity"

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    lateinit var firestoreService: FirestoreService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firestoreService = FirestoreService(FirebaseFirestore.getInstance())
    }


    fun onStartClicked(view: View) {
        view.isEnabled = false

        auth.signInAnonymously()
            .addOnCompleteListener {
                task ->
                if( task.isSuccessful) {
                    val username = etUserName.text.toString()
                    firestoreService.findUserById(username, object: Callback<User> {
                        override fun onSuccess(result: User?) {
                            if(result==null) {
                                val userDocument = User()
                                userDocument.username = username
                                saveUserAndStartMainActivity(userDocument, view)
                            } else {
                                startMainActivity(username)
                            }
                        }

                        override fun onFailed(exception: Exception) {
                            showErrorMessage(view)
                        }

                    })

                } else {
                    showErrorMessage(view)
                    view.isEnabled = true
                }
            }
    }

    private fun saveUserAndStartMainActivity(user: User, view: View) {
        firestoreService.setDocument(user, USERS_COLLECTION, user.username, object: Callback<Void>{
            override fun onSuccess(result: Void?) {
                startMainActivity(user.username)
            }

            override fun onFailed(exception: Exception) {
                showErrorMessage(view)
                Log.e(TAG, "error", exception)
                view.isEnabled = true
            }

        })
    }

    private fun showErrorMessage(view: View) {
        Snackbar.make(view, getString(R.string.error_while_connecting_to_the_server), Snackbar.LENGTH_LONG)
            .setAction("Info", null).show()
    }

    private fun startMainActivity(username: String) {
        val intent = Intent(this@LoginActivity, TraderActivity::class.java)
        intent.putExtra(USERNAME_KEY, username)
        startActivity(intent)
        finish()
    }

}
