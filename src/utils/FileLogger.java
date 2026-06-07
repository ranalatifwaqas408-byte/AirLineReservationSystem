package utils;

import model.Booking;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReentrantLock;

public class FileLogger {
    private static FileLogger instance;
    private static final String BOOKING_LOG_FILE = "logs/booking_logs.txt";
    private static final String TRANSACTION_LOG_FILE = "logs/transaction_logs.txt";
    private final ReentrantLock lock = new ReentrantLock();

    private FileLogger() {
        new File("logs").mkdirs();
        initializeLogFiles();
    }

    private void initializeLogFiles() {
        try {
            File bookingFile = new File(BOOKING_LOG_FILE);
            if (!bookingFile.exists()) {
                try (FileWriter fw = new FileWriter(bookingFile)) {
                    fw.write("# BOOKING|BookingRef|Username|FlightNo|Passengers|Amount|Status|Timestamp\n");
                }
            }

            File transactionFile = new File(TRANSACTION_LOG_FILE);
            if (!transactionFile.exists()) {
                try (FileWriter fw = new FileWriter(transactionFile)) {
                    fw.write("# TYPE|BookingRef|Username|FlightNo|Details|Timestamp\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Error initializing log files: " + e.getMessage());
        }
    }

    public static synchronized FileLogger getInstance() {
        if (instance == null) {
            instance = new FileLogger();
        }
        return instance;
    }

    public void logBooking(Booking booking) {
        lock.lock();
        try (FileWriter fw = new FileWriter(BOOKING_LOG_FILE, true);
             PrintWriter out = new PrintWriter(fw)) {

            out.printf("BOOKING|%s|%s|%s|%d|%.2f|%s|%s%n",
                    booking.getBookingReference(),
                    booking.getUser().getUsername(),
                    booking.getFlight().getFlightNumber(),
                    booking.getPassengerCount(),
                    booking.getTotalAmount(),
                    booking.getStatus(),
                    booking.getBookingDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
        } catch (IOException e) {
            System.err.println("Error writing to booking log: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public void logCancellation(Booking booking) {
        lock.lock();
        try (FileWriter fw = new FileWriter(TRANSACTION_LOG_FILE, true);
             PrintWriter out = new PrintWriter(fw)) {

            out.printf("CANCELLATION|%s|%s|%s|Amount: %.2f|%s%n",
                    booking.getBookingReference(),
                    booking.getUser().getUsername(),
                    booking.getFlight().getFlightNumber(),
                    booking.getTotalAmount(),
                    java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
        } catch (IOException e) {
            System.err.println("Error writing to transaction log: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public void logTransaction(String type, String details) {
        lock.lock();
        try (FileWriter fw = new FileWriter(TRANSACTION_LOG_FILE, true);
             PrintWriter out = new PrintWriter(fw)) {

            out.printf("%s|%s|%s%n", type, details,
                    java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
        } catch (IOException e) {
            System.err.println("Error writing to transaction log: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }
}