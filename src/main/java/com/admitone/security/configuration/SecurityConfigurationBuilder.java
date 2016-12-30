package com.admitone.security.configuration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.picketlink.annotations.PicketLink;
import org.picketlink.event.SecurityConfigurationEvent;
import org.picketlink.idm.config.IdentityConfiguration;
import org.picketlink.idm.config.IdentityConfigurationBuilder;
import org.picketlink.idm.jpa.model.sample.simple.AccountTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.AttributeTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.DigestCredentialTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.GroupTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.IdentityTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.OTPCredentialTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.PartitionTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.PasswordCredentialTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.RelationshipIdentityTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.RelationshipTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.RoleTypeEntity;
import org.picketlink.idm.jpa.model.sample.simple.X509CredentialTypeEntity;
import org.picketlink.idm.model.Relationship;
import org.picketlink.internal.EntityManagerContextInitializer;

import com.admitone.security.interfaces.IIdentityManagementService;

import lombok.extern.slf4j.Slf4j;



/** <p> Builds the IdentityConfiguration for PicketLink.  </p>

 *
 * <a href="mailto:Stephen.Agneta@bjondinc.com">Steve 'Crash' Agneta</a>
 *
 */

@ApplicationScoped
@Slf4j
public class SecurityConfigurationBuilder {
    public static final String IDENTITY_STORE = IIdentityManagementService.PERSISTENCE_UNIT;

	@Produces
	@PicketLink
	@PersistenceContext(unitName = IDENTITY_STORE, type=PersistenceContextType.TRANSACTION)
	public static EntityManager picketLinkEntityManager;


    @Inject
    private EntityManagerContextInitializer contextInitializer;

    private IdentityConfiguration identityConfig = null;


    /**
     *  <code>IdentityConfiguration</code> method will produce the Identity Configuration 
     *  required to initialize PicketLink. 
     *
     * @return a <code>@Produces</code> value
     */
    
    @Produces IdentityConfiguration createConfig() {
        if (identityConfig == null) {
            initConfig();
        }
        return identityConfig;
    }

    /**
     * This method uses the IdentityConfigurationBuilder to create an IdentityConfiguration, which 
     * defines how PicketLink stores identity-related data.  In this particular example, a 
     * JPAIdentityStore is configured to allow the identity data to be stored in a relational database
     * using JPA.
     */
    @SuppressWarnings("unchecked") 
    private void initConfig() {
        IdentityConfigurationBuilder builder = new IdentityConfigurationBuilder();

        log.info("--------------------------------------------------------------------------------------------");
        log.info("IdentityConfiguration will be produced.");
        log.info("--------------------------------------------------------------------------------------------");


        builder
            .named(IDENTITY_STORE)
            .stores()
            .jpa()
            .mappedEntity(
                          AccountTypeEntity.class,
                          RoleTypeEntity.class,
                          GroupTypeEntity.class,
                          IdentityTypeEntity.class,
                          RelationshipTypeEntity.class,
                          RelationshipIdentityTypeEntity.class,
                          PartitionTypeEntity.class,
                          PasswordCredentialTypeEntity.class,
                          DigestCredentialTypeEntity.class,
                          X509CredentialTypeEntity.class,
                          OTPCredentialTypeEntity.class,
                          AttributeTypeEntity.class
                          )
            .supportGlobalRelationship(Relationship.class)
            .addContextInitializer(this.contextInitializer)
            // Specify that this identity store configuration supports all features
            .supportAllFeatures();

        identityConfig = builder.build();


        picketLinkEntityManager = Persistence.createEntityManagerFactory(IDENTITY_STORE).createEntityManager(); // Retrieve an application manager

        log.info("--------------------------------------------------------------------------------------------");
        log.info("IdentityConfiguration has been produced.");
        log.info("--------------------------------------------------------------------------------------------");

    }


    public void configureHttpSecurity(@Observes SecurityConfigurationEvent event) {
        org.picketlink.config.SecurityConfigurationBuilder builder = event.getBuilder();
        
        builder
            .http()   
            .forPath("/admitone/services/administration/*")
            .authenticateWith()
            .basic()
            .realmName("default");
        }

    

    

}
