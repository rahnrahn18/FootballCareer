package com.championstar.soccer.data.static

import com.championstar.soccer.domain.models.Currency

data class Business(
    val id: String,
    val name: String,
    val description: String,
    val cost: Long,
    val weeklyIncome: Long,
    val riskFactor: Double, // 0.0 to 1.0 (Higher is riskier)
    val requiredReputation: Double = 0.0,
    val currency: Currency = Currency.STAR // Usually bought with cash/stars? Let's assume standard Money for now, but using Star for logic if needed. Actually, businesses usually cost Money.
    // Re-evaluating: The prompt implies business management. Let's assume they cost "Money" (which we need to track if not already).
    // Checking Player model: It has `marketValue`, `contract` (salary), but maybe not liquid `cash`?
    // Memory check: "5 Financial Pillars".
    // Let's add `cash` to Player model if missing, or use `stars` as a proxy for "Wealth/Lifestyle" points.
    // For now, let's stick to a generic cost and assume we'll use a `cash` field or similar.
)

object BusinessDatabase {

    val businesses = listOf(
        Business("b1", "Lemonade Stand", "A humble start.", 500, 50, 0.05),
        Business("b2", "Paper Route", "Classic hustle.", 1000, 100, 0.05),
        Business("b3", "Car Wash", "Local favorite.", 5000, 400, 0.1),
        Business("b4", "Vending Machine", "Passive income.", 8000, 600, 0.1),
        Business("b5", "Food Truck", "Hipster tacos.", 15000, 1200, 0.2),
        Business("b6", "Laundromat", "Steady cash flow.", 25000, 1800, 0.15),
        Business("b7", "Online Course", "Teach your skills.", 10000, 1500, 0.3),
        Business("b8", "Coffee Shop", "Brewing success.", 50000, 3000, 0.2),
        Business("b9", "Sneaker Resell", "High fashion flip.", 20000, 2000, 0.4),
        Business("b10", "Gym Franchise", "Get fit, get rich.", 100000, 6000, 0.25),
        Business("b11", "Nightclub", "Party all night.", 250000, 15000, 0.5),
        Business("b12", "E-Sports Team", "Gaming investments.", 150000, 10000, 0.6),
        Business("b13", "Restaurant", "Fine dining.", 300000, 18000, 0.4),
        Business("b14", "Real Estate - Apt", "Rental income.", 500000, 25000, 0.1),
        Business("b15", "Tech Startup", "The next big app?", 200000, 50000, 0.8), // High risk high reward
        Business("b16", "Clothing Brand", "Your own label.", 400000, 22000, 0.3),
        Business("b17", "Energy Drink", "Fuel your fans.", 600000, 35000, 0.3),
        Business("b18", "Hotel Chain", "Luxury stays.", 1000000, 50000, 0.2),
        Business("b19", "Record Label", "Music mogul.", 800000, 45000, 0.5),
        Business("b20", "Golf Course", "Elite networking.", 2000000, 80000, 0.2),
        Business("b21", "Airline", "Fly the skies.", 5000000, 150000, 0.4),
        Business("b22", "Theme Park", "Fun for all.", 8000000, 200000, 0.3),
        Business("b23", "Film Studio", "Blockbuster hits.", 4000000, 120000, 0.6),
        Business("b24", "Casino", "House always wins.", 10000000, 300000, 0.5),
        Business("b25", "Space Agency", "To the moon.", 50000000, 1000000, 0.7),
        Business("b26", "Social Network", "Connect the world.", 25000000, 800000, 0.6),
        Business("b27", "Electric Car Co", "Future drive.", 30000000, 900000, 0.5),
        Business("b28", "Football Academy", "Train the next you.", 1000000, 40000, 0.1),
        Business("b29", "Private Island", "Ultimate luxury.", 15000000, 0, 0.0), // No income, just flex? Or tourism? Let's say tourism. 400k.
        Business("b30", "Diamond Mine", "Rock solid.", 100000000, 2500000, 0.3)
    )
}
