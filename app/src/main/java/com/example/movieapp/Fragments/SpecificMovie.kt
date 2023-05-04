package com.example.movieapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentMoviesBinding
import com.example.movieapp.databinding.FragmentSpecificMovieBinding
import com.squareup.picasso.Picasso

class SpecificMovie : Fragment() {

    private var _binding: FragmentSpecificMovieBinding?= null
    private val binding get() = checkNotNull(_binding){ "Cannot access binding because it is null. Is the view visible?" }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSpecificMovieBinding.inflate(inflater,container,false)

        val safeArgs: SpecificMovieArgs by navArgs()
        val overview = safeArgs.overview
        val title = safeArgs.title
        val backdrop = safeArgs.backdrop
        val calificacion = safeArgs.calificacion

        Picasso.get().load("https://image.tmdb.org/t/p/w500/${backdrop}").into(binding.imagenBack)
        binding.titulo.text = title
        binding.overview.text = overview
        binding.ratingBar.rating = (calificacion/2)
        binding.calificacion.text = "Rating: ${calificacion}"
        return binding.root
    }

}