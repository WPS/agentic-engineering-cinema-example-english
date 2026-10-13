
artifact domainlayering
{
    include "backend/**"
    
    artifact filmselection
    {
        include "**/cinema/filmselection/**"
    }
    
    artifact ticketsale
    {
        include "**/cinema/ticketsale/**"
        
        relaxed artifact common
        {
            priority -1
            include "**"
        }
        
        artifact sales
        {
            include "**/domain/sales/**"
            connect to seatallocation, screenings
        }
        
        artifact seatallocation
        {
            include "**/domain/seatallocation/**"
            connect to screenings
        }
        
        artifact screenings
        {
            include "**/domain/screenings/**"
        }
        
        artifact payment
        {
            include "**/domain/payment/**"
        }
    }
    
    public artifact common
    {
        include "**/cinema/common/**"
    }
}
