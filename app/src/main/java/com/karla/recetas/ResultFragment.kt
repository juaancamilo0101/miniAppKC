package com.karla.recetas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.karla.recetas.vm.RecipeViewModel
import androidx.fragment.app.commit
import com.karla.recetas.databinding.FragmentResultBinding

class ResultFragment : Fragment() {
  private var _binding: FragmentResultBinding? = null
  private val binding get() = _binding!!
  private val vm: RecipeViewModel by activityViewModels()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    _binding = FragmentResultBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    vm.loading.observe(viewLifecycleOwner) { binding.loading.visibility = if (it) View.VISIBLE else View.GONE }
    vm.error.observe(viewLifecycleOwner) {
      binding.error.text = it ?: ""
      binding.error.visibility = if (it == null) View.GONE else View.VISIBLE
    }
    vm.recipe.observe(viewLifecycleOwner) {
      binding.title.text = it?.title ?: ""
      binding.ingredients.text = it?.ingredients ?: ""
      binding.steps.text = it?.steps ?: ""
    }
    binding.save.setOnClickListener { vm.saveFavorite() }
    binding.favs.setOnClickListener { parentFragmentManager.commit { replace(R.id.container, FavoritesFragment()) } }
  }

  override fun onDestroyView() {
    _binding = null
    super.onDestroyView()
  }
}