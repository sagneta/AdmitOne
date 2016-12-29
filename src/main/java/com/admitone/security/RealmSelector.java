package com.admitone.security;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

// PicketLink
import org.picketlink.annotations.PicketLink;
import org.picketlink.idm.model.Partition;
import org.picketlink.idm.model.basic.Group;
import org.picketlink.idm.model.basic.Realm;

/**
 * <p>We use this class to hold the current realm (tenant) or tier (personae of a tenant) for a specific user.</p>
 * 
 * Without this session class we can't have multi-tenant systems.
 */
@SessionScoped
@Named
public class RealmSelector implements Serializable {

	private static final long serialVersionUID = -3840476292582225412L;

    /**
     * The Tenant realm is the Realm associated with the tenant (company)
     * of the currently authenticated user. Think of this as the top level
     * ring.
     */
    private Realm tenantRealm;

    /**
     * A Tier or Realm. Most likely it will be a Tier associated with a Personae 
     but could be a realm and thus partition == tenantRealm
     */
    
    private Partition partition;

    /**
     * 
     You current group is the group in which you are associated.
     Users are situated inside a group and determines which division
     of data is used. If this group is null (doesn't exist) then
     you are at the realm level and are most likely an administrator 
     of some sort.
     */
    
    private Group currentGroup;

    @Produces
    @PicketLink
    public @Nullable Partition select() {
        return this.partition;
    }


    /**
     *  <code>getPartition</code> method will return the current partition
     *  from which to IdentityManagers are constucted. Could be a Realm (tenant) 
     *  or a Tier (personae and thus a restricted subset of Realm (tenant))
     *
     * @return a <code>Partition</code> value
     */
    public @Nullable Partition getPartition() {
        if (this.partition == null) {
            return null;
        }

        return this.partition;
    }

    /**
     *  <code>setPartition</code> method will will set the partition
     *  from which IdentityManagers are constructed. Could be a Realm (tenant) 
     *  or a Tier (personae and thus a restricted subset of Realm (tenant))
     *
     * @param partition a <code>Partition</code> value
     */
    public void setPartition(@Nullable Partition partition) {
        this.partition = partition;
    }


    /**
     *  <code>getTenantRealm</code> method will return the Realm (Tenant).
     * Presumably this is set to ensure we know which tenant we are associated with
     * should we setPartition to a subserviant tier.
     *
     * @return a <code>Realm</code> value
     */
    
    public @Nullable Realm getTenantRealm() { return this.tenantRealm;}

    /**
     * Describe <code>setTenantRealm</code> method will set the Realm (Tenant).
     * Presumably this is set to ensure we know which tenant we are associated with
     * should we setPartition to a subserviant tier.
     *
     * @param enantRealm a <code>Realm</code> value
     */
    
    public void setTenantRealm(@Nullable Realm tenantRealm) { this.tenantRealm = tenantRealm;}



    /**
     *  <code>getCurrentGroup</code> method return the current shoudl it exist.
     *
     You current group is the group in which you are associated.
     Users are situated inside a group and determines which division
     of data is used. If this group is null (doesn't exist) then
     you are at the realm level and are most likely an administrator 
     of some sort.

     * @return a <code>Group</code> value
     */
    
    public @Nullable Group getCurrentGroup() { return currentGroup;}


    /**
     *  <code>setCurrentGroup</code> method sets the current group for the current user.
     *
     *  The current user MUST obviously be a member of this group.
     *  Although it is possible for a user to be a member of more than one group only one group
     *  may be active at one time as it partitions the data within the RDBMS schema.
     *
     * @param a_currentGroup a <code>Group</code> value
     */
    public void setCurrentGroup(@Nullable Group a_currentGroup) { currentGroup = a_currentGroup;}




    /////////////////////////////////////////////////////////////////////////
    //                        Private below this point                     //
    /////////////////////////////////////////////////////////////////////////

}
