package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import enums.Difficulty;
import objects.NaturalDisaster;
import objects.cards.Choice;
import objects.cards.EventCard;
import objects.cards.QuizCard;

/** 
 * GameLoader acts as data repository for game
 * Responisble for instantiating and returning the complete decks of all card types throughout game
 */

/** 1. Class Definition and Initialisation */
public class GameLoader {

    /** 
    * 2. Event Card Initialisation
    * Generates the deck of situational choices
    * Constructor: (Ddescription, Cost, Money Chnage, Sustainability, Global Temp Change)
    */

    public List<EventCard> loadEventCards()
    {
        List<EventCard> deck = new ArrayList<>();

        // STANDARD EVENT CARD EXAMPLE
        deck.add(new EventCard(
            "Oil Rig",
            "You have been offered an oil rig investment opportunity",
            Arrays.asList(
                new Choice("Buy oil rig", 0, 10, -15, 0.6f),
                new Choice("Reject offer", 0, 0, 5, 0.0f)
            ),
            false,
            false
        ));

        deck.add(new EventCard(
            "Late for work!",
            "You are running late for an important meeting to secure funding for a new project",
            Arrays.asList(
                new Choice("Drive to work", 0, 15, -5, 0.3f),
                new Choice("Cycle to work", 0, 5, 8, 0.0f)
            ),
            false,
            false
        ));

        deck.add(new EventCard(
            "Charity Gala",
            "You are hosting a gala to raise money for charity",
            Arrays.asList(
                new Choice("Provide locally sourced food", 20, -20, 5, 0.0f),
                new Choice("Provide wholesale food", 5, -5, -5, 0.2f)
            ),
            false,
            false
        ));

        deck.add(new EventCard(
            "Budget Surplus",
            "You have ended the month with a budget surplus, and have been approached with a solar panel scheme",
            Arrays.asList(
                new Choice("Save the money", 0, 15, 0, 0.1f),
                new Choice("Invest in solar panel subsidies", 15, -15, +15, 0.0f)
            ),
            false,
            false
        ));

        deck.add(new EventCard(
            "Budget Surplus",
            "You have ended the month with a budget surplus, and have been approached to invest in wind energy",
            Arrays.asList(
                new Choice("Save the money", 0, 10, -5, 0.1f),
                new Choice("Invest in wind energy", 0, 0, +15, 0.0f)
            ),
            false,
            false
        ));

        deck.add(new EventCard(
            "Budget Surplus",
            "You have ended the month with a budget surplus, and have been approached to invest in hydropower",
            Arrays.asList(
                new Choice("Save the money", 0, 20, 0, 0.2f),
                new Choice("Invest in hydropower", 0, -35, +15, 0.0f)
            ),
            false,
            false
        ));

        deck.add(new EventCard(
            "Office Renovation",
            "Your office is being renovated",
            Arrays.asList(
                new Choice("Replace windows with double-glazed", 15, -15, 5, 0.0f),
                new Choice("Leave windows as they are", 0, 0, -5, 0.2f)
            ),
            false,
            false
        ));

        deck.add(new EventCard(
            "Office Renovation",
            "Your office is being renovated",
            Arrays.asList(
                new Choice("Pay for wall insulation", 15, -15, 5, 0.0f),
                new Choice("Leave walls as they are", 0, 0, -5, 0.2f)
            ),
            false,
            false
        ));

        deck.add(new EventCard(
            "Employee Appreciation",
            "It is employee appreciation day at your office, and you are buying everyone a gift",
            Arrays.asList(
                new Choice("Buy from a local supplier", 20, -20, 5, 0.0f),
                new Choice("Bulk buy from the cheapest supplier", 10, -10, -10, 0.2f)
            ),
            false,
            false
        ));

        // MANDATORY CARD EXAMPLE
        deck.add(new EventCard(
            "Storm Damage",
            "Wind turbine failure, pay repairs",
            Arrays.asList(
                new Choice("Repair", 0, -50, 20, 0.00f),
                new Choice("Ignore repairs", 0, 0, -20, 0.30f)
            ),
            false,
            true
        ));

        deck.add(new EventCard(
            "Long Summer",
            "It has been an unprecedentedly hot summer, many farmers have lost their crops",
            Arrays.asList(
                new Choice("Subsidise farmers", 0, -35, 5, 0.1f),
                new Choice("Do nothing", 0, 0, -15, 0.3f)
            ),
            false,
            true
        ));

        deck.add(new EventCard(
            "Oil Shortage",
            "Due to a shortage of oil, oil prices have risen nationwide",
            Arrays.asList(
                new Choice("Pay the difference", 0, -30, -5, 0.4f),
                new Choice("Reduce energy consumption", 0, 10, 5, 0.0f)
            ),
            false,
            true
        ));

        deck.add(new EventCard(
            "Hospitals Overrun",
            "Decreased air quality has caused an increase in respiratory illnesses, and hospitals are demanding funding",
            Arrays.asList(
                new Choice("Provide more funding", 20, -20, 10, 0.00f),
                new Choice("Do nothing", 0, 0, -10, 0.20f)
            ),
            false,
            true
        ));

        // BLIND EVENT CARD 
        deck.add(new EventCard(
            "New Green Energy Investment",
            "a company pitches new invention",
            Arrays.asList(
                new Choice("invest in green energy startup", 45, -45, 10, 0.0f),
                new Choice("reject, keep money safe", 0, 0, 0, 0.2f)
            ),
            true,
            false
        ));

        deck.add(new EventCard(
            "Company Car Policy",
            "You have been pitched a policy where high-ranking employees can benefit from an electric car",
            Arrays.asList(
                new Choice("Instate policy", 30, -30, 10, 0.0f),
                new Choice("Reject policy", 0, 0, -10, 0.3f)
            ),
            true,
            false
        ));

        deck.add(new EventCard(
            "Coal Power Plant",
            "You have been approached with an opportunity to invest in a coal power plant",
            Arrays.asList(
                new Choice("Accept", 60, -60, -20, 0.5f),
                new Choice("Reject", 0, 0, 5, 0.0f)
            ),
            true,
            false
        ));

        deck.add(new EventCard(
            "Office Renovation",
            "Your office is being renovated, and you are asked if you would like to purchase solar panels",
            Arrays.asList(
                new Choice("Buy solar panels", 25, -25, 10, 0.0f),
                new Choice("Refuse solar panels", 0, 0, -3, 0.3f)
            ),
            true,
            false
        ));

        deck.add(new EventCard(
            "Transportation Scheme",
            "You have been pitched a scheme which subsidises the use of public transport",
            Arrays.asList(
                new Choice("Accept", 30, -30, 30, 0.0f),
                new Choice("Reject", 0, 0, -10, 0.5f)
            ),
            true,
            false
        ));

        

        return deck;
    }

