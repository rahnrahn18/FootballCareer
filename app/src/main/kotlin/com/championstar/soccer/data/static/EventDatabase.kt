package com.championstar.soccer.data.static

import com.championstar.soccer.simulation.engine.EventChoice
import com.championstar.soccer.simulation.engine.RandomEvent
import com.championstar.soccer.domain.models.Player
import kotlin.random.Random

object EventDatabase {

    private val random = Random(System.currentTimeMillis())

    // --- MATCH EVENTS (50) ---
    val matchEvents = listOf(
        // Pre-Match Jitters
        RandomEvent("Pre-Match Nerves", "You feel butterflies in your stomach before the big game.", listOf(
            EventChoice("Meditate") { p -> p.morale += 2.0; "You find your center. Morale +2." },
            EventChoice("Listen to Music") { p -> p.form += 1.0; "Pumped up! Form +1." }
        )),
        RandomEvent("Locker Room Speech", "The captain is giving a rousing speech.", listOf(
            EventChoice("Cheer Along") { p -> p.morale += 3.0; "Team spirit is high! Morale +3." },
            EventChoice("Stay Focused") { p -> p.form += 2.0; "Zone in. Form +2." }
        )),
        RandomEvent("Tactical Dilemma", "Coach asks your opinion on the formation.", listOf(
            EventChoice("Suggest Attack") { p -> p.overallRating += 0.1; "Coach appreciates the input. Skill +0.1." },
            EventChoice("Stay Silent") { p -> p.morale += 1.0; "Keep your head down. Morale +1." }
        )),
        RandomEvent("Media Circus", "Reporters swarm the team bus.", listOf(
            EventChoice("Give Interview") { p -> p.reputation += 0.5; "Fans love it! Rep +0.5." },
            EventChoice("Ignore Them") { p -> p.form += 1.0; "Focus on the game. Form +1." }
        )),
        RandomEvent("Fan Interaction", "A young fan asks for an autograph.", listOf(
            EventChoice("Sign It") { p -> p.reputation += 0.2; p.morale += 2.0; "Made their day! Rep +0.2, Morale +2." },
            EventChoice("Rush Past") { p -> p.morale -= 1.0; "Felt bad but needed focus. Morale -1." }
        )),
        RandomEvent("Kit Issue", "Your kit is missing a sock.", listOf(
            EventChoice("Borrow from Reserve") { p -> p.morale -= 1.0; "Embarrassing but fixed. Morale -1." },
            EventChoice("Complain to Kitman") { p -> p.reputation -= 0.1; "Looks unprofessional. Rep -0.1." }
        )),
        RandomEvent("Rainy Day", "It's pouring rain outside.", listOf(
            EventChoice("Wear Long Studs") { p -> p.form += 2.0; "Better grip. Form +2." },
            EventChoice("Normal Boots") { p -> p.stamina -= 5.0; "Slipping everywhere. Stamina -5." }
        )),
        RandomEvent("Rival Taunts", "Opposing fans are chanting your name.", listOf(
            EventChoice("Ignore It") { p -> p.morale += 1.0; "Thick skin. Morale +1." },
            EventChoice("Respond on Pitch") { p -> p.form += 3.0; "Fuel for the fire! Form +3." }
        )),
        RandomEvent("Late Arrival", "Traffic made you late to the stadium.", listOf(
            EventChoice("Apologize Profusely") { p -> p.morale -= 2.0; "Coach is annoyed. Morale -2." },
            EventChoice("Blame Traffic") { p -> p.reputation -= 0.2; "Excuses look bad. Rep -0.2." }
        )),
        RandomEvent("New Boots", "Sponsor sent new prototype boots.", listOf(
            EventChoice("Wear Them") { p -> p.reputation += 0.5; p.form -= 1.0; "Look cool, feel weird. Rep +0.5, Form -1." },
            EventChoice("Stick to Old Pair") { p -> p.form += 1.0; "Comfort is key. Form +1." }
        )),
        RandomEvent("Scout in Stands", "Rumor has it a big club scout is watching.", listOf(
            EventChoice("Play Selfishly") { p -> p.form += 2.0; p.morale -= 2.0; "Showboating worked? Form +2, Morale -2." },
            EventChoice("Play for Team") { p -> p.reputation += 0.3; "Mature performance. Rep +0.3." }
        )),
        RandomEvent("Half-Time Oranges", "Traditional snack at half-time.", listOf(
            EventChoice("Eat One") { p -> p.stamina += 5.0; "Refreshed! Stamina +5." },
            EventChoice("Skip It") { p -> "You stay hungry."; "No effect." }
        )),
        RandomEvent("Referee Dispute", "Ref makes a bad call against a teammate.", listOf(
            EventChoice("Argue") { p -> p.morale += 2.0; p.reputation -= 0.1; "Defended teammate. Morale +2, Rep -0.1." },
            EventChoice("Calm Down") { p -> p.form += 1.0; "Stayed cool. Form +1." }
        )),
        RandomEvent("Penalty Practice", "Coach wants you to practice penalties.", listOf(
            EventChoice("Stay Late") { p -> p.stamina -= 5.0; p.overallRating += 0.2; "Nailed it. Skill +0.2, Stamina -5." },
            EventChoice("Go Home") { p -> p.stamina += 2.0; "Rest is important. Stamina +2." }
        )),
        RandomEvent("Social Media Post", "Team social media wants a pre-game selfie.", listOf(
            EventChoice("Do It") { p -> p.reputation += 0.2; "Likes rolling in. Rep +0.2." },
            EventChoice("Decline") { p -> p.form += 0.5; "Focused. Form +0.5." }
        )),
        RandomEvent("Captain's Armband", "Captain is injured, you're offered the band.", listOf(
            EventChoice("Accept with Pride") { p -> p.morale += 5.0; p.reputation += 1.0; "Leading the team! Morale +5, Rep +1." },
            EventChoice("Pass to Senior") { p -> p.morale += 2.0; "Respect shown. Morale +2." }
        )),
        RandomEvent("Dirty Tackle", "Opponent tackles you hard in warm-up.", listOf(
            EventChoice("Get Angry") { p -> p.form += 2.0; "Adrenaline spike. Form +2." },
            EventChoice("Shake it Off") { p -> p.stamina += 2.0; "Saved energy. Stamina +2." }
        )),
        RandomEvent("Sponsor Request", "Sponsor wants you to wear a specific headband.", listOf(
            EventChoice("Wear It") { p -> p.reputation += 0.5; "Happy sponsor. Rep +0.5." },
            EventChoice("Refuse") { p -> p.morale += 1.0; "Not a billboard. Morale +1." }
        )),
        RandomEvent("Bad Hotel", "Away game hotel was noisy.", listOf(
            EventChoice("Complain") { p -> p.morale -= 1.0; "Didnt help sleep. Morale -1." },
            EventChoice("Use Earplugs") { p -> p.stamina += 2.0; "Slept okay. Stamina +2." }
        )),
        RandomEvent("Old Friend", "An old friend is playing for the opposition.", listOf(
            EventChoice("Chat Before Game") { p -> p.morale += 3.0; "Good to see them. Morale +3." },
            EventChoice("Ignore Until After") { p -> p.form += 1.0; "Game face on. Form +1." }
        )),
         RandomEvent("Hydration Issue", "Water bottles are warm.", listOf(
            EventChoice("Drink Anyway") { p -> p.stamina -= 1.0; "Gross. Stamina -1." },
            EventChoice("Find Cold Water") { p -> p.morale += 1.0; "Worth the effort. Morale +1." }
        )),
        RandomEvent("Tactical Shift", "Coach changes tactics last minute.", listOf(
            EventChoice("Adapt Quickly") { p -> p.overallRating += 0.1; "Smart player. Skill +0.1." },
            EventChoice("Stick to Plan") { p -> p.form -= 1.0; "Confused on pitch. Form -1." }
        )),
        RandomEvent("Intimidation", "Opponent defender stares you down.", listOf(
            EventChoice("Stare Back") { p -> p.morale += 2.0; "Not scared. Morale +2." },
            EventChoice("Laugh") { p -> p.reputation += 0.2; "Confidence! Rep +0.2." }
        )),
        RandomEvent("Ball Boy Delay", "Ball boy is slow returning the ball.", listOf(
            EventChoice("Shout") { p -> p.reputation -= 0.2; "Looked mean. Rep -0.2." },
            EventChoice("Wait Patiently") { p -> p.morale += 1.0; "Zen. Morale +1." }
        )),
        RandomEvent("Loose Shoelace", "Shoelace untied during counter attack.", listOf(
            EventChoice("Stop to Tie") { p -> p.form -= 2.0; "Coach is furious. Form -2." },
            EventChoice("Keep Running") { p -> p.stamina -= 2.0; "Tripped a bit. Stamina -2." }
        )),
        RandomEvent("Crowd Whistle", "Crowd whistles when you touch the ball.", listOf(
            EventChoice("Embrace the Hate") { p -> p.form += 2.0; "Villain arc. Form +2." },
            EventChoice("Let it Affect You") { p -> p.morale -= 2.0; "Nerves. Morale -2." }
        )),
        RandomEvent("Corner Kick Duty", "Teammate offers you the corner kick.", listOf(
            EventChoice("Take It") { p -> p.overallRating += 0.1; "Good practice. Skill +0.1." },
            EventChoice("Let Specialist Take") { p -> p.morale += 1.0; "Team first. Morale +1." }
        )),
        RandomEvent("VAR Check", "Long VAR check on your goal.", listOf(
            EventChoice("Celebrate Anyway") { p -> p.morale += 2.0; "Confidence! Morale +2." },
            EventChoice("Wait Anxiously") { p -> p.stamina -= 1.0; "Stressful. Stamina -1." }
        )),
        RandomEvent("Substitute Warmup", "Sent to warm up in 80th minute.", listOf(
            EventChoice("Sprint Hard") { p -> p.stamina -= 2.0; p.form += 1.0; "Ready to go! Form +1." },
            EventChoice("Jog Lazily") { p -> p.morale -= 1.0; "Coach noticed. Morale -1." }
        )),
        RandomEvent("Post-Match Interview", "Man of the Match interview.", listOf(
            EventChoice("Praise Teammates") { p -> p.morale += 3.0; "Class act. Morale +3." },
            EventChoice("Take Credit") { p -> p.reputation += 1.0; "Star power. Rep +1." }
        )),
         RandomEvent("Ice Bath", "Mandatory ice bath after game.", listOf(
            EventChoice("Jump In") { p -> p.stamina += 5.0; p.morale -= 1.0; "Freezing but good. Stamina +5." },
            EventChoice("Skip It") { p -> p.stamina -= 2.0; "Sore muscles tomorrow. Stamina -2." }
        )),
        RandomEvent("Fan Mail", "Received a letter from a fan.", listOf(
            EventChoice("Read It") { p -> p.morale += 2.0; "Heartwarming. Morale +2." },
            EventChoice("Ignore") { p -> "Too busy."; "No effect." }
        )),
        RandomEvent("Nutmeg Opportunity", "Defender legs are open.", listOf(
            EventChoice("Try Nutmeg") { p -> p.reputation += 0.5; p.form += 1.0; "Olé! Rep +0.5, Form +1." },
            EventChoice("Safe Pass") { p -> p.overallRating += 0.1; "Smart play. Skill +0.1." }
        )),
        RandomEvent("Offside Trap", "Team trying high line.", listOf(
            EventChoice("Coordinate") { p -> p.overallRating += 0.1; "Good comms. Skill +0.1." },
            EventChoice("Focus on Man") { p -> p.form -= 0.5; "Broke the line. Form -0.5." }
        )),
        RandomEvent("Goalkeeper Shout", "Keeper shouting instructions.", listOf(
            EventChoice("Listen") { p -> p.overallRating += 0.1; "Defensive awareness up. Skill +0.1." },
            EventChoice("Ignore") { p -> p.morale -= 1.0; "Keeper annoyed. Morale -1." }
        )),
        RandomEvent("Slippery Pitch", "Pitch is overwatered.", listOf(
            EventChoice("Play Aerial") { p -> p.overallRating += 0.2; "Heading practice. Skill +0.2." },
            EventChoice("Play Ground") { p -> p.form -= 1.0; "Passes slowing down. Form -1." }
        )),
        RandomEvent("Tunnel Stare", "Opponent staring in tunnel.", listOf(
            EventChoice("Wink") { p -> p.morale += 2.0; "Psychological warfare. Morale +2." },
            EventChoice("Look Away") { p -> p.form += 0.5; "Focused. Form +0.5." }
        )),
        RandomEvent("Mascot High Five", "Mascot offers high five.", listOf(
            EventChoice("High Five") { p -> p.reputation += 0.1; "Good PR. Rep +0.1." },
            EventChoice("Walk Past") { p -> p.reputation -= 0.1; "Cold. Rep -0.1." }
        )),
        RandomEvent("Coin Toss", "Captain asks for heads or tails.", listOf(
            EventChoice("Heads") { p -> p.morale += 1.0; "Feeling lucky. Morale +1." },
            EventChoice("Tails") { p -> p.morale += 1.0; "Never fails. Morale +1." }
        )),
        RandomEvent("Lost Shinpad", "Can't find left shinpad.", listOf(
            EventChoice("Tape Cardboard") { p -> p.morale -= 2.0; "Desperate times. Morale -2." },
            EventChoice("Borrow Spare") { p -> p.morale += 1.0; "Teammate saved you. Morale +1." }
        )),
        RandomEvent("Drone Delay", "Drone flying over stadium stopped play.", listOf(
            EventChoice("Do Tricks") { p -> p.reputation += 0.5; "Crowd loves it. Rep +0.5." },
            EventChoice("Stretch") { p -> p.stamina += 1.0; "Stay loose. Stamina +1." }
        )),
        RandomEvent("Streaker", "Streaker on the pitch!", listOf(
            EventChoice("Laugh") { p -> p.morale += 2.0; "Funny break. Morale +2." },
            EventChoice("Look Away") { p -> p.form += 0.5; "Professional. Form +0.5." }
        )),
        RandomEvent("Laser Pointer", "Fan shining laser in eyes.", listOf(
            EventChoice("Complain to Ref") { p -> p.morale -= 1.0; "Distracting. Morale -1." },
            EventChoice("Ignore") { p -> p.overallRating += 0.1; "Focus training. Skill +0.1." }
        )),
        RandomEvent("Broken Net", "Goal net needs fixing.", listOf(
            EventChoice("Help Fix") { p -> p.reputation += 0.2; "Handyman. Rep +0.2." },
            EventChoice("Drink Water") { p -> p.stamina += 1.0; "Hydrate. Stamina +1." }
        )),
        RandomEvent("Dog on Pitch", "A stray dog runs on.", listOf(
            EventChoice("Pet Dog") { p -> p.reputation += 1.0; "Viral moment. Rep +1.0." },
            EventChoice("Chase Dog") { p -> p.stamina -= 2.0; "Tiring. Stamina -2." }
        )),
        RandomEvent("Floodlight Failure", "Lights go out for 10 mins.", listOf(
            EventChoice("Rest") { p -> p.stamina += 5.0; "Power nap. Stamina +5." },
            EventChoice("Chat with Opponent") { p -> p.reputation += 0.2; "Networking. Rep +0.2." }
        )),
         RandomEvent("Manager Scream", "Manager screaming from sideline.", listOf(
            EventChoice("Nod") { p -> p.morale += 1.0; "Acknowledged. Morale +1." },
            EventChoice("Shrug") { p -> p.morale -= 2.0; "Manager furious. Morale -2." }
        )),
        RandomEvent("Substitution Board", "Your number is up.", listOf(
            EventChoice("Slow Walk") { p -> p.stamina += 1.0; "Time wasting. Stamina +1." },
            EventChoice("Sprint Off") { p -> p.reputation += 0.2; "Professional. Rep +0.2." }
        )),
        RandomEvent("Final Whistle", "Game over.", listOf(
            EventChoice("Swap Shirts") { p -> p.reputation += 0.5; "Respect. Rep +0.5." },
            EventChoice("Clap Fans") { p -> p.morale += 2.0; "Connection. Morale +2." }
        )),
        RandomEvent("Doping Test", "Random selection for doping control.", listOf(
            EventChoice("Comply") { p -> p.stamina -= 2.0; "Late night. Stamina -2." },
            EventChoice("Grumble") { p -> p.morale -= 1.0; "Annoying. Morale -1." }
        ))
    )

