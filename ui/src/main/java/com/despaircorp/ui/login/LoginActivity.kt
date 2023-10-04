package com.despaircorp.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.despaircorp.ui.R
import com.despaircorp.ui.databinding.ActivityLoginBinding
import com.despaircorp.ui.databinding.LoginPopUpBinding
import com.despaircorp.ui.utils.viewBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val binding by viewBinding { ActivityLoginBinding.inflate(it) }
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        
        
        FirebaseApp.initializeApp(this)
        FacebookSdk.setApplicationId("675979727605735")
        FacebookSdk.setClientToken("fbc31c3fd2d7c940b43b233a417d098d")
        FacebookSdk.sdkInitialize(this)
        
        callbackManager = CallbackManager.Factory.create()
        
        
        binding.activityLoginMaterialButtonGithub.setOnClickListener {
        
        }
        
        binding.activityLoginMaterialButtonFacebook.setOnClickListener {
            LoginManager.getInstance()
                .logInWithReadPermissions(this, listOf("email", "public_profile"))
            
            LoginManager.getInstance().registerCallback(callbackManager, object :
                FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    handleFacebookAccessToken(result.accessToken)
                }
                
                override fun onCancel() {
                    // Handle cancel
                }
                
                override fun onError(error: FacebookException) {
                    // Handle error
                }
            })
        }
        
        binding.activityLoginMaterialButtonGoogle.setOnClickListener {
        
        }
        
        binding.activityLoginMaterialButtonMailPassword.setOnClickListener {
            
            val popUpBinding by viewBinding { LoginPopUpBinding.inflate(it) }
            
            MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialAlertDialog_Rounded)
                .setView(popUpBinding.root)
                .create()
                .show()
            
            popUpBinding.loginPopUpTextInputEditTextMail.addTextChangedListener {
                viewModel.onMailTextChanged(it.toString())
            }
            
            popUpBinding.loginPopUpTextInputEditTextPassword.addTextChangedListener {
                viewModel.onPasswordTextChanged(it.toString())
            }
            
            popUpBinding.loginPopUpMaterialButtonConnect.setOnClickListener {
                viewModel.onConnectWithCredentialsClicked()
            }
        }
        
        viewModel.viewAction.observe(this) {
            when (val action = it.getContentIfNotHandled()) {
                
                is LoginAction.Error -> {
                    Toast.makeText(this, action.message, Toast.LENGTH_SHORT).show()
                }
                
                LoginAction.GoToWelcome -> {
                    Log.i("Monokouma", "To welcome")
                }
                
                LoginAction.ChoseUsername -> {
                    Log.i("Monokouma", "To chose name")
                }
                
                else -> {
                    Log.i("Monokouma", "else")
                    Unit
                }
            }
        }
    }
    
    private fun handleFacebookAccessToken(token: AccessToken) {
        viewModel.onFacebookConnection(token)
    }
    
    @Suppress("OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                
            } catch (e: ApiException) {
                e.printStackTrace()
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }
    
    companion object {
        private const val GOOGLE_SIGN_IN = 9001
    }
}