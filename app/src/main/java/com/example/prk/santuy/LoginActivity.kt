
package com.example.prk.santuy

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import com.example.prk.santuy.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
//import com.linecorp.linesdk.LoginDelegate
//import com.linecorp.linesdk.Scope
//import com.linecorp.linesdk.widget.LoginButton
//import com.linecorp.linesdk.auth.LineAuthenticationParams
import java.util.*
//import com.linecorp.linesdk.auth.LineLoginResult
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import com.bumptech.glide.request.RequestOptions
import com.example.prk.santuy.helper.Mode
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import org.json.JSONException
import org.json.JSONObject
//import com.linecorp.linesdk.LineApiResponseCode
//import com.linecorp.linesdk.LoginListener
import kotlin.collections.ArrayList

class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    var database : FirebaseFirestore = FirebaseFirestore.getInstance()
    lateinit var callbackmanager : CallbackManager
    private lateinit var auth: FirebaseAuth

    lateinit var mGoogleApiClient : GoogleApiClient
    lateinit var googleSignInClient : GoogleSignInClient

    val RC_SIGN_IN = 1
    val TAG = "INI WEEEEEEEE"

    val facebookCallback = object : FacebookCallback<LoginResult>{
        override fun onSuccess(result: LoginResult?) {
            Log.d(TAG, result.toString())
            handleFacebookAccessToken(result?.accessToken)
        }
        override fun onCancel() {
            Log.d(TAG,"CANCEL" )
        }
        override fun onError(error: FacebookException?) {
            Log.d(TAG, error.toString())
        }
    }

    public override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        val sharePref = PreferenceManager.getDefaultSharedPreferences(this)
        var username = sharePref.getString("username", "")

        if(currentUser != null || !username.equals("") || !username.isEmpty()){
            updateUI(currentUser,username)
        }

    }

    private fun updateUI(currentUser: FirebaseUser?, username : String) {

        val dialog = ProgressDialog(this)
        dialog.setMessage("Please Wait ...")
        dialog.show()

        var tempname = ""

        if(!username.equals("") || !username.isEmpty()){
            tempname = username
        }else{
            tempname = currentUser?.displayName.toString()
        }

        database.collection("users")
                .whereEqualTo("username", tempname).get().addOnCompleteListener{
                    task ->
                    if (!task.result.isEmpty){
                        val partner = task.result.first().data.get("partner") as ArrayList<String>
                        val user = User(
                                task.result.first().getString("userID"),
                                task.result.first().getString("username"),
                                task.result.first().getString("email"),
                                task.result.first().getString("password"),
                                task.result.first().getString("image"),
                                task.result.first().getString("status"),
                                task.result.first().getString("coins"),
                                partner
                        )


                        val intent =  Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(
                                intent.putExtra("user", user)
                        )
                        finish()
                    }else{

                        val photouri = currentUser?.photoUrl.toString()
                        var user = User(
                                "",
                                currentUser?.displayName,
                                currentUser?.email,
                                "",
                                photouri,
                                "",
                                "0",
                                ArrayList()
                        )

                        database.collection("users").add(user).addOnCompleteListener{
                            task ->
                            database.collection("users")
                                    .document(task.result.id)
                                    .update("userID",task.result.id ).addOnCompleteListener {
                                        database.collection("users")
                                                .whereEqualTo("username", currentUser?.displayName).get().addOnCompleteListener { task ->
                                                    if (!task.result.isEmpty) {
                                                        val partner = task.result.first().data.get("partner") as ArrayList<String>
                                                        val user = User(
                                                                task.result.first().getString("userID"),
                                                                task.result.first().getString("username"),
                                                                task.result.first().getString("email"),
                                                                task.result.first().getString("password"),
                                                                task.result.first().getString("image"),
                                                                task.result.first().getString("status"),
                                                                task.result.first().getString("coins"),
                                                                partner
                                                        )


                                                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                                        startActivity(
                                                                intent.putExtra("user", user)
                                                        )
                                                        dialog.dismiss()
                                                        finish()
                                                    }
                                                }
                                    }
                        }
                    }
                }


    }

    private fun handleFacebookAccessToken(token: AccessToken?) {
        Log.d(TAG, "handleFacebookAccessToken: $token")

        val credential = FacebookAuthProvider.getCredential(token!!.token)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = auth.currentUser
                        updateUI(user, user?.displayName!!)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }

                }
    }

    private fun settheme(){
        var sharePref = PreferenceManager.getDefaultSharedPreferences(this)
        val mode = sharePref.getInt("mode",1)

        if(mode == 2){
            this.setTheme(R.style.darkmode)
        }
        else{
            this.setTheme(R.style.AppTheme)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mode.setTheme_(this)

        setContentView(R.layout.activity_login)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleApiClient = GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();

        auth = FirebaseAuth.getInstance()
        val login_button_email = findViewById<Button>(R.id.loginBtn_email)

        loginBtn.setOnClickListener {
          checkUser(database)
        }

        registerBtn.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        callbackmanager = CallbackManager.Factory.create()

        val facebookLoginButton = findViewById<LoginButton>(R.id.facebook_login_button)

        facebookLoginButton.setReadPermissions("email", "public_profile")
        facebookLoginButton.registerCallback(callbackmanager, facebookCallback)

        login_button_email.setOnClickListener{
            Log.d("resultSignInResult","click login")

            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signInIntent,RC_SIGN_IN)
        }

    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }

        callbackmanager.onActivityResult(requestCode, resultCode,data)

    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = auth.currentUser
                        updateUI(user,"")
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        updateUI(null,"")
                    }

                }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d("resultSignInResult","Failed")
    }

    private fun checkUser(database: FirebaseFirestore){

        val dialog = ProgressDialog(this)
        dialog.setMessage("Please Wait ...")
        dialog.show()

        if (usernameTxt.text.toString().isEmpty()) {
            usernameTxt.error = "Please enter your username"
            usernameTxt.requestFocus()
            return
        }

        if (passwordTxt.text.toString().isEmpty()) {
            passwordTxt.error = "Please enter your password"
            passwordTxt.requestFocus()
            return
        }

        database.collection("users").whereEqualTo("username", usernameTxt.text.toString()).
                whereEqualTo("password", passwordTxt.text.toString()).get()
                .addOnCompleteListener{
                    task ->
                    dialog.dismiss()
                    if(task.result.isEmpty){
                        Toast.makeText(this@LoginActivity,"Wrong Username or Password", Toast.LENGTH_LONG).show()
                    }else{
                        for (doc in task.result){
                            val partner = doc.data.get("partner") as ArrayList<String>
                            val user = User(
                                    doc.getString("userID"),
                                    doc.getString("username"),
                                    doc.getString("email"),
                                    doc.getString("password"),
                                    doc.getString("image"),
                                    doc.getString("status"),
                                    doc.getString("coins"),
                                    partner
                            )
                            val sharePref = PreferenceManager.getDefaultSharedPreferences(this)

                            sharePref.edit().putString("userid", user.userID).apply()
                            sharePref.edit().putString("username", user.username).apply()

                            val intent =  Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(
                                    intent.putExtra("user", user)
                            )

                            finish()
                        }
                    }
                }
    }

}


