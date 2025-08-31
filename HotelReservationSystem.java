import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class HotelReservationSystem {
    
    private static final String url = "jdbc:mysql://localhost:3306/Hotel_db";
    private static final String username = "root";
    private static final String password = "M@372003";

    public static void main(String[] args) throws ClassNotFoundException,SQLException {

        try{
        Class.forName("com.mysql.cj.jdbc.Driver");
        System.out.println("Drivers loaded");
        }
        catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url,username,password);
            Scanner scanner = new Scanner(System.in);
            Statement statement = connection.createStatement();

            while (true) { 
                
                System.out.println("-------------------WELCOME------------------");
                System.out.println("----------HOTEL RESERVATION SYSTEM----------");
                System.out.println("Please choose an option :");
                System.out.println("1. Reserve a Room");
                System.out.println("2. View Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");
                System.out.println();
                
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1 -> ReserveRoom(scanner,statement);
                    case 2 -> ViewReservation(connection);
                    case 3 -> GetroomNumber(scanner,statement);
                    case 4 -> UpdateReservation(scanner,statement,connection);
                    case 5 -> DeleteReservation(scanner,statement);
                    case 0 -> {
                        exit();
                        scanner.close();
                        return;
                    }
                    default -> System.out.println("Invalid Choice");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void ReserveRoom(Scanner scanner,Statement statement){
        System.out.println("Enter Guest Name :");
        String guestname = scanner.next();
        scanner.nextLine();
        System.out.println("Enter Room Number :");
        int roomnumber = scanner.nextInt();
        System.out.println("Enter Contact Number :");
        String contact = scanner.next();

        String query = "INSERT INTO RESERVATION (GUEST_NAME,ROOM_NUMBER,CONTACT_NUMBER)"+ "VALUES('"+guestname+"','"+roomnumber+"','"+contact+"')";

        try{
            int affectedrows = statement.executeUpdate(query);

            if (affectedrows >0) {
                System.out.println("Room booked");
                System.out.println(affectedrows+" Entry Added");
            } else {
                System.out.println("Failed to book room");
            }
        System.out.println();
        }

        catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }
    private static void ViewReservation(Connection connection)throws SQLException{
        String query = "SELECT Reservation_id, Guest_name, Room_number, Contact_number, Reservation_date FROM reservation;";

        try(Statement statement = connection.createStatement();
            ResultSet resultset =statement.executeQuery(query) ) {
            
            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number       | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

            while (resultset.next()) {
                int Reserve_id = resultset.getInt("Reservation_id");
                String Guest = resultset.getString("Guest_name");
                int Room = resultset.getInt("Room_number");
                String Contact = resultset.getString("Contact_number");
                String reservationDate = resultset.getTimestamp("reservation_date").toString();

                // Format and display the reservation data in a table-like format
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        Reserve_id, Guest, Room, Contact, reservationDate);
            }
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println();
        } catch (Exception e) {
        }

    }
    private static void GetroomNumber(Scanner scanner,Statement statement){
        try {
            System.out.println("Enter Reservation id : ");
            int Reservation_id = scanner.nextInt();
             scanner.nextLine(); // consume leftover newline
            System.out.println("Enter Guest name: ");
            String Guest = scanner.nextLine();

            String query = "SELECT Room_number  FROM reservation WHERE Reservation_id = "+Reservation_id+" AND Guest_name='" + Guest + "'";
            try(ResultSet resultset = statement.executeQuery(query)){
                if(resultset.next()){
                    int room = resultset.getInt("Room_number");
                    System.out.println();
                    System.out.println("The Reserved room for reservation id : "+Reservation_id+" AND guest name : " +Guest+" is : "+room);
                    System.out.println();
                }
                else{
                    System.out.println();
                    System.out.println("There is no room book for above Guest name and Reservation id");
                    System.out.println();
                }
            }
        } catch (SQLException e) {
            e.getMessage();
        }
    }
    private static void UpdateReservation( Scanner scanner,Statement statement, Connection connection){
        System.out.println("Enter Reservation id to update : ");
        int Reservation_id = scanner.nextInt();
        scanner.nextLine();

        if(!reservations(statement,Reservation_id)){
            System.out.println("there is no reservation for the id : "+Reservation_id);
            return;
        }

        System.out.println("Enter new Room number : ");
        int Room_num = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter new Guest name : ");
        String Guest_name = scanner.nextLine();
        System.out.println("Enter new Contact number : ");
        String Contact_num = scanner.next();

        String query = "UPDATE reservation SET Guest_name = ?, Room_number = ?, Contact_number = ? WHERE Reservation_id = ?";
        
        try(PreparedStatement preparedtatement = connection.prepareStatement(query)) {
            preparedtatement.setString(1, Guest_name);
            preparedtatement.setInt(2, Room_num);
            preparedtatement.setString(3, Contact_num);
            preparedtatement.setInt(4, Reservation_id);

            int affectedrows = preparedtatement.executeUpdate();
            if(affectedrows >0 ){
                System.out.println("Reservation Updated Successfully "+affectedrows+" Data Updated");
            }
            else{
                System.out.println("Reservation updating failed");
            }

        } catch (SQLException e) {
            e.getMessage();
        }

    }
    
    private static void DeleteReservation( Scanner scanner,Statement statement){
            System.out.println("Enter Reservation Id to delete : ");
            int Reservation_id = scanner.nextInt();

            if(!reservations(statement, Reservation_id)){
                System.out.println("there is no reservation for the id : "+Reservation_id);
                return;
            }

            String query = "DELETE FROM reservation WHERE Reservation_id = "+Reservation_id;

            try{
                int affectedrows = statement.executeUpdate(query);
                if(affectedrows >0){
                    System.out.println("Data deleted successfully");
                }
                else{
                    System.out.println("Data not deleted ");

                }
            }
            catch(SQLException e){

            }

        }
    
    private  static boolean  reservations(Statement statement , int Reservation_id){
        String query = "SELECT Reservation_id FROM reservation WHERE Reservation_id = "+Reservation_id;

        try(ResultSet resultset = statement.executeQuery(query)){
            return resultset.next();
        }
        catch(SQLException e){
            e.getMessage();
            return false;
        }
    }
    public static void exit()throws InterruptedException{
        System.out.print("Exiting System");
        int i =5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("Thank you fro using HOTEL RESERVATION SYSTEM..");
    }
}
