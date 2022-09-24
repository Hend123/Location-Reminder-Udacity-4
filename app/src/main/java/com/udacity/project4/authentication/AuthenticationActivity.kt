package com.udacity.project4.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {
    private var _binding: ActivityAuthenticationBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AuthenticationViewModel>()

    companion object {
        const val TAG = "AuthenticationActivity"
    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication)
//         TODO: Implement the create account and sign in using FirebaseUI, use sign in using email and sign in using Google
        binding.authBtn.setOnClickListener {
            launchSignInFlow()
        }
//          TODO: If the user was authenticated, send him to RemindersActivity
        observeFirebaseUserState()

//          TODO: a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout

    }

    private fun launchSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            Log.i(
                TAG,
                "Successfully signed in user ${user?.displayName}!"
            )
        } else {
            Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
        }
    }

    private fun observeFirebaseUserState() {
        viewModel.authenticationState.observe(this, Observer {
            when (it) {
                AuthenticationViewModel.AuthenticationState.AUTHENTICATED -> {
                    Log.i(TAG, "User Authenticated")
                    startActivity(Intent(this, RemindersActivity::class.java))
                }
                AuthenticationViewModel.AuthenticationState.UNAUTHENTICATED -> {
                    Log.i(TAG, "User Unauthenticated")
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
