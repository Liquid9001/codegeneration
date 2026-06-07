package nl.codegeneratie.els.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@accountSecurity.isOwner(#accountId) or hasRole('ADMIN') or hasRole('EMPLOYEE')")
public @interface AccountOwnerEmployeeOnly {
}
