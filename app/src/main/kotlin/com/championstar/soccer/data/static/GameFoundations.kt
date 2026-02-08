package com.championstar.soccer.data.static

/**
 * GameFoundations
 *
 * Defines the core pillars (foundations) of the game as requested.
 * Covers Points, Finance, Skills, and detailed Game Skills.
 */
object GameFoundations {

    // --- 1. Fondasi Poin (5 Aspek) ---
    // Represents the 5 core attribute categories or scoring metrics.
    enum class PointFoundation(val description: String) {
        ATTACKING("Ability to create and convert goal-scoring opportunities."),
        DEFENDING("Ability to prevent the opponent from scoring."),
        POSSESSION("Ability to control the ball and dictate play."),
        PHYSICAL("Athletic capabilities like speed, strength, and stamina."),
        MENTAL("Psychological attributes like composure, leadership, and determination.")
    }

    // --- 2. Fondasi Keuangan (5 Aspek) ---
    // Represents the 5 pillars of the game's economy.
    enum class FinancialFoundation(val description: String) {
        INCOME("Revenue streams from tickets, sponsors, and merchandise."),
        EXPENSE("Costs including wages, maintenance, and transfer fees."),
        VALUATION("The market value of players, brand, and assets."),
        BANKING("Cash reserves, loans, and interest rates."),
        INVESTMENT("External business ventures and capital growth.")
    }

    // --- 3. Fondasi Keterampilan (10 Aspek) ---
    // The 10 primary skill categories that define a player's overall quality.
    enum class SkillFoundation(val description: String) {
        PASSING("Accuracy and vision in distributing the ball."),
        SHOOTING("Precision and power in finishing chances."),
        DRIBBLING("Ball control and ability to beat defenders."),
        TACKLING("Winning the ball back cleanly."),
        POSITIONING("Being in the right place at the right time."),
        SPEED("Sprint speed and acceleration."),
        STAMINA("Endurance throughout the match."),
        STRENGTH("Physical dominance in duels."),
        VISION("Reading the game and spotting opportunities."),
        TECHNIQUE("General ball mastery and first touch.")
    }

    // --- 4. Fondasi Skill Permainan (60 Aspek) ---
    // A comprehensive list of 60 detailed technical skills and attributes.
    // Grouped by the SkillFoundation for organization, but flattened here as requested.
    enum class GameSkill(val category: SkillFoundation, val description: String) {
        // PASSING (6)
        SHORT_PASSING(SkillFoundation.PASSING, "Accuracy of short ground passes."),
        LONG_PASSING(SkillFoundation.PASSING, "Accuracy of long aerial balls."),
        CROSSING(SkillFoundation.PASSING, "Delivering the ball from wide areas."),
        THROUGH_BALLS(SkillFoundation.PASSING, "Weighted passes into space."),
        CURVE_PASSING(SkillFoundation.PASSING, "Bending the ball around opponents."),
        ONE_TOUCH_PASSING(SkillFoundation.PASSING, "Quick distribution without controlling."),

        // SHOOTING (6)
        FINISHING(SkillFoundation.SHOOTING, "Accuracy inside the box."),
        LONG_SHOTS(SkillFoundation.SHOOTING, "Power and accuracy from distance."),
        PENALTIES(SkillFoundation.SHOOTING, "Composure from the spot."),
        FREE_KICKS(SkillFoundation.SHOOTING, "Dead ball expertise."),
        VOLLEYS(SkillFoundation.SHOOTING, "Hitting the ball mid-air."),
        HEADING_ACCURACY(SkillFoundation.SHOOTING, "Directing headers on goal."),

        // DRIBBLING (6)
        BALL_CONTROL(SkillFoundation.DRIBBLING, "First touch and close control."),
        AGILITY(SkillFoundation.DRIBBLING, "Quick changes of direction."),
        BALANCE(SkillFoundation.DRIBBLING, "Staying upright under pressure."),
        SKILL_MOVES(SkillFoundation.DRIBBLING, "Ability to perform tricks."),
        REACTIONS(SkillFoundation.DRIBBLING, "Responding to loose balls."),
        COMPOSURE(SkillFoundation.DRIBBLING, "Staying calm on the ball."),