    // --- FAMILY EVENTS (15) ---
    val familyEvents = listOf(
        RandomEvent("Family Dinner", "Mom wants a family dinner this Sunday.", listOf(
            EventChoice("Attend") { p -> p.morale += 5.0; p.stamina -= 2.0; "Great food, tired. Morale +5, Stamina -2." },
            EventChoice("Skip for Training") { p -> p.overallRating += 0.1; p.morale -= 2.0; "Mom is sad. Skill +0.1, Morale -2." }
        )),
        RandomEvent("Sister's Wedding", "Your sister is getting married!", listOf(
            EventChoice("Party Hard") { p -> p.morale += 10.0; p.stamina -= 10.0; "What a night! Morale +10, Stamina -10." },
            EventChoice("Leave Early") { p -> p.stamina += 5.0; "Responsible. Stamina +5." }
        )),
        RandomEvent("Loan Request", "Cousin wants a loan for a 'sure thing'.", listOf(
            EventChoice("Give Money") { p -> p.reputation += 0.1; "Generous. Rep +0.1." },
            EventChoice("Refuse") { p -> p.morale -= 1.0; "Awkward. Morale -1." }
        )),
        RandomEvent("New Pet", "Partner wants to get a dog.", listOf(
            EventChoice("Get Dog") { p -> p.morale += 5.0; "New best friend. Morale +5." },
            EventChoice("No Time") { p -> p.stamina += 1.0; "Less responsibility. Stamina +1." }
        )),
        RandomEvent("House Hunting", "Time to buy a bigger house?", listOf(
            EventChoice("Buy Mansion") { p -> p.reputation += 2.0; "Living the life. Rep +2." },
            EventChoice("Stay Humble") { p -> p.morale += 2.0; "Cozy. Morale +2." }
        )),
        RandomEvent("School Reunion", "High school reunion invite.", listOf(
            EventChoice("Go and Brag") { p -> p.reputation += 1.0; "Big shot. Rep +1." },
            EventChoice("Skip It") { p -> p.stamina += 2.0; "Rest day. Stamina +2." }
        )),
        RandomEvent("Partner's Job", "Partner got a job offer abroad.", listOf(
            EventChoice("Support Them") { p -> p.morale += 5.0; "Relationship strong. Morale +5." },
            EventChoice("Ask to Stay") { p -> p.morale -= 5.0; "Tension at home. Morale -5." }
        )),
        RandomEvent("Dad's Birthday", "Forgot Dad's birthday!", listOf(
            EventChoice("Buy Expensive Gift") { p -> p.morale += 2.0; "Saved it. Morale +2." },
            EventChoice("Call and Apologize") { p -> p.morale -= 1.0; "He understands. Morale -1." }
        )),
        RandomEvent("Holiday Planning", "Summer break is coming.", listOf(
            EventChoice("Ibiza Party") { p -> p.morale += 5.0; p.stamina -= 5.0; "Wild. Morale +5, Stamina -5." },
            EventChoice("Cabin in Woods") { p -> p.stamina += 10.0; "Recharged. Stamina +10." }
        )),
        RandomEvent("Baby on Way?", "Partner mentions having kids.", listOf(
            EventChoice("Excited") { p -> p.morale += 5.0; "Future planning. Morale +5." },
            EventChoice("Focus on Career") { p -> p.form += 2.0; "Not yet. Form +2." }
        )),
        RandomEvent("Family Business", "Uncle wants you to invest in his shop.", listOf(
            EventChoice("Invest") { p -> p.reputation += 0.5; "Supportive. Rep +0.5." },
            EventChoice("Decline") { p -> "Too risky."; "No effect." }
        )),
        RandomEvent("Grandma's Cooking", "Grandma sent a care package.", listOf(
            EventChoice("Eat it All") { p -> p.stamina += 5.0; "Carb loading. Stamina +5." },
            EventChoice("Share with Team") { p -> p.reputation += 0.5; "Generous. Rep +0.5." }
        )),
        RandomEvent("Childhood Room", "Parents cleaning out your old room.", listOf(
            EventChoice("Keep Trophies") { p -> p.morale += 2.0; "Nostalgia. Morale +2." },
            EventChoice("Throw Away") { p -> p.form += 1.0; "Look forward. Form +1." }
        )),
        RandomEvent("Family Feud", "Parents arguing.", listOf(
            EventChoice("Mediate") { p -> p.stamina -= 5.0; "Stressful. Stamina -5." },
            EventChoice("Stay Out") { p -> p.morale -= 2.0; "Guilt. Morale -2." }
        )),
        RandomEvent("Surprise Visit", "Parents visit unannounced.", listOf(
            EventChoice("Welcome Them") { p -> p.morale += 3.0; "Quality time. Morale +3." },
            EventChoice("Hide") { p -> p.stamina += 1.0; "Avoided drama. Stamina +1." }
        ))
    )

