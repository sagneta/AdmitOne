/*  Copyright (c) 2013
 *  by Bjnd Health, Inc., Boston, MA
 *
 *  This software is furnished under a license and may be used only in
 *  accordance with the terms of such license.  This software may not be
 *  provided or otherwise made available to any other party.  No title to
 *  nor ownership of the software is hereby transferred.
 *
 *  This software is the intellectual property of Bjönd, Inc.,
 *  and is protected by the copyright laws of the United States of America.
 *  All rights reserved internationally.
 *
 */

package com.admitone.security;

import static fj.data.Array.array;

import static javax.ejb.TransactionAttributeType.REQUIRED;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
// Cryptography
import java.security.Security;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.credential.storage.EncodedPasswordStorage;
import org.picketlink.idm.model.Account;
import org.picketlink.idm.model.basic.Realm;
import org.picketlink.idm.model.basic.Role;
import org.picketlink.idm.model.basic.User;
import org.picketlink.idm.query.Condition;
import org.picketlink.idm.query.IdentityQuery;
import org.picketlink.idm.query.IdentityQueryBuilder;

import com.admitone.security.cdi.BasicModel;
import com.admitone.security.interfaces.IIdentityManagementService;

// Functional Java
import fj.F;

import lombok.val;
import lombok.extern.slf4j.Slf4j;


/**
 * <p>The Identity Management Service manages identities (Users) and related security items such as
 Roles, Groups, GroupRoles, Partitions, Realms, Tiers and Relationships.
 </p>
 *
 * @author sagneta
 * @author
 * @version
 */
@Stateless
@TransactionAttribute(REQUIRED)
@Slf4j
public class IdentityManagementService implements IIdentityManagementService
{
    // @PersistenceContext(unitName = "admitone", type=PersistenceContextType.TRANSACTION)
    // @Getter private EntityManager entityManager;

	@Inject 
    private PartitionManager partitionManager;

    @PostConstruct
    protected void startService()
    {
        log.debug("IdentityManagementService has begun and will initialize the Authentication and Authorization System.  ");

        //Cryptography provided by the Legion of the Bouncy Castle
        // http://www.bouncycastle.org/
        try {
            Security.addProvider(new BouncyCastleProvider());
        } catch(final SecurityException e){
            log.info("Bouncy Castle already added previously. Continuing...");
        }

        log.debug("IdentityManagementService has started successfully.                                         ");
    }







    /////////////////////////////////////////////////////////////////////////
    //                            Public Interface                         //
    /////////////////////////////////////////////////////////////////////////






    /**
     *  <code>getRealmByID</code> method will return the realm by ID.
     *  If none exists NULL is returned.
     *
     * @param ID a <code>String</code> value
     * @return a <code>Realm</code> value
     */
    
    public @Nullable Realm getRealmByID(@NotNull(message="id must not be null.") String ID) {
        return partitionManager.lookupById(Realm.class, ID);
    }


    /**
     *  <code>getRealm</code> method will return the Realm given the realm name.
     *
     *
     * @param realmName a <code>String</code> value
     * @return a <code>Realm</code> value
     */
    public @Nullable Realm getRealm(@NotNull(message="realmName must not be null.") String realmName){

        return partitionManager.getPartition(Realm.class, realmName);
    }

    /**
     *  <code>forRealm</code> method generates an IdentityManager given the realm.
     *
     * @param realm a <code>Realm</code> value
     * @return an <code>IdentityManager</code> value
     */
    public @Nullable IdentityManager forRealm(@NotNull(message="realm must not be null.") Realm realm){
        return partitionManager.createIdentityManager(realm);
    }


    /**
     *  <code>getRole</code> method returns the role with the name passed as a parameter.
     *
     * @param identityManager an <code>IdentityManager</code> value
     * @param name a <code>String</code> value
     * @return a <code>Role</code> value
     */
    public @Nullable Role getRole(@NotNull(message="identitymanager must not be null.") IdentityManager identityManager,@NotNull(message="name must not be null.")  String name){
        return BasicModel.getRole(identityManager, name);
    }

    /**
     *  <code>getRoleByID</code> method returns the role that corresponds with ID.
     *
     * @param identityManager an <code>IdentityManager</code> value
     * @param ID a <code>String</code> value
     * @return a <code>Role</code> value
     */
    public @Nullable Role getRoleByID(@NotNull(message="identitymanager must not be null.") IdentityManager identityManager, @NotNull(message="id must not be null.") String ID){
        return identityManager.lookupById(Role.class, ID);
    }


    /**
     *  <code>getAllRoles</code> method returns all roles for identityManager passed as a parameter.
     *
     * @param identityManager an <code>IdentityManager</code> value
     * @return a <code>List<Role></code> value
     */
    public  List<Role> getAllRoles(@NotNull(message="identitymanager must not be null.") IdentityManager identityManager) {
        return identityManager.getQueryBuilder().createIdentityQuery(Role.class).getResultList();
    }

