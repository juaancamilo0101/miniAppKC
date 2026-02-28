package com.karla.recetas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.karla.recetas.vm.RecipeViewModel
import androidx.fragment.app.commit
import com.karla.recetas.databinding.FragmentSelectionBinding

class
SelectionFragment : Fragment() {
  private var _binding: FragmentSelectionBinding? = null
  private val binding get() = _binding!!
  private val vm: RecipeViewModel by activityViewModels()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    _binding = FragmentSelectionBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    binding.btnA.setOnClickListener { vm.setChoice(1, "Arroz"); showQ2() }
    binding.btnB.setOnClickListener { vm.setChoice(1, "Pasta"); showQ2() }
    binding.btnC.setOnClickListener { vm.setChoice(1, "Quinoa"); showQ2() }
    binding.btnD.setOnClickListener { vm.setChoice(1, "Patata"); showQ2() }

  }

  private fun showQ2() {
    binding.title.text = "Selecciona la proteína"
    binding.btnA.text = "Pollo"
    binding.btnB.text = "Pescado"
    binding.btnC.text = "Ternera"
    binding.btnD.text = "Tofu (veg)"
    binding.btnA.setOnClickListener { vm.setChoice(2, "Pollo"); generate() }
    binding.btnB.setOnClickListener { vm.setChoice(2, "Pescado"); generate() }
    binding.btnC.setOnClickListener { vm.setChoice(2, "Ternera"); generate() }
    binding.btnD.setOnClickListener { vm.setChoice(2, "Tofu"); generate() }
  }

  private fun generate() {
    vm.generateRecipe()
    parentFragmentManager.commit { replace(R.id.container, ResultFragment()) }
  }

  override fun onDestroyView() {
    _binding = null
    super.onDestroyView()
  }
}