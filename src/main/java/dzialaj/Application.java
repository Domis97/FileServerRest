package dzialaj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import dzialaj.storage.database.Db_api;
import dzialaj.storage.database.Db_conn;

import java.util.Scanner;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		addUser();
	}

	public static void addUser(){
		System.out.println("\nJesli chcesz dodac uzytkownika nacisji 1: \n");
        int choice;


        Scanner in = new Scanner(System.in);//TODO
        choice = in.nextInt();
        if (choice==1){
			System.out.println("\nPodaj googleID usera : \n");
			String userID = in.next();
			Db_api.getInstance().procedure_add_user(userID, Db_conn.getInstance().connect());
		}
        addUser();
	}
}
