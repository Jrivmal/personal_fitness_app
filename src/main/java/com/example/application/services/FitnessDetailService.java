package com.example.application.services;

import com.example.application.data.FitnessDetail;
import com.example.application.data.FitnessDetailRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class FitnessDetailService {

    private final FitnessDetailRepository repository;

    public FitnessDetailService(FitnessDetailRepository repository) {
        this.repository = repository;
    }

    public Optional<FitnessDetail> get(Long id) {
        return repository.findById(id);
    }

    public FitnessDetail update(FitnessDetail entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<FitnessDetail> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<FitnessDetail> list(Pageable pageable, Specification<FitnessDetail> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
