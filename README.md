# Beat the Heat 🌍🌡️

**Beat the Heat** is a strategic, text-based Java board game designed to educate players about the delicate balance between financial prosperity and environmental sustainability. 

Players navigate a board filled with crossroads, random events, and educational quizzes. Every decision impacts not only the player's personal wealth and sustainability score but also the collective Global Temperature. If the temperature rises too high, everyone loses.

## 🎯 Core Features

* **Dynamic Global Temperature:** Player actions directly impact the global climate. Hitting 1.0°C triggers the Paris Agreement alert, and crossing 1.5°C and 2.0°C unleashes minor and major natural disasters that damage all players.
* **Resource Management:** Players start with £20 and 20 Sustainability. You must balance both to survive. 
* **Event & Quiz Systems:** Draw Event Cards featuring standard, mandatory, and "blind" choices. Answer difficulty-scaled Quiz Cards to earn monetary rewards and learn about climate action.
* **Custom Save/Load Engine:** Features a fully custom-built JSON parser to save and resume game states without relying on any external libraries.
* **Educational Design:** All in-game text and trivia are written to a Flesch-Kincaid 8.0 reading level to ensure accessibility for teenage audiences.

## 🏆 Win and Loss Conditions

**How to Win:**
1. **Sustainability Victory:** Be the first player to reach a Sustainability Score of 100.
2. **Last Player Standing:** Outlast your opponents without going bankrupt or destroying your sustainability.

**How to Lose:**
1. **Bankruptcy/Apathy:** Drop to £0 or 0 Sustainability and you are eliminated from the game.
2. **Collective Loss (The Doomsday Scenario):** If the Global Temperature exceeds 2.5°C, the game ends immediately and everyone loses.

## 💻 Tech Stack

* **Language:** Java (JDK 8+)
* **Dependencies:** None (100% vanilla Java, including custom file parsing and state management).

## 🚀 Getting Started

### Prerequisites
Ensure you have the Java Development Kit (JDK) installed on your machine.

### Installation & Execution
1. Clone the repository:
   ```bash
   git clone [https://github.com/yourusername/Beat-the-Heat.git](https://github.com/yourusername/Beat-the-Heat.git)
