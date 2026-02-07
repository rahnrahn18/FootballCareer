package com.championstar.soccer.data.static

/**
 * BusinessDatabase
 *
 * Defines over 50 unique business ventures a player can invest in.
 * Each business has a specific cost, risk profile, and return on investment (ROI).
 */
object BusinessDatabase {

    enum class BusinessCategory {
        RETAIL, REAL_ESTATE, TECH, HOSPITALITY, FINANCE, ENTERTAINMENT, TRANSPORT, LUXURY
    }

    data class BusinessType(
        val id: String,
        val name: String,
        val description: String,
        val cost: Long,
        val roi: Double, // Annual Return on Investment (e.g., 0.10 = 10%)
        val risk: Double, // 0.0 (Safe) to 1.0 (Very Risky)
        val category: BusinessCategory
    )

    val businesses = listOf(
        // --- RETAIL ---
        BusinessType("RET_01", "Lemonade Stand", "A simple neighborhood stand.", 500, 0.05, 0.01, BusinessCategory.RETAIL),
        BusinessType("RET_02", "Corner Shop", "Local convenience store.", 50000, 0.08, 0.05, BusinessCategory.RETAIL),
        BusinessType("RET_03", "Clothing Boutique", "Trendy fashion outlet.", 150000, 0.12, 0.15, BusinessCategory.RETAIL),
        BusinessType("RET_04", "Supermarket Franchise", "Large chain grocery store.", 2000000, 0.06, 0.03, BusinessCategory.RETAIL),
        BusinessType("RET_05", "Sneaker Resell", "Buying and selling limited kicks.", 20000, 0.20, 0.25, BusinessCategory.RETAIL),
        BusinessType("RET_06", "Electronics Store", "Selling gadgets and gizmos.", 500000, 0.10, 0.10, BusinessCategory.RETAIL),
        BusinessType("RET_07", "Luxury Watch Dealer", "High-end timepieces.", 1000000, 0.15, 0.20, BusinessCategory.RETAIL),

        // --- REAL ESTATE ---
        BusinessType("RE_01", "Rental Apartment", "Single unit for monthly rent.", 200000, 0.05, 0.02, BusinessCategory.REAL_ESTATE),
        BusinessType("RE_02", "Duplex House", "Two-family home investment.", 450000, 0.06, 0.03, BusinessCategory.REAL_ESTATE),
        BusinessType("RE_03", "Commercial Office", "Office space for businesses.", 1500000, 0.07, 0.05, BusinessCategory.REAL_ESTATE),
        BusinessType("RE_04", "Luxury Villa", "High-end vacation rental.", 3000000, 0.09, 0.10, BusinessCategory.REAL_ESTATE),
        BusinessType("RE_05", "Apartment Complex", "Multi-unit residential building.", 10000000, 0.08, 0.04, BusinessCategory.REAL_ESTATE),
        BusinessType("RE_06", "Land Development", "Raw land for future construction.", 500000, 0.15, 0.30, BusinessCategory.REAL_ESTATE),
        BusinessType("RE_07", "Parking Garage", "City center parking structure.", 2500000, 0.06, 0.02, BusinessCategory.REAL_ESTATE),

        // --- TECH ---
        BusinessType("TEC_01", "Mobile App Startup", "A new social app.", 50000, 0.50, 0.80, BusinessCategory.TECH),
        BusinessType("TEC_02", "SaaS Company", "Software as a Service platform.", 250000, 0.25, 0.40, BusinessCategory.TECH),
        BusinessType("TEC_03", "E-Sports Team", "Competitive gaming organization.", 100000, 0.15, 0.30, BusinessCategory.TECH),
        BusinessType("TEC_04", "Crypto Mining Farm", "Hardware for cryptocurrency.", 75000, 0.30, 0.60, BusinessCategory.TECH),
        BusinessType("TEC_05", "AI Research Lab", "Cutting edge artificial intelligence.", 5000000, 0.40, 0.50, BusinessCategory.TECH),
        BusinessType("TEC_06", "Cybersecurity Firm", "Digital protection services.", 1000000, 0.12, 0.15, BusinessCategory.TECH),
        BusinessType("TEC_07", "Streaming Platform", "Video content service.", 8000000, 0.20, 0.40, BusinessCategory.TECH),

        // --- HOSPITALITY ---
        BusinessType("HOS_01", "Coffee Shop", "Artisan coffee and pastries.", 80000, 0.10, 0.15, BusinessCategory.HOSPITALITY),
        BusinessType("HOS_02", "Food Truck", "Mobile gourmet food.", 40000, 0.15, 0.20, BusinessCategory.HOSPITALITY),
        BusinessType("HOS_03", "Fast Food Franchise", "Burgers and fries chain.", 500000, 0.08, 0.05, BusinessCategory.HOSPITALITY),
        BusinessType("HOS_04", "Fine Dining Restaurant", "Michelin-star aspirations.", 1200000, 0.12, 0.25, BusinessCategory.HOSPITALITY),
        BusinessType("HOS_05", "Boutique Hotel", "Stylish accommodation.", 4000000, 0.09, 0.10, BusinessCategory.HOSPITALITY),
        BusinessType("HOS_06", "Nightclub", "Party venue in the city.", 1500000, 0.20, 0.30, BusinessCategory.HOSPITALITY),
        BusinessType("HOS_07", "Beach Resort", "Tropical getaway destination.", 15000000, 0.11, 0.15, BusinessCategory.HOSPITALITY),

        // --- FINANCE ---
        BusinessType("FIN_01", "Stock Portfolio", "Diversified market stocks.", 10000, 0.07, 0.10, BusinessCategory.FINANCE),
        BusinessType("FIN_02", "Angel Investing", "Funding early-stage startups.", 100000, 0.30, 0.70, BusinessCategory.FINANCE),
        BusinessType("FIN_03", "Hedge Fund", "High-risk managed fund.", 500000, 0.15, 0.25, BusinessCategory.FINANCE),
        BusinessType("FIN_04", "Micro-lending Firm", "Small loans service.", 200000, 0.10, 0.15, BusinessCategory.FINANCE),
        BusinessType("FIN_05", "Forex Trading", "Currency exchange speculation.", 50000, 0.25, 0.50, BusinessCategory.FINANCE),
        BusinessType("FIN_06", "Venture Capital", "Large scale startup funding.", 5000000, 0.20, 0.40, BusinessCategory.FINANCE),

        // --- ENTERTAINMENT ---
        BusinessType("ENT_01", "Record Label", "Music production company.", 300000, 0.15, 0.35, BusinessCategory.ENTERTAINMENT),
        BusinessType("ENT_02", "Film Studio", "Independent movie production.", 2000000, 0.18, 0.45, BusinessCategory.ENTERTAINMENT),
        BusinessType("ENT_03", "Bowling Alley", "Family entertainment center.", 800000, 0.08, 0.10, BusinessCategory.ENTERTAINMENT),
        BusinessType("ENT_04", "Theme Park", "Rides and attractions.", 25000000, 0.10, 0.15, BusinessCategory.ENTERTAINMENT),
        BusinessType("ENT_05", "Modeling Agency", "Talent management.", 150000, 0.12, 0.20, BusinessCategory.ENTERTAINMENT),
        BusinessType("ENT_06", "Event Planning", "Weddings and corporate events.", 60000, 0.15, 0.10, BusinessCategory.ENTERTAINMENT),

        // --- TRANSPORT ---
        BusinessType("TRA_01", "Taxi Fleet", "City cab service.", 200000, 0.10, 0.10, BusinessCategory.TRANSPORT),
        BusinessType("TRA_02", "Car Rental Agency", "Vehicle hire service.", 500000, 0.09, 0.12, BusinessCategory.TRANSPORT),
        BusinessType("TRA_03", "Logistics Company", "Freight and shipping.", 1000000, 0.08, 0.08, BusinessCategory.TRANSPORT),
        BusinessType("TRA_04", "Private Jet Charter", "Luxury air travel.", 5000000, 0.15, 0.25, BusinessCategory.TRANSPORT),
        BusinessType("TRA_05", "Yacht Charter", "Sea leisure rentals.", 3000000, 0.12, 0.20, BusinessCategory.TRANSPORT),

        // --- LUXURY ---
        BusinessType("LUX_01", "Vineyard", "Wine production estate.", 4000000, 0.06, 0.15, BusinessCategory.LUXURY),
        BusinessType("LUX_02", "Race Horse Stable", "Thoroughbred breeding.", 1000000, 0.10, 0.40, BusinessCategory.LUXURY),
        BusinessType("LUX_03", "Art Gallery", "Fine art dealership.", 800000, 0.08, 0.25, BusinessCategory.LUXURY),
        BusinessType("LUX_04", "Golf Course", "Private country club.", 12000000, 0.07, 0.10, BusinessCategory.LUXURY),
        BusinessType("LUX_05", "Diamond Mine", "Resource extraction.", 50000000, 0.25, 0.60, BusinessCategory.LUXURY)
    )

    fun getBusinessesByCategory(category: BusinessCategory) = businesses.filter { it.category == category }
    fun getBusinessById(id: String) = businesses.find { it.id == id }
}
