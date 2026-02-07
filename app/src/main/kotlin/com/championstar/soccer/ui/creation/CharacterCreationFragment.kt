package com.championstar.soccer.ui.creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.championstar.soccer.R
import com.championstar.soccer.core.SoundManager
import com.championstar.soccer.data.model.PlayerProfile
import com.championstar.soccer.databinding.FragmentCharacterCreationBinding

class CharacterCreationFragment : Fragment() {

    private var _binding: FragmentCharacterCreationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CharacterCreationViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCharacterCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // Setup Spinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.countries_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCountry.adapter = adapter
        }

        // Listeners
        binding.editPlayerName.addTextChangedListener {
            viewModel.playerName.value = it.toString()
        }

        binding.spinnerCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.selectedCountry.value = parent?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.btnPositionAttacker.setOnClickListener { selectPosition("Attacker") }
        binding.btnPositionMidfielder.setOnClickListener { selectPosition("Midfielder") }
        binding.btnPositionDefender.setOnClickListener { selectPosition("Defender") }

        binding.btnNextAvatar.setOnClickListener { viewModel.nextAvatar() }
        binding.btnPrevAvatar.setOnClickListener { viewModel.previousAvatar() }

        binding.btnPlusFinishing.setOnClickListener { viewModel.allocatePoint("Finishing") }
        binding.btnPlusSpeed.setOnClickListener { viewModel.allocatePoint("Speed") }
        binding.btnPlusDribbling.setOnClickListener { viewModel.allocatePoint("Dribbling") }

        binding.btnStartCareer.setOnClickListener {
            SoundManager.playUiClick()
            if (viewModel.isDataValid()) {
                val profile = PlayerProfile(
                    name = viewModel.playerName.value!!,
                    country = viewModel.selectedCountry.value!!,
                    position = viewModel.selectedPosition.value!!,
                    avatarResourceName = "char_${(viewModel.currentAvatarIndex.value ?: 0) + 1}"
                )
                
                val attributes = viewModel.initialAttributes.value!!

                // sesuai hasil generate Safe Args dari snake_case di XML.
                val action = CharacterCreationFragmentDirections.actionCharacterCreationFragmentToDashboardFragment(
                    playerProfile = profile,
                    playerAttributes = attributes
                    // LOADEDPLAYER tidak perlu diisi karena akan menggunakan default value null
                )
                findNavController().navigate(action)

            } else {
                Toast.makeText(requireContext(), "Please fill all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.currentAvatarIndex.observe(viewLifecycleOwner) { index ->
            val avatarResourceName = "char_${index + 1}"
            val avatarResourceId = resources.getIdentifier(avatarResourceName, "drawable", requireActivity().packageName)
            binding.imgAvatar.setImageResource(avatarResourceId)
        }

        viewModel.unallocatedPoints.observe(viewLifecycleOwner) { points ->
            binding.tvPointsRemaining.text = "Points Remaining: $points"
            val areButtonsEnabled = points > 0
            binding.btnPlusFinishing.isEnabled = areButtonsEnabled
            binding.btnPlusSpeed.isEnabled = areButtonsEnabled
            binding.btnPlusDribbling.isEnabled = areButtonsEnabled
        }

        viewModel.initialAttributes.observe(viewLifecycleOwner) { attrs ->
            binding.tvStatFinishing.text = "Finishing: ${attrs.technical.finishing}"
            binding.tvStatSpeed.text = "Speed: ${attrs.physical.sprintSpeed}"
            binding.tvStatDribbling.text = "Dribbling: ${attrs.technical.dribbling}"
        }
    }
    
    private fun selectPosition(position: String) {
        viewModel.selectedPosition.value = position
        binding.btnPositionAttacker.alpha = if (position == "Attacker") 1.0f else 0.5f
        binding.btnPositionMidfielder.alpha = if (position == "Midfielder") 1.0f else 0.5f
        binding.btnPositionDefender.alpha = if (position == "Defender") 1.0f else 0.5f
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}