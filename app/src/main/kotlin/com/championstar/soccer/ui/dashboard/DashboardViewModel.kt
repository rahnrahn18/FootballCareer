package com.championstar.soccer.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.championstar.soccer.core.Event
import com.championstar.soccer.data.model.*
import com.championstar.soccer.data.repository.*
import com.championstar.soccer.game.calendar.GameCalendar
import com.championstar.soccer.game.engine.MatchEngine
import com.championstar.soccer.game.league.LeagueManager
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.random.Random

class DashboardViewModel : ViewModel() {

    // Player & Calendar
    private val _playerData = MutableLiveData<Player>()
    val playerData: LiveData<Player> = _playerData
    private val _currentDate = MutableLiveData<java.time.LocalDate>()
    val currentDate: LiveData<java.time.LocalDate> = _currentDate
    private val _nextEvent = MutableLiveData<CalendarEvent?>()
    val nextEvent: LiveData<CalendarEvent?> = _nextEvent
    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy")

    // Match Simulation
    private val _matchCommentary = MutableLiveData<List<MatchCommentary>>()
    val matchCommentary: LiveData<List<MatchCommentary>> = _matchCommentary
    private val _currentKeyMoment = MutableLiveData<KeyMoment?>()
    val currentKeyMoment: LiveData<KeyMoment?> = _currentKeyMoment
    private val _matchScore = MutableLiveData<Pair<Int, Int>>()
    val matchScore: LiveData<Pair<Int, Int>> = _matchScore
    private var matchTimer: Timer? = null
    private val _matchMinute = MutableLiveData(0)
    val matchMinute: LiveData<Int> = _matchMinute
    private val _currentStamina = MutableLiveData(100)
    val currentStamina: LiveData<Int> = _currentStamina
    private val _fourStats = MutableLiveData("0 0 0 0")
    val fourStats: LiveData<String> = _fourStats
    private var currentOpponent: Club? = null

    // Overlays & Events
    private val _statusMessage = MutableLiveData<Event<String>>()
    val statusMessage: LiveData<Event<String>> = _statusMessage
    private val _availableAgents = MutableLiveData<List<Agent>>()
    val availableAgents: LiveData<List<Agent>> = _availableAgents
    private val _sponsorshipOffer = MutableLiveData<Event<Sponsor?>>()
    val sponsorshipOffer: LiveData<Event<Sponsor?>> = _sponsorshipOffer
    private val _trialOffers = MutableLiveData<Event<List<Club>>>()
    val trialOffers: LiveData<Event<List<Club>>> = _trialOffers
    private val _contractOffer = MutableLiveData<Event<ContractOffer?>>()
    val contractOffer: LiveData<Event<ContractOffer?>> = _contractOffer
    private val _leagueTable = MutableLiveData<List<LeagueStanding>>()
    val leagueTable: LiveData<List<LeagueStanding>> = _leagueTable
    private var _currentPlayerAgent = MutableLiveData<Agent?>()
    val currentPlayerAgent: LiveData<Agent?> = _currentPlayerAgent
    private val _matchCompletedEvent = MutableLiveData<Event<Unit>>()
    val matchCompletedEvent: LiveData<Event<Unit>> = _matchCompletedEvent

    // Narrative Event
    private val _activeNarrativeEvent = MutableLiveData<Event<GameEvent?>>()
    val activeNarrativeEvent: LiveData<Event<GameEvent?>> = _activeNarrativeEvent

    private val _availableShopItems = MutableLiveData<List<ShopItem>>()
    val availableShopItems: LiveData<List<ShopItem>> = _availableShopItems

    fun initializePlayer(profile: PlayerProfile, attributes: PlayerAttributes, loadedPlayer: Player?) {
        val player = loadedPlayer ?: Player(
            profile = profile,
            attributes = attributes,
            careerState = PlayerCareerState.UNATTACHED_NO_AGENT
        )
        _playerData.value = player

        val allLeagues = DatabaseRepository.getLeagues()
        LeagueManager.initialize(allLeagues)
        GameCalendar.initialize()

        if (loadedPlayer == null) {
            SaveRepository.saveSlot1(player)
        }

        updateCalendarData()
        updateLeagueTable()
        _availableShopItems.value = ShopRepository.getAllItems()
        updateFourStats()
    }

    fun onContinuePressed() {
        val player = _playerData.value ?: return
        val processedEvent = GameCalendar.advanceToNextEvent(player)
        processEventConsequences(processedEvent)
        updateCalendarData()
        _playerData.value?.let { SaveRepository.saveSlot1(it) }
    }

