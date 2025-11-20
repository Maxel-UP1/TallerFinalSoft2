package co.edu.uptc.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/secure")
public class SecureController {

    @GetMapping("/admin-area")
    @RequiresRoles("admin")
    public String adminArea() {
        return "Eres admin (endpoint protegido con @RequiresRoles)";
    }

    @GetMapping("/manager-area")
    @RequiresRoles("manager")
    public String managerArea() {
        return "Eres manager (endpoint protegido con @RequiresRoles)";
    }

    @PostMapping("/productos")
    @RequiresPermissions("producto:create")
    public String crearProducto() {
        return "Producto creado (requiere producto:create)";
    }

    @PutMapping("/productos/{id}")
    @RequiresPermissions("producto:update")
    public String editarProducto(@PathVariable Long id) {
        return "Producto editado (requiere producto:update)";
    }

    @DeleteMapping("/productos/{id}")
    @RequiresPermissions("producto:delete")
    public String eliminarProducto(@PathVariable Long id) {
        return "Producto eliminado (requiere producto:delete)";
    }
}
