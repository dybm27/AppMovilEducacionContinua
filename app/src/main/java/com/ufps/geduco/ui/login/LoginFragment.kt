package com.ufps.geduco.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ufps.geduco.databinding.FragmentLoginBinding
import com.ufps.geduco.data.RetrofitApi
import com.ufps.geduco.toast
import com.ufps.geduco.viewmodel.DataViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    companion object {
        private const val TAG = "SignInActivity"
    }

    @Inject
    lateinit var retrofitApi: RetrofitApi

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val model: DataViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    handleSignInResult(task)
                } else {
                    binding.signInButton.isEnabled = true
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.signInButton.setOnClickListener {
            it.isEnabled = false
            signIn()
        }
        initObserver()
        return binding.root
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            verifyUser(account)
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            verifyUser(null)
        }
    }

    private fun verifyUser(account: GoogleSignInAccount?) {
        if (account != null) {
            model.verifyUser(account.idToken)
        } else {
            binding.signInButton.isEnabled = true
            toast("Hubo un fallo al intentar iniciar sesion, comunicate con el administrador")
        }
    }

    private fun initObserver() {
        model.user.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { user ->
                findNavController().navigate(
                    LoginFragmentDirections.actionLoginFragmentToHomeFragment(
                        user
                    )
                )
            }
        })
        model.message.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { msg ->
                googleSignInClient.revokeAccess()
                    .addOnCompleteListener(requireActivity()) {
                        toast(msg)
                    }
            }
        })
        model.isLoading.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { isLoading ->
                binding.signInButton.isEnabled = !isLoading
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}