    private fun processEventConsequences(event: CalendarEvent) {
        when (event.type) {
            EventType.AGENT_MEETING -> {
                requestAgentSelection()
            }
            EventType.SPECIAL_OPPORTUNITY -> {
                findTrialOffers()
            }
            EventType.NARRATIVE_EVENT -> {
                val eventId = event.details["eventId"]
                if (eventId != null) {
                    val gameEvent = EventRepository.getEventById(eventId)
                    _activeNarrativeEvent.value = Event(gameEvent)
                }
            }
            else -> {
                // No special action
            }
        }
        processWeeklyPayouts()
        checkForNewSponsorOffers()
    }

    fun resolveNarrativeChoice(choice: Choice) {
        val player = _playerData.value ?: return
        var message = ""

        choice.effects.forEach { effect ->
            when (effect.type) {
                "setAttribute" -> {
                    when (effect.target) {
                        "LEADERSHIP" -> player.attributes.mental.leadership += effect.value?.toInt() ?: 0
                        "DETERMINATION" -> player.attributes.mental.determination += effect.value?.toInt() ?: 0
                        "PROFESSIONALISM" -> player.attributes.personal.professionalism += effect.value?.toInt() ?: 0
                        "MEDIA_HANDLING" -> player.attributes.personal.mediaHandling += effect.value?.toInt() ?: 0
                        "LOYALTY" -> player.attributes.personal.loyalty += effect.value?.toInt() ?: 0
                    }
                }
                "setRelationship" -> {
                    // Future logic for relationships
                }
                "changeCash" -> {
                    player.cash += effect.value ?: 0.0
                }
                "changeEnergy" -> {
                    player.energy = (player.energy + (effect.value?.toInt() ?: 0)).coerceIn(0, 100)
                }
                "addRandomOutcome" -> {
                    val diceRoll = Random.nextDouble()
                    var cumulativeChance = 0.0
                    val outcome = effect.outcomes?.find {
                        cumulativeChance += it.chance
                        diceRoll < cumulativeChance
                    }
                    if (outcome != null) {
                        message = outcome.label
                        outcome.effects.forEach { nestedEffect ->
                            if (nestedEffect.type == "changeCash") {
                                player.cash += nestedEffect.value ?: 0.0
                            }
                        }
                    }
                }
            }
        }

        if (message.isNotEmpty()) {
            _statusMessage.value = Event(message)
        }

        _playerData.value = player
        _activeNarrativeEvent.value = Event(null)
    }

    private fun updateCalendarData() {
        val player = _playerData.value ?: return
        _currentDate.value = GameCalendar.getCurrentDate()
        _nextEvent.value = GameCalendar.getNextEvent(player)
    }

    private fun postCalendarData() {
        val player = _playerData.value ?: return
        _currentDate.postValue(GameCalendar.getCurrentDate())
        _nextEvent.postValue(GameCalendar.getNextEvent(player))
    }

    fun selectAgent(agent: Agent) {
        val player = _playerData.value ?: return
        _currentPlayerAgent.value = agent
        player.careerState = PlayerCareerState.UNATTACHED_WITH_AGENT
        _playerData.value = player
        _statusMessage.value = Event("You are now represented by ${agent.name}.")
        _availableAgents.value = emptyList()
        updateCalendarData()
    }

    fun acceptContract(offer: ContractOffer) {
        val player = _playerData.value ?: return
        val newClub = DatabaseRepository.getClubById(offer.clubId) ?: return

        player.club = newClub
        player.currentContract = offer
        player.careerState = PlayerCareerState.UNDER_CONTRACT
        _playerData.value = player

        GameCalendar.scheduleLeagueMatches(newClub.id)
        updateLeagueTable()
        _statusMessage.value = Event("Congratulations! You have signed with ${newClub.name}.")
        updateFourStats()
        updateCalendarData()
    }

    fun matchCompleted() {
        matchTimer?.cancel()
        matchTimer = null
        currentOpponent = null
        updateLeagueTable()

        val player = _playerData.value ?: return
        GameCalendar.advanceToNextEvent(player)
        postCalendarData()

        _statusMessage.postValue(Event("Full Time!"))
        _playerData.value?.let { SaveRepository.saveSlot1(it) }
        _matchCompletedEvent.postValue(Event(Unit))
    }

    private fun findTrialOffers() {
        val player = _playerData.value ?: return
        val agent = _currentPlayerAgent.value

        val playerRating = with(player.attributes) {
            (technical.finishing + physical.sprintSpeed + technical.dribbling + technical.shortPassing + mental.positioning) / 5
        }

        val agentReputation = agent?.reputation ?: 1
        var numberOfOffers = (1 + (agentReputation / 30)).coerceAtMost(3)
        var reputationBoost = 0

        if (agent?.specialty == AgentSpecialty.TALENT_SCOUT) {
            numberOfOffers += 1
            reputationBoost = 5
        }

        val allClubs = DatabaseRepository.getLeagues().flatMap { it.clubs }
        val suitableClubs = allClubs.filter { club ->
            val requiredReputation = when {
                playerRating >= 15 -> 80
                playerRating >= 10 -> 65
                playerRating >= 5  -> 50
                else -> 0
            }
            club.reputation >= (requiredReputation - reputationBoost)
        }

        val finalOffers = suitableClubs.shuffled().take(numberOfOffers)
        _trialOffers.value = Event(finalOffers)
        _statusMessage.value = Event("${agent?.name ?: "Someone"} has found ${finalOffers.size} trial opportunities for you!")
    }

