package unisa.poultryfarm.fornitoreservice.application.rest;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unisa.poultryfarm.fornitoreservice.application.model.suppliers.DropdownFornitoreDto;
import unisa.poultryfarm.fornitoreservice.application.model.suppliers.SupplierDto;
import unisa.poultryfarm.fornitoreservice.application.model.suppliers.SupplierResponse;
import unisa.poultryfarm.fornitoreservice.application.model.suppliers.UpdateSupplierDto;
import unisa.poultryfarm.fornitoreservice.business.exception.SupplierNotFoundException;
import unisa.poultryfarm.fornitoreservice.business.service.SupplierService;
import unisa.poultryfarm.fornitoreservice.persistence.entity.Fornitore;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for handling operations related to suppliers.
 * Provides APIs for retrieving, creating, and updating Supplier entities.
 */
@RestController
@RequestMapping("/fornitori")
public class FornitoreRestController extends BaseRestController {

    private final SupplierService supplierService;

    @Autowired
    public FornitoreRestController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    /**
     * Retrieves a supplier entity by its unique identifier.
     *
     * @param id the unique identifier of the supplier to retrieve
     * @return a {@code ResponseEntity} containing the supplier entity if found, or a {@code ResponseEntity} with
     *         a 404 Not Found status if the supplier entity does not exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<Fornitore> findById(@PathVariable("id") Long id) {
        final Optional<Fornitore> supplierEntityOptional = this.supplierService.findById(id);
        return supplierEntityOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Retrieves a paginated list of suppliers.
     *
     * @param pageNumber the page number to retrieve; if null, the default value will be used
     * @param pageSize the number of records per page; if null, the default value will be used
     * @return a {@code ResponseEntity} containing the {@code SupplierResponse},
     *         which includes the list of suppliers and pagination details
     */
    @GetMapping("/paginated")
    public ResponseEntity<SupplierResponse> findAllWithPageable(@RequestParam(required = false) Integer pageNumber,
                                                                @RequestParam(required = false) Integer pageSize) {
        SupplierResponse supplierResponse = this.supplierService.findAll(
                this.getPageNumber(pageNumber),
                this.getPageSize(pageSize));

        return ResponseEntity.ok(supplierResponse);
    }

    @GetMapping("/dropdown")
    public ResponseEntity<List<DropdownFornitoreDto>> findAll() {
        return ResponseEntity.ok(this.supplierService.findAll());
    }

    /**
     * Creates a new supplier based on the provided SupplierDto.
     *
     * @param supplierDto the object containing details of the supplier to be created.
     *        Must be valid and include all required fields.
     * @return a {@link ResponseEntity} containing the created {@link Fornitore}
     *         with a status of 201 (Created), or an error response if the operation fails.
     */
    @PostMapping
    public ResponseEntity<Fornitore> createSupplier(@RequestBody @Valid SupplierDto supplierDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.supplierService.createSupplier(supplierDto));
    }

    /**
     * Updates an existing supplier entity with the data provided in the {@code supplierDto}.
     * If the supplier with the provided codice provenienza does not exist, a 404 Not Found
     * response is returned.
     *
     * @param supplierDto the data transfer object containing the updated information for the supplier
     *                    to be updated; must be valid and not null
     * @return the updated supplier entity wrapped in a {@link ResponseEntity} with an HTTP 200 OK status if the
     *         supplier was successfully updated, or an HTTP 404 Not Found status if the supplier does not exist
     */
    @PutMapping
    public ResponseEntity<Fornitore> updateSupplier(@RequestBody @Valid UpdateSupplierDto supplierDto) {
        try {
            Fornitore updatedSupplier = this.supplierService.updateSupplier(supplierDto);
            return ResponseEntity.ok(updatedSupplier);
        } catch (SupplierNotFoundException invalidCodProvenienzaException) {
            return ResponseEntity.notFound().build();
        }
    }

}
