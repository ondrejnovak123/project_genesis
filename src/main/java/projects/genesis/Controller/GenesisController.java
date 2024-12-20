package projects.genesis.Controller;

import org.springframework.web.bind.annotation.*;
import projects.genesis.Model.User;
import projects.genesis.Service.GenesisService;

@RestController
public class GenesisController {
    GenesisService genService = new GenesisService();

    @PostMapping("/api/v1/users")
    public String addUser(@RequestBody User user){
        return genService.newUser(user);
    }

    @GetMapping("api/v1/users")
    public String getUsers( @RequestParam(value = "detail" , defaultValue = "false") boolean detail){
        return genService.getUsers(detail);
    }

    @GetMapping("/api/v1/users/{id}")
    public String getUser( @PathVariable("id") Integer id, @RequestParam(value = "detail" , defaultValue = "false") boolean detail){
        return genService.getUser(id, detail);
    }

    @PutMapping("api/v1/users")
    public String updateCrypto(
            @RequestBody User user
    ){
        return genService.updateUser(user);
    }

    @DeleteMapping("api/v1/users/{id}")
    public String getUser( @PathVariable("id") Integer id){
        return genService.deleteUser(id);
    }
}
