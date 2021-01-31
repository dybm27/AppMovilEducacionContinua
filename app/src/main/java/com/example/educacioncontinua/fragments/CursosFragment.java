package com.example.educacioncontinua.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.educacioncontinua.HomeActivity;
import com.example.educacioncontinua.R;
import com.example.educacioncontinua.adapter.CursoAdapter;
import com.example.educacioncontinua.config.ToastrConfig;
import com.example.educacioncontinua.dagger.BaseApplication;
import com.example.educacioncontinua.interfaces.CallbackJornadas;
import com.example.educacioncontinua.interfaces.RetrofitApi;
import com.example.educacioncontinua.models.Curso;
import com.example.educacioncontinua.models.Jornada;
import com.example.educacioncontinua.models.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CursosFragment extends Fragment {

    private TextView textViewNombre, textViewTipo, textViewSinCursos;
    private List<Curso> cursos = new ArrayList<>();
    private RecyclerView recyclerView;
    private HomeActivity homeActivity;
    private CursoAdapter cursoAdapter;
    private SwipeRefreshLayout swipeContainer;
    private CallbackJornadas buscarJornadas = this::obtenerJornadas;

    @Inject
    RetrofitApi retrofitApi;

    public CursosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        homeActivity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cursos, container, false);
        Bundle datosRecuperados = getArguments();
        if (datosRecuperados != null)
            cursos = datosRecuperados.getParcelableArrayList("cursos");
        setUpDagger();
        setUpSwipe(view);
        setUpView(view);
        setUpRecycler(view);
        verificarCursos();
        return view;
    }

    private void verificarCursos() {
        if (cursos.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            textViewSinCursos.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            textViewSinCursos.setVisibility(View.VISIBLE);
        }
    }

    private void setUpDagger() {
        ((BaseApplication) Objects.requireNonNull(getActivity()).getApplication()).getRetrofitComponent().inject(this);
    }

    private void setUpView(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewCurso);
        textViewSinCursos = view.findViewById(R.id.textViewSinCursos);
        textViewNombre = view.findViewById(R.id.textViewNombre);
        textViewTipo = view.findViewById(R.id.textViewTipo);
        String nombre = Usuario.getUsuario().getPrimerNombre() + " " + Usuario.getUsuario().getSegundoNombre()
                + " " + Usuario.getUsuario().getPrimerApellido() + " " + Usuario.getUsuario().getSegundoApellido();
        String tipo = verificarTipo();
        textViewNombre.setText(nombre);
        textViewTipo.setText(tipo);
    }

    private String verificarTipo() {
        if (Usuario.getUsuario().getAdministrativo()) {
            return "Administrativo";
        }
        if (Usuario.getUsuario().getEstudiante()) {
            return "Estudiante";
        }
        if (Usuario.getUsuario().getExterno()) {
            return "Externo";
        }
        if (Usuario.getUsuario().getGraduado()) {
            return "Graduado";
        }
        return "Docente";
    }

    private void setUpRecycler(View view) {
        cursoAdapter = new CursoAdapter(homeActivity, cursos, buscarJornadas);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(cursoAdapter);
    }

    private void setUpSwipe(View view) {
        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refrescarCursos();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorPrimary);
        swipeContainer.setProgressBackgroundColorSchemeResource(R.color.colorAccent);
    }

    private void refrescarCursos() {
        Call<List<Curso>> call = retrofitApi.obtenerCursos(Usuario.getUsuario().getId());
        call.enqueue(new Callback<List<Curso>>() {
            @Override
            public void onResponse(Call<List<Curso>> call, Response<List<Curso>> response) {
                swipeContainer.setRefreshing(false);
                try {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (response.body().size() > 0) {
                            recyclerView.setVisibility(View.VISIBLE);
                            textViewSinCursos.setVisibility(View.GONE);
                            cursoAdapter.setData(response.body());
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            textViewSinCursos.setVisibility(View.VISIBLE);
                        }
                    } else {
                        ocultarLinearLayout();
                        ToastrConfig.mensaje(getContext(), "Error server");
                    }
                } catch (Exception ex) {
                    ocultarLinearLayout();
                    ToastrConfig.mensaje(getContext(), "Error tipografico");
                }
            }

            @Override
            public void onFailure(Call<List<Curso>> call, Throwable t) {
                ToastrConfig.mensaje(getContext(), "Grave error");
                ocultarLinearLayout();
                swipeContainer.setRefreshing(false);
            }
        });
    }

    private void ocultarLinearLayout() {
        recyclerView.setVisibility(View.GONE);
        textViewSinCursos.setVisibility(View.VISIBLE);
    }

    private void abrirJornadas(List<Jornada> jornadas, int idContinua) {
        Curso curso = obtenerCurso(idContinua);
        if (jornadas.size() != 0) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("jornadas", (ArrayList<? extends Parcelable>) jornadas);
            bundle.putInt("idEduContinua", curso.getId());
            bundle.putString("nombreEvento", curso.getNombre());
            JornadasQrFragment jornadasQrFragment = new JornadasQrFragment();
            jornadasQrFragment.setArguments(bundle);
            FragmentManager fragmentManager = homeActivity.getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.contenedorFragment, jornadasQrFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            ToastrConfig.mensaje(getContext(), "No hay jornadas disponibles.");
        }
    }

    private Curso obtenerCurso(int id) {
        Curso curso = null;
        for (Curso c : cursos) {
            if (c.getId() == id) {
                curso = c;
            }
        }
        return curso;
    }

    private void obtenerJornadas(int idEdu) {
        Call<List<Jornada>> call = retrofitApi.obtenerJornadas(idEdu);
        call.enqueue(new Callback<List<Jornada>>() {
            @Override
            public void onResponse(Call<List<Jornada>> call, Response<List<Jornada>> response) {
                try {
                    abrirJornadas(response.body(), idEdu);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<Jornada>> call, Throwable t) {
            }
        });
    }
}