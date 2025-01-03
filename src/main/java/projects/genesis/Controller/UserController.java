package projects.genesis.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Object> addUser(@RequestBody User user){
        if (genService.newUser(user)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body("User created.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User failed to create.");
        }
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
    public ResponseEntity<Object> updateUser(
            @RequestBody User user
    ){
        if (genService.updateUser(user)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body("User updated.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User failed to update.");
        }

    }

    @DeleteMapping("users/{id}")
    public ResponseEntity<Object> getUser( @PathVariable("id") Integer id){
        if (genService.deleteUser(id)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body("User deleted.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User failed to delete.");
        }

    }
}
