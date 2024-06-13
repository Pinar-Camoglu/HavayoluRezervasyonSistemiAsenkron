import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Date;
import java.text.SimpleDateFormat;

class ReservationSystem {
    private List<Boolean> seats;

    public ReservationSystem(int numSeats) {
        seats = new ArrayList<>();
        for (int i = 0; i < numSeats; i++) {
            seats.add(false); // All seats are initially unreserved
        }
    }

    public synchronized boolean queryReservation(int seatNumber) {
        return seats.get(seatNumber);
    }

    public synchronized void makeReservation(int seatNumber) {
        if (!seats.get(seatNumber)) {
            seats.set(seatNumber, true);
            System.out.println(getCurrentTime() + " - Writer reserved seat " + seatNumber);
        } else {
            System.out.println(getCurrentTime() + " - Writer tried to reserve seat " + seatNumber + " but it's already reserved");
        }
    }

    public synchronized void cancelReservation(int seatNumber) {
        if (seats.get(seatNumber)) {
            seats.set(seatNumber, false);
            System.out.println(getCurrentTime() + " - Writer canceled reservation for seat " + seatNumber);
        } else {
            System.out.println(getCurrentTime() + " - Writer tried to cancel reservation for seat " + seatNumber + " but it's not reserved");
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        return sdf.format(new Date());
    }

    public synchronized void printAvailableSeats() {
        System.out.print(getCurrentTime() + " - Reader is checking available seats. Seats status: ");
        for (int i = 0; i < seats.size(); i++) {
            System.out.print("Seat No " + i + (seats.get(i) ? " (reserved)" : " (available)") + " ");
        }
        System.out.println();
    }
}

class Reader extends Thread {
    private ReservationSystem reservationSystem;

    public Reader(ReservationSystem reservationSystem) {
        this.reservationSystem = reservationSystem;
    }

    public void run() {
        reservationSystem.printAvailableSeats();
    }
}

class Writer extends Thread {
    private ReservationSystem reservationSystem;
    private int seatNumber;

    public Writer(ReservationSystem reservationSystem, int seatNumber) {
        this.reservationSystem = reservationSystem;
        this.seatNumber = seatNumber;
    }

    public void run() {
        reservationSystem.makeReservation(seatNumber);
    }
}

public class Main {
    public static void main(String[] args) {
        ReservationSystem reservationSystem = new ReservationSystem(10);

        // Create multiple readers and writers
        Reader reader1 = new Reader(reservationSystem);
        Reader reader2 = new Reader(reservationSystem);
        Reader reader3 = new Reader(reservationSystem);
        Writer writer1 = new Writer(reservationSystem, 1);
        Writer writer2 = new Writer(reservationSystem, 1);
        Writer writer3 = new Writer(reservationSystem, 1);

        // Start threads
        reader1.start();
        reader2.start();
        writer1.start();
        writer2.start();
        writer3.start();
        reader3.start();

        try {
            reader1.join();
            reader2.join();
            writer1.join();
            writer2.join();
            writer3.join();
            reader3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
