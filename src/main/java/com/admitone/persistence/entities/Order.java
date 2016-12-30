package com.admitone.persistence.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Table;
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
            @NamedQuery(name="Order.findCountOfAllAttendingPerUser",    query="SELECT COUNT(o.id) FROM Order o WHERE userID = :userid AND orderType IN ('Purchase', 'Exchange') AND canceled = FALSE ORDER BY toShowID"),
            @NamedQuery(name="Order.findAllAttendingPerUser",    query="SELECT o FROM Order o WHERE userID = :userid AND orderType IN ('Purchase', 'Exchange') AND canceled = FALSE ORDER BY toShowID"),
            @NamedQuery(name="Order.findAllTicketsOwnedPerUser", query="SELECT SUM(tickets) FROM Order o WHERE userID = :userid AND canceled = FALSE AND orderType IN ('Purchase', 'Exchange')"),
            @NamedQuery(name="Order.findOrderHistoryPerUser",    query="SELECT o FROM Order o WHERE userID = :userid ORDER BY toShowID"),
            @NamedQuery(name="Order.findOrderRangedPerUser",     query="SELECT o FROM Order o WHERE userID = :userid AND canceled = FALSE AND orderType IN ('Purchase', 'Exchange') AND toShowID BETWEEN :min AND :max"),
            @NamedQuery(name="Order.findSpecificPurchaseOrExchange", query="SELECT o FROM Order o WHERE userID = :userid AND orderType IN ('Purchase', 'Exchange') AND canceled = FALSE AND toShowID = :toshowid AND tickets = :tickets"),                                    
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
	private Integer toFromID;
    
    @Enumerated(EnumType.STRING)
    @Column(name="order_type")
    private ORDER_TYPE orderType = ORDER_TYPE.Purchase;

	@Column(name="canceled")
	private Boolean canceled;

    

    /////////////////////////////////////////////////////////////////////////
    //                     Private Static search methods.                  //
    /////////////////////////////////////////////////////////////////////////

    public static List<Order> findAll(final EntityManager EM, final int limit , final int start) {
        final TypedQuery<Order> query = EM.createNamedQuery("Order.findAll", Order.class);
        query.setFirstResult(start);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    public Long findCountOfAllAttendingPerUser(final EntityManager EM, final String userID) {
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

    public Long findAllTicketsOwnedPerUser(final EntityManager EM, final String userID) {
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
    
}
