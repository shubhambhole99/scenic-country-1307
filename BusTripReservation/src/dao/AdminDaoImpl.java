package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import exception.AdminException;
import exception.BusException;
import model.Bus;
import model.Customers;
import utility.DButil;

public class AdminDaoImpl implements AdminDao{

	@Override
	public String AdminLogin(String username, String password) throws AdminException {
		// TODO Auto-generated method stub
		String message="Admin Login Failed! "
				+ "Try Again";
		if(username.equals("abcd") && password.equals("1234") ) {
			message="Login Successful";
		}
		else {
			throw new AdminException(message);
		}
		return message;
	}

	@Override
	public String AddBus(Bus bus) throws BusException {
			String message="Insertion Unsuccessful";
			int busno=0;
			try (Connection conn = DButil.provideConnection()) {
				
			PreparedStatement ps = conn.prepareStatement("select MAX(busno) from bus");
			ResultSet rs=ps.executeQuery();
			
			if(rs.next()) {
			busno=rs.getInt("MAX(busno)");
			
				}
			else {
			busno=0;
				}
			busno++;
			//mess=String.valueOf(cusid);
			
			} catch (SQLException e) {
			// TODO Auto-generated catch block
					e.printStackTrace();
			}
			
			
			try (Connection conn = DButil.provideConnection()) {
				
				PreparedStatement ps = conn.prepareStatement("insert into bus values(?,?,?,?,?,?,?,?,?,?)");
				ps.setInt(1, busno);
				ps.setString(2, bus.getBusname());
				ps.setString(3, bus.getBusType());
				ps.setString(4, bus.getRouteFrom());
				ps.setString(5, bus.getRouteTo());
				ps.setString(6, bus.getDeparturetime());
				ps.setString(7, bus.getArrivaltime());
				ps.setInt(8, bus.getTotalseats());
				ps.setInt(9, bus.getAvailableseats());
				ps.setInt(10, bus.getFare());

				
				int x = ps.executeUpdate();
				
				if(x>0) {
					message = "Insertion successful.";
				} else {
					throw new BusException("Error!");
				}
				
			} catch (SQLException e) {
				throw new BusException(e.getMessage());
			}
			
			return message;
		
		
	}

	@Override
	public List<Bus> ShowAllBuses() throws BusException {
		// TODO Auto-generated method stub
List<Bus> al = new ArrayList<>();
		
		try (Connection conn = DButil.provideConnection()) {
			
			PreparedStatement ps = conn.prepareStatement("select * from bus");
						
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				
				Bus bus = new Bus();
				bus.setBusno(rs.getInt("busno"));
				bus.setBusname(rs.getString("busname"));
				bus.setBusType(rs.getString("bustype"));
				bus.setRouteFrom(rs.getString("routefrom"));
				bus.setRouteTo(rs.getString("routeto"));
				bus.setArrivaltime(rs.getString("arrivaltime"));
				bus.setDeparturetime(rs.getString("departuretime"));
				bus.setTotalseats(rs.getInt("totalseats"));
				bus.setAvailableseats(rs.getInt("availableseats"));
				bus.setFare(rs.getInt("fare"));
				
				al.add(bus);
			}
			
			if(al.size() == 0) {
				throw new BusException("No buses found!");
			}
			
		} catch (SQLException e) {
			throw new BusException(e.getMessage());
		}		
		
		return al;
	}
	public Customers ConfirmTicket() throws BusException {
		Customers cust = new Customers();
			try (Connection conn = DButil.provideConnection()){
				PreparedStatement ps = conn.prepareStatement("select * from bookings");
				
				
				ResultSet rs = ps.executeQuery();
				boolean flag = false;
				int count = 1;
				while(rs.next()) {
					flag = true;
					if(count == 1) {
						System.out.printf("+------------+----------------------+------------+-------------------------+%n");
						System.out.printf("| %-10s | %-20s | %-10s | %-15s | %-23s |%n", "Booking ID","CustomerID", "Bus No","Confirm", "Seats");
						System.out.printf("+------------+----------------------+------------+-------------------------+%n");
						count++;
					}
					System.out.printf("| %-10s | %-20s | %-10s | %-15s | %-23s |%n", rs.getInt("bookingid"), rs.getInt("customerid"), rs.getInt("busno"), rs.getBoolean("confirm"),rs.getInt("seats"));
				}
			
			if(flag) {
				Scanner sc = new Scanner(System.in);
				System.out.println("");
				System.out.println("To confirm ticket");
				System.out.println("Enter Booking ID:");
				int bid = sc.nextInt();
				
				PreparedStatement ps1 = conn.prepareStatement("update bookings set confirm = 1 where bookingid = ?");
				ps1.setInt(1, bid);
				
				int x = ps1.executeUpdate();
				
				if(x > 0) {
					PreparedStatement ps3 = conn.prepareStatement("select seats from bookings where bookingid = ?");
					ps3.setInt(1, bid);
					
					ResultSet rs3 = ps3.executeQuery();
					
					if(rs3.next()) {
						PreparedStatement ps4 = conn.prepareStatement("update bus set availableseats = availableseats - ? where busno = (select busno from bookings where bookingid = ?)");
						ps4.setInt(1, rs3.getInt("seats"));
						ps4.setInt(2, bid);
						
						int k = ps4.executeUpdate();
						if(k>0) {
							System.out.println("");
							System.out.println("Booking confirmed.");
							
							PreparedStatement ps2 = conn.prepareStatement("select * from customers where customerid = (select customerid from bookings where bookingid = ?)");
							ps2.setInt(1, bid);
							
							ResultSet rs2 = ps2.executeQuery();
							
							if(rs2.next()) {
								cust.setName(rs2.getString("name"));
								cust.setAge(rs2.getInt("age"));
								cust.setAddress(rs2.getString("address"));
								cust.setMobile(rs2.getString("mobile"));
							} else {
								throw new BusException("Customer not found!");
							}
						}
					}
					
					
				} else {
					throw new BusException("Ticket not confirmed.");
				}
			} else {
				throw new BusException("No confirmation pendings.");
			}
				
		} catch (SQLException e) {
			throw new BusException(e.getMessage());
		}
		
		return cust;
	}


	@Override
	public String Logout() throws AdminException {
		// TODO Auto-generated method stub
		String message="Logout Successful";
		return message;
	}

	


}
