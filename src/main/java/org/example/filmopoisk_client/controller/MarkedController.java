package org.example.filmopoisk_client.controller;

import org.example.filmopoisk_client.entity.DTO.MarkedFilmRequest;
import org.example.filmopoisk_client.entity.User;
import org.example.filmopoisk_client.repository.UserRepository;
import org.example.filmopoisk_client.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/marked")
public class MarkedController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil; // Добавляем jwtUtil

    @PostMapping("/add")
    public ResponseEntity<?> addMarkedFilm(@RequestBody MarkedFilmRequest markedFilmRequest, @RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Integer userId = jwtUtil.extractUserId(jwt);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User.MarkedFilm markedFilm = new User.MarkedFilm();
        markedFilm.setKinopoiskId(markedFilmRequest.getKinopoiskId());
        markedFilm.setTypeMarked(markedFilmRequest.getTypeMarked());

        user.getMarked().add(markedFilm);
        userRepository.save(user);

        return ResponseEntity.ok("Marked film added successfully");
    }

    @PostMapping("/get")
    public ResponseEntity<?> getMarkedFilms(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Integer userId = jwtUtil.extractUserId(jwt);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        List<User.MarkedFilm> markedFilms = user.getMarked();
        return ResponseEntity.ok(markedFilms);
    }

    @PostMapping("/get-by-type")
    public ResponseEntity<?> getMarkedFilmsByType(@RequestBody GetMarkedFilmsByTypeRequest request, @RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Integer userId = jwtUtil.extractUserId(jwt);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        List<User.MarkedFilm> markedFilms = user.getMarked().stream()
                .filter(film -> film.getTypeMarked().equals(request.getTypeMarked()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(markedFilms);
    }


    // Вспомогательный класс для запроса по типу
    public static class GetMarkedFilmsByTypeRequest {
        private Integer userId;
        private Integer typeMarked;

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getTypeMarked() {
            return typeMarked;
        }

        public void setTypeMarked(Integer typeMarked) {
            this.typeMarked = typeMarked;
        }
    }
}
