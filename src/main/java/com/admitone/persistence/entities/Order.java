package com.admitone.persistence.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/** <p> Entity for the order table.  </p>

 *
 * <a href="mailto:sagneta@gmail.com">Steve 'Crash' Agneta</a>
 *
 */


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="orders")
@NamedQueries({
        @NamedQuery(name="Order.findAll", query="SELECT o FROM Order o"),
            @NamedQuery(name="Order.findCountOfAllAttendingPerUser",    query="SELECT COUNT(o.id) FROM Order o WHERE o.userID = :userid AND o.orderType IN ('Purchase', 'Exchange') AND o.canceled = FALSE"),
            @NamedQuery(name="Order.findAllAttendingPerUser",    query="SELECT o FROM Order o WHERE userID = :userid AND orderType IN ('Purchase', 'Exchange') AND canceled = FALSE ORDER BY toShowID"),
            @NamedQuery(name="Order.findAllTicketsOwnedPerUser", query="SELECT SUM(tickets) FROM Order o WHERE userID = :userid AND canceled = FALSE AND orderType IN ('Purchase', 'Exchange')"),
            @NamedQuery(name="Order.findOrderHistoryPerUser",    query="SELECT o FROM Order o WHERE userID = :userid ORDER BY toShowID"),
            @NamedQuery(name="Order.findOrderRangedPerUser",     query="SELECT o FROM Order o WHERE userID = :userid AND canceled = FALSE AND orderType IN ('Purchase', 'Exchange') AND toShowID BETWEEN :min AND :max"),

            @NamedQuery(name="Order.findOrderRanged", query="SELECT o FROM Order o WHERE canceled = FALSE AND orderType IN ('Purchase', 'Exchange') AND toShowID BETWEEN :min AND :max"),


            
            @NamedQuery(name="Order.findSpecificPurchaseOrExchange", query="SELECT o FROM Order o WHERE userID = :userid AND orderType IN ('Purchase', 'Exchange') AND canceled = FALSE AND toShowID = :toshowid AND tickets = :tickets"),
            @NamedQuery(name="Order.findAllTicketsOwnedPerUserAndShow", query="SELECT o FROM Order o WHERE userID = :userid AND orderType IN ('Purchase', 'Exchange') AND canceled = FALSE AND toShowID = :toshowid"),
            @NamedQuery(name="Order.findCountOfAllTicketsOwnedPerUserAndShow", query="SELECT SUM(o.tickets) FROM Order o WHERE userID = :userid AND orderType IN ('Purchase', 'Exchange') AND canceled = FALSE AND toShowID = :toshowid"),
            
            })
@NamedNativeQueries({
        @NamedNativeQuery(
                          name="Order.findOrderRangedNative",
                          query="SELECT o.id, o.showid_to AS toShowID, a.loginname AS username, o.tickets FROM orders o, accounttypeentity a WHERE canceled = FALSE AND order_type IN ('Purchase', 'Exchange') AND showid_to BETWEEN ? AND ? AND o.fk_user = a.id ",
                          resultSetMapping="Order.stub"
                          ),
    })
    @SqlResultSetMappings({
            @SqlResultSetMapping(name="Order.stub",
                                 classes={
                                     @ConstructorResult(targetClass=Order.class, columns={
                                             @ColumnResult(name="id", type=String.class),
                                             @ColumnResult(name="toShowID", type=Integer.class),
                                             @ColumnResult(name="username", type=String.class),
                                             @ColumnResult(name="tickets", type=Integer.class)                                             
                                         })
                                 }
                                 ),
                })
public class Order {
    public enum ORDER_TYPE {
        Purchase,
        Exchange
    };

	@Id
	private String id;

	@Column(name="fk_user")
	private String userID;

	@Column(name="tickets")
	private Integer tickets;

	@Column(name="showid_to")
	private Integer toShowID;
    
	@Column(name="showid_from")
	private Integer fromShowID;
    
    @Enumerated(EnumType.STRING)
    @Column(name="order_type")
    private ORDER_TYPE orderType = ORDER_TYPE.Purchase;

	@Column(name="canceled")
	private Boolean canceled;

    @Transient
    private String username;
    
    public Order(final String id, final Integer toShowID, final String username, final Integer tickets) {
        setId(id);
        setToShowID(toShowID);
        setUsername(username);
        setTickets(tickets);
    }
    
    /////////////////////////////////////////////////////////////////////////
    //                     Private Static search methods.                  //
    /////////////////////////////////////////////////////////////////////////

