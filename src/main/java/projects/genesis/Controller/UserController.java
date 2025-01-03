package projects.genesis.Controller;

import org.springframework.web.bind.annotation.*;
import projects.genesis.Model.User;
import projects.genesis.Service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/")
public class UserController {
    UserService genService = new UserService();

    @PostMapping("users")
    public String addUser(@RequestBody User user){
        return genService.newUser(user);
    }

    @GetMapping("users")
    public List<?> getUsers(@RequestParam(value = "detail" , defaultValue = "false") boolean detail){
        if (detail) {
            return genService.getUsers();
        } else {
            ArrayList<User> users = genService.getUsers();
            if (users.isEmpty()) {
                return null;
            }
            return users.stream()
                    .map(user -> new UserService.UserShort(user.getId(), user.getName(), user.getSurname()))
                    .collect(Collectors.toList());
        }

    }

    @GetMapping("users/{id}")
    public Object getUser( @PathVariable("id") Integer id, @RequestParam(value = "detail" , defaultValue = "false") boolean detail){
        if (detail) {
            return genService.getUserFromDB(id);
        } else {
            User user = genService.getUserFromDB(id);
            if (user == null) { return null; }
            return new UserService.UserShort(user.getId(), user.getName(), user.getSurname());
        }

    }

    @PutMapping("users")
    public String updateCrypto(
            @RequestBody User user
    ){
        return genService.updateUser(user);
    }

    @DeleteMapping("users/{id}")
    public String getUser( @PathVariable("id") Integer id){
        return genService.deleteUser(id);
    }
}
