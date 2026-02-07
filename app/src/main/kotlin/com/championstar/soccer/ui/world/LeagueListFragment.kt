package com.championstar.soccer.ui.world

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.championstar.soccer.databinding.FragmentLeagueListBinding

class LeagueListFragment : Fragment() {

    private var _binding: FragmentLeagueListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LeagueListViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLeagueListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.leagues.observe(viewLifecycleOwner) { leagues ->
            binding.recyclerViewLeagues.adapter = LeagueAdapter(leagues) { league ->
                // Navigasi ke daftar klub saat liga diklik
                val action = LeagueListFragmentDirections.actionLeagueListFragmentToClubListFragment(
                    leagueId = league.id,
                    leagueName = league.name
                )
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}