    public static List<Order> findAll(final EntityManager EM, final int limit , final int start) {
        final TypedQuery<Order> query = EM.createNamedQuery("Order.findAll", Order.class);
        query.setFirstResult(start);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    public static Long findCountOfAllAttendingPerUser(final EntityManager EM, final String userID) {
        try {        
            final TypedQuery<Long> query = EM.createNamedQuery("Order.findCountOfAllAttendingPerUser", Long.class);
            query.setParameter("userid", userID);
            
            return query.getSingleResult();
        }
        catch(final NoResultException e){
            return 0L;
        }
    }

    public static List<Order> findAllAttendingPerUser(final EntityManager EM, final String userID, final int limit , final int start) {
        final TypedQuery<Order> query = EM.createNamedQuery("Order.findAllAttendingPerUser", Order.class);

        query.setParameter("userid", userID);
        query.setFirstResult(start);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    public static Long findAllTicketsOwnedPerUser(final EntityManager EM, final String userID) {
        try {        
            final TypedQuery<Long> query = EM.createNamedQuery("Order.findAllTicketsOwnedPerUser", Long.class);
            query.setParameter("userid", userID);
            
            return query.getSingleResult();
        }
        catch(final NoResultException e){
            return 0L;
        }
    }


    public static List<Order> findOrderHistoryPerUser(final EntityManager EM, final String userID, final int limit , final int start) {
        final TypedQuery<Order> query = EM.createNamedQuery("Order.findOrderHistoryPerUser", Order.class);

        query.setParameter("userid", userID);
        query.setFirstResult(start);
        query.setMaxResults(limit);

        return query.getResultList();
    }


    public static List<Order> findOrderRangedPerUser(final EntityManager EM,
                                                     final String userID,
                                                     final int minShowID, final int maxShowID,
                                                     final int limit, final int start) {
        final TypedQuery<Order> query = EM.createNamedQuery("Order.findOrderRangedPerUser", Order.class);

        query.setParameter("userid", userID);
        query.setParameter("min", minShowID);
        query.setParameter("max", maxShowID);
        query.setFirstResult(start);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    public static List<Order> findOrderRanged(final EntityManager EM,
                                              final int minShowID, final int maxShowID,
                                              final int limit, final int start) {
        final TypedQuery<Order> query = EM.createNamedQuery("Order.findOrderRanged", Order.class);

        query.setParameter("min", minShowID);
        query.setParameter("max", maxShowID);
        query.setFirstResult(start);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    
    public static List<Order> findSpecificPurchaseOrExchange(final EntityManager EM,
                                                             final String userID,
                                                             final int toShowID,
                                                             final int tickets,
                                                             final int limit, final int start) {
        final TypedQuery<Order> query = EM.createNamedQuery("Order.findSpecificPurchaseOrExchange", Order.class);

        query.setParameter("userid", userID);
        query.setParameter("toshowid", toShowID);
        query.setParameter("tickets", tickets);
        query.setFirstResult(start);
        query.setMaxResults(limit);

        return query.getResultList();
    }


    public static List<Order> findAllTicketsOwnedPerUserAndShow(final EntityManager EM,
                                                                final String userID,
                                                                final int toShowID,
                                                                final int limit, final int start) {
        final TypedQuery<Order> query = EM.createNamedQuery("Order.findAllTicketsOwnedPerUserAndShow", Order.class);

        query.setParameter("userid", userID);
        query.setParameter("toshowid", toShowID);
        query.setFirstResult(start);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    public static long findCountOfAllTicketsOwnedPerUserAndShow(final EntityManager EM, final String userID, final int toShowID) {
        try {        
            final TypedQuery<Long> query = EM.createNamedQuery("Order.findCountOfAllTicketsOwnedPerUserAndShow", Long.class);
            query.setParameter("userid", userID);
            query.setParameter("toshowid", toShowID);
            
            final Long result = query.getSingleResult();
            return (result == null) ? 0L : result;
        }
        catch(final NoResultException e){
            return 0L;
        }
    }

    public static List<Order> findOrderRangedNative(final EntityManager EM,
                                                    final int minShowID, final int maxShowID,
                                                    final int iLimit, final int iOffset) {
        try {
            final TypedQuery<Order> query = EM.createNamedQuery("Order.findOrderRangedNative", Order.class);

            query.setParameter(1, minShowID);
            query.setParameter(2, maxShowID);

            query.setMaxResults(iLimit);
            query.setFirstResult(iOffset);

            return query.getResultList();

        } catch(final NoResultException e){
            return null;
        }
    }
    
}
;
