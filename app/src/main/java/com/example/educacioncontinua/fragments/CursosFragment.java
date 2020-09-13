package com.example.educacioncontinua.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.educacioncontinua.HomeActivity;
import com.example.educacioncontinua.R;
import com.example.educacioncontinua.adapter.CursoAdapter;
import com.example.educacioncontinua.config.ToastrConfig;
import com.example.educacioncontinua.dagger.BaseApplication;
import com.example.educacioncontinua.interfaces.RetrofitApi;
import com.example.educacioncontinua.models.Curso;
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
    private List<Curso> cursos;
    private RecyclerView recyclerView;
    private CursoAdapter cursoAdapter;
    private LinearLayout linearLayoutCursos;
    private SwipeRefreshLayout swipeContainer;

    @Inject
    RetrofitApi retrofitApi;

    public CursosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cursos, container, false);
        Bundle datosRecuperados = getArguments();
        assert datosRecuperados != null;
        cursos = datosRecuperados.getParcelableArrayList("cursos");
        setUpDagger();
        setUpSwipe(view);
        setUpView(view);
        setUpRecycler(view);
        verificarCursos();
        return view;
    }

    private void verificarCursos() {
        if (false) {
            linearLayoutCursos.setVisibility(View.VISIBLE);
            textViewSinCursos.setVisibility(View.GONE);
        } else {
            linearLayoutCursos.setVisibility(View.GONE);
            textViewSinCursos.setVisibility(View.VISIBLE);
        }
    }

    private void setUpDagger() {
        ((BaseApplication) Objects.requireNonNull(getActivity()).getApplication()).getRetrofitComponent().inject(this);
    }

    private void setUpView(View view) {
        textViewSinCursos = view.findViewById(R.id.textViewSinCursos);
        linearLayoutCursos = view.findViewById(R.id.linearLayoutCursos);
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
        cursoAdapter = new CursoAdapter(getContext(), cursos);
        recyclerView = view.findViewById(R.id.recyclerViewCurso);
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
                            linearLayoutCursos.setVisibility(View.VISIBLE);
                            textViewSinCursos.setVisibility(View.GONE);
                            cursoAdapter.setData(response.body());
                        } else {
                            linearLayoutCursos.setVisibility(View.GONE);
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
                ToastrConfig.mensaje(getContext(), "Grave Error");
                ocultarLinearLayout();
                swipeContainer.setRefreshing(false);
            }
        });
    }

    private void ocultarLinearLayout() {
        linearLayoutCursos.setVisibility(View.GONE);
        textViewSinCursos.setVisibility(View.VISIBLE);
    }
}