    /** 
     * 3. Quiz Card Initialisation
     * Generates the deck of trivia questions spanning three difficulties.
     * Constructor: (Question, Options Array, Explanation, Correct Index, Reward, Penalty, Difficulty)
     */

    public List<QuizCard> loadQuizCards()
    {

        List<QuizCard> deck = new ArrayList<>();

        //easy questions
        deck.add(new QuizCard(
            "When is the government's target to reach net zero by?", 
            new String[] {"2023", "2050", "2060"}, "The UK government has a legally binding target to reach net zero greenhouse gas emissions by 2050", 1, 15, 5, Difficulty.EASY));

        deck.add(new QuizCard(
            "Which of the following releases the most carbon dioxide into the atmosphere?",
            new String[] {"Solar energy sources", "Wind energy sources", "Burning fossil fuels"},"Burning fossil fuels releases roughly 36–37 billion metric tons (gigatons) of CO2 annually, accounting for nearly 90% of global carbon dioxide emissions.", 2, 15, 5, Difficulty.EASY));

         deck.add(new QuizCard(
            "What is the definition of renewable energy?", 
            new String[] {"Energy derived from natural sources that are replenished at a higher rate than they are consumed", "Energy that will run out during our lifetime", "Energy that does not release carbon into the atmosphere"},"Sunlight and wind, for example, are such sources that are constantly being replenished. Renewable energy sources are plentiful and all around us.", 0, 15, 5, Difficulty.EASY));    
        
        deck.add(new QuizCard(
            "Which of the following can usually be recycled in the UK?", 
            new String[] {"Crisp packets", "Plastic bottles", "Food waste"},"Plastic bottles are widely recycled across the UK through kerbside collections and, increasingly, via bottle return schemes.", 1, 15, 5, Difficulty.EASY));

        deck.add(new QuizCard(
            "Which of the following is a renewable energy source widely used in the UK?", 
            new String[] {"Coal", "Natural gas", "Wind power"},"Offshore wind providing over 17% of the UK’s electricity.", 2, 15, 5, Difficulty.EASY));  

         deck.add(new QuizCard(
            "What are fossil fuels?",
            new String[] {"Fuels formed from plants grown each year", "Fuels that never run out", "Fuels formed from ancient plants and animals"},"The continuous extraction and consumption of fossil fuels at an unsustainable rate depletes the Earth's fossil fuel reserves.", 2, 15, 5, Difficulty.EASY
        ));

        deck.add(new QuizCard(
            "What is the greenhouse effect?",
            new String[] {"Pollution destroying plants", "Heat trapped by gases in the atmosphere", "Heat from the sun disappearing"},"The greenhouse effect is the process through which heat is trapped near Earth's surface by substances known as 'greenhouse gases. ", 1,15, 5, Difficulty.EASY
        ));

        deck.add(new QuizCard(
            "Which of the following is a major effect of climate change?",
            new String[] {"Lower sea levels", "More frequent extreme weather", "Less rainfall globally"},"Climate change is significantly increasing the frequency, intensity, and duration of extreme weather events, making once-rare disasters commonplace.", 1,15, 5, Difficulty.EASY
        ));


        //medium questions
        deck.add(new QuizCard(
            "What average global temperature are we above industrial levels?",
            new String[] {"1.1", "1.4", "1.7"},"The world is rapidly approaching the 1.5 degree limit set in the Paris Agreement.",
            0, // answer index
            25, 10, Difficulty.MEDIUM
        ));

        deck.add(new QuizCard(
            "The UK government has banned sales of new petrol and diesel cars from which year?",
            new String[] {"2030", "2040", "2050"},"The UK has confirmed a ban on the sale of new conventional petrol and diesel cars and vans from 2030.", 0, 25, 10, Difficulty.MEDIUM
        ));

        deck.add(new QuizCard(
            "How can climate change increase social inequality?",
            new String[] {"Lowering national income", "Affecting low-income households disproportionately", "Reducing tourism"},"These households face higher risks due to living in more vulnerable, low-quality housing in high-risk areas, limited resources to adapt or recover, and higher exposure to heatwaves and flooding.", 1, 25, 10, Difficulty.MEDIUM
        ));

         deck.add(new QuizCard(
            "Which of the following is a greenhouse gas?",
            new String[] {"CO2", "CH4", "Both"},"Carbon dioxide (C02) is a major greenhouse gas that traps heat in Earth's atmosphere, essential for maintaining a temperature capable of supporting life.", 2, 25, 10, Difficulty.MEDIUM
        ));

        deck.add(new QuizCard(
            "What was agreed to in the Paris Agreement that came out of COP-21?",
            new String[] {"To limit sea level rise to three feet above current limits", "To limit global warming to 1.5 degrees", "To pursue a goal of 100% clean, renewable energy"},"The Paris Agreement is a legally binding international treaty adopted in 2015 to combat climate change by limiting global warming to 1.5 degrees above pre-industrial levels. ", 1, 25, 10, Difficulty.MEDIUM
        ));

        deck.add(new QuizCard(
            "Which of these countries emits the most CO2 annually?",
            new String[] {"USA", "UK", "China"},"China is the world's largest annual C02 emitter, responsible for over 26–32% of global emissions.", 2, 25, 10, Difficulty.MEDIUM
        ));

        deck.add(new QuizCard(
            "Which of the following economic sectors emit the largest percentage of greenhouse gas?",
            new String[] {"Electricity/Heat Production", "Transport", "Industry"},"Globally, the energy sector is the largest greenhouse gas emitter, responsible for over 73% of emissions.", 0, 25, 10, Difficulty.MEDIUM
        ));

        deck.add(new QuizCard(
            "Which of the following is NOT a consequence associated with climate change?",
            new String[] {"More acidic oceans", "More extreme weather", "Decrease in migration"},"Climate change drives overall migration by forcing displacement due to extreme weather and environmental degradation.", 2, 25, 10, Difficulty.MEDIUM
        ));

        deck.add(new QuizCard(
            "As of 2026, which year has been the hottest on record?",
            new String[] {"2002", "2016", "2024"},"2024 was the first year to exceed the 1.5°C threshold of warming relative to pre-industrial levels.", 2, 25, 10, Difficulty.MEDIUM
        ));

        deck.add(new QuizCard(
            "Approximately what percentage of the atmosphere is carbon dioxide?",
            new String[] {"0.04", "0.1", "2"},"The concentration of carbon dioxide in the Earth's atmosphere is approximately 428 parts per million (ppm) or 0.0428%", 0, 25, 10, Difficulty.MEDIUM
        ));

        //hard questions
        deck.add(new QuizCard(
            "What percentage of global greenhouse gas does the transport industry emit?",
            new String[] {"1%", "15%", "38%"},"The transport industry emits approximately 15% of global greenhouse gas emissions, making it the third-largest sectoral emitter.", 1, 35, 15, Difficulty.HARD
        ));

        deck.add(new QuizCard(
            "What is a trade-off of rapid decarbonisation?",
            new String[] {"Less clean air", "Temporarily lower public health", "Short-term economic disruption"},"A major trade-off of rapid decarbonisation would be transition risks, such as stranded assets, job losses in fossil fuel sectors, and high upfront capital costs.", 2, 35, 15, Difficulty.HARD
        ));

        deck.add(new QuizCard(
            "Why is deforestation harmful for climate change?",
            new String[] {"It increases rainfall causing flash floods", "It destroys the natural habitat of animals", "It releases stored CO2 from trees"},"Deforestation is highly harmful to the climate because it turns carbon-absorbing forests into carbon-releasing sources, contributing to 12–20% of global greenhouse gas emissions.", 2, 35, 15, Difficulty.HARD
        ));

         deck.add(new QuizCard(
            "Why is energy storage important for renewable energy systems?",
            new String[] {"Renewable sources produce constant power", "Some renewable sources are intermittent", "It replaces the need for power stations"},"Solar and wind only generate power when the sun shines or wind blows. Storage bridges the gap between peak production and high demand times, preventing energy waste and ensuring a consistent supply.", 1, 35, 15, Difficulty.HARD
        ));

         deck.add(new QuizCard(
            "How much have sea temperatures increased every decade since the start of the 20th century?",
            new String[] {"0.05 degrees", "0.01 degrees", "0.27 degrees"},"Ocean temperatures were rising at about 0.06 degrees Celsius per decade in the late 1980s, but are now increasing at 0.27 degrees Celsius per decade.", 2, 35, 15, Difficulty.HARD
        ));

         deck.add(new QuizCard(
            "What makes methane a more serious greenhouse gas?",
            new String[] {"Methane traps more heat than other gases", "Methane lasts forever in the atmosphere", "Methane is released in large quantities when trees are cut down"},"Methane is a highly potent greenhouse gas, responsible for over 25% of current global warming.", 0, 35, 15, Difficulty.HARD
        ));

        deck.add(new QuizCard(
            "What is the current global average temperature increase compared to pre-industrial levels?",
            new String[] {"0.7 degrees", "1.4 degrees", "1.7 degrees"},"As of early 2026, the long-term global average temperature increase compared to pre-industrial levels (1850-1900) is estimated to be approximately 1.4 degrees", 1, 35, 15, Difficulty.HARD
        ));

        deck.add(new QuizCard(
            "What is the main cause of ocean acidification?",
            new String[] {"Oil spills", "Plastic waste", "CO2 absorption"},"The main cause of ocean acidification is the absorption of excess carbon dioxide from the atmosphere into the ocean.", 2, 35, 15, Difficulty.HARD
        ));

        deck.add(new QuizCard(
            "Which earth system absorbs the most excess heat from global warming?",
            new String[] {"Land", "Oceans", "Ice sheets"},"The ocean is the Earth system that absorbs the most excess heat from global warming, taking in approximately 90% of the heat generated by increased greenhouse gas emissions.", 1, 35, 15, Difficulty.HARD
        ));

        deck.add(new QuizCard(
            "Climate change will contribute to the extinction of how many animal species?",
            new String[] {"1000", "100,000", "1,000,000"},"Climate change will have a severe impact on the animal kingdom if it continues at its current trajectory", 2,35, 15, Difficulty.HARD
        ));

         deck.add(new QuizCard(
            "At 1.5 degrees of warming, what percentage of animal species will face very high risk of extinction?",
            new String[] {"8%", "18%", "28%"},"Climate change will have a severe impact on the animal kingdom if earth is allowed to reach 1.5 degrees warming", 1 ,35, 15, Difficulty.HARD
        ));

        deck.add(new QuizCard(
            "At 3 degrees of warming, what percentage of animal species will face very high risk of extinction?",
            new String[] {"9%", "19%", "29%"},"Climate change will have a severe impact on the animal kingdom if earth is allowed to reach 3 degrees warming", 2,35, 15, Difficulty.HARD
        ));

        deck.add(new QuizCard(
            "How many animal species were declared extinct in 2025?",
            new String[] {"3", "6", "9"},"The slender-billed curlew, Christmas island shrew, cone snail, and three distinct breeds of Australian bandicoots were the six animal species declared extinct in 2025", 1,35, 15, Difficulty.HARD
        ));

        deck.add(new QuizCard(
            "How many animal species have gone extinct directly due to climate change?",
            new String[] {"10", "15", "20"},"Although there are only 20 specific, documented cases of extinction directly due to climate change, this is widely considered a significant underestimation by experts", 2,35, 15, Difficulty.HARD
        ));


        return deck;
    }

