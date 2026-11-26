package com.cardtrade.config;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Al arrancar deja lista una cuenta administradora de DESARROLLO (por defecto foc / foc):
 *  - si no existe, la crea con rol ADMINISTRADOR y su carrito;
 *  - si existe pero la contrasena no coincide o no es administrador, la repara.
 * La contrasena se guarda siempre con BCrypt (el mismo codificador que usa el login).
 *
 * Se configura en application.properties (app.admin-inicial.*). En un entorno real hay que
 * cambiar la contrasena o poner app.admin-inicial.habilitado=false.
 */
@Component
public class AdminInicial implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInicial.class);

    private final JdbcClient jdbc;
    private final BCryptPasswordEncoder encoder;

    @Value("${app.admin-inicial.habilitado:true}")
    private boolean habilitado;

    @Value("${app.admin-inicial.usuario:foc}")
    private String usuario;

    @Value("${app.admin-inicial.password:foc}")
    private String password;

    @Value("${app.admin-inicial.email:foc@cardtrade.local}")
    private String email;

    public AdminInicial(JdbcClient jdbc, BCryptPasswordEncoder encoder) {
        this.jdbc = jdbc;
        this.encoder = encoder;
    }

    @Override
    public void run(ApplicationArguments args) {

        if (!habilitado) {
            log.info("Cuenta administradora inicial desactivada (app.admin-inicial.habilitado=false).");
            return;
        }

        try {
            asegurarAdministrador();
        } catch (DataAccessException e) {
            // No se oculta el error: sin base de datos la aplicacion no tiene sentido
            log.error("No se pudo preparar la cuenta administradora '{}'. Comprueba que la base de datos "
                    + "esta iniciada, que la contrasena de application.properties es correcta y que se "
                    + "ejecuto instalar_base_datos.bat. Causa: {}", usuario, e.getMostSpecificCause().getMessage());
            throw new IllegalStateException(
                    "No se pudo conectar con la base de datos o preparar la cuenta administradora", e);
        }
    }

    private void asegurarAdministrador() {

        Map<String, Object> actual = jdbc.sql("SELECT id, password, rol FROM Usuario WHERE usuario = ?")
                .param(usuario)
                .query()
                .listOfRows()
                .stream()
                .findFirst()
                .orElse(null);

        long id;

        if (actual == null) {
            KeyHolder clave = new GeneratedKeyHolder();
            jdbc.sql("INSERT INTO Usuario (usuario, email, password, rol) VALUES (?, ?, ?, 'ADMINISTRADOR')")
                    .params(usuario, email, encoder.encode(password))
                    .update(clave);
            id = clave.getKey().longValue();
            log.warn("Cuenta administradora de desarrollo '{}' CREADA.", usuario);
        } else {
            id = ((Number) actual.get("id")).longValue();

            if (!encoder.matches(password, (String) actual.get("password"))) {
                jdbc.sql("UPDATE Usuario SET password = ? WHERE id = ?")
                        .params(encoder.encode(password), id)
                        .update();
                log.warn("Contrasena de la cuenta '{}' REPARADA.", usuario);
            }

            if (!"ADMINISTRADOR".equals(String.valueOf(actual.get("rol")))) {
                jdbc.sql("UPDATE Usuario SET rol = 'ADMINISTRADOR' WHERE id = ?")
                        .param(id)
                        .update();
                log.warn("La cuenta '{}' pasa a ser ADMINISTRADOR.", usuario);
            }
        }

        // Carrito (usuario_id es UNIQUE: si ya existe no se duplica)
        jdbc.sql("INSERT IGNORE INTO Carrito(usuario_id) VALUES (?)")
                .param(id)
                .update();

        log.warn("Cuenta de desarrollo '{}' lista. Cambia su contrasena o desactivala "
                + "(app.admin-inicial.habilitado=false) fuera del entorno de desarrollo.", usuario);
    }
}
