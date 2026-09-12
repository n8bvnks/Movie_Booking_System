package nz.ac.ara.comp713.booking_service.client;

public class NotEnoughSeatsException extends RuntimeException
{
    public NotEnoughSeatsException(String movietitle, int seats, int availiable)
    {
        super("Unfortunately the number of seats requested exceeds the number of seats availiable for " + movietitle + ". Seats availiable = " + availiable );
    
}
}