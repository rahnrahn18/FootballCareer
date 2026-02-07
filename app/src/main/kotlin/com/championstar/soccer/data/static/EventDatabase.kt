package com.championstar.soccer.data.static

/**
 * EventDatabase
 *
 * Contains a massive collection of text descriptions for match simulation events.
 * Events are categorized by zone, type, and outcome to allow dynamic commentary generation.
 */
object EventDatabase {

    enum class Zone { DEFENSE, MIDFIELD, ATTACK }
    enum class EventType { PASS, DRIBBLE, SHOT, TACKLE, INTERCEPTION, FOUL, CARD, SAVE }
    enum class Outcome { SUCCESS, FAILURE, GOAL, MISS, BLOCKED, WOODWORK }

    data class MatchEventTemplate(
        val text: String,
        val zone: Zone,
        val type: EventType,
        val outcome: Outcome,
        val excitementLevel: Int // 1-10, for highlighting important moments
    )

    // --- Event Repository ---

    val events = listOf(
        // --- MIDFIELD PASSING (SUCCESS) ---
        MatchEventTemplate("{player} spreads the play wide to {receiver}.", Zone.MIDFIELD, EventType.PASS, Outcome.SUCCESS, 2),
        MatchEventTemplate("{player} plays a simple one-two with {receiver}.", Zone.MIDFIELD, EventType.PASS, Outcome.SUCCESS, 2),
        MatchEventTemplate("A lovely weighted pass from {player} finds {receiver} in space.", Zone.MIDFIELD, EventType.PASS, Outcome.SUCCESS, 3),
        MatchEventTemplate("{player} dictates the tempo with a calm pass to {receiver}.", Zone.MIDFIELD, EventType.PASS, Outcome.SUCCESS, 2),
        MatchEventTemplate("{player} sprays a long diagonal ball towards {receiver}.", Zone.MIDFIELD, EventType.PASS, Outcome.SUCCESS, 3),
        MatchEventTemplate("{player} threads the needle to find {receiver} between the lines.", Zone.MIDFIELD, EventType.PASS, Outcome.SUCCESS, 4),
        MatchEventTemplate("Quick transition! {player} releases {receiver} immediately.", Zone.MIDFIELD, EventType.PASS, Outcome.SUCCESS, 4),
        MatchEventTemplate("{player} holds off a challenger and lays it off to {receiver}.", Zone.MIDFIELD, EventType.PASS, Outcome.SUCCESS, 2),
        MatchEventTemplate("{player} switches play to the opposite flank for {receiver}.", Zone.MIDFIELD, EventType.PASS, Outcome.SUCCESS, 3),
        MatchEventTemplate("Tiki-taka football here as {player} finds {receiver}.", Zone.MIDFIELD, EventType.PASS, Outcome.SUCCESS, 3),

        // --- MIDFIELD PASSING (FAILURE) ---
        MatchEventTemplate("{player} tries a risky pass but it's cut out.", Zone.MIDFIELD, EventType.PASS, Outcome.FAILURE, 2),
        MatchEventTemplate("{player} overhits the pass, ball goes out for a throw-in.", Zone.MIDFIELD, EventType.PASS, Outcome.FAILURE, 1),
        MatchEventTemplate("Poor vision from {player}, straight to the opposition.", Zone.MIDFIELD, EventType.PASS, Outcome.FAILURE, 2),
        MatchEventTemplate("{player} misplaces the pass under pressure.", Zone.MIDFIELD, EventType.PASS, Outcome.FAILURE, 2),
        MatchEventTemplate("The pass from {player} is too heavy for {receiver} to reach.", Zone.MIDFIELD, EventType.PASS, Outcome.FAILURE, 1),

        // --- ATTACK SHOTS (GOAL) ---
        MatchEventTemplate("GOAL! {player} smashes it into the top corner!", Zone.ATTACK, EventType.SHOT, Outcome.GOAL, 10),
        MatchEventTemplate("GOAL! A composed finish from {player} into the bottom corner.", Zone.ATTACK, EventType.SHOT, Outcome.GOAL, 9),
        MatchEventTemplate("GOAL! {player} rounds the keeper and taps it in!", Zone.ATTACK, EventType.SHOT, Outcome.GOAL, 10),
        MatchEventTemplate("GOAL! A bullet header from {player} finds the net!", Zone.ATTACK, EventType.SHOT, Outcome.GOAL, 9),
        MatchEventTemplate("GOAL! {player} scores with a spectacular volley!", Zone.ATTACK, EventType.SHOT, Outcome.GOAL, 10),
        MatchEventTemplate("GOAL! {player} converts the penalty with ease.", Zone.ATTACK, EventType.SHOT, Outcome.GOAL, 8),
        MatchEventTemplate("GOAL! A deflection helps {player}'s shot wrong-foot the keeper.", Zone.ATTACK, EventType.SHOT, Outcome.GOAL, 8),
        MatchEventTemplate("GOAL! {player} pounces on the rebound to score!", Zone.ATTACK, EventType.SHOT, Outcome.GOAL, 9),
        MatchEventTemplate("GOAL! Incredible solo effort from {player}, finishing low.", Zone.ATTACK, EventType.SHOT, Outcome.GOAL, 10),
        MatchEventTemplate("GOAL! {player} bends it like Beckham into the far post!", Zone.ATTACK, EventType.SHOT, Outcome.GOAL, 10),

        // --- ATTACK SHOTS (MISS/SAVE) ---
        MatchEventTemplate("{player} shoots! But it flies over the bar.", Zone.ATTACK, EventType.SHOT, Outcome.MISS, 5),
        MatchEventTemplate("Great save! The keeper denies {player} from close range.", Zone.ATTACK, EventType.SHOT, Outcome.BLOCKED, 7),
        MatchEventTemplate("{player} scuffs the shot, easy gather for the keeper.", Zone.ATTACK, EventType.SHOT, Outcome.MISS, 3),
        MatchEventTemplate("OFF THE POST! {player} is denied by the woodwork!", Zone.ATTACK, EventType.SHOT, Outcome.WOODWORK, 8),
        MatchEventTemplate("Last ditch block! The defender slides to stop {player}'s shot.", Zone.ATTACK, EventType.SHOT, Outcome.BLOCKED, 6),
        MatchEventTemplate("{player} drags the shot wide of the left post.", Zone.ATTACK, EventType.SHOT, Outcome.MISS, 4),
        MatchEventTemplate("The keeper tips {player}'s long range effort over the bar.", Zone.ATTACK, EventType.SHOT, Outcome.BLOCKED, 6),

        // --- DEFENSE (TACKLES/INTERCEPTIONS) ---
        MatchEventTemplate("{player} reads the game perfectly and intercepts the pass.", Zone.DEFENSE, EventType.INTERCEPTION, Outcome.SUCCESS, 4),
        MatchEventTemplate("Crunching tackle by {player} to win the ball back!", Zone.DEFENSE, EventType.TACKLE, Outcome.SUCCESS, 5),
        MatchEventTemplate("{player} stays on their feet and shepherds the ball out.", Zone.DEFENSE, EventType.TACKLE, Outcome.SUCCESS, 3),
        MatchEventTemplate("Crucial intervention by {player} to stop the counter.", Zone.DEFENSE, EventType.INTERCEPTION, Outcome.SUCCESS, 5),
        MatchEventTemplate("{player} clears the lines with a no-nonsense punt.", Zone.DEFENSE, EventType.TACKLE, Outcome.SUCCESS, 2),

        // --- FOULS / CARDS ---
        MatchEventTemplate("Foul! {player} trips the opponent.", Zone.MIDFIELD, EventType.FOUL, Outcome.FAILURE, 3),
        MatchEventTemplate("The referee blows the whistle. {player} pushed the defender.", Zone.ATTACK, EventType.FOUL, Outcome.FAILURE, 3),
        MatchEventTemplate("YELLOW CARD! {player} goes into the book for a cynical foul.", Zone.DEFENSE, EventType.CARD, Outcome.FAILURE, 6),
        MatchEventTemplate("RED CARD! {player} is sent off for a dangerous two-footed lunge!", Zone.DEFENSE, EventType.CARD, Outcome.FAILURE, 10),
        MatchEventTemplate("Handball by {player}. Free kick given.", Zone.MIDFIELD, EventType.FOUL, Outcome.FAILURE, 3)
    )

    // --- Helper Functions ---

    /**
     * Retrieves a random event matching the criteria.
     */
    fun getEvent(zone: Zone, type: EventType, outcome: Outcome): String {
        val candidates = events.filter { it.zone == zone && it.type == type && it.outcome == outcome }
        return if (candidates.isNotEmpty()) candidates.random().text else "Event occurred."
    }
}
