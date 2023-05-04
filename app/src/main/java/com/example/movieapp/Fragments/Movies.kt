package com.example.movieapp.Fragments

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentMoviesBinding
import kotlinx.coroutines.launch

class Movies : Fragment() {

    private var _binding: FragmentMoviesBinding?= null
    private val binding get() = checkNotNull(_binding){ "Cannot access binding because it is null. Is the view visible?" }
    private val viewModel: InventoryViewModel by activityViewModels { InventoryViewModelFactory((activity?.application as InventoryApplication).database.itemDao()) }
    lateinit var adapter: MoviesAdapter
    lateinit var adapterCartelera: MoviesAdapter
    private var paginasDisponibles: ArrayList<String> = ArrayList()
    private var pagina = 1
    lateinit var peliculasLista: ArrayList<MovieItem>
    lateinit var peliculasCartelera: ArrayList<MovieItem>
    private val moviesApi = Repository.getInstance().create(MovieAPI::class.java)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMoviesBinding.inflate(inflater,container,false)

        binding.topRecycler.setHasFixedSize(true)
        binding.topRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.carteleraRecycler.setHasFixedSize(true)
        binding.carteleraRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.CantidadTopRated.text = pagina.toString()

        if (isConnected(requireContext())) {
            showNewAdapter(pagina)
            viewLifecycleOwner.lifecycleScope.launch {
                val resultadoCartelera1 = moviesApi.fetchMovie("now_playing?api_key=c88e52755192d1c1990a4199bf3f4ffa&language=en-US&page=1")
                val resultadoCartelera2 = moviesApi.fetchMovie("now_playing?api_key=c88e52755192d1c1990a4199bf3f4ffa&language=en-US&page=2")
                peliculasCartelera = resultadoCartelera1.results
                peliculasCartelera.addAll(resultadoCartelera2.results)

                adapterCartelera = MoviesAdapter(peliculasCartelera)
                binding.carteleraRecycler.adapter = adapterCartelera
                peliculasCartelera.forEach {
                    viewModel.addNewItemCartelera(
                        it.poster_path,
                        it.overview,
                        it.title,
                        it.backdrop_path,
                        it.vote_average.toFloat()
                    )
                }
            }
        }else{
            viewModel.allItems.observe(this.viewLifecycleOwner){ items->
                if (!items.isNullOrEmpty()){
                    paginasDisponibles= ArrayList()
                    val listaNueva: ArrayList<MovieItem> = ArrayList()
                    var i = 1
                    for (item in items){
                        i+=1
                        if (i==20){
                            i=1
                            paginasDisponibles.add(item.page.toString())
                        }
                        if (item.page==1){
                            listaNueva.add(MovieItem(item.poster_path,item.overview,item.title,item.backdrop_path,item.vote_average))
                        }
                    }
                    peliculasLista=listaNueva
                    adapter = MoviesAdapter(listaNueva)
                    binding.topRecycler.adapter = adapter
                }
            }
            viewModel.allCartelera.observe(this.viewLifecycleOwner){ items ->
                if (!items.isNullOrEmpty()){
                    val listaNueva: ArrayList<MovieItem> = ArrayList()
                    for (item in items){
                        listaNueva.add(MovieItem(item.poster_path,item.overview,item.title,item.backdrop_path,item.vote_average))
                    }
                    peliculasCartelera=listaNueva
                    adapterCartelera = MoviesAdapter(listaNueva)
                    binding.carteleraRecycler.adapter = adapterCartelera
                }
            }
        }

        binding.CantidadTopRated.setOnClickListener {
            if (isConnected(requireContext())) {
                colocarCantidadProducto()
            }else{
                seleccionarMultiListaDialog()
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

    private fun showNewAdapter(page:Int){
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val resultadoTop = moviesApi.fetchMovie("top_rated?api_key=c88e52755192d1c1990a4199bf3f4ffa&language=en-US&page=${page}")
                peliculasLista = resultadoTop.results
                pagina = resultadoTop.page

                adapter = MoviesAdapter(resultadoTop.results)
                binding.topRecycler.adapter = adapter
                adapter.notifyDataSetChanged()
                peliculasLista.forEach {
                    viewModel.addNewItem(pagina, it.poster_path,it.overview,it.title,it.backdrop_path,it.vote_average.toFloat())
                }

            } catch (e: Exception) {
                Log.d("errorBaseDatos",e.message.toString())
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun filter(text: String){
        val filteredList: ArrayList<MovieItem> = ArrayList()
        val filteredCartList: ArrayList<MovieItem> = ArrayList()
        for (item in peliculasLista){
            if (item.title.lowercase().contains(text.lowercase())){
                filteredList.add(item)
            }
        }
        for (item in peliculasCartelera){
            if (item.title.lowercase().contains(text.lowercase())){
                filteredCartList.add(item)
            }
        }
        if (filteredList.isNotEmpty()){
            adapter.filterMovie(filteredList)
            adapterCartelera.filterMovie(filteredCartList)
        }
    }

    private fun colocarCantidadProducto(){
        val alertDialog = AlertDialog.Builder(context,R.style.CustomDialogTheme)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_cant, null)
        val cantidadText = view.findViewById<EditText>(R.id.cantidad_producto)
        alertDialog.apply {
            setView(view)
            setCancelable(false)
            setTitle("Enter the page")
            setPositiveButton("Accept") { dialog, id ->
                if (cantidadText.text.isNullOrBlank() || cantidadText.text.toString().toInt() == 0) {
                    Toast.makeText(requireContext(),"Enter a number",Toast.LENGTH_LONG).show()
                } else {
                    binding.CantidadTopRated.text = cantidadText.text.toString()
                    showNewAdapter(cantidadText.text.toString().toInt())
                }
            }
            setNegativeButton("Cancelar") { dialog, id -> dialog.cancel() }
        }.create().show()
    }

    private fun seleccionarMultiListaDialog(){
        var itemSeleccionado = -1
        val alertDialog = AlertDialog.Builder(requireContext(),R.style.CustomDialogTheme)
        alertDialog.apply {
            setCancelable(false)
            setTitle("Select a page")
            setSingleChoiceItems(paginasDisponibles.toTypedArray(),-1) { dialog, which ->
                itemSeleccionado = which }
            setPositiveButton("Accept") { dialog, id ->
                binding.CantidadTopRated.text = paginasDisponibles[itemSeleccionado]
                viewModel.allItems.observe(viewLifecycleOwner){ items->
                    if (!items.isNullOrEmpty()){
                        val listaNueva: ArrayList<MovieItem> = ArrayList()
                        for (item in items){
                            if (item.page==paginasDisponibles[itemSeleccionado].toInt()){
                                listaNueva.add(MovieItem(item.poster_path,item.overview,item.title,item.backdrop_path,item.vote_average))
                            }
                        }
                        peliculasLista=listaNueva
                        adapter = MoviesAdapter(listaNueva)
                        binding.topRecycler.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                }
            }
            setNegativeButton("Cancel") { dialog, id -> dialog.cancel() }

        }.create().show()
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