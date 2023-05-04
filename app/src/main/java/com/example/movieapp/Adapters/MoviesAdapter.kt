package com.example.movieapp.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.API.MovieItem
import com.example.movieapp.Fragments.MoviesDirections
import com.example.movieapp.R
import com.example.movieapp.databinding.MovieElementoBinding
import com.squareup.picasso.Picasso

class MoviesAdapter(private var courseList: ArrayList<MovieItem>): RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return courseList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(courseList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(MovieElementoBinding.inflate(LayoutInflater.from(parent.context)))
    }

    inner class ViewHolder(private val binding: MovieElementoBinding): RecyclerView.ViewHolder(binding.root){
        val options = navOptions {
            anim {
                enter = R.anim.slide_in_right
                exit = R.anim.slide_out_left
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_right
            }
        }

        fun bind(pelicula: MovieItem){
            Picasso.get().load("https://image.tmdb.org/t/p/w500/${pelicula.poster_path}").into(binding.imagenPoster)
            binding.tituloMovie.text = pelicula.title
            binding.ratingBar.rating = (pelicula.vote_average.toFloat()/2)
            binding.calificacion.text = "Rating: ${pelicula.vote_average}"

            binding.relative.setOnClickListener {
                val action = MoviesDirections.actionMoviesToSpecificMovie(pelicula.overview,pelicula.backdrop_path,pelicula.title,pelicula.vote_average.toFloat())
                it.findNavController().navigate(action,options)
            }
        }
    }

    fun filterMovie(movieFilter: ArrayList<MovieItem>){
        courseList = movieFilter
        notifyDataSetChanged()
    }
}