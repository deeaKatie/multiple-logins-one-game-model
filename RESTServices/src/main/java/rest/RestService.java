package rest;

import exception.RepositoryException;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repository.UserDBRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/services/users")
public class RestService {

    @Autowired
    private UserDBRepository userDBRepository;

    @GetMapping("/{id}")
    private ResponseEntity<?> get(@PathVariable Long id) {
        try {
            User entity = userDBRepository.findById(id);
            return new ResponseEntity<User>(entity, HttpStatus.OK);
        } catch (RepositoryException ex) {
            return new ResponseEntity<String>("Can't find the requested entity", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam (value = "username", required = false) String username,
                                    @RequestParam (value = "sort", required = false) String sort) {
        // if username, get all user with same username
        if (username != null) {
            User user = null;
            try {
                user = userDBRepository.findUserByUsername(username);
            } catch (RepositoryException ex) {
                return new ResponseEntity<String>("Can't find the requested entity", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<User>(user, HttpStatus.OK);
        }
        //else get all users
        if (sort != null) {
            ArrayList<User> entities = (ArrayList<User>) userDBRepository.getAll();
            entities.sort(Comparator.comparing(User::getUsername));
            return new ResponseEntity<Iterable<User>>(entities, HttpStatus.OK);
        }
        return new ResponseEntity<Iterable<User>>(userDBRepository.getAll(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody User entity, @PathVariable Long id) {
        try {
            User user = userDBRepository.findById(id);
            user.setUsername(entity.getUsername());
            user.setPassword(entity.getPassword());
            userDBRepository.update(user);
            return new ResponseEntity<User>(user, HttpStatus.OK);
        } catch (RepositoryException ex) {
            return new ResponseEntity<String>("Can't find the requested entity", HttpStatus.NOT_FOUND);
        }
    }

    //header Content-Type: application/json
    @PostMapping
    public ResponseEntity<?> add(@RequestBody User entity) {
        User user = userDBRepository.add(entity);
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            User user = userDBRepository.findById(id);
            userDBRepository.delete(user);
            return new ResponseEntity<User>(user, HttpStatus.OK);
        } catch (RepositoryException ex) {
            return new ResponseEntity<String>("Can't find the requested entity", HttpStatus.NOT_FOUND);
        }
    }


}
