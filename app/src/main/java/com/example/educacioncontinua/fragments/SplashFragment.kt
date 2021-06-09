package com.example.educacioncontinua.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.educacioncontinua.R
import com.example.educacioncontinua.databinding.FragmentSplashBinding
import com.example.educacioncontinua.interfaces.RetrofitApi
import com.example.educacioncontinua.models.User
import com.example.educacioncontinua.toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private lateinit var callback: OnBackPressedCallback

    companion object {
        private const val SPLASH_SCREEN = 2000L
    }

    @Inject
    lateinit var retrofitApi: RetrofitApi

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        val bottomAnimation = AnimationUtils.loadAnimation(context, R.anim.bottom_animation)
        val lestAnimation = AnimationUtils.loadAnimation(context, R.anim.left_animation)

        binding.ivSplashButtom.animation = bottomAnimation
        binding.ivLogoUfps.animation = lestAnimation

        callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            println("Se desactiva el boton atras")
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(context)
        verifyUser(account)
    }

    private fun verifyUser(account: GoogleSignInAccount?) {
        if (account != null) {
            val call = retrofitApi.verifyUser(account.idToken)
            call.enqueue(object : Callback<User?> {
                override fun onResponse(call: Call<User?>, response: Response<User?>) {
                    try {
                        if (response.isSuccessful) {
                            startSplash(response.body())
                        } else {
                            revokeAccess()
                        }
                    } catch (ex: Exception) {
                        startSplash(null)
                    }
                }

                override fun onFailure(call: Call<User?>, t: Throwable) {
                   startSplash(null)
                }
            })
        } else {
            startSplash(null)
        }
    }

    private fun revokeAccess() {
        googleSignInClient.revokeAccess()
            .addOnCompleteListener(requireActivity()) {
                startSplash(null)
            }
    }

    fun startSplash(user: User?) {
        Handler(Looper.getMainLooper()).postDelayed({
            if (user != null) {
                findNavController().navigate(
                    SplashFragmentDirections.actionSplashFragmentToHomeFragment(
                        user
                    )
                )
            } else {
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
            }
        }, SPLASH_SCREEN)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        callback.remove()
    }
}