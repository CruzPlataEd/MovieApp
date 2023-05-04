package com.example.movieapp.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.API.MovieAPI
import com.example.movieapp.API.Repository
import com.example.movieapp.Adapters.MoviesAdapter
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentMoviesBinding
import kotlinx.coroutines.launch
import retrofit2.create

class Movies : Fragment() {

    private var _binding: FragmentMoviesBinding?= null
    private val binding get() = checkNotNull(_binding){ "Cannot access binding because it is null. Is the view visible?" }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMoviesBinding.inflate(inflater,container,false)

        val moviesApi = Repository.getInstance().create(MovieAPI::class.java)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val adapter = MoviesAdapter()
                val adapterCartelera = MoviesAdapter()
                val resultadoTop = moviesApi.fetchMovie("top_rated?api_key=c88e52755192d1c1990a4199bf3f4ffa&language=en-US&page=1")
                val resultadoCartelera = moviesApi.fetchMovie("now_playing?api_key=c88e52755192d1c1990a4199bf3f4ffa&language=en-US&page=1")
                binding.topRecycler.setHasFixedSize(true)
                binding.topRecycler.layoutManager = LinearLayoutManager(requireContext())
                adapter.MoviesAdapter(resultadoTop.results)
                binding.topRecycler.adapter = adapter

                binding.carteleraRecycler.setHasFixedSize(true)
                binding.carteleraRecycler.layoutManager = LinearLayoutManager(requireContext())
                adapterCartelera.MoviesAdapter(resultadoCartelera.results)
                binding.carteleraRecycler.adapter = adapterCartelera
            }catch(e:Exception){ }
        }
        return binding.root
    }


}