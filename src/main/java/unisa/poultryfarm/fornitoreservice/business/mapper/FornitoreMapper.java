package unisa.poultryfarm.fornitoreservice.business.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import unisa.poultryfarm.fornitoreservice.application.model.suppliers.DropdownFornitoreDto;
import unisa.poultryfarm.fornitoreservice.application.model.suppliers.SupplierDto;
import unisa.poultryfarm.fornitoreservice.application.model.suppliers.UpdateSupplierDto;
import unisa.poultryfarm.fornitoreservice.persistence.entity.Fornitore;

/**
 * Mapper interface for converting between {@code SupplierDto} and {@code SupplierEntity}.
 * This interface leverages MapStruct to facilitate mapping between the application's DTO layer
 * and persistence layer, ensuring a clean separation of responsibilities.
 *
 * The mapping strategy used is constructor-based injection for ensuring dependencies are
 * properly managed within a Spring application context.
 */
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface FornitoreMapper {

    /**
     * Maps a {@code SupplierDto} to a {@code SupplierEntity}.
     * This method is used to convert data from the application's DTO layer
     * to the persistence layer, ensuring proper data representation for storage
     * or further processing.
     *
     * @param supplierDto the {@code SupplierDto} object containing data to be mapped.
     *                    It must include supplier details such as codice provenienza,
     *                    partita IVA, telefono, and indirizzo.
     * @return a {@code SupplierEntity} object representing the mapped data
     *         from the provided {@code SupplierDto}.
     */
    Fornitore supplierDtoToSupplierEntity(SupplierDto supplierDto);
    /**
     * Updates the specified {@code SupplierEntity} with the details provided in the {@code SupplierDto}.
     * This method merges data from the {@code SupplierDto} into the target {@code SupplierEntity},
     * enabling partial or complete updates. Fields from the DTO will override corresponding fields
     * in the entity, preserving other existing data.
     *
     * @param fornitoreToBeUpdated the target {@code SupplierEntity} to be updated
     * @param supplierDto the source {@code SupplierDto} containing the updated data
     * @return the updated {@code SupplierEntity} after applying the changes from the {@code SupplierDto}
     */
    Fornitore updateSupplier(@MappingTarget Fornitore fornitoreToBeUpdated, UpdateSupplierDto supplierDto);


    DropdownFornitoreDto supplierEntityToDropdownFornitoreDto(Fornitore fornitore);

}
