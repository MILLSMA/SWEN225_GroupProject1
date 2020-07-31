import java.util.Arrays;
import java.util.Collection;

public enum RoomCard implements Card {
    KITCHEN,
    BALLROOM,
    CONSERVATORY,
    BILLIARD_ROOM,
    DINING_ROOM,
    LOUNGE,
    HALL,
    LIBRARY,
    STUDY;

    public static Collection<RoomCard> getRooms(){
        return Arrays.asList(values());
    }

    @Override
    public String getName() {
        return name().toLowerCase();
    }
    @Override
    public String toString() {
        return getName();
    }
}
