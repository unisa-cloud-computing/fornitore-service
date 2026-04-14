package unisa.poultryfarm.fornitoreservice.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unisa.poultryfarm.fornitoreservice.application.model.PageInfo;
import unisa.poultryfarm.fornitoreservice.application.model.suppliers.DropdownFornitoreDto;
import unisa.poultryfarm.fornitoreservice.application.model.suppliers.SupplierDto;
import unisa.poultryfarm.fornitoreservice.application.model.suppliers.SupplierResponse;
import unisa.poultryfarm.fornitoreservice.application.model.suppliers.UpdateSupplierDto;
import unisa.poultryfarm.fornitoreservice.business.exception.SupplierNotFoundException;
import unisa.poultryfarm.fornitoreservice.business.mapper.FornitoreMapper;
import unisa.poultryfarm.fornitoreservice.persistence.entity.Fornitore;
import unisa.poultryfarm.fornitoreservice.persistence.repository.FornitoreRepository;
import java.util.List;
import java.util.Optional;

/**
 * Service class for handling operations related to suppliers.
 * This class provides methods for finding, creating, updating, and retrieving suppliers
 * with support for pagination and validation of supplier data.
 */
@Service
@RequiredArgsConstructor
public class SupplierService {

    private final FornitoreRepository fornitoreRepository;
    private final FornitoreMapper fornitoreMapper;
    private static final String SUPPLIER_NOT_FOUND_MESSAGE = "supplier with codice provenienza %s not found";

    /**
     * Finds a SupplierEntity by its unique id.
     *
     * @param id of the supplier to be retrieved, must not be null.
     * @return an Optional containing the SupplierEntity if found, or an empty Optional if not found.
     */
    @Transactional(readOnly = true)
    public Optional<Fornitore> findById(Long id) {
        return this.fornitoreRepository.findById(id);
    }

    /**
     * Retrieves a paginated list of supplier entities and relevant paging information.
     *
     * @param pageNumber the page number to retrieve, zero-based index
     * @param pageSize   the number of records to include in a single page
     * @return a {@code SupplierResponse} object containing the list of supplier entities
     *         and page information such as total pages, total elements, page number, and page size
     */
    @Transactional(readOnly = true)
    public SupplierResponse findAll(Integer pageNumber,
                                    Integer pageSize) {
        Page<Fornitore> page = this.fornitoreRepository.findAll(PageRequest.of(pageNumber, pageSize));
        return SupplierResponse
                .builder()
                .supplierEntities(page.toList())
                .pageInfo(PageInfo
                        .builder()
                        .totalPages(page.getTotalPages())
                        .totalElements(page.getTotalElements())
                        .pageNumber(page.getPageable().getPageNumber())
                        .pageSize(page.getPageable().getPageSize())
                        .build())
                .build();
    }

    @Transactional(readOnly = true)
    public List<DropdownFornitoreDto> findAll() {
        return this.fornitoreRepository.findAll().stream()
                .map(fornitoreMapper::supplierEntityToDropdownFornitoreDto)
                .toList();
    }

    /**
     * Creates a new supplier using the provided SupplierDto object.
     * Validates the supplier's codice provenienza before saving the new supplier entity.
     *
     * @param supplierDto the data transfer object containing supplier details
     * @return the created SupplierEntity object after persistence
     */
    @Transactional
    public Fornitore createSupplier(SupplierDto supplierDto) {
        Fornitore fornitore = fornitoreMapper.supplierDtoToSupplierEntity(supplierDto);
        return this.fornitoreRepository.save(fornitore);
    }

    /**
     * Updates an existing supplier in the system based on the provided SupplierDto.
     * The method retrieves the existing supplier by its codice provenienza (codProv) from the database,
     * updates its details using the provided SupplierDto, and saves the updated supplier.
     * If the supplier with the specified codice provenienza is not found, a CertiniSupplierNotFoundException is thrown.
     *
     * @param updateSupplierDto the data transfer object containing the updated information of the supplier
     * @return the updated SupplierEntity after it is saved in the database
     * @throws SupplierNotFoundException if a supplier with the specified codice provenienza is not found
     */
    @Transactional
    public Fornitore updateSupplier(UpdateSupplierDto updateSupplierDto) throws SupplierNotFoundException {
        return this.fornitoreRepository.findById(updateSupplierDto.getId())
                .map(supplierFromDb -> {
                    Fornitore supplierToBeUpdated = fornitoreMapper.updateSupplier(supplierFromDb, updateSupplierDto);
                    return this.fornitoreRepository.save(supplierToBeUpdated);
                })
                .orElseThrow(() -> new SupplierNotFoundException(
                        String.format(SUPPLIER_NOT_FOUND_MESSAGE, updateSupplierDto.getCodiceProvenienza())
                ));
    }
}
