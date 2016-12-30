
CREATE TABLE orders
(
    id VARCHAR NOT NULL,
    fk_user VARCHAR NOT NULL,
    tickets INTEGER NOT NULL,
    showid_to INTEGER , 
    showid_from INTEGER NOT NULL, -- used only for exchanges
    order_type VARCHAR NOT NULL,
    canceled BOOLEAN DEFAULT false NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT user_fk1 FOREIGN KEY (fk_user) REFERENCES accounttypeentity (id)
);

CREATE INDEX fk_user_idx     ON orders (fk_user ASC);
CREATE INDEX showid_to_idx   ON orders (showid_to ASC);
CREATE INDEX showid_from_idx ON orders (showid_from ASC);
CREATE INDEX order_type_idx  ON orders (order_type ASC);
CREATE INDEX canceled_idx    ON orders (canceled);
