package com.admitone.security.interfaces;


import java.util.List;

import javax.annotation.Nullable;
import javax.ejb.Local;
import javax.validation.constraints.NotNull;

import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.Account;
import org.picketlink.idm.model.basic.Role;
import org.picketlink.idm.model.basic.User;



/** <p> Interface for the IdentityManagementService Session Bean </p>

 *
 * <a href="mailto:Stephen.Agneta@bjondinc.com">Steve 'Crash' Agneta</a>
 *
 */

@Local
public interface IIdentityManagementService {
    /////////////////////////////////////////////////////////////////////////
    //                                 Users                               //
    /////////////////////////////////////////////////////////////////////////
    public @Nullable int getUserCount(@NotNull(message="identitymanager must not be null.") IdentityManager identityManager);
    
    public @Nullable User getUserByName(@NotNull(message="identitymanager must not be null.") IdentityManager identityManager, @NotNull(message="userName must not be null.") String userName);
    public @Nullable User getUserByID(@NotNull(message="identitymanager must not be null.") IdentityManager identityManager, @NotNull(message="id must not be null.") String ID);
    public @Nullable User getUserByEmail(@NotNull(message="identitymanager must not be null.") IdentityManager identityManager, @NotNull(message="email must not be null.") String email);
    public @Nullable List<User> getAllUsers(@NotNull(message="identitymanager must not be null.") IdentityManager identityManager, int limit, int offset);
    
    
    /////////////////////////////////////////////////////////////////////////
    //                                 Roles                               //
    /////////////////////////////////////////////////////////////////////////

    public @Nullable Role getRole(@NotNull(message="identitymanager must not be null.") IdentityManager identityManager, @NotNull(message="name must not be null.") String name);
    public @Nullable Role getRoleByID(@NotNull(message="identitymanager must not be null.") IdentityManager identityManager, @NotNull(message="id must not be null.") String id);
    public @NotNull(message="return must not be null.") List<Role> getAllRoles(@NotNull(message="identitymanager must not be null.") IdentityManager identityManager);
    public boolean hasRole(@NotNull(message="user must not be null.") User user, @NotNull(message="role must not be null.") Role role);
    public boolean hasRole(@NotNull(message="identitymanager must not be null.") IdentityManager identityManger, @NotNull(message="user must not be null.") Account user, @NotNull(message="roleName must not be null.") String roleName);
    public boolean hasRole(@NotNull(message="identitymanager must not be null.") IdentityManager identityManager,@NotNull(message="user must not be null.")   Account user,@NotNull(message="role must not be null.")  Role role);
    public boolean hasOneOfRoles(@NotNull(message="identitymanger must not be null.") IdentityManager identityManager, @NotNull(message="user must not be null.") User user, Role ... roles);

}



