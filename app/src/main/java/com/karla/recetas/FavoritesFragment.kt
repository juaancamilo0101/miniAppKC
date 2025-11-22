package com.karla.recetas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karla.recetas.data.RecipeEntity // 1. Fix: Import necesario
import com.karla.recetas.databinding.FragmentFavoritesBinding
import com.karla.recetas.vm.RecipeViewModel

/**
 * Fragment to display the list of favorite recipes.
 */
class FavoritesFragment : Fragment() {
  private var _binding: FragmentFavoritesBinding? = null
  private val binding get() = _binding!!

  // Use 'val' for immutable references
  private val vm: RecipeViewModel by activityViewModels()
  private val adapter = FavoritesAdapter()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.list.layoutManager = LinearLayoutManager(requireContext())
    binding.list.adapter = adapter

    vm.favorites.observe(viewLifecycleOwner) { favorites ->
      adapter.submit(favorites)
    }
    vm.loadFavorites()
  }

  override fun onDestroyView() {
    _binding = null
    super.onDestroyView()
  }
}

/**
 * Adapter for the Favorites RecyclerView.
 */
class FavoritesAdapter : RecyclerView.Adapter<FavoritesVH>() {

  // 2. Fix: Removed "data." prefix, using imported class
  private val items = mutableListOf<RecipeEntity>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesVH {
    val v = LayoutInflater.from(parent.context)
      .inflate(android.R.layout.simple_list_item_1, parent, false)
    return FavoritesVH(v)
  }

  override fun onBindViewHolder(holder: FavoritesVH, position: Int) {
    holder.bind(items[position])
  }

  override fun getItemCount() = items.size

  fun submit(list: List<RecipeEntity>) {
    items.clear()
    items.addAll(list)
    notifyDataSetChanged()
  }
}

/**
 * ViewHolder for a favorite item.
 */
class FavoritesVH(view: View) : RecyclerView.ViewHolder(view) {
  fun bind(item: RecipeEntity) {
    // Cast to TextView safely or use findViewById if needed (simple_list_item_1 is a TextView)
    (itemView as? TextView)?.text = item.title
  }
}