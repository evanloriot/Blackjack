import java.util.ArrayList;
public class Deck {
    
    private ArrayList<Card> deck;
    private int cardsDealt;
    
    public Deck()
    {
        deck = new ArrayList<>();
        cardsDealt = 0;
        for(int i = 0; i < 4; i++)
        {
            for(int j = 1; j <= 13; j++)
            {
                deck.add(new Card(i,j));
            }
        }
    }
    
    public Card deal()
    {
        Card dealt = deck.get(0);
        deck.remove(0);
        cardsDealt++;
        return dealt;
    }
    
    public boolean isEmpty()
    {
        return cardsDealt == 0;
    }
    
    public void shuffle()
    {
        ArrayList<Card> shuffled = new ArrayList<>();
        while(!deck.isEmpty())
        {
            int index = (int)(Math.random() * deck.size());
            Card temp = deck.get(index);
            deck.remove(temp);
            shuffled.add(temp);
        }
        deck = shuffled;
    }
    
}
