package com.championstar.soccer.ui.world

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.championstar.soccer.databinding.FragmentClubListBinding

class ClubListFragment : Fragment() {

    private var _binding: FragmentClubListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ClubListViewModel by viewModels()
    private val args: ClubListFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentClubListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvLeagueTitle.text = "Clubs in ${args.leagueName}"
        viewModel.loadClubs(args.leagueId)

        viewModel.clubs.observe(viewLifecycleOwner) { clubs ->
            binding.recyclerViewClubs.adapter = ClubAdapter(clubs)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}