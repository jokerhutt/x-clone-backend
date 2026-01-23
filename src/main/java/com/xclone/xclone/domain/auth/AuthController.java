package com.xclone.xclone.domain.auth;

import com.xclone.xclone.domain.user.UserService;
import com.xclone.xclone.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, UserService userService, JwtService jwtService) {
        this.authService = authService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> authenticateWithGoogle(@RequestBody Map<String, String> body) {
        String accessToken = body.get("token");

        User authenticatedUser = authService.authenticateGoogleUser(accessToken);
        UserDTO dtoToReturn = userService.generateUserDTOByUserId(authenticatedUser.getId());

        String token = jwtService.createToken(dtoToReturn.id);
        return ResponseEntity.ok(Map.of(
                "token", token,
                "user", dtoToReturn
        ));
    }

    @PostMapping("/update-profile")
    public ResponseEntity<?> updateProfile(
            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture,
            @RequestParam(value = "bannerImage", required = false) MultipartFile bannerImage,
            @RequestParam("displayName") String displayName,
            @RequestParam("username") String username,
            @RequestParam("bio") String bio,
            Authentication auth
    ) throws IOException {
        Integer authUserId = (Integer) auth.getPrincipal();
        System.out.println("Authenticating user:" + authUserId);
        userService.updateUserProfile(authUserId, profilePicture, bannerImage, displayName, username, bio);

        return ResponseEntity.ok(userService.generateUserDTOByUserId(authUserId));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getAuthenticatedUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        String token = authHeader.replace("Bearer ", "");
        if (!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        int userId = jwtService.extractUserId(token);
        UserDTO dto = userService.generateUserDTOByUserId(userId);

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/demo-signup")
    public ResponseEntity<?> authenticateWithTempSignup() {
        User newTempuser = authService.registerTemporaryUser();
        UserDTO dtoToReturn = userService.generateUserDTOByUserId(newTempuser.getId());
        String token = jwtService.createToken(newTempuser.getId());
        return ResponseEntity.ok(Map.of("token", token, "user", dtoToReturn));
    }

}