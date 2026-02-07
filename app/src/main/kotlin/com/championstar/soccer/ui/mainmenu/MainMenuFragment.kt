package com.championstar.soccer.ui.mainmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.championstar.soccer.R
import com.championstar.soccer.core.SoundManager
import com.championstar.soccer.databinding.FragmentMainMenuBinding

class MainMenuFragment : Fragment() {

    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainMenuViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeViewModel()

        viewModel.checkForSaveData()
    }

    private fun setupClickListeners() {
        binding.btnNewGame.setOnClickListener {
            SoundManager.playUiClick()
            findNavController().navigate(R.id.action_mainMenuFragment_to_characterCreationFragment)
        }

        binding.btnLoadGame.setOnClickListener {
            SoundManager.playUiClick()
            viewModel.onLoadGameClicked()
        }

        binding.btnExit.setOnClickListener {
            SoundManager.playUiClick()
            requireActivity().finish()
        }
    }

    private fun observeViewModel() {
        viewModel.hasSaveData.observe(viewLifecycleOwner) { hasData ->
            binding.btnLoadGame.isEnabled = hasData
            binding.btnLoadGame.alpha = if (hasData) 1.0f else 0.5f
        }

        viewModel.navigateToDashboard.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { loadedPlayer ->
                val action = MainMenuFragmentDirections.actionMainMenuFragmentToDashboardFragment(
                    playerProfile = loadedPlayer.profile,
                    playerAttributes = loadedPlayer.attributes,
                    loadedPlayer = loadedPlayer
                )
                findNavController().navigate(action)
            }
        }
        
        viewModel.showNoSaveDataToast.observe(viewLifecycleOwner) { event ->
            // KODE YANG DIPERBAIKI DAN LENGKAP
            event.getContentIfNotHandled()?.let {
                Toast.makeText(requireContext(), "No saved game found.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}