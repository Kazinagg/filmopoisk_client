package org.example.filmopoisk_client.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.example.filmopoisk_client.entity.DTO.MarkedFilmRequest;
import org.example.filmopoisk_client.entity.User;
import org.example.filmopoisk_client.repository.UserRepository;
import org.example.filmopoisk_client.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/marked")
@SecurityRequirement(name = "bearerAuth")
public class MarkedController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil; // Добавляем jwtUtil

    @PostMapping("/add")
    public ResponseEntity<?> addMarkedFilm(@RequestBody MarkedFilmRequest markedFilmRequest, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }

        String jwt = authorizationHeader.substring(7);
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
    public ResponseEntity<?> getMarkedFilms(HttpServletRequest request) {
        System.out.println(request);
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }
        System.out.println(request);
        String jwt = authorizationHeader.substring(7);
        Integer userId = jwtUtil.extractUserId(jwt);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found 63");
        }

        List<User.MarkedFilm> markedFilms = user.getMarked();
        return ResponseEntity.ok(markedFilms);
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeMarkedFilm(@RequestBody MarkedFilmRequest markedFilmRequest, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }

        String jwt = authorizationHeader.substring(7);
        Integer userId = jwtUtil.extractUserId(jwt);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User.MarkedFilm markedFilmToRemove = user.getMarked().stream()
                .filter(markedFilm -> markedFilm.getKinopoiskId().equals(markedFilmRequest.getKinopoiskId()))
                .findFirst()
                .orElse(null);

        if (markedFilmToRemove == null) {
            return ResponseEntity.badRequest().body("Marked film not found");
        }

        user.getMarked().remove(markedFilmToRemove);
        userRepository.save(user);

        return ResponseEntity.ok("Marked film removed successfully");
    }


}
