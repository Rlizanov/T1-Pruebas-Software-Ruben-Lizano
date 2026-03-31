package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Vehiculo;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.service.impl.MultaServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MultaServiceImplTest {

    @Mock
    private MultaRepository multaRepository;

    @Mock
    private InfractorRepository infractorRepository;

    @InjectMocks
    private MultaServiceImpl multaService;

    @Test
    void transferirMulta_Pregunta3() {
        // Arrange
        Long multaId = 1L;
        Long nuevoInfractorId = 2L;

        // Vehículo involucrado
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(10L);

        // Infractor A (Actual)
        Infractor infractorA = new Infractor();
        infractorA.setId(1L);

        // Multa PENDIENTE asignada al Infractor A y al Vehículo 10L
        Multa multa = new Multa();
        multa.setId(multaId);
        multa.setEstado(EstadoMulta.PENDIENTE);
        multa.setInfractor(infractorA);
        multa.setVehiculo(vehiculo);

        // Infractor B (Nuevo), NO bloqueado y TIENE el vehículo
        Infractor infractorB = new Infractor();
        infractorB.setId(nuevoInfractorId);
        infractorB.setBloqueado(false);
        List<Vehiculo> vehiculosB = new ArrayList<>();
        vehiculosB.add(vehiculo);
        infractorB.setVehiculos(vehiculosB);

        // Simulamos la BD
        when(multaRepository.findById(multaId)).thenReturn(Optional.of(multa));
        when(infractorRepository.findById(nuevoInfractorId)).thenReturn(Optional.of(infractorB));

        // Act
        multaService.transferirMulta(multaId, nuevoInfractorId);

        // Assert
        assertEquals(nuevoInfractorId, multa.getInfractor().getId());
        verify(multaRepository, times(1)).save(multa);
    }

    @Captor
    private ArgumentCaptor<Multa> multaCaptor;

    @Test
    void transferirMulta_LanzaExcepcion_Pregunta4() {

        Long multaId = 1L;
        Long nuevoInfractorId = 2L;

        // Multa PENDIENTE (cumple la regla 1)
        Multa multa = new Multa();
        multa.setId(multaId);
        multa.setEstado(EstadoMulta.PENDIENTE);

        // Infractor B está BLOQUEADO (esto debe hacer fallar)
        Infractor infractorB = new Infractor();
        infractorB.setId(nuevoInfractorId);
        infractorB.setBloqueado(true);

        when(multaRepository.findById(multaId)).thenReturn(Optional.of(multa));
        when(infractorRepository.findById(nuevoInfractorId)).thenReturn(Optional.of(infractorB));

        // Act & Assert

        Exception excepcion = Assertions.assertThrows(RuntimeException.class, () -> {
            multaService.transferirMulta(multaId, nuevoInfractorId);
        });

        assertEquals("El nuevo infractor está bloqueado", excepcion.getMessage());

        verify(multaRepository, never()).save(any(Multa.class));

        verify(multaRepository, times(0)).save(multaCaptor.capture());
    }
}