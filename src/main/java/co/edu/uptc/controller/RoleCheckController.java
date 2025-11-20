package co.edu.uptc.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoleCheckController {

    @GetMapping("/api/checkRole")
    public String checkRole(@RequestParam String role) {
        Subject currentUser = SecurityUtils.getSubject();
        boolean has = currentUser.hasRole(role);
        return has ? "Eres " + role : "No eres " + role;
    }
}
