import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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

    public boolean queryReservation(int seatNumber) {
        return seats.get(seatNumber);
    }

    public void makeReservation(int seatNumber) {
        if (!seats.get(seatNumber)) {
            seats.set(seatNumber, true);
            System.out.println("\n" + getCurrentTime() + " - Writer reserved seat " + seatNumber);
        } else {
            System.out.println(getCurrentTime() + " - Writer tried to reserve seat " + seatNumber + " but it's already reserved");
        }
    }

    public void cancelReservation(int seatNumber) {
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

    public void printAvailableSeats() {
        System.out.print(getCurrentTime() + " - Reader is checking available seats. \nSeats status: ");
        for (int i = 1; i < seats.size(); i++) {
            System.out.print("Seat No " + i + " : " + (seats.get(i) ? "1" : "0") + " ");
        }
        System.out.println();
    }
}

class Reader implements Runnable {
    private ReservationSystem reservationSystem;

    public Reader(ReservationSystem reservationSystem) {
        this.reservationSystem = reservationSystem;
    }

    @Override
    public void run() {
        reservationSystem.printAvailableSeats();
    }
}

class Writer implements Runnable {
    private ReservationSystem reservationSystem;
    private int seatNumber;
    private boolean reserve;

    public Writer(ReservationSystem reservationSystem, int seatNumber, boolean reserve) {
        this.reservationSystem = reservationSystem;
        this.seatNumber = seatNumber;
        this.reserve = reserve;
    }

    @Override
    public void run() {
        if (reserve) {
            reservationSystem.makeReservation(seatNumber);
        } else {
            reservationSystem.cancelReservation(seatNumber);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        ReservationSystem reservationSystem = new ReservationSystem(6);

        ExecutorService executor = Executors.newCachedThreadPool();

        // Create multiple readers and writers
        CompletableFuture<Void> reader1 = CompletableFuture.runAsync(new Reader(reservationSystem), executor);
        CompletableFuture<Void> reader2 = CompletableFuture.runAsync(new Reader(reservationSystem), executor);
        CompletableFuture<Void> reader3 = CompletableFuture.runAsync(new Reader(reservationSystem), executor);
        CompletableFuture<Void> reader4 = CompletableFuture.runAsync(new Reader(reservationSystem), executor);
        CompletableFuture<Void> writer1 = CompletableFuture.runAsync(new Writer(reservationSystem, 0, true), executor);
        CompletableFuture<Void> writer2 = CompletableFuture.runAsync(new Writer(reservationSystem, 0, true), executor);
        CompletableFuture<Void> writer3 = CompletableFuture.runAsync(new Writer(reservationSystem, 1, true), executor);
        CompletableFuture<Void> writer4 = CompletableFuture.runAsync(new Writer(reservationSystem, 1, false), executor);
        CompletableFuture<Void> writer5 = CompletableFuture.runAsync(new Writer(reservationSystem, 2, true), executor);
        CompletableFuture<Void> writer6 = CompletableFuture.runAsync(new Writer(reservationSystem, 2, false), executor);
        CompletableFuture<Void> writer7 = CompletableFuture.runAsync(new Writer(reservationSystem, 2, true), executor);
        CompletableFuture<Void> writer8 = CompletableFuture.runAsync(new Writer(reservationSystem, 3, true), executor);
        CompletableFuture<Void> writer9 = CompletableFuture.runAsync(new Writer(reservationSystem, 4, true), executor);
        CompletableFuture<Void> writer10 = CompletableFuture.runAsync(new Writer(reservationSystem, 4, false), executor);

        // Wait for all tasks to complete
        CompletableFuture.allOf(reader1, reader2, reader3, reader4, writer1, writer2, writer3, writer4, writer5, writer6, writer7, writer8, writer9, writer10).join();

        executor.shutdown();
        try {
            if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
