package com.example.selenaapp.ui.transaction.file

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.selenaapp.R
import com.example.selenaapp.databinding.FragmentChooseFileBinding

class ChooseFileFragment : Fragment(R.layout.fragment_choose_file) {

    private lateinit var binding: FragmentChooseFileBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentChooseFileBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        binding.btnTokopedia.setOnClickListener {
            val fragment = TokopediaFragment()
            replaceFragment(fragment)
        }

        binding.btnShopee.setOnClickListener {
            val fragment = ShopeeFragment()
            replaceFragment(fragment)
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

}
