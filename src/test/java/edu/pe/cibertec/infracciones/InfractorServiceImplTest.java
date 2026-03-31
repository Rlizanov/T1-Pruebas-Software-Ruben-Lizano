package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.service.impl.InfractorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InfractorServiceImplTest {

    @Mock
    private MultaRepository multaRepository;

    @InjectMocks
    private InfractorServiceImpl infractorService;

    @Test
    void calcularDeuda() {

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
}