    /** 
     * Natural Disaster Initialisation
     * Generates the environmental triggers based on game temperature state.
     * Constructor: (Name, Description, Money Change, Sustainability Change)
     */
    
    public List<NaturalDisaster> loadMinorNaturalDisasters()
    {
        List<NaturalDisaster> deck = new ArrayList<>();

        deck.add(new NaturalDisaster(
            "Drought",
            "The city is beginning to run low on water",
            -75,0
        ));

        deck.add(new NaturalDisaster(
            "Wildfire",
            "A wildfire has started in the local forest",
            -75,0
        ));

          deck.add(new NaturalDisaster(
            "Storm",
            "A bad storm has broken out, causing damage to housing and infrastructure",
            -75,0
        ));

        deck.add(new NaturalDisaster(
            "Heatwave",
            "A heatwave has begun, and the population are using significantly more water and electricity for air conditioning",
            -75,0
        ));

        deck.add(new NaturalDisaster(
            "Hurricane",
            "A hurricane has occurred, causing damage to infrastructure and necessitating school and work closures",
            -75,0
        ));

        deck.add(new NaturalDisaster(
            "Tornado",
            "A tornado has occurred, causing damage to infrastructure and necessitating school and work closures",
            -75,0
        ));

        deck.add(new NaturalDisaster(
            "Acid Rain",
            "An increase in acid rain is worsening the health of the population",
            -75,0
        ));

        deck.add(new NaturalDisaster(
            "Smog",
            "An increase in smog is worsening the respiratory health of the population",
            -75,0
        ));
        
        return deck;
    }

