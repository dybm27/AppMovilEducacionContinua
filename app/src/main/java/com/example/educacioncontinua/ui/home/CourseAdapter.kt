package com.example.educacioncontinua.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.educacioncontinua.R
import com.example.educacioncontinua.databinding.CardCourseItemBinding
import com.example.educacioncontinua.data.model.Course

class CourseAdapter(
    private val context: Context,
    private var courses: List<Course>,
    val callbackWorkingDay: CallbackWorkingDay
) :
    RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CourseViewHolder(layoutInflater.inflate(R.layout.card_course_item, parent, false))
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(courses[position])
    }

    override fun getItemCount(): Int = courses.size

    fun setData(courses: List<Course>) {
        this.courses = courses
        notifyDataSetChanged()
    }

    inner class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = CardCourseItemBinding.bind(view)
        fun bind(course: Course) {
            with(binding) {
                textViewNombre.text = course.name
                textView1.text = course.typeContinuingEducation
                textView2.text = course.responsibleProgram
                textView3.text = course.responsibleProfessor

                imgView.setOnClickListener {
                    val animation = AnimationUtils.loadAnimation(context, R.anim.alpha)
                    it.startAnimation(animation)
                    callbackWorkingDay.getWorkingDay(course.id)
                }
            }
        }
    }
}