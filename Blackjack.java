public class Blackjack {
    public static void main(String[] args) {
        
        //everything is run in subroutines
        beginGame();
        
    }
    private static int rounds = 0;
    
    //begin game by asking for the number of players, their names, and their antes.
    public static void beginGame()
    {
        String playAgain = "y";
        System.out.println("Let's play blackjack! \n\nEnter the number of players (1-6): ");
        int numberOfPlayers = IO.readInt();
        while(numberOfPlayers < 1 && numberOfPlayers > 6)
        {
            System.out.println("Enter a valid number of players (1-6): ");
            numberOfPlayers = IO.readInt();
        }

        Player[] players = new Player[numberOfPlayers+1];
        System.out.println("\nEnter the player names: ");
        for(int i = 0; i < numberOfPlayers; i++)
        {
            System.out.println("\nPlayer " + (i+1));
            String name = IO.readString();
            name = name.replaceAll("[^A-Za-z]", "");
            players[i] = new Player(name);
        }
        players[players.length-1] = new Player("Dealer");

        //begin while loop that allows the user to play again when asked whether or not to play again. 
        while(playAgain.equals("y"))
        {
            //Things that reset every game: bets, player hands, rounds, deck (shuffled)
            System.out.println("\nEnter bets: ");
            double[] bets = new double[numberOfPlayers];
            double[] insurance = new double[numberOfPlayers];
            
            for(int i = 0; i < numberOfPlayers; i++)
            {
                System.out.println(players[i].getName() + " ($" + players[i].getCash() + ") :");
                double amount = IO.readDouble();
                while(amount > players[i].getCash())
                {
                    System.out.println("You dont have enough money. Choose a different amount (" + players[i].getCash() + "): ");
                    amount = IO.readDouble();
                }
                players[i].removeCash(amount);
                bets[i] = amount;
                insurance[i] = 0;
            }
            for(Player player : players)
            {
                player.resetHand();
            }
            rounds = 0;
            
            System.out.println("\nLet's begin!");
            Deck deck = new Deck();
            deck.shuffle();
            
            //Each player receives two cards
            int deal = 0;
            while(deal < 2)
            {
                //each player receives one card, and then while loop hits number two, and each person receves another card
                for(int i = 0; i < players.length; i++)
                {
                    players[i].receive(deck.deal());
                }
                deal++;
            }
            
            //prints out board with dealer card face down
            System.out.println("Round 1:");
            visualizeBoardFacedown(players);
            
            //arePlayersDone keeps track of whether or not a player has busted, stayed
            while(!arePlayersDone(players))
            {
                playRound(players, deck, bets, insurance);
            }
            
            //dealer plays at the end of the game
            dealerPlay(deck, players[players.length-1], players);
            
            //gives results of insurance wins and losses
            if(players[players.length-1].canTakeInsurance())
            {
                insuranceResults(players, bets, insurance);
            }
            
            //gives results and balances after loss, win, or push
            results(players, bets);
            
            //asks user to choose to play again or not
            System.out.println("\n\nPlay again? Yes(y) or No(n)?");
            playAgain = IO.readString();
            boolean valid = false;
            while(valid)
            {
                if(playAgain.equals("y") || playAgain.equals("n"))
                {
                    valid = true;
                }
                else
                {
                    playAgain = IO.readString();
                }
            }
        }
    }
    //begin one person's turn
    public static void play(Deck deck, Player[] players, double[] bets, double[] insurance, int index)
    {
        Player player = players[index];
        if(player.isSplit())
        {
            if(player.is21())
            {
                player.stayed();
            }
            else if(player.isStayed())
            {
                player.stayed();
            }
            else if(player.isBust())
            {
                player.stayed();
            }
            else
            {
                System.out.println("\n" + player.getName() + ", Hit(h), Stand(s), or Double Down(d) for hand 1? Hint(p)?");
                String answer = IO.readString();
                boolean valid = false;
                while(!valid)
                {
                    if(answer.equals("h") || answer.equals("s") || answer.equals("d") || answer.equals("p"))
                    {
                        valid = true;
                    }
                    else
                    {
                        System.out.println("Enter a valid command: h/s/d/p");
                        answer = IO.readString();
                    }
                }
                switch(answer)
                {
                    case "h" : player.receive(deck.deal());
                        break;
                    case "s" : player.stayed();
                        break;
                    case "d" : 
                    {
                        player.receive(deck.deal());
                        if(player.getCash() >= bets[index])
                        {
                            player.removeCash(bets[index]);
                            bets[index] = 2*bets[index];
                            player.stayed();
                        }
                        else
                        {
                            System.out.println(player.getName() + " does not have enough money to double down. " + player.getName() + " hit instead.");
                        }
                        break;
                    }
                    case "p" :
                    {
                        help(players, index);
                        play(deck, players, bets, insurance, index);
                        return;
                    }
                }
            }
            if(!player.isStayed())
            {
                visualizeBoardFacedown(players);
            }
            if(player.is21(true))
            {
                player.stayed(true);
            }
            else if(player.isStayed(true))
            {
                player.stayed(true);
            }
            else if(player.isBust(true))
            {
                player.stayed(true);
            }
            else
            {
                System.out.println("\n" + player.getName() + ", Hit(h), Stand(s), or Double Down(d) for hand 2? Hint(p)?");
                String answer = IO.readString();
                boolean valid = false;
                while(!valid)
                {
                    if(answer.equals("h") || answer.equals("s") || answer.equals("d") || answer.equals("p"))
                    {
                        valid = true;
                    }
                    else
                    {
                        System.out.println("Enter valid command: h/s/d/p");
                        answer = IO.readString();
                    }
                }
                switch(answer)
                {
                    case "h" : player.receive(deck.deal(), true);
                        break;
                    case "s" : player.stayed(true);
                        break;
                    case "d" : 
                    {
                        player.receive(deck.deal(), true);
                        if(player.getCash() >= bets[index])
                        {
                            player.removeCash(bets[index]);
                            bets[index] = 2*bets[index];
                            player.stayed(true);
                        }
                        else
                        {
                            System.out.println(player.getName() + " does not have enough money to double down. " + player.getName() + " hit instead.");
                        }
                        break;
                    }
                    case "p" :
                    {
                        help(players, index);
                        play(deck, players, bets, insurance, index);
                        return;
                    }
                }
                visualizeBoardFacedown(players);
            }
        }
        else
        {
            if(player.is21())
            {
                player.stayed();
            }
            else if(player.isStayed())
            {
                player.stayed();
            }
            else if(player.isBust())
            {
                player.stayed();
            }
            else
            {
                if(player.canSplit(bets[index]))
                {
                    String ins = "";
                    if(players[players.length-1].canTakeInsurance() && rounds == 1)
                    {
                        System.out.println("\n" + player.getName() + ", you can take insurance out on the dealer. Would you like to? Yes(y) or No(n)?");
                        ins = IO.readString();
                        boolean valid = ins.equals("y") || ins.equals("n");
                        while(!valid)
                        {
                            ins = IO.readString();
                            valid = ins.equals("y") || ins.equals("n");
                        }
                        double amount = 0;
                        if(ins.equals("y"))
                        {
                            System.out.println("How much? ($" + player.getCash() + ")");
                            amount = IO.readDouble();
                            while(amount > player.getCash())
                            {
                                System.out.println("You don't have enough money for that. ($" + player.getCash() + ")");
                                amount = IO.readDouble();
                            }
                            insurance[index] = amount;
                            player.removeCash(amount);
                        }
                        else
                        {
                            insurance[index] = 0;
                        }
                    }
                    System.out.println("\n" + player.getName() + ", Hit(h), Stand(s), Double Down(d), or Split(x)? Help(p)?");
                    String answer = IO.readString();
                    boolean valid = false;
                    while(!valid)
                    {
                        if(answer.equals("h") || answer.equals("s") || answer.equals("d") || answer.equals("x") || answer.equals("p"))
                        {
                            valid = true;
                        }
                        else
                        {
                            System.out.println("Enter valid command: h/s/d/x/p");
                            answer = IO.readString();
                        }
                    }
                    switch(answer)
                    {
                        case "h" : player.receive(deck.deal());
                            break;
                        case "s" : player.stayed();
                            break;
                        case "d" : 
                        {
                            player.receive(deck.deal());
                            if(player.getCash() >= bets[index])
                            {
                                player.removeCash(bets[index]);
                                bets[index] = 2*bets[index];
                                player.stayed();
                            }
                            else
                            {
                                System.out.println(player.getName() + " does not have enough money to double down. " + player.getName() + " hit instead.");
                            }
                            break;
                        }
                        case "x" : 
                        {
                            player.split();
                            visualizeBoardFacedown(players);
                            player.removeCash(bets[index]);
                            bets[index] = bets[index] * 2;
                            play(deck, players, bets, insurance, index);
                            break;
                        }
                        case "p" :
                        {
                            help(players, index);
                            play(deck, players, bets, insurance, index);
                            return;
                        }
                    }
                }
                else
                {
                    String ins = "";
                    if(players[players.length-1].canTakeInsurance() && rounds == 1)
                    {
                        System.out.println("\n" + player.getName() + ", you can take insurance out on the dealer. Would you like to? Yes(y) or No(n)?");
                        ins = IO.readString();
                        boolean valid = ins.equals("y") || ins.equals("n");
                        while(!valid)
                        {
                            ins = IO.readString();
                            valid = ins.equals("y") || ins.equals("n");
                        }
                        double amount = 0;
                        if(ins.equals("y"))
                        {
                            System.out.println("How much? ($" + player.getCash() + ")");
                            amount = IO.readDouble();
                            while(amount > player.getCash())
                            {
                                System.out.println("You don't have enough money for that. " + player.getCash());
                                amount = IO.readDouble();
                            }
                            player.removeCash(amount);
                            insurance[index] = amount;
                        }
                        else
                        {
                            insurance[index] = 0;
                        }
                    }
                    
                    System.out.println("\n" + player.getName() + ", Hit(h), Stand(s), or Double Down(d)? Help(p)?");
                    String answer = IO.readString();
                    boolean valid = false;
                    while(!valid)
                    {
                        if(answer.equals("h") || answer.equals("s") || answer.equals("d") || answer.equals("p"))
                        {
                            valid = true;
                        }
                        else
                        {
                            System.out.println("Enter valid command: h/s/d/p");
                            answer = IO.readString();
                        }
                    }
                    switch(answer)
                    {
                        case "h" : player.receive(deck.deal());
                            break;
                        case "s" : player.stayed();
                            break;
                        case "d" : 
                        {
                            player.receive(deck.deal());
                            if(player.getCash() >= bets[index])
                            {
                                player.removeCash(bets[index]);
                                bets[index] = 2*bets[index];
                                player.stayed();
                            }
                            else
                            {
                                System.out.println(player.getName() + " does not have enough money to double down. " + player.getName() + " hit instead.");
                            }
                            break;
                        }
                        case "p" :
                        {
                            help(players, index);
                            play(deck, players, bets, insurance, index);
                            return;
                        }
                    }
                }
                visualizeBoardFacedown(players);
            }
        }
    }
    //begin dealer turn
    public static void dealerPlay(Deck deck, Player dealer, Player[] players)
    {
        System.out.println("\nDealer's turn:");
        while(!dealer.isBust() && dealer.getTotal() < 17)
        {
            dealer.receive(deck.deal());
        }
        visualizeBoard(players);
    }
    //begin round
    public static void playRound(Player[] players, Deck deck, double[] bets, double[] insurance)
    {
        rounds++;
        if(rounds != 1)
        {
            System.out.println("\nRound " + rounds + ":");
        }
        for(int i = 0; i < players.length-1; i++)
        {
            play(deck, players, bets, insurance, i);
        }
    }
    //keeps track of whether or not a person stays or busts
    public static boolean arePlayersDone(Player[] players)
    {
        boolean output = true;
        for(int i = 0; i < players.length-1; i++)
        {
            if(players[i].isSplit())
            {
                if(!players[i].isStayed())
                {
                    output = false;
                }
                if(!players[i].isStayed(true))
                {
                    output = false;
                }
            }
            if(!players[i].isStayed())
            {
                output = false;
            }
        }
        return output;
    }
    //prints board with dealer card facedown
    public static void visualizeBoardFacedown(Player[] players)
    {
        System.out.println("");
        for(int i = 0; i < players.length-1; i++)
        {
            if(players[i].isSplit())
            {
                System.out.print(players[i].getName() + ":\t\t" + players[i].toString());
                {
                    if(players[i].isBust())
                    {
                        System.out.print("\t(Busted)");
                    }
                    else if(players[i].is21())
                    {
                        System.out.print("\t(Blackjack)");
                    }
                    else
                    {
                        System.out.print("\t(" + players[i].getTotal() + ")");
                    }
                    if(players[i].isBust(true))
                    {
                        System.out.print("(Busted)\n");
                    }
                    else if(players[i].is21(true))
                    {
                        System.out.print("(Blackjack)\n");
                    }
                    else
                    {
                        System.out.print("(" + players[i].getTotal(true) + ")\n");
                    }
                }
            }
            else
            {
                System.out.print(players[i].getName() + ":\t\t" + players[i].toString());
                {
                    if(players[i].isBust())
                    {
                        System.out.print("\t(Busted)\n");
                    }
                    else if(players[i].is21())
                    {
                        System.out.print("\t(Blackjack)\n");
                    }
                    else
                    {
                        System.out.println("\t(" + players[i].getTotal() + ")");
                    }
                }
            }
        }
        System.out.println("Dealer: \t" + players[players.length-1].dealerToString());
    }
    //prints board with dealer cards face up
    public static void visualizeBoard(Player[] players)
    {
        System.out.println("");
        for(int i = 0; i < players.length; i++)
        {
            if(players[i].isSplit())
            {
                System.out.print(players[i].getName() + ":\t\t" + players[i].toString());
                {
                    if(players[i].isBust())
                    {
                        System.out.print("\t(Busted)");
                    }
                    else if(players[i].is21())
                    {
                        System.out.print("\t(Blackjack)");
                    }
                    else
                    {
                        System.out.print("\t(" + players[i].getTotal() + ")");
                    }
                    if(players[i].isBust(true))
                    {
                        System.out.print("(Busted)\n");
                    }
                    else if(players[i].is21(true))
                    {
                        System.out.print("(Blackjack)\n");
                    }
                    else
                    {
                        System.out.print("(" + players[i].getTotal(true) + ")\n");
                    }
                }
            }
            else
            {
                System.out.print(players[i].getName() + ":\t\t" + players[i].toString());
                {
                    if(players[i].isBust())
                    {
                        System.out.print("\t(Busted)\n");
                    }
                    else if(players[i].is21())
                    {
                        System.out.print("\t(Blackjack)\n");
                    }
                    else
                    {
                        System.out.println("\t(" + players[i].getTotal() + ")");
                    }
                }
            }
        }
    }
    
    public static void insuranceResults(Player[] players, double[] bets, double[] insurance)
    {
        System.out.println("\nInsurance Results:");
        for(int i = 0; i < players.length-1; i++)
        {
            if(insurance[i] == 0)
            {
                System.out.print("\n" + players[i].getName() + " did not bet insurance - (+0)");
            }
            else if(!players[players.length-1].canWinInsurance())
            {
                System.out.print("\n" + players[i].getName() + " - (-" + insurance[i] + ")");
            }
            else
            {
                players[i].addCash(insurance[i]*2);
                System.out.println("\n" + players[i].getName() + " - (+" + insurance[i] + ")");
            }
        }
        System.out.println("");
    }
    
    //prints results of game, wins, losses, and balances
    public static void results(Player[] players, double[] bets)
    {
        System.out.println("\nFinal Results:");
        for(int i = 0; i < players.length-1; i++)
        {
            if(players[i].isSplit())
            {
                if(players[i].isBust() && players[i].isBust(true))
                {
                    System.out.print("\n" + players[i].getName() + " lost Hand 1 and Hand 2 - $" + players[i].getCash() + " (-" + bets[i] + ")");
                }
                else if(!players[players.length-1].isBust() && players[i].isBust() && !players[i].isBust(true) && players[i].getTotal(true) < players[players.length-1].getTotal())
                {
                    System.out.print("\n" + players[i].getName() + " lost Hand 1 and Hand 2 - $" + players[i].getCash() + " (-" + bets[i] + ")");
                }
                else if(!players[players.length-1].isBust() && !players[i].isBust() && players[i].isBust(true) && players[i].getTotal() < players[players.length-1].getTotal())
                {
                    System.out.print("\n" + players[i].getName() + " lost Hand 1 and Hand 2 - $" + players[i].getCash() + " (-" + bets[i] + ")");
                }
                else if(!players[players.length-1].isBust() && !players[i].isBust() && !players[i].isBust(true) && players[i].getTotal() < players[players.length-1].getTotal() && players[i].getTotal(true) > players[players.length-1].getTotal())
                {
                    players[i].addCash(bets[i]);
                    System.out.print("\n" + players[i].getName() + " lost Hand 1 and won Hand 2 - $" + players[i].getCash() + " (+0)");
                }
                else if(!players[players.length-1].isBust() && !players[i].isBust() && !players[i].isBust(true) && players[i].getTotal() > players[players.length-1].getTotal() && players[i].getTotal(true) < players[players.length-1].getTotal())
                {
                    players[i].addCash(bets[i]);
                    System.out.print("\n" + players[i].getName() + " won Hand 1 and lost Hand 2 - $" + players[i].getCash() + " (+0)");
                }
                else if(!players[players.length-1].isBust() && !players[i].isBust() && !players[i].isBust(true) && players[i].getTotal() > players[players.length-1].getTotal() && players[i].getTotal(true) > players[players.length-1].getTotal())
                {
                    players[i].addCash(bets[i]*2);
                    System.out.print("\n" + players[i].getName() + " won Hand 1 and Hand 2 - $" + players[i].getCash() + " (+" + bets[i] + ")");
                }
                else if(players[players.length-1].isBust() && players[i].isBust() && !players[i].isBust(true))
                {
                    players[i].addCash(bets[i]);
                    System.out.print("\n" + players[i].getName() + " lost Hand 1 and won Hand 2 - $" + players[i].getCash() + " (+0)");
                }
                else if(players[players.length-1].isBust() && !players[i].isBust() && players[i].isBust(true))
                {
                    players[i].addCash(bets[i]);
                    System.out.print("\n" + players[i].getName() + " won Hand 1 and lost Hand 2 - $" + players[i].getCash() + " (+0)");
                }
                else if(players[players.length-1].isBust() && !players[i].isBust() && !players[i].isBust(true))
                {
                    players[i].addCash(bets[i]*2);
                    System.out.print("\n" + players[i].getName() + " won Hand 1 and Hand 2 - $" + players[i].getCash() + " (+" + bets[i] + ")");
                }
                else if(!players[players.length-1].isBust() && !players[i].isBust() && !players[i].isBust(true) && players[i].getTotal() == players[players.length-1].getTotal() && players[i].getTotal(true) == players[players.length-1].getTotal())
                {
                    players[i].addCash(bets[i]);
                    System.out.print("\n" + players[i].getName() + " pushes - $" + players[i].getCash());
                }
                else if(!players[players.length-1].isBust() && !players[i].isBust() && !players[i].isBust(true) && players[i].getTotal() < players[players.length-1].getTotal() && players[i].getTotal(true) == players[players.length-1].getTotal())
                {
                    players[i].addCash(bets[i]/2);
                    System.out.print("\n" + players[i].getName() + " lost Hand 1 and pushes Hand 2 - $" + players[i].getCash() + " (-" + bets[i]/2 + ")");
                }
                else if(!players[players.length-1].isBust() && !players[i].isBust() && !players[i].isBust(true) && players[i].getTotal() == players[players.length-1].getTotal() && players[i].getTotal(true) < players[players.length-1].getTotal())
                {
                    players[i].addCash(bets[i]/2);
                    System.out.print("\n" + players[i].getName() + " pushes Hand 1 and lost Hand 2 - $" + players[i].getCash() + " (-" + bets[i]/2 + ")");
                }
                else if(!players[players.length-1].isBust() && players[i].isBust() && !players[i].isBust(true) &&  players[i].getTotal(true) < players[players.length-1].getTotal())
                {
                    players[i].addCash(bets[i]/2);
                    System.out.print("\n" + players[i].getName() + " lost Hand 1 and pushes Hand 2 - $" + players[i].getCash() + " (-" + bets[i]/2 + ")");
                }
                else if(!players[players.length-1].isBust() && !players[i].isBust() && players[i].isBust(true) && players[i].getTotal() == players[players.length-1].getTotal())
                {
                    players[i].addCash(bets[i]/2);
                    System.out.print("\n" + players[i].getName() + " pushes Hand 1 and lost Hand 2 - $" + players[i].getCash() + " (-" + bets[i]/2 + ")");
                }
                else if(!players[players.length-1].isBust() && !players[i].isBust() && !players[i].isBust(true) && players[i].getTotal() > players[players.length-1].getTotal() && players[i].getTotal(true) == players[players.length-1].getTotal())
                {
                    players[i].addCash(bets[i]/2*3);
                    System.out.print("\n" + players[i].getName() + " wins Hand 1 and pushes Hand 2 - $" + players[i].getCash() + " (+" + bets[i]/2 + ")");
                }
                else if(!players[players.length-1].isBust() && !players[i].isBust() && !players[i].isBust(true) && players[i].getTotal() == players[players.length-1].getTotal() && players[i].getTotal(true) > players[players.length-1].getTotal())
                {
                    players[i].addCash(bets[i]/2*3);
                    System.out.print("\n" + players[i].getName() + " pushes Hand 1 and wins Hand 2 - $" + players[i].getCash() + " (+" + bets[i]/2 + ")");
                }
                else
                {
                    System.out.println("\n" + players[i].getName() + " lost - $" + players[i].getCash() + " (-" + bets[i] + ")");
                }
            }
            else
            {
                if(players[i].isBust())
                {
                    System.out.print("\n" + players[i].getName() + " lost - $" + players[i].getCash() + " (-" + bets[i] + ")");
                }
                else if(!players[players.length-1].isBust() && players[i].getTotal() < players[players.length-1].getTotal())
                {
                    System.out.print("\n" + players[i].getName() + " lost - $" + players[i].getCash() + " (-" + bets[i] + ")");
                }
                else if(!players[players.length-1].isBust() && players[i].getTotal() > players[players.length-1].getTotal())
                {
                    players[i].addCash(bets[i]*2);
                    System.out.print("\n" + players[i].getName() + " won - $" + players[i].getCash() + " (+" + bets[i] + ")");
                }
                else if(players[players.length-1].isBust() && !players[i].isBust())
                {
                    players[i].addCash(bets[i]*2);
                    System.out.print("\n" + players[i].getName() + " won - $" + players[i].getCash() + " (+" + bets[i] + ")");
                }
                else
                {
                    players[i].addCash(bets[i]);
                    System.out.print("\n" + players[i].getName() + " pushes - $" + players[i].getCash());
                }
            }
        }
    }
    public static void help(Player[] players, int index)
    {
        System.out.println("\nHelp: ");
        if(players[index].canSplit(0))
        {
            if(players[index].getFaceUp2() == 2 || players[index].getFaceUp2() == 3 || players[index].getFaceUp2() == 6 || players[index].getFaceUp2() == 7 || players[index].getFaceUp2() == 9)
            {
                if(players[players.length-1].getFaceUp2() >= 2 && players[players.length-1].getFaceUp2() <= 6)
                {
                    System.out.println("Hint: you might want to split.");
                }
                else
                {
                    if(players[index].getTotal() != 18)
                    {
                        System.out.println("Hint: you might not want to split, but you might want to hit.");
                    }
                    else
                    {
                        System.out.println("Hint: you might not want to split, but you might want to stand.");
                    }
                }
            }
            else if(players[index].getFaceUp2() == 8 || players[index].getFaceUp2() == 1)
            {
                System.out.println("Hint: you might want to split.");
            }
            else
            {
                if(players[index].getTotal() == 8)
                {
                    System.out.println("Hint: you might not want to split, but you might want to hit.");
                }
                else if(players[index].getTotal() == 10)
                {
                    System.out.println("Hint: you might not want to split, but you might want to double down.");
                }
                else
                {
                    System.out.println("Hint: you might not want to split, but you might want to stand.");
                }
            }
        }
        else if(players[index].isSplit())
        {
            if(!players[index].isSoft())
            {
                if(players[index].getTotal() >= 4 && players[index].getTotal() <= 8)
                {
                    System.out.println("Hint: you might want to hit Hand 1.");
                }
                else if(players[index].getTotal() == 9)
                {
                    if(players[players.length-1].getFaceUp2() >= 2 && players[players.length-1].getFaceUp2() >= 6)
                    {
                        System.out.println("Hint: you might want to double down Hand 1.");
                    }
                    else
                    {
                        System.out.println("Hint: you might want to hit Hand 1.");
                    }
                }
                else if(players[index].getTotal() == 10 || players[index].getTotal() == 11)
                {
                    System.out.println("Hint: you might want to double down Hand 1.");
                }
                else if(players[index].getTotal() >= 12 && players[index].getTotal() <= 16)
                {
                    if(players[players.length-1].getFaceUp2() >= 2 && players[players.length-1].getFaceUp2() <= 6)
                    {
                        System.out.println("Hint: you might want to stand on Hand 1.");
                    }
                    else
                    {
                        System.out.println("Hint: you might want to hit Hand 1.");
                    }
                }
                else
                {
                    System.out.println("Hint: you might want to stand on Hand 1.");
                }
            }
            else
            {
                if(players[index].getTotal() >= 13 && players[index].getTotal() <= 15)
                {
                    System.out.println("Hint: you might want to hit Hand 1.");
                }
                else if(players[index].getTotal() >= 16 && players[index].getTotal() <= 18)
                {
                    if(players[players.length-1].getFaceUp2() >= 2 && players[players.length-1].getFaceUp2() >= 6)
                    {
                        System.out.println("Hint: you might want to double down Hand 1.");
                    }
                    else
                    {
                        System.out.println("Hint: you might want to hit Hand 1.");
                    }
                }
                else
                {
                    System.out.println("Hint: you might want to stand on Hand 1.");
                }
            }
            if(!players[index].isSoft())
            {
                if(players[index].getTotal(true) >= 4 && players[index].getTotal(true) <= 8)
                {
                    System.out.print(" You might want to hit Hand 2.");
                }
                else if(players[index].getTotal(true) == 9)
                {
                    if(players[players.length-1].getFaceUp2() >= 2 && players[players.length-1].getFaceUp2() >= 6)
                    {
                        System.out.print(" You might want to double down Hand 2.");
                    }
                    else
                    {
                        System.out.print(" You might want to hit Hand 2.");
                    }
                }
                else if(players[index].getTotal(true) == 10 || players[index].getTotal(true) == 11)
                {
                    System.out.print(" You might want to double down Hand 2.");
                }
                else if(players[index].getTotal(true) >= 12 && players[index].getTotal(true) <= 16)
                {
                    if(players[players.length-1].getFaceUp2() >= 2 && players[players.length-1].getFaceUp2() <= 6)
                    {
                        System.out.print(" You might want to stand on Hand 2.");
                    }
                    else
                    {
                        System.out.print(" You might want to hit Hand 2.");
                    }
                }
                else
                {
                    System.out.print(" You might want to stand on Hand 2.");
                }
            }
            else
            {
                if(players[index].getTotal(true) >= 13 && players[index].getTotal(true) <= 15)
                {
                    System.out.print(" You might want to hit Hand 2.");
                }
                else if(players[index].getTotal(true) >= 16 && players[index].getTotal(true) <= 18)
                {
                    if(players[players.length-1].getFaceUp2() >= 2 && players[players.length-1].getFaceUp2() >= 6)
                    {
                        System.out.print(" You might want to double down Hand 2.");
                    }
                    else
                    {
                        System.out.print(" You might want to hit Hand 2.");
                    }
                }
                else
                {
                    System.out.print(" You might want to stand on Hand 2.");
                }
            }
        }
        else if(!players[index].isSoft())
        {
            if(players[index].getTotal() >= 4 && players[index].getTotal() <= 8)
            {
                System.out.println("Hint: you might want to hit.");
            }
            else if(players[index].getTotal() == 9)
            {
                if(players[players.length-1].getFaceUp2() >= 2 && players[players.length-1].getFaceUp2() >= 6)
                {
                    System.out.println("Hint: you might want to double down.");
                }
                else
                {
                    System.out.println("Hint: you might want to hit.");
                }
            }
            else if(players[index].getTotal() == 10 || players[index].getTotal() == 11)
            {
                System.out.println("Hint: you might want to double down.");
            }
            else if(players[index].getTotal() >= 12 && players[index].getTotal() <= 16)
            {
                if(players[players.length-1].getFaceUp2() >= 2 && players[players.length-1].getFaceUp2() <= 6)
                {
                    System.out.println("Hint: you might want to stand.");
                }
                else
                {
                    System.out.println("Hint: you might want to hit.");
                }
            }
            else
            {
                System.out.println("Hint: you might want to stand.");
            }
        }
        else
        {
            if(players[index].getTotal() >= 13 && players[index].getTotal() <= 15)
            {
                System.out.println("Hint: you might want to hit.");
            }
            else if(players[index].getTotal() >= 16 && players[index].getTotal() <= 18)
            {
                if(players[players.length-1].getFaceUp2() >= 2 && players[players.length-1].getFaceUp2() >= 6)
                {
                    System.out.println("Hint: you might want to double down.");
                }
                else
                {
                    System.out.println("Hint: you might want to hit.");
                }
            }
            else
            {
                System.out.println("Hint: you might want to stand.");
            }
        }
    }
}