    fun requestAgentSelection() {
        if (_playerData.value?.club?.isUnattached == true) {
            _availableAgents.value = AgentRepository.getInitialAgents()
        } else {
            _statusMessage.value = Event("You already have an agent.")
        }
    }

    fun acceptTrial(club: Club) {
        _trialOffers.value = Event(emptyList())

        val weeklyWage = when(club.tier) {
            ClubTier.AMATEUR -> 100.0 + (club.reputation * 2)
            ClubTier.SEMI_PRO -> 250.0 + (club.reputation * 5)
            ClubTier.PROFESSIONAL -> 500.0 + (club.reputation * 10)
        }
        val role = if (club.tier == ClubTier.AMATEUR) "Key Player" else "Reserve Player"

        val contract = ContractOffer(
            clubId = club.id,
            leagueId = DatabaseRepository.getLeagues().find { l -> l.clubs.any { c -> c.id == club.id } }?.id ?: -1,
            weeklyWage = weeklyWage,
            contractLengthYears = 1,
            role = role
        )
        _contractOffer.value = Event(contract)
    }

    fun startMatchSimulation(opponent: Club) {
        this.currentOpponent = opponent
        val player = _playerData.value ?: return
        val leagueId = DatabaseRepository.getLeagues().find { l -> l.clubs.any { c -> c.id == player.club.id } }?.id ?: -1
        MatchEngine.startMatch(player, opponent, leagueId)
        _matchCommentary.value = MatchEngine.commentaryLog.toList()
        _matchScore.value = MatchEngine.getScore()
        resumeMatchSimulation()
    }

    private fun resumeMatchSimulation() {
        val player = _playerData.value ?: return
        matchTimer?.cancel()
        matchTimer = Timer()
        matchTimer?.scheduleAtFixedRate(timerTask {
            val keyMoment = MatchEngine.simulateMinute()
            val minute = MatchEngine.commentaryLog.lastOrNull()?.minute ?: 0
            _matchMinute.postValue(minute)
            val stamina = (player.attributes.physical.stamina - (minute * 0.5)).coerceAtLeast(0.0).toInt()
            _currentStamina.postValue(stamina)
            _matchCommentary.postValue(MatchEngine.commentaryLog.toList())
            _matchScore.postValue(MatchEngine.getScore())

            if (keyMoment != null) {
                _currentKeyMoment.postValue(keyMoment)
                matchTimer?.cancel()
            } else if (minute >= 90) {
                matchCompleted()
            }
        }, 500, 100)
    }

    fun makePlayerChoice(choice: PlayerChoice) {
        MatchEngine.resolvePlayerChoice(choice)
        _currentKeyMoment.postValue(null)
        resumeMatchSimulation()
    }

    fun resetMatchState() {
        _matchCommentary.postValue(emptyList())
        _matchScore.postValue(Pair(0, 0))
        _matchMinute.postValue(0)
    }

    fun trainAttribute(type: String) {
        val player = _playerData.value ?: return
        if (player.energy < 10) {
            _statusMessage.value = Event("Not enough energy to train!")
            return
        }
        player.energy -= 10
        when (type) {
            "ATTACK" -> player.attributes.technical.finishing += 1
            "DEFENSE" -> player.attributes.mental.positioning += 1
            "TECHNIQUE" -> player.attributes.technical.ballControl += 1
        }
        _playerData.value = player
        updateFourStats()
        _statusMessage.value = Event("$type skill increased!")
        _playerData.value?.let { SaveRepository.saveSlot1(it) }
    }

    private fun updateLeagueTable() {
        val player = _playerData.value ?: return
        if (!player.club.isUnattached) {
            val leagueId = DatabaseRepository.getLeagues().find { l -> l.clubs.any { c -> c.id == player.club.id } }?.id
            if (leagueId != null) {
                _leagueTable.postValue(LeagueManager.getTableForLeague(leagueId))
            }
        }
    }

    private fun updateFourStats() {
        val p = _playerData.value ?: return
        _fourStats.value = "${p.attributes.technical.finishing} " +
                "${p.attributes.technical.shortPassing} " +
                "${p.attributes.mental.positioning} " +
                "${p.attributes.technical.ballControl}"
    }

    fun buyItem(item: ShopItem) {}
    fun equipItem(item: ShopItem) {}
    fun acceptSponsorOffer(sponsor: Sponsor) {}
    fun declineSponsorOffer() {}
    private fun processWeeklyPayouts() {}
    private fun checkForNewSponsorOffers() {}
}

