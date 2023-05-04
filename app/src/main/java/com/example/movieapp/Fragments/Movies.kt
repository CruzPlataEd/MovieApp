package com.example.movieapp.Fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.API.MovieAPI
import com.example.movieapp.API.MovieItem
import com.example.movieapp.API.Repository
import com.example.movieapp.Adapters.MoviesAdapter
import com.example.movieapp.DB.InventoryViewModel
import com.example.movieapp.DB.InventoryViewModelFactory
import com.example.movieapp.InventoryApplication
import com.example.movieapp.databinding.FragmentMoviesBinding
import kotlinx.coroutines.launch

class Movies : Fragment() {

    private var _binding: FragmentMoviesBinding?= null
    private val binding get() = checkNotNull(_binding){ "Cannot access binding because it is null. Is the view visible?" }
    private val viewModel: InventoryViewModel by activityViewModels { InventoryViewModelFactory((activity?.application as InventoryApplication).database.itemDao()) }
    lateinit var adapter: MoviesAdapter
    lateinit var adapterCartelera: MoviesAdapter
    var pagina = 0
    var paginaCartelera = 0
    lateinit var peliculasLista: ArrayList<MovieItem>
    lateinit var peliculasCartelera: ArrayList<MovieItem>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMoviesBinding.inflate(inflater,container,false)
        val moviesApi = Repository.getInstance().create(MovieAPI::class.java)

        if (isConnected(requireContext())) {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val resultadoTop =
                        moviesApi.fetchMovie("top_rated?api_key=c88e52755192d1c1990a4199bf3f4ffa&language=en-US&page=1")
                    val resultadoCartelera =
                        moviesApi.fetchMovie("now_playing?api_key=c88e52755192d1c1990a4199bf3f4ffa&language=en-US&page=1")

                    peliculasLista = resultadoTop.results
                    pagina = resultadoTop.page

                    paginaCartelera = resultadoCartelera.page
                    peliculasCartelera = resultadoCartelera.results

                    adapter = MoviesAdapter(resultadoTop.results)
                    adapterCartelera = MoviesAdapter(peliculasCartelera)

                    binding.topRecycler.setHasFixedSize(true)
                    binding.topRecycler.layoutManager = LinearLayoutManager(requireContext())
                    binding.topRecycler.adapter = adapter
                    adapter.notifyDataSetChanged()

                    binding.carteleraRecycler.setHasFixedSize(true)
                    binding.carteleraRecycler.layoutManager = LinearLayoutManager(requireContext())
                    binding.carteleraRecycler.adapter = adapterCartelera
                    adapterCartelera.notifyDataSetChanged()

                    peliculasLista.forEach {
                        viewModel.addNewItem(pagina, it.poster_path,it.overview,it.title,it.backdrop_path,it.vote_average.toFloat())
                    }
                    peliculasCartelera.forEach {
                        viewModel.addNewItemCartelera(paginaCartelera, it.poster_path,it.overview,it.title,it.backdrop_path,it.vote_average.toFloat())
                    }

                } catch (e: Exception) {
                    Log.d("errorBaseDatos",e.message.toString())
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                }
            }


        }else{
            viewModel.allItems.observe(this.viewLifecycleOwner){ items->
                if (!items.isNullOrEmpty()){
                    val listaNueva: ArrayList<MovieItem> = ArrayList()
                    for (item in items){
                        if (item.page==1){
                            listaNueva.add(MovieItem(item.poster_path,item.overview,item.title,item.backdrop_path,item.vote_average))
                        }
                    }
                    peliculasLista=listaNueva
                    adapter = MoviesAdapter(listaNueva)
                    binding.topRecycler.setHasFixedSize(true)
                    binding.topRecycler.layoutManager = LinearLayoutManager(requireContext())
                    binding.topRecycler.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }
            viewModel.allCartelera.observe(this.viewLifecycleOwner){ items ->
                if (!items.isNullOrEmpty()){
                    val listaNueva: ArrayList<MovieItem> = ArrayList()
                    for (item in items){
                        if (item.page==1){
                            listaNueva.add(MovieItem(item.poster_path,item.overview,item.title,item.backdrop_path,item.vote_average))
                        }
                    }
                    peliculasCartelera=listaNueva
                    adapterCartelera = MoviesAdapter(listaNueva)
                    binding.carteleraRecycler.setHasFixedSize(true)
                    binding.carteleraRecycler.layoutManager = LinearLayoutManager(requireContext())
                    binding.carteleraRecycler.adapter = adapterCartelera
                    adapterCartelera.notifyDataSetChanged()
                }
            }
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText!!)
                return false
            }
        })
        return binding.root
    }

    private fun filter(text: String){
        val filteredList: ArrayList<MovieItem> = ArrayList()
        val filteredCartList: ArrayList<MovieItem> = ArrayList()
        for (item in peliculasLista){
            if (item.title.toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item)
            }
        }
        for (item in peliculasCartelera){
            if (item.title.toLowerCase().contains(text.toLowerCase())){
                filteredCartList.add(item)
            }
        }
        if (filteredList.isNotEmpty()){
            adapter.filterMovie(filteredList)
            adapterCartelera.filterMovie(filteredCartList)
        }
    }

    fun isConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }


}