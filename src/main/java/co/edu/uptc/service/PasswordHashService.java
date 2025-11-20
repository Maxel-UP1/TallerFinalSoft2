package co.edu.uptc.service;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Servicio para hashear y verificar contraseñas - Parte 3
 * Implementa múltiples algoritmos: BCrypt, Argon2, PBKDF2, SHA-512
 */
@Service
public class PasswordHashService {

    private final BCryptPasswordEncoder bCryptEncoder;
    private final Argon2 argon2;
    private final SecureRandom secureRandom;

    public PasswordHashService() {
        this.bCryptEncoder = new BCryptPasswordEncoder(12); // Costo 12 para mayor seguridad
        this.argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        this.secureRandom = new SecureRandom();
    }

    /**
     * Enum para los algoritmos de hash soportados
     */
    public enum HashAlgorithm {
        BCRYPT, ARGON2, PBKDF2, SHA512
    }

    /**
     * Hashea una contraseña usando el algoritmo especificado
     */
    public String hashPassword(String plainPassword, HashAlgorithm algorithm) {
        switch (algorithm) {
            case BCRYPT:
                return hashWithBCrypt(plainPassword);
            case ARGON2:
                return hashWithArgon2(plainPassword);
            case PBKDF2:
                return hashWithPBKDF2(plainPassword);
            case SHA512:
                return hashWithSHA512(plainPassword);
            default:
                throw new IllegalArgumentException("Algoritmo no soportado: " + algorithm);
        }
    }

    /**
     * Verifica una contraseña contra un hash usando el algoritmo especificado
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword, HashAlgorithm algorithm) {
        switch (algorithm) {
            case BCRYPT:
                return verifyBCrypt(plainPassword, hashedPassword);
            case ARGON2:
                return verifyArgon2(plainPassword, hashedPassword);
            case PBKDF2:
                return verifyPBKDF2(plainPassword, hashedPassword);
            case SHA512:
                return verifySHA512(plainPassword, hashedPassword);
            default:
                throw new IllegalArgumentException("Algoritmo no soportado: " + algorithm);
        }
    }

    /**
     * BCrypt - Algoritmo recomendado para Spring Security
     */
    private String hashWithBCrypt(String plainPassword) {
        String hashed = bCryptEncoder.encode(plainPassword);
        System.out.println("🔒 BCRYPT Hash generado: " + hashed);
        return hashed;
    }

    private boolean verifyBCrypt(String plainPassword, String hashedPassword) {
        boolean matches = bCryptEncoder.matches(plainPassword, hashedPassword);
        System.out.println("✅ BCRYPT Verificación: " + (matches ? "EXITOSA" : "FALLIDA"));
        return matches;
    }

    /**
     * Argon2 - Algoritmo ganador de la Password Hashing Competition
     */
    private String hashWithArgon2(String plainPassword) {
        String hashed = argon2.hash(4, 1024 * 1024, 8, plainPassword.toCharArray());
        System.out.println("🔒 ARGON2 Hash generado: " + hashed);
        return hashed;
    }

    private boolean verifyArgon2(String plainPassword, String hashedPassword) {
        boolean matches = argon2.verify(hashedPassword, plainPassword.toCharArray());
        System.out.println("✅ ARGON2 Verificación: " + (matches ? "EXITOSA" : "FALLIDA"));
        // Limpiar la memoria
        argon2.wipeArray(plainPassword.toCharArray());
        return matches;
    }

