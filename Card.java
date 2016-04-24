public class Card {
    
    private int suit;
    private int face;
    
    public Card(int cardSuit, int cardFace)
    {
        suit = cardSuit;
        face = cardFace;
    }
    
    public int getSuit()
    {
        return suit;
    }
    
    public int getFace()
    {
        return face;
    }
    
    public int getValue()
    {
        if(face > 10)
        {
            return 10;
        }
        else
        {
            return face;
        }
    }
    
    @Override
    public String toString()
    {
        String output = "[";
        switch(face)
        {
            case 1 : output += "A";
                break;
            case 11: output += "J";
                break;
            case 12: output += "Q";
                break;
            case 13: output += "K";
                break;
            default: output += face;
                break;
        }
        switch(suit)
        {
            case 0 : output += "♠";
                break;
            case 1 : output += "♥";
                break;
            case 2 : output += "♣";
                break;
            default: output += "♦";
        }
        return output + "]";
    }
    
}
