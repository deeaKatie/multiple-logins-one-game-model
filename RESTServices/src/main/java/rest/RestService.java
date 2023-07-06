package rest;

import exception.RepositoryException;
import model.Game;
import repository.GameDBRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/services/games")
public class RestService {

    @Autowired
    private GameDBRepository gameDBRepository;

    @GetMapping("/{id}")
    private ResponseEntity<?> getGame(@PathVariable Long id) {
        try {
            Game game = gameDBRepository.findById(id);
            return new ResponseEntity<Game>(game, HttpStatus.OK);
        } catch (RepositoryException ex) {
            return new ResponseEntity<String>("Can't find the game", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public Iterable<Game> getAllGames() {
        Game game = new Game();
        gameDBRepository.add(game);
        var games = gameDBRepository.getAll();
        System.out.println("GA<ES");
        for (var g : games) {
            System.out.println(g);
        }
        System.out.println("END<ES");
        return games;
    }
}
