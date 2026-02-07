package com.championstar.soccer.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.championstar.soccer.R
import com.championstar.soccer.core.SoundManager
import com.championstar.soccer.data.model.EventType
import com.championstar.soccer.data.model.FeatureIcon
import com.championstar.soccer.data.model.LeagueStanding
import com.championstar.soccer.data.repository.DatabaseRepository
import com.championstar.soccer.data.repository.SaveRepository
import com.championstar.soccer.databinding.ItemLeagueStandingBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModels()
    private val args: DashboardFragmentArgs by navArgs()

    // Adapter
    private lateinit var agentAdapter: AgentAdapter
    private lateinit var shopAdapter: ShopAdapter
    private lateinit var trialOfferAdapter: TrialOfferAdapter
    private lateinit var commentaryAdapter: MatchCommentaryAdapter
    private lateinit var leagueTableAdapter: LeagueTableAdapter
    private lateinit var featureIconAdapter: FeatureIconAdapter

    // Views from the compact layout
    private lateinit var fabContinue: FloatingActionButton
    private lateinit var recyclerViewFeatureIcons: RecyclerView
    private lateinit var tvPlayerCash: TextView
    private lateinit var tvPlayerEnergy: TextView
    private lateinit var tvCurrentDate: TextView
    private lateinit var tvPlayerNameMini: TextView
    private lateinit var tvPlayerClubMini: TextView
    private lateinit var tvCoreStatsMini: TextView
    private lateinit var imgPlayerAvatarMini: ImageView
    private lateinit var tvNextOpponent: TextView
    private lateinit var tvNextMatchDate: TextView

    // Overlay views
    private var trainingPanelOverlay: View? = null
    private var shopOverlay: View? = null
    private var matchOverlay: View? = null
    private var agentSelectionOverlay: View? = null
    private var sponsorshipOfferOverlay: View? = null
    private var trialOfferOverlay: View? = null
    private var contractOfferOverlay: View? = null
    private var leagueOverlay: View? = null
    private var narrativeEventOverlay: View? = null // <-- PROPERTI BARU

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (closeAnyVisibleOverlay()) return
                    showExitConfirmation()
                }
            })
    }

    private fun closeAnyVisibleOverlay(): Boolean {
        listOfNotNull(
            trainingPanelOverlay, shopOverlay, matchOverlay,
            agentSelectionOverlay, sponsorshipOfferOverlay,
            trialOfferOverlay, contractOfferOverlay, leagueOverlay,
            narrativeEventOverlay // <-- Tambahkan ke daftar
        ).forEach { panel ->
            if (panel.visibility == View.VISIBLE) {
                panel.visibility = View.GONE
                return true
            }
        }
        return false
    }

    private fun showExitConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Exit Game")
            .setMessage("Are you sure you want to quit to the main menu? Unsaved progress will be lost.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Quit") { _, _ ->
                findNavController().popBackStack(R.id.mainMenuFragment, false)
            }
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_dashboard_compact, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initializePlayer(args.playerProfile, args.playerAttributes, args.loadedPlayer)

        initializeViews(view)
        setupRecyclerViewIcons()
        setupRecyclerViews()
        observeViewModelCompact()
        setupFabContinue()
        setupOverlayClickListeners()
        setupFeatureIconClickListeners()
    }

    private fun initializeViews(view: View) {
        // Top status bar
        tvPlayerCash = view.require(R.id.tv_player_cash)
        tvPlayerEnergy = view.require(R.id.tv_player_energy)
        tvCurrentDate = view.require(R.id.tv_current_date)

        // Player status panel
        tvPlayerNameMini = view.require(R.id.tv_player_name_mini)
        tvPlayerClubMini = view.require(R.id.tv_player_club_mini)
        tvCoreStatsMini = view.require(R.id.tv_core_stats_mini)
        imgPlayerAvatarMini = view.require(R.id.img_player_avatar_mini)

        // Next match panel
        tvNextOpponent = view.require(R.id.tv_next_opponent)
        tvNextMatchDate = view.require(R.id.tv_next_match_date)
        fabContinue = view.require(R.id.fab_continue)

        // Features panel
        recyclerViewFeatureIcons = view.require(R.id.recycler_view_feature_icons)

        // Overlay panels
        trainingPanelOverlay = view.findViewById(R.id.training_panel_overlay)
        shopOverlay = view.findViewById(R.id.shop_overlay)
        matchOverlay = view.findViewById(R.id.match_overlay)
        agentSelectionOverlay = view.findViewById(R.id.agent_selection_overlay)
        sponsorshipOfferOverlay = view.findViewById(R.id.sponsorship_offer_overlay)
        trialOfferOverlay = view.findViewById(R.id.trial_offer_overlay)
        contractOfferOverlay = view.findViewById(R.id.contract_offer_overlay)
        leagueOverlay = view.findViewById(R.id.league_overlay)
        narrativeEventOverlay = view.findViewById(R.id.narrative_event_overlay) // <-- INISIALISASI VIEW BARU
    }

    private inline fun <reified T : View> View?.require(@IdRes id: Int): T =
        this?.findViewById<T>(id) ?: error("View $id not found")

    private fun setupRecyclerViewIcons() {
        val icons = listOf(
            FeatureIcon("training", "Training", R.drawable.ic_training_cone),
            FeatureIcon("skills", "Skills", R.drawable.ic_boot),
            FeatureIcon("shop", "Shop", R.drawable.ic_shop),
            FeatureIcon("league", "League", R.drawable.ic_world),
            FeatureIcon("club", "Club", R.drawable.ic_club),
            FeatureIcon("agent", "Agent", R.drawable.agent_david_roth),
            FeatureIcon("finances", "Finances", R.drawable.ic_finances),
            FeatureIcon("lifestyle", "Lifestyle", R.drawable.ic_lifestyle),
            FeatureIcon("social", "Social", R.drawable.ic_social),
            FeatureIcon("trophies", "Trophies", R.drawable.ic_trophies),
            FeatureIcon("stats", "Stats", R.drawable.ic_stats),
            FeatureIcon("profile", "Profile", R.drawable.ic_profile),
            FeatureIcon("mail", "Mail", R.drawable.ic_mail),
            FeatureIcon("settings", "Settings", R.drawable.ic_settings),
            FeatureIcon("save", "Save", R.drawable.ic_save)
        )

        featureIconAdapter = FeatureIconAdapter(icons) { icon ->
            SoundManager.playUiClick()
            handleFeatureIconClick(icon)
        }
        recyclerViewFeatureIcons.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerViewFeatureIcons.adapter = featureIconAdapter
    }

    private fun handleFeatureIconClick(icon: FeatureIcon) {
        when (icon.id) {
            "training" -> trainingPanelOverlay?.visibility = View.VISIBLE
            "shop" -> shopOverlay?.visibility = View.VISIBLE
            "league" -> leagueOverlay?.visibility = View.VISIBLE
            "agent" -> viewModel.requestAgentSelection()
            "skills" -> Toast.makeText(requireContext(), "Skills feature coming soon!", Toast.LENGTH_SHORT).show()
            "club" -> Toast.makeText(requireContext(), "Club feature coming soon!", Toast.LENGTH_SHORT).show()
            "finances" -> Toast.makeText(requireContext(), "Finances feature coming soon!", Toast.LENGTH_SHORT).show()
            "save" -> {
                viewModel.playerData.value?.let { player ->
                    SaveRepository.saveSlot1(player)
                    Toast.makeText(requireContext(), "Progress saved successfully", Toast.LENGTH_SHORT).show()
                }
            }
            else -> Toast.makeText(requireContext(), "${icon.label} feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerViews() {
        agentAdapter = AgentAdapter { agent -> viewModel.selectAgent(agent) }
        shopAdapter = ShopAdapter { item, action ->
            when (action) {
                "BUY" -> viewModel.buyItem(item)
                "EQUIP" -> viewModel.equipItem(item)
            }
        }
        trialOfferAdapter = TrialOfferAdapter { club -> viewModel.acceptTrial(club) }
        commentaryAdapter = MatchCommentaryAdapter()
        leagueTableAdapter = LeagueTableAdapter()

        agentSelectionOverlay?.findViewById<RecyclerView>(R.id.recycler_view_agents)?.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = agentAdapter
        }
        shopOverlay?.findViewById<RecyclerView>(R.id.recycler_view_shop)?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = shopAdapter
        }
        trialOfferOverlay?.findViewById<RecyclerView>(R.id.recycler_view_trial_offers)?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trialOfferAdapter
        }
        matchOverlay?.findViewById<RecyclerView>(R.id.recycler_view_commentary)?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentaryAdapter
        }
        leagueOverlay?.findViewById<RecyclerView>(R.id.recycler_view_league_table)?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = leagueTableAdapter
        }
    }

    private fun observeViewModelCompact() {
        viewModel.playerData.observe(viewLifecycleOwner) { player ->
            tvPlayerCash.text = "$${player.cash.toInt()}"
            tvPlayerEnergy.text = "${player.energy}⚡"
            tvPlayerNameMini.text = player.profile.name
            tvPlayerClubMini.text = player.club.name
            tvCoreStatsMini.text = "FIN ${player.attributes.technical.finishing}  |  SPD ${player.attributes.physical.sprintSpeed}  |  STA ${player.attributes.physical.stamina}"
            val avatarRes = resources.getIdentifier(player.profile.avatarResourceName, "drawable", requireActivity().packageName)
            imgPlayerAvatarMini.setImageResource(avatarRes)
        }

        viewModel.currentDate.observe(viewLifecycleOwner) { date ->
            tvCurrentDate.text = date.format(viewModel.dateFormatter)
        }

        viewModel.nextEvent.observe(viewLifecycleOwner) { event ->
            event?.let {
                when (it.type) {
                    EventType.MATCH_DAY -> {
                        val opponentId = it.details["opponentId"]?.toIntOrNull() ?: 0
                        val opponent = DatabaseRepository.getClubById(opponentId)
                        tvNextOpponent.text = "vs ${opponent?.name ?: "Unknown"}"
                        tvNextMatchDate.text = "Today"
                    }
                    EventType.TRAINING_SESSION -> {
                        tvNextOpponent.text = "Training Session"
                        tvNextMatchDate.text = "Today"
                    }
                    else -> {
                        tvNextOpponent.text = it.description
                        tvNextMatchDate.text = "Today"
                    }
                }
            } ?: run {
                tvNextOpponent.text = "No upcoming events"
                tvNextMatchDate.text = "Rest day"
            }
        }

        viewModel.leagueTable.observe(viewLifecycleOwner) { table ->
            leagueTableAdapter.submitList(table)
        }

        viewModel.matchMinute.observe(viewLifecycleOwner) { minute ->
            matchOverlay?.findViewById<TextView>(R.id.tv_match_minute)?.text = "$minute'"
        }
        viewModel.currentStamina.observe(viewLifecycleOwner) { stamina ->
            matchOverlay?.findViewById<TextView>(R.id.tv_match_stamina)?.text = "$stamina"
        }
        viewModel.fourStats.observe(viewLifecycleOwner) { stats ->
            matchOverlay?.findViewById<TextView>(R.id.tv_four_stats)?.text = stats
        }
        viewModel.matchCommentary.observe(viewLifecycleOwner) { commentary ->
            commentaryAdapter.submitList(commentary)
            matchOverlay?.findViewById<RecyclerView>(R.id.recycler_view_commentary)?.scrollToPosition(commentary.size - 1)
        }
        viewModel.matchScore.observe(viewLifecycleOwner) { (home, away) ->
            matchOverlay?.findViewById<TextView>(R.id.tv_score)?.text = "$home - $away"
        }
        viewModel.currentKeyMoment.observe(viewLifecycleOwner) { keyMoment ->
            val panel = matchOverlay?.findViewById<View>(R.id.key_moment_panel)
            if (keyMoment != null) {
                panel?.visibility = View.VISIBLE
                matchOverlay?.findViewById<TextView>(R.id.tv_key_moment_description)?.text = keyMoment.description
                val buttons = listOfNotNull(
                    matchOverlay?.findViewById<Button>(R.id.btn_choice_1),
                    matchOverlay?.findViewById<Button>(R.id.btn_choice_2),
                    matchOverlay?.findViewById<Button>(R.id.btn_choice_3)
                )
                buttons.forEach { it.visibility = View.GONE }
                keyMoment.choices.forEachIndexed { index, choice ->
                    if (index < buttons.size) {
                        buttons[index].apply {
                            visibility = View.VISIBLE
                            text = choice.description
                            setOnClickListener {
                                SoundManager.playUiClick()
                                viewModel.makePlayerChoice(choice)
                            }
                        }
                    }
                }
            } else {
                panel?.visibility = View.GONE
            }
        }

        viewModel.availableAgents.observe(viewLifecycleOwner) { agents ->
            if (agents.isNotEmpty()) {
                agentAdapter.submitList(agents)
                agentSelectionOverlay?.visibility = View.VISIBLE
            } else {
                agentSelectionOverlay?.visibility = View.GONE
            }
        }

        viewModel.sponsorshipOffer.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { sponsor ->
                sponsorshipOfferOverlay?.findViewById<TextView>(R.id.tv_sponsor_name)?.text = sponsor.name
                val payout = sponsor.baseWeeklyPayout * (1.0 + (viewModel.currentPlayerAgent.value?.reputation ?: 0) / 200.0)
                sponsorshipOfferOverlay?.findViewById<TextView>(R.id.tv_sponsor_payout)?.text = "$${"%.2f".format(payout)} / week"
                sponsorshipOfferOverlay?.visibility = View.VISIBLE
            }
        }

        viewModel.trialOffers.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { offers ->
                if (offers.isNotEmpty()) {
                    trialOfferAdapter.submitList(offers)
                    trialOfferOverlay?.visibility = View.VISIBLE
                } else {
                    trialOfferOverlay?.visibility = View.GONE
                }
            }
        }

        viewModel.contractOffer.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { offer ->
                val club = DatabaseRepository.getClubById(offer.clubId)
                contractOfferOverlay?.findViewById<TextView>(R.id.tv_contract_club_name)?.text = club?.name ?: "Unknown Club"
                contractOfferOverlay?.findViewById<TextView>(R.id.tv_contract_wage)?.text = "$${"%.2f".format(offer.weeklyWage)} / week"
                contractOfferOverlay?.findViewById<TextView>(R.id.tv_contract_details)?.text = "${offer.contractLengthYears} year contract as ${offer.role}"
                contractOfferOverlay?.visibility = View.VISIBLE
            }
        }

        viewModel.matchCompletedEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                matchOverlay?.visibility = View.GONE
                viewModel.resetMatchState()
            }
        }

        // *** OBSERVER BARU UNTUK EVENT NARATIF ***
        viewModel.activeNarrativeEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { gameEvent ->
                if (gameEvent != null) {
                    // Tampilkan dan isi data overlay
                    narrativeEventOverlay?.visibility = View.VISIBLE
                    narrativeEventOverlay?.findViewById<TextView>(R.id.tv_narrative_title)?.text = gameEvent.title
                    narrativeEventOverlay?.findViewById<TextView>(R.id.tv_narrative_description)?.text = gameEvent.description

                    val choicesLayout = narrativeEventOverlay?.findViewById<LinearLayout>(R.id.layout_narrative_choices)
                    choicesLayout?.removeAllViews() // Bersihkan pilihan lama

                    gameEvent.choices.forEach { choice ->
                        val button = Button(requireContext()).apply {
                            text = choice.text
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            setOnClickListener {
                                SoundManager.playUiClick()
                                viewModel.resolveNarrativeChoice(choice)
                            }
                        }
                        choicesLayout?.addView(button)
                    }
                } else {
                    // Sembunyikan overlay
                    narrativeEventOverlay?.visibility = View.GONE
                }
            }
        }
    }

    private fun setupFabContinue() {
        fabContinue.setOnClickListener {
            SoundManager.playUiClick()
            val player = viewModel.playerData.value ?: return@setOnClickListener
            val nextEvent = viewModel.nextEvent.value

            if (nextEvent?.type == EventType.MATCH_DAY) {
                if (player.club.isUnattached) {
                    Toast.makeText(requireContext(), "You must join a club before playing a match.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                matchOverlay?.visibility = View.VISIBLE
                val opponentId = nextEvent.details["opponentId"]?.toIntOrNull() ?: 24
                val opponent = DatabaseRepository.getClubById(opponentId)!!
                viewModel.startMatchSimulation(opponent)
            } else {
                viewModel.onContinuePressed()
            }
        }
    }

    private fun setupOverlayClickListeners() {
        trainingPanelOverlay?.findViewById<View>(R.id.btn_close_training_panel)?.setOnClickListener {
            SoundManager.playUiClick()
            trainingPanelOverlay?.visibility = View.GONE
        }
        trainingPanelOverlay?.findViewById<View>(R.id.btn_train_attack)?.setOnClickListener {
            SoundManager.playUiClick()
            viewModel.trainAttribute("ATTACK")
            trainingPanelOverlay?.visibility = View.GONE
        }
        trainingPanelOverlay?.findViewById<View>(R.id.btn_train_defense)?.setOnClickListener {
            SoundManager.playUiClick()
            viewModel.trainAttribute("DEFENSE")
            trainingPanelOverlay?.visibility = View.GONE
        }
        trainingPanelOverlay?.findViewById<View>(R.id.btn_train_technique)?.setOnClickListener {
            SoundManager.playUiClick()
            viewModel.trainAttribute("TECHNIQUE")
            trainingPanelOverlay?.visibility = View.GONE
        }
        shopOverlay?.findViewById<View>(R.id.btn_close_shop)?.setOnClickListener {
            SoundManager.playUiClick()
            shopOverlay?.visibility = View.GONE
        }
        sponsorshipOfferOverlay?.findViewById<View>(R.id.btn_accept_sponsor)?.setOnClickListener {
            SoundManager.playUiClick()
            viewModel.sponsorshipOffer.value?.peekContent()?.let { sponsor ->
                viewModel.acceptSponsorOffer(sponsor)
            }
            sponsorshipOfferOverlay?.visibility = View.GONE
        }
        sponsorshipOfferOverlay?.findViewById<View>(R.id.btn_decline_sponsor)?.setOnClickListener {
            SoundManager.playUiClick()
            viewModel.declineSponsorOffer()
            sponsorshipOfferOverlay?.visibility = View.GONE
        }
        contractOfferOverlay?.findViewById<View>(R.id.btn_accept_contract)?.setOnClickListener {
            SoundManager.playUiClick()
            viewModel.contractOffer.value?.peekContent()?.let { offer ->
                viewModel.acceptContract(offer)
            }
            contractOfferOverlay?.visibility = View.GONE
        }
        leagueOverlay?.findViewById<View>(R.id.btn_close_league)?.setOnClickListener {
            SoundManager.playUiClick()
            leagueOverlay?.visibility = View.GONE
        }
        
        agentSelectionOverlay?.findViewById<View>(R.id.btn_close_agent_panel)?.setOnClickListener {
            SoundManager.playUiClick()
            agentSelectionOverlay?.visibility = View.GONE
        }
    }

    private fun setupFeatureIconClickListeners() {
        view?.findViewById<ImageView>(R.id.feature_save)?.setOnClickListener {
            SoundManager.playUiClick()
            viewModel.playerData.value?.let { player ->
                SaveRepository.saveSlot1(player)
                Toast.makeText(requireContext(), "Progress saved successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        SoundManager.playMenuMusic(requireContext())
    }

    override fun onStop() {
        super.onStop()
        SoundManager.stopMenuMusic()
    }

    inner class LeagueTableAdapter :
        ListAdapter<LeagueStanding, LeagueTableAdapter.VH>(object : DiffUtil.ItemCallback<LeagueStanding>() {
            override fun areItemsTheSame(oldItem: LeagueStanding, newItem: LeagueStanding) = oldItem.clubId == newItem.clubId
            override fun areContentsTheSame(oldItem: LeagueStanding, newItem: LeagueStanding) = oldItem == newItem
        }) {
        inner class VH(val b: ItemLeagueStandingBinding) : RecyclerView.ViewHolder(b.root)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
            VH(ItemLeagueStandingBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        override fun onBindViewHolder(holder: VH, position: Int) {
            val standing = getItem(position)
            with(holder.b) {
                tvPos.text = "${position + 1}"
                tvClub.text = standing.clubName
                tvP.text = standing.played.toString()
                tvW.text = standing.won.toString()
                tvD.text = standing.drawn.toString()
                tvL.text = standing.lost.toString()
                tvPts.text = standing.pts.toString()
            }
        }
    }
}

