package com.bahairesources.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.graphics.Color
import androidx.cardview.widget.CardView
import android.content.Intent

class PrayersActivity : AppCompatActivity() {
    
    private lateinit var categoryLayout: LinearLayout
    private var isDarkMode: Boolean = false
    private var currentFontSize: Float = SettingsManager.FONT_MEDIUM
    
    // Vastly expanded prayer categories with many more prayers
    private val prayerCategories = listOf(
        PrayerCategory("🌅 Morning Prayers", "Start your day with divine guidance", listOf(
            Prayer("Morning Communion", "Bahá'u'lláh", "I have wakened this morning by Thy grace, O my God, and found myself lying at the door of Thy mercy. Arise, O my soul, and hasten to His court of holiness, and say: O my God! I have turned my face unto Thy face, and I have turned away from all else save Thee."),
            Prayer("Dawn Prayer", "Bahá'u'lláh", "Praised be Thou, O Lord my God! I implore Thee, by Thy Most Great Name, to open mine eyes that I may behold Thy wondrous signs, and incline mine ear that I may hear the melodies of the birds of heaven, chanting the praise of Thy unity."),
            Prayer("Prayer for Divine Assistance", "Bahá'u'lláh", "O God! Refresh and gladden my spirit. Purify my heart. Illumine my powers. I lay all my affairs in Thy hand. Thou art my Guide and my Refuge. I will no longer be sorrowful and grieved; I will be a happy and joyful being."),
            Prayer("Morning Light", "Abdul-Bahá", "O my God! As the dawn breaketh and the Divine Sun riseth from the horizon of mercy, kindle the light of love in my heart and illumine my sight with beholding the splendors of Thy face.")
        )),
        PrayerCategory("🌙 Evening Prayers", "Reflect and give thanks as day ends", listOf(
            Prayer("Evening Gratitude", "Bahá'u'lláh", "I have attained unto Thee, O my God, after I had striven long to approach the sanctuary of Thy presence. All praise be to Thee for having enabled me to turn my face towards Thee, after I had been hindered by the veils that have intervened between Thee and Thy creatures."),
            Prayer("Night's Refuge", "Bahá'u'lláh", "All praise be to Thee, O my God, for having awakened me out of my sleep, and brought me forth after my disappearance, and raised me up from my slumber. I have wakened this morning with my face set towards the splendors of the Day-Star of Thy Revelation."),
            Prayer("Evening Reflection", "The Báb", "I call upon Thee, O Lord my God, to bear me witness that this is the Day whereon I have fixed my heart upon Thee, and severed it from all else but Thee, and proclaimed unto all the peoples of the earth the signs of Thy unity."),
            Prayer("Sunset Devotion", "Abdul-Bahá", "O my Lord! As the shades of evening fall and the stars appear in the heavens, I turn my heart to Thee in gratitude for the blessings of this day.")
        )),
        PrayerCategory("🍽️ Prayers Before Meals", "Thanksgiving for sustenance", listOf(
            Prayer("Blessed is Thou, O God", "Bahá'u'lláh", "Blessed is Thou, O God, as Thou hast nourished my body with earthly food, nourish my soul with heavenly food from the table of Thy bounty."),
            Prayer("Prayer of Sustenance", "Bahá'u'lláh", "He is God! Thou hast given us food for sustenance and water for drink. We render thanks unto Thee, O God. Blessing and praise be unto Thee, and may Thy mercy and loving-kindness rest upon all mankind."),
            Prayer("Grace at Table", "Abdul-Bahá", "O Thou kind God! We are seated at Thy table and are delighting in Thy bounty. We are the birds of Thy meadow and are warbling in Thy rose garden."),
            Prayer("Gratitude for Provisions", "Bahá'u'lláh", "I have tasted of the honey of reunion with Thee, and of the wine of Thy presence. All the atoms of the earth have I found to be the recipients of the splendors of Thy face.")
        )),
        PrayerCategory("🌍 Prayers for Humanity", "Unity of mankind and world peace", listOf(
            Prayer("Prayer for Mankind", "Bahá'u'lláh", "O Thou kind Lord! Thou hast created all humanity from the same stock. Thou hast decreed that all shall belong to the same household. In thy Holy Presence they are all Thy servants, and all mankind are sheltered beneath Thy Tabernacle."),
            Prayer("Unity of Hearts", "Abdul-Bahá", "O my God! O my God! Unite the hearts of Thy servants, and reveal to them Thy great purpose. May they follow Thy commandments and abide in Thy law. Help them, O God, in their endeavor, and grant them strength to serve Thee."),
            Prayer("World Peace", "Bahá'u'lláh", "The time must come when the imperative necessity for the holding of a vast, an all-embracing assemblage of men will be universally realized. Be united, O kings of the earth, for thereby will the tempest of discord be stilled amongst you."),
            Prayer("For All Nations", "Abdul-Bahá", "O ye peoples of the world! The religion of God is for love and unity; make it not the cause of enmity or dissension.")
        )),
        PrayerCategory("🙏 Obligatory Prayers", "Daily spiritual duties", listOf(
            Prayer("Short Obligatory Prayer", "Bahá'u'lláh", "I bear witness, O my God, that Thou hast created me to know Thee and to worship Thee. I testify, at this moment, to my powerlessness and to Thy might, to my poverty and to Thy wealth. There is none other God but Thee, the Help in Peril, the Self-Subsisting. (To be said once in twenty-four hours, at noon)"),
            Prayer("Medium Obligatory Prayer", "Bahá'u'lláh", "Whoso wisheth to pray, let him wash his hands, and while he washeth, let him say: Strengthen my hand, O my God, that it may take hold of Thy Book with such steadfastness that the hosts of the worlds shall be powerless to take it out of my grasp..."),
            Prayer("Long Obligatory Prayer", "Bahá'u'lláh", "Whoso wisheth to recite this prayer, let him stand up and turn unto God, and, as he standeth in his place, let him gaze to the right and to the left, as if awaiting the mercy of his Lord, the Most Merciful, the Compassionate...")
        )),
        PrayerCategory("💝 Prayers for Special Occasions", "Life events and milestones", listOf(
            Prayer("For the Departed", "Bahá'u'lláh", "O my God! O Thou forgiver of sins, bestower of gifts, dispeller of afflictions! Verily, I beseech Thee to forgive the sins of such as have abandoned the physical garment and have ascended to the spiritual world."),
            Prayer("For the Sick", "Bahá'u'lláh", "Thy name is my healing, O my God, and remembrance of Thee is my remedy. Nearness to Thee is my hope, and love for Thee is my companion. Thy mercy to me is my healing and my succor in both this world and the world to come."),
            Prayer("For Children", "Abdul-Bahá", "O Thou peerless Lord! Grant that these children may be reared in Thy faith and Thy love, and grant that they may grow to become radiant stars in the heaven of knowledge."),
            Prayer("For Marriage", "Bahá'u'lláh", "He is God! O my Lord! O my Lord! These two bright stars have been joined together in Thy love, in Thy care and under Thy protection. May they ever be united. May their union be everlasting."),
            Prayer("For Youth", "Abdul-Bahá", "O ye spiritual youth! Thanks be to God that ye have obtained such blessings and have been blessed with such bounty in the world of existence."),
            Prayer("For Tests and Difficulties", "Bahá'u'lláh", "O God, my God! Thou seest me detached from all else but Thee, holding fast unto Thee and turning toward the ocean of Thy bounty, unto the heaven of Thy favor, unto the Daystar of Thy grace.")
        )),
        PrayerCategory("🕊️ Prayers for Forgiveness", "Seeking divine mercy", listOf(
            Prayer("Prayer of Forgiveness", "Bahá'u'lláh", "O God! Thou hast inspired my soul to offer its supplication to Thee, and but for Thee, I would not call upon Thee. Lauded and glorified art Thou; I yield Thee praise inasmuch as Thou hast made Thyself known unto me."),
            Prayer("Seeking Pardon", "The Báb", "I implore Thee, by Thy mercy that hath surpassed the mercy of all who show mercy, to forgive me my sins and to have compassion upon me. Thou art, verily, the Most Generous, the All-Forgiving, the Most Compassionate."),
            Prayer("Divine Mercy", "Bahá'u'lláh", "I know not, O my God, what the water is with which Thou hast created me, or what the fire Thou hast kindled within me, or what the clay whereof Thou hast kneaded me."),
            Prayer("Remorse and Return", "Abdul-Bahá", "O my God! I have turned my back to Thee many times, yet Thy loving-kindness hath watched over me. I have committed many sins, yet Thou hast covered me with the cloak of Thy mercy.")
        )),
        PrayerCategory("✨ Prayers for Spiritual Growth", "Seeking divine attributes", listOf(
            Prayer("Spiritual Progress", "Bahá'u'lláh", "O my God! O my God! Verily, these servants are turning to Thee, supplicating Thy kingdom of mercy. Verily, they are attracted by Thy holiness and set aglow with the fire of Thy love."),
            Prayer("Divine Confirmation", "Abdul-Bahá", "O Thou forgiving Lord! Although certain souls have spent the days of their lives in ignorance, and have destroyed their precious time in pursuing their carnal desires, yet the ocean of Thy forgiveness is, verily, able to redeem and purify them."),
            Prayer("Wisdom and Knowledge", "Bahá'u'lláh", "O my Lord! Make Thy beauty to be my food, and Thy presence my drink, and Thy pleasure my hope, and praise of Thee my action, and remembrance of Thee my companion."),
            Prayer("Divine Qualities", "The Báb", "O my God! Assist me to be faithful, and guard me from all that might lead me astray from the path of Thy good-pleasure. Help me to exemplify the divine attributes and to manifest the signs of Thy unity."),
            Prayer("Turning to God", "Bahá'u'lláh", "I have turned my face unto Thy face, and turned away from all else save Thee, and approached the sanctuary of Thy presence, and walked in the shadow of Thy mercy.")
        )),
        PrayerCategory("🌟 Prayers for Guidance", "Seeking divine direction", listOf(
            Prayer("Divine Guidance", "Bahá'u'lláh", "O Thou Who art the Lord of all names and the Maker of the heavens! I beseech Thee by them Who are the Daysprings of Thine invisible Essence, the Most Exalted, the All-Glorious, to make of my prayer a fire that will burn away the veils."),
            Prayer("Seeking Direction", "Abdul-Bahá", "O Divine Providence! This assemblage is composed of Thy servants who are turning to Thy kingdom and are in need of Thy bestowal and blessing. We are weak and Thou art mighty. We are poor and Thou art rich."),
            Prayer("Light of Truth", "The Báb", "Guide me, O my Lord, unto the path of Thy good-pleasure, and enable me to be steadfast in my love for Thee. Thou art, verily, the All-Powerful, the Most High."),
            Prayer("Spiritual Illumination", "Bahá'u'lláh", "Create in me a pure heart, O my God, and renew a tranquil conscience within me, O my Hope! Through the spirit of power confirm Thou me in Thy Cause, O my Best-Beloved.")
        )),
        PrayerCategory("🌈 Prayers for Assistance", "Divine help in times of need", listOf(
            Prayer("Remover of Difficulties", "The Báb", "Is there any Remover of difficulties save God? Say: Praised be God! He is God! All are His servants, and all abide by His bidding!"),
            Prayer("Divine Aid", "Bahá'u'lláh", "O God! Refresh and gladden my spirit. Purify my heart. Illumine my powers. I lay all my affairs in Thy hand. Thou art my Guide and my Refuge."),
            Prayer("In Times of Crisis", "Abdul-Bahá", "O Thou kind Lord! Thou hast promised that whosoever will turn his face toward Thee and will trust in Thee, verily Thou wilt suffice him and will be his helper and his supporter."),
            Prayer("Spiritual Strength", "Bahá'u'lláh", "Armed with the power of Thy name nothing can ever hurt me, and with Thy love in my heart all the world's afflictions can in no wise alarm me.")
        )),
        PrayerCategory("🎵 Prayers and Tablets", "Sacred writings for reflection", listOf(
            Prayer("Tablet of Ahmad", "Bahá'u'lláh", "He is the King, the All-Knowing, the Wise! Lo, the Nightingale of Paradise singeth upon the twigs of the Tree of Eternity, with holy and sweet melodies, proclaiming to the sincere ones the glad tidings of the nearness of God..."),
            Prayer("Fire Tablet", "Bahá'u'lláh", "In the Name of God, the Most Ancient, the Most Great. Indeed the hearts of the sincere are consumed in the fire of separation: Where is the gleaming of the light of Thy Countenance, O Beloved of the worlds?"),
            Prayer("Tablet of the Holy Mariner", "Bahá'u'lláh", "He is God, exalted is He, the Lord of might and grandeur! O Holy Mariner! Bid thine ark of eternity appear before the Celestial Concourse, Launch it upon the ancient sea..."),
            Prayer("Long Healing Prayer", "Bahá'u'lláh", "He is the Healer, the Sufficer, the Helper, the All-Forgiving, the All-Merciful. I call upon Thee O Exalted One, O Faithful One, O Glorious One! Thou the Sufficing One!")
        )),
        PrayerCategory("🏠 Prayers for the Home", "Blessing the household", listOf(
            Prayer("Blessing for the Home", "Abdul-Bahá", "O my Lord! This house is turning to Thee, the inhabitants thereof are turning to Thy Kingdom, and its people are submissive before Thy singleness. O Lord! Send down upon it Thy blessing and let angels of mercy dwell therein."),
            Prayer("Family Unity", "Bahá'u'lláh", "Blessed is the house that hath attained unto My tender mercy, wherein My remembrance is celebrated, and which is ennobled by the presence of My loved ones, who have proclaimed My praise, cleaved fast to the cord of My grace, and been honored by chanting My verses."),
            Prayer("Protection of Family", "Abdul-Bahá", "O Thou kind God! These are Thy servants who have gathered in this meeting, turned toward Thy Kingdom and are in need of Thy bestowal and blessing. O God! Be compassionate to them and make them confident in Thee.")
        )),
        PrayerCategory("💼 Prayers for Work and Service", "Divine assistance in endeavors", listOf(
            Prayer("Prayer for Work", "Bahá'u'lláh", "O my God! I ask Thee, by Thy most glorious Name, to aid Thy servants to hearken unto Thy most holy Word, that haply they may all be guided to Thy Truth."),
            Prayer("Service to Humanity", "Abdul-Bahá", "O God! Make me a servant of Thy threshold, a spreader of Thy teachings and a promoter of understanding among Thy people. Enable me to serve Thy Cause and to work for the good of humanity."),
            Prayer("Divine Confirmation in Service", "Bahá'u'lláh", "O Lord! Assist those who have renounced all else but Thee, and grant them a mighty victory. Send down upon them, O Lord, the concourse of the angels in heaven and earth and all that is between them.")
        )),
        PrayerCategory("🌱 Prayers for Tests and Trials", "Strength through difficulties", listOf(
            Prayer("Through Difficulties", "Bahá'u'lláh", "O God, my God! Thou seest me detached from all else but Thee, holding fast unto Thee and turning toward the ocean of Thy bounty, unto the heaven of Thy favor, unto the Daystar of Thy grace."),
            Prayer("Patience in Tests", "The Báb", "O my God! O my God! All are but poor and needy, and Thou alone art the All-Possessing, the All-Subduing. Have mercy, then, upon us through the wonders of Thy grace and bounty."),
            Prayer("Steadfastness", "Abdul-Bahá", "O Thou forgiving Lord! These servants have turned to Thy Kingdom and have received Thy bestowal. They have obtained honor in Thy Cause and have drunk from the chalice of Thy favor.")
        ))
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize settings
        isDarkMode = SettingsManager.isDarkMode(this)
        currentFontSize = SettingsManager.getFontSize(this)
        
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 30, 20, 30)
            setBackgroundColor(if (isDarkMode) Color.parseColor("#121212") else Color.parseColor("#F8F9FA"))
        }
        
        // Header
        val headerLayout = createHeader()
        
        // Statistics card
        val statsCard = createStatsCard()
        
        // Info card
        val infoCard = createInfoCard()
        
        // Search functionality
        val searchCard = createSearchCard()
        
        // Categories container
        categoryLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        
        mainLayout.addView(headerLayout)
        mainLayout.addView(statsCard)
        mainLayout.addView(createSpacing(15))
        mainLayout.addView(infoCard)
        mainLayout.addView(createSpacing(15))
        mainLayout.addView(searchCard)
        mainLayout.addView(createSpacing(20))
        mainLayout.addView(categoryLayout)
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
        
        // Display prayer categories
        displayPrayerCategories()
    }
    
    private fun createHeader(): LinearLayout {
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(10, 10, 10, 30)
        }
        
        val backButton = Button(this).apply {
            text = "← Back"
            setBackgroundColor(if (isDarkMode) Color.parseColor("#1565C0") else Color.parseColor("#1976D2"))
            setTextColor(Color.WHITE)
            setPadding(20, 10, 20, 10)
            textSize = currentFontSize
            setOnClickListener { finish() }
        }
        
        val titleText = TextView(this).apply {
            text = "🙏 Prayers Collection"
            textSize = currentFontSize + 4f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setPadding(20, 10, 0, 10)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val settingsButton = Button(this).apply {
            text = "⚙️"
            textSize = currentFontSize
            setBackgroundColor(if (isDarkMode) Color.parseColor("#37474F") else Color.parseColor("#E0E0E0"))
            setTextColor(if (isDarkMode) Color.WHITE else Color.parseColor("#333333"))
            setPadding(15, 10, 15, 10)
            setOnClickListener {
                startActivity(Intent(this@PrayersActivity, SettingsActivity::class.java))
            }
        }
        
        headerLayout.addView(backButton)
        headerLayout.addView(titleText)
        headerLayout.addView(settingsButton)
        return headerLayout
    }
    
    private fun createStatsCard(): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.parseColor("#E3F2FD"))
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(20, 15, 20, 15)
        }
        
        val totalPrayers = prayerCategories.sumOf { it.prayers.size }
        val categoryCount = prayerCategories.size
        
        val statsText = TextView(this).apply {
            text = "📊 $totalPrayers prayers across $categoryCount categories"
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#81C784") else Color.parseColor("#1976D2"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val randomButton = Button(this).apply {
            text = "🎲 Random"
            textSize = currentFontSize - 2f
            setBackgroundColor(if (isDarkMode) Color.parseColor("#4CAF50") else Color.parseColor("#2E7D32"))
            setTextColor(Color.WHITE)
            setPadding(15, 8, 15, 8)
            setOnClickListener { showRandomPrayer() }
        }
        
        layout.addView(statsText)
        layout.addView(randomButton)
        card.addView(layout)
        return card
    }
    
    private fun createSearchCard(): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.parseColor("#FFFFFF"))
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(20, 15, 20, 15)
        }
        
        val searchIcon = TextView(this).apply {
            text = "🔍"
            textSize = currentFontSize + 2f
            setPadding(0, 0, 15, 0)
        }
        
        val searchInfo = TextView(this).apply {
            text = "Search through prayers by author, keyword, or purpose"
            textSize = currentFontSize - 1f
            setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val searchButton = Button(this).apply {
            text = "Search"
            textSize = currentFontSize - 1f
            setBackgroundColor(if (isDarkMode) Color.parseColor("#1565C0") else Color.parseColor("#1976D2"))
            setTextColor(Color.WHITE)
            setPadding(15, 8, 15, 8)
            setOnClickListener { showSearchDialog() }
        }
        
        layout.addView(searchIcon)
        layout.addView(searchInfo)
        layout.addView(searchButton)
        card.addView(layout)
        return card
    }
    
    private fun createInfoCard(): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 4f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.parseColor("#E8F5E8"))
        }
        
        val infoText = TextView(this).apply {
            text = "Explore an extensive collection of prayers from Bahá'u'lláh, Abdul-Bahá, and the Báb. This expanded collection includes prayers for every occasion, spiritual state, and time of day. Tap any category to discover the prayers within."
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#2E7D32"))
            setPadding(20, 20, 20, 20)
        }
        
        card.addView(infoText)
        return card
    }
    
    private fun displayPrayerCategories() {
        categoryLayout.removeAllViews()
        
        prayerCategories.forEach { category ->
            val categoryCard = createCategoryCard(category)
            categoryLayout.addView(categoryCard)
            categoryLayout.addView(createSpacing(15))
        }
    }
    
    private fun createCategoryCard(category: PrayerCategory): CardView {
        val card = CardView(this).apply {
            radius = 12f
            cardElevation = 6f
            setCardBackgroundColor(if (isDarkMode) Color.parseColor("#1E1E1E") else Color.parseColor("#FFFFFF"))
            isClickable = true
            isFocusable = true
            setOnClickListener { showPrayerList(category) }
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        
        val titleView = TextView(this).apply {
            text = category.name
            textSize = currentFontSize + 2f
            setTextColor(if (isDarkMode) Color.parseColor("#E0E0E0") else Color.parseColor("#1976D2"))
            setPadding(0, 0, 0, 8)
        }
        
        val descView = TextView(this).apply {
            text = category.description
            textSize = currentFontSize
            setTextColor(if (isDarkMode) Color.parseColor("#B0B0B0") else Color.parseColor("#666666"))
            setPadding(0, 0, 0, 8)
        }
        
        val countView = TextView(this).apply {
            text = "${category.prayers.size} prayers available"
            textSize = currentFontSize - 1f
            setTextColor(if (isDarkMode) Color.parseColor("#81C784") else Color.parseColor("#4CAF50"))
        }
        
        layout.addView(titleView)
        layout.addView(descView)
        layout.addView(countView)
        card.addView(layout)
        
        return card
    }
    
    private fun showRandomPrayer() {
        val allPrayers = prayerCategories.flatMap { it.prayers }
        val randomPrayer = allPrayers.random()
        showPrayerReader(randomPrayer)
    }
    
    private fun showSearchDialog() {
        val searchInput = EditText(this).apply {
            hint = "Enter search terms..."
            textSize = currentFontSize
            setPadding(20, 20, 20, 20)
        }
        
        android.app.AlertDialog.Builder(this)
            .setTitle("🔍 Search Prayers")
            .setMessage("Search by keywords, author, or prayer title:")
            .setView(searchInput)
            .setPositiveButton("Search") { dialog, _ ->
                val searchTerm = searchInput.text.toString().lowercase()
                if (searchTerm.isNotEmpty()) {
                    performSearch(searchTerm)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }
    
    private fun performSearch(searchTerm: String) {
        val matchingPrayers = mutableListOf<Prayer>()
        
        prayerCategories.forEach { category ->
            category.prayers.forEach { prayer ->
                if (prayer.title.lowercase().contains(searchTerm) ||
                    prayer.author.lowercase().contains(searchTerm) ||
                    prayer.text.lowercase().contains(searchTerm) ||
                    category.name.lowercase().contains(searchTerm)) {
                    matchingPrayers.add(prayer)
                }
            }
        }
        
        if (matchingPrayers.isEmpty()) {
            Toast.makeText(this, "No prayers found matching '$searchTerm'", Toast.LENGTH_SHORT).show()
        } else {
            showSearchResults(matchingPrayers, searchTerm)
        }
    }
    
    private fun showSearchResults(prayers: List<Prayer>, searchTerm: String) {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("🔍 Search Results: $searchTerm")
            .setMessage("Found ${prayers.size} matching prayers:")
            .create()
        
        val scrollView = ScrollView(this)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        
        prayers.forEach { prayer ->
            val prayerButton = Button(this).apply {
                text = "${prayer.title}\nby ${prayer.author}"
                textSize = currentFontSize
                setBackgroundColor(if (isDarkMode) Color.parseColor("#1565C0") else Color.parseColor("#1976D2"))
                setTextColor(Color.WHITE)
                setPadding(15, 15, 15, 15)
                setOnClickListener {
                    dialog.dismiss()
                    showPrayerReader(prayer)
                }
            }
            layout.addView(prayerButton)
            layout.addView(createSpacing(10))
        }
        
        scrollView.addView(layout)
        dialog.setView(scrollView)
        dialog.show()
    }
    
    private fun showPrayerList(category: PrayerCategory) {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle(category.name)
            .setMessage("Select a prayer to read:")
            .create()
        
        val scrollView = ScrollView(this)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        
        category.prayers.forEach { prayer ->
            val prayerButton = Button(this).apply {
                text = "${prayer.title}\nby ${prayer.author}"
                textSize = currentFontSize
                setBackgroundColor(if (isDarkMode) Color.parseColor("#1565C0") else Color.parseColor("#1976D2"))
                setTextColor(Color.WHITE)
                setPadding(15, 15, 15, 15)
                setOnClickListener {
                    dialog.dismiss()
                    showPrayerReader(prayer)
                }
            }
            layout.addView(prayerButton)
            layout.addView(createSpacing(10))
        }
        
        scrollView.addView(layout)
        dialog.setView(scrollView)
        dialog.show()
    }
    
    private fun showPrayerReader(prayer: Prayer) {
        val intent = Intent(this, PrayerReaderActivity::class.java).apply {
            putExtra("prayer_title", prayer.title)
            putExtra("prayer_author", prayer.author)
            putExtra("prayer_text", prayer.text)
        }
        startActivity(intent)
    }
    
    private fun createSpacing(height: Int): android.view.View {
        return android.view.View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 
                height
            )
        }
    }
}

// Data models
data class PrayerCategory(val name: String, val description: String, val prayers: List<Prayer>)
data class Prayer(val title: String, val author: String, val text: String)