    /**
     *  <code>hasOneOfRoles</code> method returns true if user has at least one of the roles.
     *
     * @param identityManager an <code>IdentityManager</code> value
     * @param user an <code>User</code> value
     * @param ... a <code>Role</code> value
     * @return a <code>boolean</code> value
     */
    public boolean hasOneOfRoles( final IdentityManager identityManager,  final User user, Role ... roles){
        return array(roles).exists(new F<Role, Boolean>() {  
                public Boolean f(final Role role) {  
                    return hasRole(identityManager, user, role);
                }  
            });
    }

    /**
     *  <code>hasRole</code> method returns true if user has roleName.
     *
     * @param identityManager an <code>IdentityManager</code> value
     * @param user an <code>Account</code> value
     * @param roleName a <code>String</code> value
     * @return a <code>boolean</code> value
     */
    public boolean hasRole(@NotNull(message="identitymanager must not be null.") IdentityManager identityManager,@NotNull(message="user must not be null.")   Account user,@NotNull(message="roleName must not be null.")  String roleName){
        Role role = getRole(identityManager, roleName);
        
        if(null == role){return false;}

        RelationshipManager manager = partitionManager.createRelationshipManager();
        
        return BasicModel.hasRole(manager, user, role);
    }

    public boolean hasRole(@NotNull(message="identitymanager must not be null.") IdentityManager identityManager,@NotNull(message="user must not be null.")   Account user,@NotNull(message="role must not be null.")  Role role){
        Role roleR = getRole(identityManager, role.getName());
        if(null == roleR){return false;}

        return BasicModel.hasRole(partitionManager.createRelationshipManager(), user, roleR);
    }



    /**
     *  <code>hasRole</code> method returns true if user has roleName.
     *
     * @param user an <code>User</code> value
     * @param role a <code>Role</code> value
     * @return a <code>boolean</code> value
     */
    public boolean hasRole(@NotNull(message="user must not be null.") User user,@NotNull(message="role must not be null.")  Role role){
        return BasicModel.hasRole(partitionManager.createRelationshipManager(), user, role);
    }


    
    /**
     *  <code>getAllUsers</code> method will return all users assoicated with identityManager.
     *
     * @param identityManager an <code>IdentityManager</code> value
     * @param limit an <code>int</code> value
     * @param offset an <code>int</code> value
     * @return a <code>List<User></code> value
     */
    public @Nullable List<User> getAllUsers(@NotNull(message="identitymanager must not be null.") IdentityManager identityManager, final int limit, final int offset){

        val QB = identityManager.getQueryBuilder();
        
        return QB.createIdentityQuery(User.class).
            //sortBy(QB.desc(User.LOGIN_NAME)).
            setOffset(offset).
            setLimit((limit > 0) ? limit : getUserCount(identityManager)).getResultList();
    }
    
    /**
     * Get the total number of users in the system. Use this as your limit if you want
     * to query everything.
     */
    public @Nullable int getUserCount(@NotNull(message="identitymanager must not be null.") IdentityManager identityManager) {
        return identityManager.getQueryBuilder().createIdentityQuery(User.class).getResultCount();
    }


	/**
	 * getAllUsers will return all users associated with the Collection of IDs passed as a parameter.
     * If the ID does not have an associated User it is skipped with warning log emitted.
	 *
	 * @param identityManager
	 * @param ids
	 * @return
	 */
    public @Nullable List<User> getAllUsers(@NotNull(message="identitymanager must not be null.") final IdentityManager identityManager,
                                            @NotNull(message="ids must not be null.") final Collection<String> ids) {

        // Apparently Only equality conditions are allowed when queryng based on the identifier. That sucks.
        // val QB = identityManager.getQueryBuilder();
        // return QB.createIdentityQuery(User.class).where(QB.in(AttributedType.ID, ids.toArray())).getResultList();
        val list = new ArrayList<User>();
        for(val ID: ids){
            val user = identityManager.lookupById(User.class, ID);
            if(user != null) {
                list.add(user); // Only way I can do it.
            } else {
                log.warn("User with id ({}) was not found. Continuing.", ID);
            }
        }
        return list;
    }

    
    /**
     *  getUserByName method will retrieve the User with name userName.
     *  Returns NULL if not found.
     *
     * @param userName a value of type 'String'
     * @return a value of type 'User @Nullable'
     */
    public @Nullable User getUserByName(@NotNull(message="identitymanager must not be null.") IdentityManager identityManager,  @NotNull(message="userName must not be null.") String userName){
        val        QB   = identityManager.getQueryBuilder();
        List<User> list = QB.createIdentityQuery(User.class).where(QB.equal(User.LOGIN_NAME, userName)).getResultList();

        if(list.isEmpty()){
            return null;
        } 
        else {
            return list.get(0);
        }
    }


    /**
     *  <code>getUserByID</code> method returns user with ID
     *
     * @param identityManager an <code>IdentityManager</code> value
     * @param ID a <code>String</code> value
     * @return an <code>User</code> value
     */
    public @Nullable User getUserByID(@NotNull(message="identitymanager must not be null.") IdentityManager identityManager,@NotNull(message="id must not be null.")  String ID){
        return identityManager.lookupById(User.class, ID);
    }
    
