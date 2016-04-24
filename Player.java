import java.util.ArrayList;
public class Player {
    
    private ArrayList<Card> hand;
    private ArrayList<Card> splitHand;
    private boolean containsAce;
    private boolean containsAce2;
    private int total;
    private int total2;
    private String playerName;
    private boolean stayed;
    private boolean stayed2;
    private double cash;
    
    public Player(String playerName)
    {
        hand = new ArrayList<>();
        containsAce = false;
        total = 0;
        this.playerName = playerName;
        stayed = false;
        cash = 1000;
    }
    
    public boolean getContainsAce()
    {
        return containsAce;
    }
    
    public boolean canSplit(double value)
    {
        if(hand.size() != 2)
        {
            return false;
        }
        return hand.get(0).getFace() == hand.get(1).getFace() && value <= cash;
    }
    
    public void split()
    {
        splitHand = new ArrayList<>();
        splitHand.add(hand.get(1));
        containsAce2 = hand.get(1).getValue() == 1;
        hand.remove(1);
        total = hand.get(0).getValue();
        total2 = splitHand.get(0).getValue();
    }
    
    public boolean isSplit()
    {
        return splitHand != null;
    }
    
    public double getCash()
    {
        return cash;
    }
    
    public void addCash(double winnings)
    {
        cash = cash + winnings;
    }
    
    public void removeCash(double amount)
    {
        cash = cash - amount;
    }
    
    public void stayed()
    {
        stayed = true;
    }
    
    public void stayed(boolean isForSplitHand)
    {
        stayed2 = true;
    }
    
    public boolean isStayed()
    {
        return stayed;
    }
    
    public boolean isStayed(boolean isForSplitHand)
    {
        return stayed2;
    }
    
    public void resetHand()
    {
        hand = new ArrayList<>();
        splitHand = null;
        containsAce = false;
        containsAce2 = false;
        total = 0;
        total2 = 0;
        stayed = false;
        stayed2 = false;
    }
    
    public String getName()
    {
        return playerName;
    }
    
    public void receive(Card card)
    {
        hand.add(card);
        if(card.getValue() == 1)
        {
            containsAce = true;
        }
        total += card.getValue();
    }
    
    public void receive(Card card, boolean isToSplit)
    {
        splitHand.add(card);
        if(card.getValue() == 1)
        {
            containsAce2 = true;
        }
        total2 += card.getValue();
    }
    
    public boolean isBust()
    {
        if(total > 21)
        {
            return true;
        }
        return false;
    }
    
    public boolean isBust(boolean isForSplitHand)
    {
        if(total2 > 21)
        {
            return true;
        }
        return false;
    }
    
    public boolean is21()
    {
        if(total == 21)
        {
            return true;
        }
        if(total == 11 && containsAce)
        {
            return true;
        }
        return false;
    }
    
    public boolean is21(boolean isForSplitHand)
    {
        if(total2 == 21)
        {
            return true;
        }
        if(total2 == 11 && containsAce2)
        {
            return true;
        }
        return false;
    }
    
    public int getTotal()
    {
        if(containsAce && total+10<=21)
        {
            return total+10;
        }
        return total;
    }
    
    public int getTotal(boolean isForSplitHand)
    {
        if(containsAce2 && total2+10<=21)
        {
            return total2+10;
        }
        return total2;
    }
    
    @Override
    public String toString()
    {
        String output = "";
        if(splitHand == null)
        {
            for(int i = 0; i < hand.size(); i++)
            {
                output += hand.get(i).toString();
            }
            return output;
        }
        for(int i = 0; i < hand.size(); i++)
        {
            output += hand.get(i).toString();
        }
        output += "\t\t";
        for(int i = 0; i < splitHand.size(); i++)
        {
            output += splitHand.get(i).toString();
        }
        return output;
    }
    
    public String dealerToString()
    {
        String output = "[XX]";
        output += hand.get(1).toString();
        return output;
    }
    
    public boolean canTakeInsurance()
    {
        return hand.get(1).getValue() == 1;
    }
    
    public boolean canWinInsurance()
    {
        return canTakeInsurance() && hand.get(0).getValue() == 10;
    }
    
    public int getFaceUp2()
    {
        return hand.get(1).getFace();
    }
    
    public boolean isSoft()
    {
        if(containsAce && total+10<=21)
        {
            return true;
        }
        return false;
    }
    
}