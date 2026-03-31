package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Vehiculo;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.service.impl.InfractorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import edu.pe.cibertec.infracciones.model.Vehiculo;
import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import java.util.ArrayList;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class InfractorServiceImplTest {

    @Mock
    private MultaRepository multaRepository;

    @InjectMocks
    private InfractorServiceImpl infractorService;

    @Test
    void calcularDeuda_Pregunta1() {

        Long infractorId = 1L;

        Multa multaPendiente = new Multa();
        multaPendiente.setId(10L);
        multaPendiente.setMonto(200.00);
        multaPendiente.setEstado(EstadoMulta.PENDIENTE);

        Multa multaVencida = new Multa();
        multaVencida.setId(20L); // ID ficticio
        multaVencida.setMonto(300.00);
        multaVencida.setEstado(EstadoMulta.VENCIDA);

        List<Multa> multasSimuladas = Arrays.asList(multaPendiente, multaVencida);

        when(multaRepository.findByInfractor_Id(infractorId)).thenReturn(multasSimuladas);

        Double resultadoDeuda = infractorService.calcularDeuda(infractorId);

        assertEquals(545.00, resultadoDeuda);
    }

    // TEST 2
    @Mock
    private InfractorRepository infractorRepository;

    @Test
    void desasignarVehiculo_Pregunta2() {
        // Arrange
        Long infractorId = 1L;
        Long vehiculoId = 1L;

        Infractor infractor = new Infractor();
        infractor.setId(infractorId);

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(vehiculoId);


        List<Vehiculo> vehiculos = new ArrayList<>();
        vehiculos.add(vehiculo);
        infractor.setVehiculos(vehiculos);


        when(infractorRepository.findById(infractorId)).thenReturn(Optional.of(infractor));
        when(multaRepository.existsByInfractor_IdAndVehiculo_IdAndEstado(infractorId, vehiculoId, EstadoMulta.PENDIENTE))
                .thenReturn(false);

        // Act
        infractorService.desAsignarVehiculo(infractorId, vehiculoId);

        // Assert
        assertTrue(infractor.getVehiculos().isEmpty()); // Verifica que se removió
        verify(infractorRepository, times(1)).save(infractor); // Verifica que se guardó
    }
}