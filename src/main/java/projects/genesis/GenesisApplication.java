package projects.genesis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GenesisApplication {
	public static final String dataPersonIdFilePath = "src/certificates/dataPersonId.txt";
	public static final String dbSourcePath = "jdbc:mysql://localhost:3306/genesis";
	public static final String dbUsername   = "sa";
	public static final String dbPassword   = "masterkey";

	public static void main(String[] args) {
		SpringApplication.run(GenesisApplication.class, args);


	}

}
