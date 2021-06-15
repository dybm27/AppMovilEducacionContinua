package com.example.educacioncontinua.ui.home

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.educacioncontinua.MainActivity
import com.example.educacioncontinua.R
import com.example.educacioncontinua.databinding.FragmentHomeBinding
import com.example.educacioncontinua.ui.dialogs.LogoutDialog
import com.example.educacioncontinua.data.RetrofitApi
import com.example.educacioncontinua.data.model.Course
import com.example.educacioncontinua.data.model.User
import com.example.educacioncontinua.data.model.WorkingDay
import com.example.educacioncontinua.toast
import com.example.educacioncontinua.viewmodel.DataViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), CallbackWorkingDay {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val courses = mutableListOf<Course>()
    private lateinit var adapter: CourseAdapter
    private lateinit var user: User
    private lateinit var mainActivity: MainActivity
    private lateinit var dialog: Dialog
    private lateinit var callback: OnBackPressedCallback
    private val model: DataViewModel by activityViewModels()
    private var idEdu: Int = 0

    @Inject
    lateinit var retrofitApi: RetrofitApi

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    private val args: HomeFragmentArgs by navArgs()

    companion object {
        const val REQUEST_KEY = "homeFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            LogoutDialog().show(childFragmentManager, "logoutDialog")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        user = args.user
        dialog = mainActivity.setUpDialogCharging(getString(R.string.charging))
        initView()
        initRecycler()
        initSwipe()
        initObserver()
        mainActivity.checkCameraPermission()
        initFragmentResultListener()
        model.getCourses(user.id)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mainActivity = context
        }
    }

    private fun initView() {
        val name = "${user.firstName} ${user.secondName} ${user.surname} ${user.secondSurname}"
        val type = verifyTypeUser()
        binding.textViewNombre.text = name
        binding.textViewTipo.text = type
        binding.floatActionButton.setOnClickListener { signOut() }
    }

    private fun initObserver() {
        model.getCourses().observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { list ->
                courses.clear()
                if (list.isNotEmpty()) {
                    courses.addAll(list)
                    binding.recyclerViewCurso.visibility = View.VISIBLE
                    binding.textViewSinCursos.visibility = View.GONE
                } else {
                    binding.recyclerViewCurso.visibility = View.GONE
                    binding.textViewSinCursos.visibility = View.VISIBLE
                }
                adapter.setData(courses)
            }
        })
        model.getMessage().observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { msg ->
                toast(msg)
            }
        })
        model.isLoading().observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { isShow ->
                if (isShow) {
                    dialog.show()
                } else {
                    dialog.dismiss()
                    binding.swipeContainer.isRefreshing = false
                }
            }
        })
        model.getWorkingsDays().observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { list ->
                openWorkingDays(list, idEdu)
            }
        })
    }

    private fun initRecycler() {
        adapter = CourseAdapter(mainActivity, courses, this)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.recyclerViewCurso.layoutManager = linearLayoutManager
        binding.recyclerViewCurso.adapter = adapter
    }

    private fun initSwipe() {
        binding.swipeContainer.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            model.getCourses(user.id, false)
        })
        // Configure the refreshing colors
        binding.swipeContainer.setColorSchemeResources(R.color.colorPrimary)
        binding.swipeContainer.setProgressBackgroundColorSchemeResource(R.color.colorAccent)
    }

    override fun getWorkingDay(id: Int) {
        idEdu = id
        model.getWorkingDays(id)
    }

    private fun verifyTypeUser(): String {
        if (user.administrative) {
            return "Administrativo"
        }
        if (user.student) {
            return "Estudiante"
        }
        if (user.external) {
            return "Externo"
        }
        if (user.graduate) {
            return "Graduado"
        }
        return "Docente"
    }

    private fun openWorkingDays(workingDays: List<WorkingDay>, idContinue: Int) {
        val course = getCourse(idContinue)
        if (workingDays.isNotEmpty()) {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToQrFragment(
                    course,
                    workingDays.toTypedArray()
                )
            )
        } else {
            toast("No hay jornadas disponibles.")
        }
    }

    private fun getCourse(id: Int): Course =
        courses.find { course -> course.id == id }!!

    private fun initFragmentResultListener() {
        childFragmentManager.setFragmentResultListener(REQUEST_KEY, this)
        { _, _ ->
            signOut()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        callback.remove()
    }

    private fun signOut() {
        googleSignInClient.signOut()
            .addOnCompleteListener(mainActivity) {
                findNavController()
                    .navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
            }
    }
}