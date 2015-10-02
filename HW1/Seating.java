public class Seating{

    public int seatsSold;
    public int numSeats;
    public String[] seats;
    public boolean soldOut = false;

    public Seating(int numSeats){
        this.seatsSold = 0;
        this.numSeats = numSeats;
        // Initialize empty seating array
        this.seats = new String[numSeats];
        for (int i=0; i<seats.length; i++){
            seats[i] = "";
        }
    }

    public int getSeatsSold(){
        return seatsSold;
    }

    public void incrementSold(){
        seatsSold++;
    }

    public void decrementSold(){
        seatsSold--;
    }

    public int getNumSeats(){
        return numSeats;
    }

    public String[] getSeats(){
        return this.seats;
    }

    public boolean isSoldOut(){
        return soldOut;
    }

    public void setSoldOut(boolean torf){
        soldOut = torf;
    }
}