package com.example.educacioncontinua.ui.splash

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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.educacioncontinua.R
import com.example.educacioncontinua.databinding.FragmentSplashBinding
import com.example.educacioncontinua.data.RetrofitApi
import com.example.educacioncontinua.data.model.User
import com.example.educacioncontinua.viewmodel.DataViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private lateinit var callback: OnBackPressedCallback
    private val model: DataViewModel by activityViewModels()

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
        initObserver()
        return binding.root
    }

    private fun initObserver() {
        model.getUser().observe(viewLifecycleOwner, Observer { it ->
            it.getContentIfNotHandled()?.let { user ->
                startSplash(user)
            }
        })
        model.getMessage().observe(viewLifecycleOwner, Observer {
            googleSignInClient.revokeAccess()
                .addOnCompleteListener(requireActivity()) {
                }
            startSplash(null)
        })
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(context)
        verifyUser(account)
    }

    private fun verifyUser(account: GoogleSignInAccount?) {
        if (account != null) {
            model.verifyUser(account.idToken)
        } else {
            startSplash(null)
        }
    }

    fun startSplash(user: User?) {
        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launchWhenResumed {
                if (user != null) {
                    findNavController().navigate(
                        SplashFragmentDirections.actionSplashFragmentToHomeFragment(
                            user
                        )
                    )
                } else {
                    findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
                }
            }
        }, SPLASH_SCREEN)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        callback.remove()
    }
}