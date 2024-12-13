package projects.genesis.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import projects.genesis.Model.User;
import projects.genesis.Service.GenesisService;

@RestController
public class GenesisController {
    GenesisService genService = new GenesisService();

    @PostMapping("/api/v1/users")
    public String addUser(@RequestBody User user){
        return genService.newUser(user);
    }
}
