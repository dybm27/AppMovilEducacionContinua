package com.example.educacioncontinua.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.educacioncontinua.databinding.FragmentLoginBinding
import com.example.educacioncontinua.interfaces.RetrofitApi
import com.example.educacioncontinua.models.User
import com.example.educacioncontinua.toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    companion object {
        private const val TAG = "SignInActivity"
        private const val RC_SIGN_IN = 9001
    }

    @Inject
    lateinit var retrofitApi: RetrofitApi

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RC_SIGN_IN) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    handleSignInResult(task)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.signInButton.setOnClickListener {
            signIn()
        }

        return binding.root
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(
                ApiException::class.java
            )
            verifyUser(account)
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            verifyUser(null)
        }
    }

    private fun verifyUser(account: GoogleSignInAccount?) {
        if (account != null) {
            val call = retrofitApi.verifyUser(account.idToken)
            call.enqueue(object : Callback<User?> {
                override fun onResponse(call: Call<User?>, response: Response<User?>) {
                    try {
                        if (response.isSuccessful) {
                            openHome(response.body()!!)
                        } else {
                            revokeAccess(response.code())
                        }
                    } catch (ex: Exception) {
                        toast("Error en el Servidor")
                    }
                }

                override fun onFailure(call: Call<User?>, t: Throwable) {
                    toast("Error de conexión")
                }
            })
        }
    }

    private fun revokeAccess(code: Int) {
        googleSignInClient.revokeAccess()
            .addOnCompleteListener(requireActivity()) {
                when (code) {
                    403 -> toast("No tienes los permisos necesarios para ingresar")
                    500 -> toast("No te encuentras registrado/a")
                    401 -> toast("Su token de validación no es valido")
                }
            }
    }

    fun openHome(user: User) {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment(user))
    }
}