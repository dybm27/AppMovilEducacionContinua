package com.example.educacioncontinua.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.educacioncontinua.MainActivity
import com.example.educacioncontinua.R
import com.example.educacioncontinua.adapter.CourseAdapter
import com.example.educacioncontinua.databinding.FragmentHomeBinding
import com.example.educacioncontinua.interfaces.CallbackJourneys
import com.example.educacioncontinua.interfaces.RetrofitApi
import com.example.educacioncontinua.models.Course
import com.example.educacioncontinua.models.User
import com.example.educacioncontinua.models.WorkingDay
import com.example.educacioncontinua.toast
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), CallbackJourneys {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val courses = mutableListOf<Course>()
    private lateinit var adapter: CourseAdapter
    private lateinit var user: User
    private lateinit var mainActivity: MainActivity
    private lateinit var dialog: Dialog

    @Inject
    lateinit var retrofitApi: RetrofitApi

    private val args: HomeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        user = args.user
        initView()
        initRecycler()
        initSwipe()
        verifyCourses()
        setUpDialogCharging()
        //mainActivity.checkCameraPermission()
        getCourses()
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
        binding.floatActionButton.setOnClickListener { mainActivity.signOut() }
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
            refreshCourses();
        })
        // Configure the refreshing colors
        binding.swipeContainer.setColorSchemeResources(R.color.colorPrimary)
        binding.swipeContainer.setProgressBackgroundColorSchemeResource(R.color.colorAccent)
    }

    private fun refreshCourses() {
        val call = retrofitApi.getCourses(user.id)
        call.enqueue(object : Callback<List<Course>> {
            override fun onResponse(call: Call<List<Course>>, response: Response<List<Course>>) {
                binding.swipeContainer.isRefreshing = false
                try {
                    if (response.isSuccessful) {
                        if (response.body()?.isNotEmpty() == true) {
                            binding.recyclerViewCurso.visibility = View.VISIBLE
                            binding.textViewSinCursos.visibility = View.GONE
                            adapter.setData(response.body()!!)
                        } else {
                            binding.recyclerViewCurso.visibility = View.GONE
                            binding.textViewSinCursos.visibility = View.VISIBLE
                        }
                    } else {
                        hideLinearLayout();
                        toast("Error server")
                    }
                } catch (ex: Exception) {
                    hideLinearLayout()
                    toast("Error tipografico")
                }
            }

            override fun onFailure(call: Call<List<Course>>, t: Throwable) {
                toast("Grave error")
                hideLinearLayout()
                binding.swipeContainer.isRefreshing = false
            }
        })

    }

    private fun hideLinearLayout() {
        binding.recyclerViewCurso.visibility = View.GONE
        binding.textViewSinCursos.visibility = View.VISIBLE
    }

    override fun getJourneys(id: Int) {
        val call = retrofitApi.getJourneys(id);
        call.enqueue(object : Callback<List<WorkingDay>> {
            override fun onResponse(
                call: Call<List<WorkingDay>>,
                response: Response<List<WorkingDay>>
            ) {
                try {
                    openJourneys(response.body()!!, id);
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

            override fun onFailure(call: Call<List<WorkingDay>>, t: Throwable) {
                toast("Grave error")
            }

        })
    }

    private fun verifyCourses() {
        if (courses.isNotEmpty()) {
            binding.recyclerViewCurso.visibility = View.VISIBLE
            binding.textViewSinCursos.visibility = View.GONE
        } else {
            binding.recyclerViewCurso.visibility = View.GONE
            binding.textViewSinCursos.visibility = View.VISIBLE
        }
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

    private fun openJourneys(journeys: List<WorkingDay>, idContinue: Int) {
        val course = getCourse(idContinue)
        if (journeys.isNotEmpty()) {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToQrFragment(
                    course,
                    journeys.toTypedArray()
                )
            )
        } else {
            toast("No hay jornadas disponibles.")
        }
    }

    private fun getCourse(id: Int): Course =
        courses.find { course -> course.id == id }!!

    private fun getCourses() {
        dialog.show()
        val call = retrofitApi.getCourses(user.id);
        call.enqueue(object : Callback<List<Course>> {
            override fun onResponse(call: Call<List<Course>>, response: Response<List<Course>>) {
                dialog.dismiss();
                try {
                    if (response.isSuccessful) {
                        courses.clear()
                        courses.addAll(response.body()!!)
                    } else {
                        msgError()
                    }
                } catch (ex: Exception) {
                    msgError()
                }
            }

            override fun onFailure(call: Call<List<Course>>, t: Throwable) {
                dialog.dismiss()
                msgError()
            }

        });
    }

    private fun msgError() {
        toast("No fue posible obtener la informacion.. Ingrese nuevamente")
        mainActivity.signOut()
    }


    private fun setUpDialogCharging(): Dialog {
        dialog = Dialog(mainActivity)
        dialog.setContentView(R.layout.progress_bar)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}