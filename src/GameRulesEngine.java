package core;

import java.util.List;

import objects.Player;
import utils.GameConstants;

/** 
 * GameRulesEngine isolates the core logic for win/loss conditions and environmental triggers.
 */
public class GameRulesEngine {

/** 
* 1. Win / Loss Evaluation
* Scans player resources and global temperature against defined constants
 */

    /** 
     * Evaluate the current state of all players and the environment to determine if the game should continue,
     * end or if a specific player even (like bankruptcy) has occurred.
     */
    
    public String checkWinLossConditions(List<Player> players, float globalTemp)
    {
        int activePlayers = 0;
        Player lastActive = null;
        String eliminationAlert = "";

        for (Player p : players)
        {
            if (p.isEliminated())
            {
                continue; // skip eliminated players
            }

            if(p.getSustainabilityScore() <= GameConstants.LOSING_SUSTAINABILITY_SCORE) {
                p.setEliminated(true);

                if(eliminationAlert.isEmpty()) {
                    eliminationAlert = "PLAYER_ELIMINATED_" + p.getName();
                }
                else {
                    eliminationAlert += "_AND_" + p.getName();
                }
                continue;
            }

            if(p.getMoney() <= GameConstants.LOSING_MONEY_AMOUNT) {
                if(p.hasLoan()) {
                    p.setEliminated(true);

                    if(eliminationAlert.isEmpty()) {
                        eliminationAlert = "PLAYER_ELIMINATED_" + p.getName();
                    }
                    else {
                        eliminationAlert += "_AND_" + p.getName();
                    }
                    continue;
                } 
                else {
                    return "OFFER_LOAN_" + p.getName();
                }
            }

            if (p.getSustainabilityScore() >= GameConstants.WINNING_SUSTAINABILITY_SCORE)
            {
                return "PLAYER_WIN_" + p.getName();
            }

            activePlayers ++;
            lastActive = p;
        }


        if (globalTemp > GameConstants.MAX_GLOBAL_TEMP)
        {
            return "GAME_OVER_TEMP";
        }

        // last player standing wins the game
        if (players.size() > 1)
        {
            if (activePlayers == 1 && lastActive != null)
            {
                return "PLAYER_WIN_" + lastActive.getName();
            }
            else if (activePlayers == 0)
            {
                return "GAME_OVER_ALL_ELIMINATED";
            }
        }

        if (!eliminationAlert.isEmpty())
        {
            return eliminationAlert;
        }

        return "CONTINUE";
    }

/** 
* 2. Environmental Triggers
* Evaluates temperature thresholds against round intervals to trigger disaster
*/

    /** 
     * Calculates if a natural disaster should occur based on current temperature
     * and the amount of rounds that have passed since threshold was exceeded
     */

    public String shouldTriggerDisaster(float globalTemp, int currentRound, int roundReached15, int roundReached20)
    {
        // Checks 2.0 degrees Threshold
        if (globalTemp >= GameConstants.DISASTER_THRESHOLD_MAJOR && roundReached20 != -1)
        {
            //Checks round reaching 2.0 degrees
            int roundsSince = currentRound - roundReached20;

            //Triggers [X[]] rounds after reaching temperature threshold
            if(roundsSince > 0 && roundsSince % GameConstants.DISASTER_INTERVAL_MAJOR == 0) {
                return "MAJOR";
            }
        }
        else if (globalTemp >= GameConstants.DISASTER_THRESHOLD_MINOR && roundReached15 != -1) {
            //Checks round reaching 1.5 degrees
            int roundsSince = currentRound - roundReached15;

            //Triggers 5 rounds after reaching 1.5 degrees
             if(roundsSince > 0 && roundsSince % GameConstants.DISASTER_INTERVAL_MINOR == 0) {
                return "MINOR";
            }
        }

        return "NONE";
    }
    
}