        // TACKLING (6)
        STANDING_TACKLE(SkillFoundation.TACKLING, "Dispossessing without going to ground."),
        SLIDING_TACKLE(SkillFoundation.TACKLING, "Timing of ground challenges."),
        INTERCEPTIONS(SkillFoundation.TACKLING, "Cutting out passes."),
        MARKING(SkillFoundation.TACKLING, "Sticking close to opponents."),
        BLOCKING(SkillFoundation.TACKLING, "Stopping shots and crosses."),
        AGGRESSION(SkillFoundation.TACKLING, "Intensity in challenges."),

        // POSITIONING (6)
        OFF_THE_BALL(SkillFoundation.POSITIONING, "Movement when attacking."),
        DEFENSIVE_AWARENESS(SkillFoundation.POSITIONING, "Reading danger."),
        ANTICIPATION(SkillFoundation.POSITIONING, "Predicting opponent moves."),
        LINE_DISCIPLINE(SkillFoundation.POSITIONING, "Holding the offside line."),
        SUPPORT_PLAY(SkillFoundation.POSITIONING, "Providing options for teammates."),
        SPACE_CREATION(SkillFoundation.POSITIONING, "Dragging defenders away."),

        // SPEED (6)
        ACCELERATION(SkillFoundation.SPEED, "Reaching top speed quickly."),
        SPRINT_SPEED(SkillFoundation.SPEED, "Maximum velocity."),
        DECELERATION(SkillFoundation.SPEED, "Stopping quickly."),
        PACE_WITH_BALL(SkillFoundation.SPEED, "Speed while dribbling."),
        RECOVERY_PACE(SkillFoundation.SPEED, "Catching up to attackers."),
        BURST_SPEED(SkillFoundation.SPEED, "Short explosive movements."),

        // STAMINA (6)
        MATCH_FITNESS(SkillFoundation.STAMINA, "Lasting 90 minutes."),
        RECOVERY_RATE(SkillFoundation.STAMINA, "Bouncing back between sprints."),
        WORK_RATE_ATTACK(SkillFoundation.STAMINA, "Effort in forward areas."),
        WORK_RATE_DEFENSE(SkillFoundation.STAMINA, "Effort tracking back."),
        CONSISTENCY(SkillFoundation.STAMINA, "Maintaining level over a season."),
        RESISTANCE_TO_INJURY(SkillFoundation.STAMINA, "Durability."),

        // STRENGTH (6)
        PHYSICAL_STRENGTH(SkillFoundation.STRENGTH, "Muscular power."),
        JUMPING_REACH(SkillFoundation.STRENGTH, "Vertical leap."),
        AERIAL_DUELS(SkillFoundation.STRENGTH, "Winning headers."),
        SHIELDING(SkillFoundation.STRENGTH, "Protecting the ball."),
        SHOULDER_BARGE(SkillFoundation.STRENGTH, "Legal physical challenges."),
        CORE_STABILITY(SkillFoundation.STRENGTH, "Balance in contact."),

        // VISION (6)
        CREATIVITY(SkillFoundation.VISION, "Inventing goal chances."),
        FLAIR(SkillFoundation.VISION, "Unpredictability."),
        TACTICAL_AWARENESS(SkillFoundation.VISION, "Understanding systems."),
        DECISION_MAKING(SkillFoundation.VISION, "Choosing the right option."),
        LEADERSHIP(SkillFoundation.VISION, "Organizing others."),
        COMMUNICATION(SkillFoundation.VISION, "Coordinating with teammates."),

        // TECHNIQUE (6)
        WEAK_FOOT(SkillFoundation.TECHNIQUE, "Ability with non-dominant foot."),
        FIRST_TOUCH(SkillFoundation.TECHNIQUE, "Controlling difficult balls."),
        SWERVE(SkillFoundation.TECHNIQUE, "Curving passes and shots."),
        LOFTED_PASS(SkillFoundation.TECHNIQUE, "High balls."),
        PENALTY_SAVING(SkillFoundation.TECHNIQUE, "Goalkeeper specific: Penalties."), // Included for completeness
        REFLEXES(SkillFoundation.TECHNIQUE, "Goalkeeper specific: Reaction saves.")
    }
}