    /**
     * PBKDF2 - Password-Based Key Derivation Function 2
     */
    private String hashWithPBKDF2(String plainPassword) {
        byte[] salt = new byte[32];
        secureRandom.nextBytes(salt);

        try {
            PBEKeySpec spec = new PBEKeySpec(plainPassword.toCharArray(), salt, 100000, 256);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();

            // Combinar salt + hash y codificar en Base64
            byte[] hashWithSalt = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, hashWithSalt, 0, salt.length);
            System.arraycopy(hash, 0, hashWithSalt, salt.length, hash.length);

            String hashed = Base64.getEncoder().encodeToString(hashWithSalt);
            System.out.println("🔒 PBKDF2 Hash generado: " + hashed);
            return hashed;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error generando hash PBKDF2", e);
        }
    }

    private boolean verifyPBKDF2(String plainPassword, String hashedPassword) {
        try {
            byte[] hashWithSalt = Base64.getDecoder().decode(hashedPassword);

            // Extraer salt (primeros 32 bytes)
            byte[] salt = new byte[32];
            System.arraycopy(hashWithSalt, 0, salt, 0, 32);

            // Extraer hash (resto de bytes)
            byte[] storedHash = new byte[hashWithSalt.length - 32];
            System.arraycopy(hashWithSalt, 32, storedHash, 0, storedHash.length);

            // Generar hash con la misma sal
            PBEKeySpec spec = new PBEKeySpec(plainPassword.toCharArray(), salt, 100000, 256);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] testHash = factory.generateSecret(spec).getEncoded();

            boolean matches = MessageDigest.isEqual(storedHash, testHash);
            System.out.println("✅ PBKDF2 Verificación: " + (matches ? "EXITOSA" : "FALLIDA"));
            return matches;
        } catch (Exception e) {
            System.out.println("❌ Error verificando PBKDF2: " + e.getMessage());
            return false;
        }
    }

    /**
     * SHA-512 con salt - Menos seguro pero rápido
     */
    private String hashWithSHA512(String plainPassword) {
        try {
            byte[] salt = new byte[32];
            secureRandom.nextBytes(salt);

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] hashedPassword = md.digest(plainPassword.getBytes(StandardCharsets.UTF_8));

            // Combinar salt + hash
            byte[] hashWithSalt = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, hashWithSalt, 0, salt.length);
            System.arraycopy(hashedPassword, 0, hashWithSalt, salt.length, hashedPassword.length);

            String hashed = Base64.getEncoder().encodeToString(hashWithSalt);
            System.out.println("🔒 SHA512 Hash generado: " + hashed);
            return hashed;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generando hash SHA-512", e);
        }
    }

    private boolean verifySHA512(String plainPassword, String hashedPassword) {
        try {
            byte[] hashWithSalt = Base64.getDecoder().decode(hashedPassword);

            // Extraer salt (primeros 32 bytes)
            byte[] salt = new byte[32];
            System.arraycopy(hashWithSalt, 0, salt, 0, 32);

            // Extraer hash almacenado
            byte[] storedHash = new byte[hashWithSalt.length - 32];
            System.arraycopy(hashWithSalt, 32, storedHash, 0, storedHash.length);

            // Generar hash con la misma sal
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] testHash = md.digest(plainPassword.getBytes(StandardCharsets.UTF_8));

            boolean matches = MessageDigest.isEqual(storedHash, testHash);
            System.out.println("✅ SHA512 Verificación: " + (matches ? "EXITOSA" : "FALLIDA"));
            return matches;
        } catch (Exception e) {
            System.out.println("❌ Error verificando SHA-512: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método de utilidad para obtener el algoritmo por nombre
     */
    public HashAlgorithm getAlgorithmByName(String name) {
        try {
            return HashAlgorithm.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return HashAlgorithm.BCRYPT; // Algoritmo por defecto
        }
    }

    /**
     * Obtener información sobre los algoritmos disponibles
     */
    public String getAlgorithmInfo() {
        return """
                📋 ALGORITMOS DE HASH DISPONIBLES:
                🔹 BCRYPT: Recomendado, adaptativo, resistente a ataques
                🔹 ARGON2: Más seguro, ganador PHC, resistente a GPU/ASIC
                🔹 PBKDF2: Estándar NIST, buena compatibilidad
                🔹 SHA512: Rápido, menos seguro, solo para compatibilidad
                """;
    }

    /**
     * Obtener lista de algoritmos disponibles para formularios web
     */
    public String[] getAvailableAlgorithms() {
        return new String[] { "bcrypt", "argon2", "pbkdf2", "sha512" };
    }
}