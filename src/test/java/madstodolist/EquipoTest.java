package madstodolist;

import madstodolist.model.Equipo;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class EquipoTest {
    @Test
    public void crearEquipo() {
        Equipo equipo = new Equipo("Proyecto P1");
        assertThat(equipo.getNombre()).isEqualTo("Proyecto P1");
    }
}
