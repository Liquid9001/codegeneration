package nl.codegeneratie.els.service;

import nl.codegeneratie.els.domain.AtmSession;
import nl.codegeneratie.els.dtos.AtmSessionDTO;
import nl.codegeneratie.els.repository.AtmSessionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AtmSessionService {

    private final AtmSessionRepository atmSessionRepository;

    public AtmSessionService(AtmSessionRepository atmSessionRepository) {
        this.atmSessionRepository = atmSessionRepository;
    }

    public List<AtmSessionDTO> getAllAtmSessions() {
        return atmSessionRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public AtmSessionDTO createAtmSession(AtmSessionDTO atmSessionDTO) {
        AtmSession atmSession = new AtmSession();
        BeanUtils.copyProperties(atmSessionDTO, atmSession);
        AtmSession savedSession = atmSessionRepository.save(atmSession);
        return convertToDTO(savedSession);
    }

    public AtmSessionDTO updateAtmSession(Long id, AtmSessionDTO atmSessionDTO) {
        AtmSession atmSession = atmSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ATM Session not found with id: " + id));
        BeanUtils.copyProperties(atmSessionDTO, atmSession, "id");
        AtmSession updatedSession = atmSessionRepository.save(atmSession);
        return convertToDTO(updatedSession);
    }

    public void deleteAtmSession(Long id) {
        AtmSession atmSession = atmSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ATM Session not found with id: " + id));
        atmSessionRepository.delete(atmSession);
    }

    private AtmSessionDTO convertToDTO(AtmSession atmSession) {
        AtmSessionDTO atmSessionDTO = new AtmSessionDTO();
        BeanUtils.copyProperties(atmSession, atmSessionDTO);
        return atmSessionDTO;
    }
}

