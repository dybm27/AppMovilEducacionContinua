package com.example.educacioncontinua.adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educacioncontinua.HomeActivity;
import com.example.educacioncontinua.R;
import com.example.educacioncontinua.config.ToastrConfig;
import com.example.educacioncontinua.fragments.JornadasQrFragment;
import com.example.educacioncontinua.interfaces.CallbackJornadas;
import com.example.educacioncontinua.models.Curso;
import com.example.educacioncontinua.models.Jornada;

import java.util.ArrayList;
import java.util.List;

public class CursoAdapter extends RecyclerView.Adapter<CursoAdapter.CursoAdapterHolder> {

    private List<Curso> cursos;
    private List<Jornada> jornadas;
    private Context context;
    private CallbackJornadas callbackJornadas;

    public CursoAdapter(Context context, List<Curso> cursos, CallbackJornadas callbackJornadas) {
        this.cursos = cursos;
        this.context = context;
        this.callbackJornadas = callbackJornadas;
    }

    @NonNull
    @Override
    public CursoAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemVieW = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_curso_layout, parent, false);
        return new CursoAdapterHolder(itemVieW);
    }

    @Override
    public void onBindViewHolder(@NonNull CursoAdapterHolder holder, int position) {
        Curso curso = cursos.get(position);
        holder.textViewNombre.setText(curso.getNombre());
        holder.textView1.setText(curso.getTipoEduContinua());
        holder.textView2.setText(curso.getProgramaResponsable());
        holder.textView3.setText(curso.getDocenteResponsable());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha);
                holder.imageView.startAnimation(animation);
                callbackJornadas.buscarJornadas(curso.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return cursos.size();
    }

    public void setData(List<Curso> cursos) {
        this.cursos = cursos;
        notifyDataSetChanged();
    }

    public class CursoAdapterHolder extends RecyclerView.ViewHolder {
        private TextView textViewNombre;
        private TextView textView1;
        private TextView textView2;
        private TextView textView3;
        private ImageView imageView;

        public CursoAdapterHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textView1 = itemView.findViewById(R.id.textView1);
            textView2 = itemView.findViewById(R.id.textView2);
            textView3 = itemView.findViewById(R.id.textView3);
            imageView = itemView.findViewById(R.id.imgView);
        }
    }
}