    public List<NaturalDisaster> loadMajorNaturalDisasters()
    {
        List<NaturalDisaster> deck = new ArrayList<>();

        deck.add(new NaturalDisaster(
            "Severe Drought",
            "The city is running out of water rapidly",
            -150,0
        ));

        deck.add(new NaturalDisaster(
            "Severe Wildfire",
            "A wildfire in the local forest is spreading uncontrollably, the whole forest is at risk of being destroyed",
            -150,0
        ));

          deck.add(new NaturalDisaster(
            "Severe Storm",
            "A severe storm has caused significant damage to housing and infrastructure",
            -150,0
        ));

        deck.add(new NaturalDisaster(
            "Severe Flood",
            "Serious flooding has occurred throughout the city, causing significant damage to infrastructure and thousands of serious injuries",
            -150,0
        ));

        deck.add(new NaturalDisaster(
            "Severe Hurricane",
            "A severe hurricane has occurred, causing significant damage to infrastructure and destroying hundreds of homes",
            -150,0
        ));

        deck.add(new NaturalDisaster(
            "Severe Tornado",
            "A severe tornado has occurred, causing significant damage to infrastructure and destroying hundreds of homes",
            -150,0
        ));

        deck.add(new NaturalDisaster(
            "Tsunami",
            "A tsunami has occurred, causing damage to infrastructure and necessitating school and work closures",
            -150,0
        ));

        return deck;
    }



}