    /**
     * Get a user by email address. We need to make sure email addresses are unique on each tenant.
     */
    public @Nullable User getUserByEmail(@NotNull(message="identitymanager must not be null.") IdentityManager identityManager,@NotNull(message="email must not be null.")  String email){
    	IdentityQueryBuilder queryBuilder = identityManager.getQueryBuilder();
    	Condition condition = queryBuilder.equal(User.EMAIL, email.toLowerCase().trim());
    	IdentityQuery<User> query = queryBuilder.createIdentityQuery(User.class).where(condition);
    	List<User> result = query.getResultList();

        if(result.isEmpty()){
            return null;
        } 
        else {
            return result.get(0);
        }
    }



	/**
	 *  <code>createUser</code> method will construct a user given a fully filled in
	 *  UserJSON object. Note that the password should be set within UserJSON otherwise
	 *  a failure will result.
	 *
	 * @param identityManager an <code>IdentityManager</code> value
	 * @param userJSON an <code>UserJSON</code> value
	 * @return an <code>User</code> value
	 * @throws Exception
	 */
    

    
	/**
	 *  <code>createUser</code> method creates a user within the database.
	 *  If realmName is null the default realm is used.
	 *  Email address is also optional.
	 *
	 * @param identityManager an <code>IdentityManager</code> value
	 * @param userName a <code>String</code> value
	 * @param passWord a <code>String</code> value
	 * @param firstName a <code>String</code> value
	 * @param lastName a <code>String</code> value
	 * @param emailAddress a <code>String</code> value
	 * @param realmName a <code>String</code> value
	 * @param groupID
	 * @return an <code>User</code> value
	 * @throws Exception
	 */
    
    public @Nullable User createUser(@NotNull(message="identitymanager must not be null.") IdentityManager identityManager,
                                     @NotNull(message="userName must not be null.") String userName, 
                                     @NotNull(message="passWord must not be null.") String passWord,
                                     @NotNull(message="firstName must not be null.") String firstName,
                                     @NotNull(message="lastName must not be null.") String lastName,
                                     @Nullable String emailAddress,
                                     @Nullable String realmName
                                     ) throws Exception{

        User user = new User(userName);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setCreatedDate(new java.util.Date());

        

        if(null != emailAddress) {user.setEmail(emailAddress);}


        identityManager.add(user);
        identityManager.updateCredential(user, new Password(passWord));


        
        // Retrieve to ensure success AND obtain ID.
        // Returns NULL if failure.
        user = getUserByName(identityManager, userName);

        return user;  
    }


    /**
     *  <code>retrieveEncodedPassword</code> method retrieves the current EncodedPasswordStorage object for user.
     *  It's possible that null will be returned if User no longer exists. Unlikely but you should check for this situation.
     *
     * @param IdentityManager a <code>final</code> value
     * @param User a <code>final</code> value
     * @return a <code>String</code> value
     */
    public @Nullable EncodedPasswordStorage retrieveEncodedPassword(@NotNull(message="identitymanager must not be null.") final IdentityManager identityManager,@NotNull(message="user must not be null.")  final User user) {
        return identityManager.retrieveCurrentCredential(user, EncodedPasswordStorage.class);
    }


    /**
     *  <code>hashPasswordSHA3</code> method will encode the password with SHA-3 and
     * return the base64 encoded string. Bytes are handled as UTF-8 internally.
     *
     * @param String a <code>final</code> value
     * @return a <code>String</code> value
     */
    public String hashPasswordSHA3( final String password) throws NoSuchAlgorithmException, NoSuchProviderException, UnsupportedEncodingException {
        final MessageDigest mda    = MessageDigest.getInstance("SHA3-512", "BC");
        final byte [] digesta      = mda.digest(password.getBytes("UTF-8"));
        return Base64.encodeBase64String(digesta);
    }

    /**
     * Describe <code>hashPasswordSHA512</code> method will encode the password with SHA1-512 and
     * return the base64 encoded string. Bytes are handled as UTF-8 internally.
     *
     * @param String a <code>final</code> value
     * @return a <code>String</code> value
     * @exception NoSuchAlgorithmException if an error occurs
     * @exception NoSuchProviderException if an error occurs
     * @exception UnsupportedEncodingException if an error occurs
     */
    
    public String hashPasswordSHA512( final String password) throws NoSuchAlgorithmException, NoSuchProviderException, UnsupportedEncodingException {
        final MessageDigest mda    = MessageDigest.getInstance("SHA512", "BC");
        final byte [] digesta      = mda.digest(password.getBytes("UTF-8"));
        return Base64.encodeBase64String(digesta);
    }

    
    /////////////////////////////////////////////////////////////////////////
    //                           Private/Protected                         //
    /////////////////////////////////////////////////////////////////////////







}