    // --- AGENT EVENTS (10) ---
    val agentEvents = listOf(
        RandomEvent("Boot Deal", "Nike wants to sponsor you.", listOf(
            EventChoice("Accept Deal") { p -> p.reputation += 1.0; "New gear! Rep +1.0." },
            EventChoice("Hold for Adidas") { p -> p.reputation += 0.1; "Playing hardball. Rep +0.1." }
        )),
        RandomEvent("Transfer Rumor", "Linked to a bigger club.", listOf(
            EventChoice("Deny Rumors") { p -> p.morale += 2.0; "Loyalty. Morale +2." },
            EventChoice("Fuel Fire") { p -> p.reputation += 1.0; "Fans talking. Rep +1.0." }
        )),
        RandomEvent("Agent Fee", "Agent demands higher commission.", listOf(
            EventChoice("Pay Up") { p -> p.morale -= 2.0; "Expensive. Morale -2." },
            EventChoice("Negotiate") { p -> p.overallRating += 0.1; "Business skills. Skill +0.1." }
        )),
        RandomEvent("Social Media Manager", "Agent suggests hiring a social media team.", listOf(
            EventChoice("Hire Them") { p -> p.reputation += 2.0; "Viral content. Rep +2.0." },
            EventChoice("Manage Yourself") { p -> p.morale += 1.0; "Authentic. Morale +1." }
        )),
        RandomEvent("Charity Event", "Agent booked a charity gala.", listOf(
            EventChoice("Attend") { p -> p.reputation += 1.5; p.stamina -= 2.0; "Good cause. Rep +1.5." },
            EventChoice("Skip") { p -> p.reputation -= 0.5; "Bad look. Rep -0.5." }
        )),
        RandomEvent("Magazine Cover", "GQ wants you on the cover.", listOf(
            EventChoice("Do Shoot") { p -> p.reputation += 3.0; "Superstar! Rep +3.0." },
            EventChoice("Focus on Football") { p -> p.form += 2.0; "Athlete first. Form +2." }
        )),
        RandomEvent("Contract Loophole", "Agent found a bonus clause you missed.", listOf(
            EventChoice("Claim Bonus") { p -> p.morale += 5.0; "Cash money. Morale +5." },
            EventChoice("Ignore") { p -> "Not worth hassle."; "No effect." }
        )),
        RandomEvent("Rival Agent", "Another agent tries to poach you.", listOf(
            EventChoice("Stay Loyal") { p -> p.morale += 2.0; "Agent is happy. Morale +2." },
            EventChoice("Listen to Offer") { p -> p.reputation += 0.5; "Business is business. Rep +0.5." }
        )),
        RandomEvent("TV Commercial", "Soft drink commercial offer.", listOf(
            EventChoice("Film It") { p -> p.reputation += 1.0; p.stamina -= 3.0; "Famous. Rep +1.0." },
            EventChoice("Refuse") { p -> p.stamina += 2.0; "Rested. Stamina +2." }
        )),
        RandomEvent("Book Deal", "Publisher wants your autobiography.", listOf(
            EventChoice("Write It") { p -> p.reputation += 2.0; "Bestseller. Rep +2.0." },
            EventChoice("Too Young") { p -> p.form += 1.0; "Story not over. Form +1." }
        ))
    )

    fun getByCategory(category: EventCategory): RandomEvent? {
        return when(category) {
            EventCategory.MATCH -> matchEvents.randomOrNull()
            EventCategory.FAMILY -> familyEvents.randomOrNull()
            EventCategory.AGENT -> agentEvents.randomOrNull()
            EventCategory.BUSINESS -> null // Handled separately
        }
    }

    enum class EventCategory {
        MATCH, FAMILY, AGENT, BUSINESS
    }
}
