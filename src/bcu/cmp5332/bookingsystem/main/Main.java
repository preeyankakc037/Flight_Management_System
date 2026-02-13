package bcu.cmp5332.bookingsystem.main;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException, FlightBookingSystemException {

        FlightBookingSystem fbs = FlightBookingSystemData.load();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Flight Booking System");
        System.out.println("Type 'help' for a list of commands.");

        while (true) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null || line.equalsIgnoreCase("exit")) {
                FlightBookingSystemData.store(fbs);
                System.out.println("Data saved. Exiting...");
                break;
            } else if (line.trim().isEmpty()) {
                continue;
            }

            try {
                Command command = CommandParser.parse(line);
                command.execute(fbs);

                // Immediate save after any command execution
                try {
                    FlightBookingSystemData.store(fbs);
                } catch (IOException e) {
                    // Rollback: Reload system from disk if save fails
                    System.out.println("Critical Error: Failed to save to disk. Changes have been rolled back.");
                    System.out.println("Error details: " + e.getMessage());
                    fbs = FlightBookingSystemData.load();
                }
            } catch (FlightBookingSystemException ex) {
                System.out.println(ex.getMessage());
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }
}
