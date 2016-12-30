
-- First setup PicketLink security framework.

-- Create the partition object.

CREATE TABLE partitionobject
(
  id character varying(255) NOT NULL,
  name character varying(255),
  type character varying(255),
  parent_id character varying(255),
  CONSTRAINT partitionobject_pkey PRIMARY KEY (id),
  CONSTRAINT fk418e02a975d3d0b5 FOREIGN KEY (parent_id)
      REFERENCES partitionobject (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- Create the default hibernate sequence.

CREATE SEQUENCE hibernate_sequence
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  


-- PicketLink ACL subsystem persistence.
CREATE TABLE permission 
        (id BIGINT NOT NULL, 
        assignee CHARACTER VARYING(255), 
        operation CHARACTER VARYING(255), 
        resourceclass CHARACTER VARYING(255), 
        resourceidentifier CHARACTER VARYING(255), 
        PRIMARY KEY (id));

CREATE INDEX permission_idx ON permission (assignee, resourceclass, resourceidentifier DESC);


-- Create the identity object table.
  
CREATE TABLE identityobject
(
  id character varying(255) NOT NULL,
  creationdate timestamp without time zone,
  discriminator character varying(255),
  email character varying(255),
  enabled boolean NOT NULL,
  expirydate timestamp without time zone,
  firstname character varying(255),
  grouppath character varying(255),
  lastname character varying(255),
  loginname character varying(255),
  name character varying(255),
  parent_id character varying(255),
  partition_id character varying(255),
  CONSTRAINT identityobject_pkey PRIMARY KEY (id),
  CONSTRAINT fkb760c5bd731c1d1 FOREIGN KEY (parent_id)
      REFERENCES identityobject (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkb760c5bd7ae9e75 FOREIGN KEY (partition_id)
      REFERENCES partitionobject (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
  
-- Create the identity object attribute table.
  
CREATE TABLE identityobjectattribute
(
  attributeid integer NOT NULL,
  name character varying(255),
  type character varying(255),
  value character varying(1024),
  identityobject_id character varying(255),
  CONSTRAINT identityobjectattribute_pkey PRIMARY KEY (attributeid),
  CONSTRAINT fkeb1f295f1019281e FOREIGN KEY (identityobject_id)
      REFERENCES identityobject (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- Create the credential object table.

CREATE TABLE credentialobject
(
  internalid bigint NOT NULL,
  credential character varying(255),
  effectivedate timestamp without time zone,
  expirydate timestamp without time zone,
  type character varying(255),
  identitytype_id character varying(255),
  CONSTRAINT credentialobject_pkey PRIMARY KEY (internalid),
  CONSTRAINT fk131dde96c8cacec3 FOREIGN KEY (identitytype_id)
      REFERENCES identityobject (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
  
-- Create the credential object attribute table.

CREATE TABLE credentialobjectattribute
(
  attributeid integer NOT NULL,
  name character varying(255),
  value character varying(1024),
  credentialobject_internalid bigint,
  CONSTRAINT credentialobjectattribute_pkey PRIMARY KEY (attributeid)
);

-- Create relationship object table.

CREATE TABLE relationshipobject
(
  id character varying(255) NOT NULL,
  type character varying(255),
  CONSTRAINT relationshipobject_pkey PRIMARY KEY (id)
);

-- Create the relationship object attribute table.

CREATE TABLE relationshipobjectattribute
(
  id integer NOT NULL,
  name character varying(255),
  value character varying(1024),
  relationshipobject_id character varying(255),
  CONSTRAINT relationshipobjectattribute_pkey PRIMARY KEY (id),
  CONSTRAINT fke00d50a5a6ccdbde FOREIGN KEY (relationshipobject_id)
      REFERENCES relationshipobject (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- Create the relationship identity object table.

CREATE TABLE relationshipidentityobject
(
  id bigint NOT NULL,
  descriptor character varying(255),
  identityobject_id character varying(255),
  relationshipobject_id character varying(255),
  CONSTRAINT relationshipidentityobject_pkey PRIMARY KEY (id),
  CONSTRAINT fkb89396151019281e FOREIGN KEY (identityobject_id)
      REFERENCES identityobject (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkb8939615a6ccdbde FOREIGN KEY (relationshipobject_id)
      REFERENCES relationshipobject (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- Create Attributed Type Entity Table.

CREATE TABLE attributedtypeentity
(
  id character varying(255) NOT NULL,
  CONSTRAINT attributedtypeentity_pkey PRIMARY KEY (id)
);


-- Create the Attribute Type Entity table.

CREATE TABLE attributetypeentity
(
  id bigint NOT NULL,
  name character varying(255),
  typename character varying(255),
  value character varying(1024),
  owner_id character varying(255),
  CONSTRAINT attributetypeentity_pkey PRIMARY KEY (id),
  CONSTRAINT fke2994759f54bda12 FOREIGN KEY (owner_id)
      REFERENCES attributedtypeentity (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- Create the Digest Credential Type Entity table.

CREATE TABLE digestcredentialtypeentity
(
  id bigint NOT NULL,
  effectivedate timestamp without time zone,
  expirydate timestamp without time zone,
  typename character varying(255),
  digestha1 bytea,
  digestrealm character varying(255),
  owner_id character varying(255),
  CONSTRAINT digestcredentialtypeentity_pkey PRIMARY KEY (id),
  CONSTRAINT fk53eaf378f54bda12 FOREIGN KEY (owner_id)
      REFERENCES attributedtypeentity (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- Create the OTP Credential Type Entity table.

CREATE TABLE otpcredentialtypeentity
(
  id bigint NOT NULL,
  effectivedate timestamp without time zone,
  expirydate timestamp without time zone,
  typename character varying(255),
  totpdevice character varying(255),
  totpsecretkey character varying(255),
  owner_id character varying(255),
  CONSTRAINT otpcredentialtypeentity_pkey PRIMARY KEY (id),
  CONSTRAINT fkc9c9831ff54bda12 FOREIGN KEY (owner_id)
      REFERENCES attributedtypeentity (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- Create the Partition Type Entity table.

CREATE TABLE partitiontypeentity
(
  configurationname character varying(255),
  name character varying(255),
  typename character varying(255),
  id character varying(255) NOT NULL,
  CONSTRAINT partitiontypeentity_pkey PRIMARY KEY (id),
  CONSTRAINT fk43972627922a4906 FOREIGN KEY (id)
      REFERENCES attributedtypeentity (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- Create the Password Credential Type Entity table.

CREATE TABLE passwordcredentialtypeentity
(
  id bigint NOT NULL,
  effectivedate timestamp without time zone,
  expirydate timestamp without time zone,
  typename character varying(255),
  passwordencodedhash character varying(255),
  passwordsalt character varying(255),
  owner_id character varying(255),
  CONSTRAINT passwordcredentialtypeentity_pkey PRIMARY KEY (id),
  CONSTRAINT fk23b1b6cff54bda12 FOREIGN KEY (owner_id)
      REFERENCES attributedtypeentity (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- Create the Relationship Type Entity table.

CREATE TABLE relationshiptypeentity
(
  typename character varying(255),
  id character varying(255) NOT NULL,
  CONSTRAINT relationshiptypeentity_pkey PRIMARY KEY (id),
  CONSTRAINT fkf8e62435922a4906 FOREIGN KEY (id)
      REFERENCES attributedtypeentity (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- Create the x509 Credential Type Entity table.

CREATE TABLE x509credentialtypeentity
(
  id bigint NOT NULL,
  effectivedate timestamp without time zone,
  expirydate timestamp without time zone,
  typename character varying(255),
  base64cert character varying(1024),
  owner_id character varying(255),
  CONSTRAINT x509credentialtypeentity_pkey PRIMARY KEY (id),
  CONSTRAINT fkd4bcc23af54bda12 FOREIGN KEY (owner_id)
      REFERENCES attributedtypeentity (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- Create the Identity Type Entity table.

CREATE TABLE identitytypeentity
(
  createddate timestamp without time zone,
  enabled boolean NOT NULL,
  expirationdate timestamp without time zone,
  typename character varying(255),
  id character varying(255) NOT NULL,
  partition_id character varying(255),
  CONSTRAINT identitytypeentity_pkey PRIMARY KEY (id),
  CONSTRAINT fk3d6a3f3b922a4906 FOREIGN KEY (id)
      REFERENCES attributedtypeentity (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk3d6a3f3bbd2e0751 FOREIGN KEY (partition_id)
      REFERENCES partitiontypeentity (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- Create Account Type entity table.

CREATE TABLE accounttypeentity
(
  id character varying(255) NOT NULL,
  email character varying(255),
  firstname character varying(255),
  lastname character varying(255),
  loginname character varying(255),

  CONSTRAINT accounttypeentity_pkey PRIMARY KEY (id),
  CONSTRAINT fk37341daa61f12e7c FOREIGN KEY (id)
      REFERENCES identitytypeentity (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);





--- Full Text Search end.




-- Create the Group Type Entity table.

CREATE TABLE grouptypeentity
(
  name character varying(255),
  path character varying(255),
  id character varying(255) NOT NULL,
  parent_id character varying(255),
  CONSTRAINT grouptypeentity_pkey PRIMARY KEY (id),
  CONSTRAINT fk9d7a437c61f12e7c FOREIGN KEY (id)
      REFERENCES identitytypeentity (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk9d7a437cf233b3e6 FOREIGN KEY (parent_id)
      REFERENCES grouptypeentity (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- Create the Relationship Identity Type Entity table.

CREATE TABLE relationshipidentitytypeentity
(
  identifier bigint NOT NULL,
  descriptor character varying(255),
  identitytype_id character varying(255),
  owner_id character varying(255),
  CONSTRAINT relationshipidentitytypeentity_pkey PRIMARY KEY (identifier),
  CONSTRAINT fkcd8d239340652182 FOREIGN KEY (owner_id)
      REFERENCES relationshiptypeentity (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkcd8d23939ef0df23 FOREIGN KEY (identitytype_id)
      REFERENCES identitytypeentity (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- Create the Role Type Entity table.

CREATE TABLE roletypeentity
(
  name character varying(255),
  id character varying(255) NOT NULL,
  CONSTRAINT roletypeentity_pkey PRIMARY KEY (id),
  CONSTRAINT fkcad418f361f12e7c FOREIGN KEY (id)
      REFERENCES identitytypeentity (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- First Create the table structure. This is the basic PicketLink table system


-- Second, fill in the security structure we desire. Realms, Tiers, Roles and Groups.
SET CONSTRAINTS ALL DEFERRED;

INSERT INTO attributedtypeentity (id) SELECT '90bffc41-c298-4a23-8976-06b08218993e' WHERE '90bffc41-c298-4a23-8976-06b08218993e' NOT IN (SELECT id FROM attributedtypeentity);
INSERT INTO attributedtypeentity (id) SELECT '9ab34f53-ff19-4ab4-baa4-79c4957af03d' WHERE '9ab34f53-ff19-4ab4-baa4-79c4957af03d' NOT IN (SELECT id FROM attributedtypeentity);
INSERT INTO attributedtypeentity (id) SELECT 'abad60b3-1438-4806-9f7e-51ca26886f9a' WHERE 'abad60b3-1438-4806-9f7e-51ca26886f9a' NOT IN (SELECT id FROM attributedtypeentity);
INSERT INTO attributedtypeentity (id) SELECT '2751b72d-9e33-43c3-aa38-a584006e67bc' WHERE '2751b72d-9e33-43c3-aa38-a584006e67bc' NOT IN (SELECT id FROM attributedtypeentity);
INSERT INTO attributedtypeentity (id) SELECT 'a083c3fd-c829-40b1-be15-80f91bd7238b' WHERE 'a083c3fd-c829-40b1-be15-80f91bd7238b' NOT IN (SELECT id FROM attributedtypeentity);

INSERT INTO partitiontypeentity (configurationname, name, typename, id) SELECT 'admitone', 'default', 'org.picketlink.idm.model.basic.Realm', '90bffc41-c298-4a23-8976-06b08218993e' WHERE '90bffc41-c298-4a23-8976-06b08218993e' NOT IN (SELECT id FROM partitiontypeentity);
INSERT INTO partitiontypeentity (configurationname, name, typename, id) SELECT 'admitone', 'admitOneRealm', 'org.picketlink.idm.model.basic.Realm', '9ab34f53-ff19-4ab4-baa4-79c4957af03d' WHERE '9ab34f53-ff19-4ab4-baa4-79c4957af03d' NOT IN (SELECT id FROM partitiontypeentity);
INSERT INTO partitiontypeentity (configurationname, name, typename, id) SELECT 'admitone', 'admitOneTier', 'org.picketlink.idm.model.basic.Tier', 'abad60b3-1438-4806-9f7e-51ca26886f9a' WHERE 'abad60b3-1438-4806-9f7e-51ca26886f9a' NOT IN (SELECT id FROM partitiontypeentity);


-- Create the admitOneParentGroup and assign to the bjöndRealm
INSERT INTO identitytypeentity (createddate, enabled, expirationdate, typename, id, partition_id) VALUES ('2014-01-22 15:38:56', true, null, 'org.picketlink.idm.model.basic.Group', '2751b72d-9e33-43c3-aa38-a584006e67bc', '9ab34f53-ff19-4ab4-baa4-79c4957af03d');
INSERT INTO grouptypeentity (name, path, id, parent_id) VALUES ('admitOneParentGroup', '/admitOneParentGroup', '2751b72d-9e33-43c3-aa38-a584006e67bc', null);


-- Create the default Roles
-- admitOneRealm

-- SystemAdministrator Role
INSERT INTO attributedtypeentity (id) VALUES ('ec6dc8a6-9fb3-4270-871b-ecce3299d1a9');
INSERT INTO identitytypeentity (createddate, enabled, expirationdate, typename, id, partition_id) VALUES ('2014-01-23 12:16:00', true, null, 'org.picketlink.idm.model.basic.Role', 'ec6dc8a6-9fb3-4270-871b-ecce3299d1a9', '9ab34f53-ff19-4ab4-baa4-79c4957af03d');
INSERT INTO roletypeentity (name, id) VALUES ('SystemAdministrator', 'ec6dc8a6-9fb3-4270-871b-ecce3299d1a9');

--User Role
INSERT INTO attributedtypeentity (id) VALUES ('c6eb3154-ffc8-44a0-a521-202abaca1408');
INSERT INTO identitytypeentity (createddate, enabled, expirationdate, typename, id, partition_id) VALUES ('2014-01-23 12:16:01', true, null, 'org.picketlink.idm.model.basic.Role', 'c6eb3154-ffc8-44a0-a521-202abaca1408', '9ab34f53-ff19-4ab4-baa4-79c4957af03d');
INSERT INTO roletypeentity (name, id) VALUES ('User', 'c6eb3154-ffc8-44a0-a521-202abaca1408');


-- Users
INSERT INTO attributedtypeentity (id) VALUES ('f93fcf75-8cd0-4797-ae78-2ec416bf9807');
INSERT INTO identitytypeentity (createddate, enabled, expirationdate, typename, id, partition_id) VALUES ('2014-01-23 14:52:25', true, null, 'org.picketlink.idm.model.basic.User', 'f93fcf75-8cd0-4797-ae78-2ec416bf9807', '9ab34f53-ff19-4ab4-baa4-79c4957af03d');
INSERT INTO accounttypeentity (email, firstname, lastname, loginname, id) VALUES ('sagneta@gmail.com', 'AdmitOne', 'Default System Administrator User', 'admin', 'f93fcf75-8cd0-4797-ae78-2ec416bf9807');

-- Add default group for Admin
INSERT INTO attributetypeentity (id, name, typename, value, owner_id) VALUES ((select nextval ('hibernate_sequence')), 'defaultgroupid', null, 'rO0ABXQAJDI3NTFiNzJkLTllMzMtNDNjMy1hYTM4LWE1ODQwMDZlNjdiYw==', 'f93fcf75-8cd0-4797-ae78-2ec416bf9807');

-- The newlines with the Base64 encoded hash are part of the data. Please do not remove.
INSERT INTO passwordcredentialtypeentity (id, effectivedate, expirydate, typename, passwordencodedhash, passwordsalt, owner_id) VALUES ((SELECT nextval('hibernate_sequence')), '2014-01-24 08:28:04.352', null, 'org.picketlink.idm.credential.storage.EncodedPasswordStorage', '7hKC5PcOwVLxf3AkjCssnBS+Hr+V+KLnrDygdfElqv/mP7KEJ5KvYXAaHR4fuUpJ3d0OXRIUjKng
oudclQVZyA==', '-1772222414295900981', 'f93fcf75-8cd0-4797-ae78-2ec416bf9807');



-- Add System Administrator User to the Bjönd Parent Group.
INSERT INTO attributedtypeentity (id) VALUES ('5081efb2-24c5-445f-9a49-d59072b8353a');
INSERT INTO relationshiptypeentity (typename, id) VALUES ('org.picketlink.idm.model.basic.GroupMembership', '5081efb2-24c5-445f-9a49-d59072b8353a');
INSERT INTO relationshipidentitytypeentity (identifier, descriptor, identitytype_id, owner_id) VALUES ((SELECT nextval('hibernate_sequence')), 'member', 'f93fcf75-8cd0-4797-ae78-2ec416bf9807', '5081efb2-24c5-445f-9a49-d59072b8353a');
INSERT INTO relationshipidentitytypeentity (identifier, descriptor, identitytype_id, owner_id) VALUES ((SELECT nextval('hibernate_sequence')), 'group', '2751b72d-9e33-43c3-aa38-a584006e67bc', '5081efb2-24c5-445f-9a49-d59072b8353a');



-- Create a regular user.
-- user: Joe Blow 
-- pass: systemsystem1
INSERT INTO attributedtypeentity (id) VALUES ('071d8f1c-0d3e-4fae-a1e4-45f1a706c7b6');
INSERT INTO identitytypeentity (createddate, enabled, expirationdate, typename, id, partition_id) VALUES ('2015-09-10 17:02:20', true, null, 'org.picketlink.idm.model.basic.User', '071d8f1c-0d3e-4fae-a1e4-45f1a706c7b6', '9ab34f53-ff19-4ab4-baa4-79c4957af03d');

-- Do NOT REMOVE the newlines which are required. I know they look funny but leave them there.
INSERT INTO passwordcredentialtypeentity (id, effectivedate, expirydate, typename, passwordencodedhash, passwordsalt, owner_id) VALUES ((SELECT nextval('hibernate_sequence')), '2015-09-10 17:02:20', null, 'org.picketlink.idm.credential.storage.EncodedPasswordStorage', 'tWt0J2wDSop+BMKMZBiSmgVXU6Vt2QhHs3l+cWv5w7F299GAMI3s/HaDsUaj2WOvekCJU5Kc14Ln
stIeTHP2BQ==', '5959875240769452180', '071d8f1c-0d3e-4fae-a1e4-45f1a706c7b6');


INSERT INTO accounttypeentity (id, email, firstname, lastname, loginname) VALUES ('071d8f1c-0d3e-4fae-a1e4-45f1a706c7b6', 'joeblow@gmail.com', 'Joe', 'Blow', 'Joe Blow');

-- Add to a group.
INSERT INTO attributedtypeentity (id) VALUES ('cc94e8a8-a4e3-4ed1-9a24-796a82eb6840');
INSERT INTO relationshiptypeentity (typename, id) VALUES ('org.picketlink.idm.model.basic.GroupMembership', 'cc94e8a8-a4e3-4ed1-9a24-796a82eb6840');
INSERT INTO relationshipidentitytypeentity (identifier, descriptor, identitytype_id, owner_id) VALUES ((SELECT nextval('hibernate_sequence')), 'member', '071d8f1c-0d3e-4fae-a1e4-45f1a706c7b6', 'cc94e8a8-a4e3-4ed1-9a24-796a82eb6840');
INSERT INTO relationshipidentitytypeentity (identifier, descriptor, identitytype_id, owner_id) VALUES ((SELECT nextval('hibernate_sequence')), 'group', '2751b72d-9e33-43c3-aa38-a584006e67bc', 'cc94e8a8-a4e3-4ed1-9a24-796a82eb6840');



-- Add System Administrator and User Roles to the System Administrator

-- System Admin Role
INSERT INTO attributedtypeentity (id) VALUES ('ad112467-0446-4b4a-ab26-e0880e59db11');

INSERT INTO relationshiptypeentity (typename, id) VALUES ('org.picketlink.idm.model.basic.Grant', 'ad112467-0446-4b4a-ab26-e0880e59db11');
INSERT INTO relationshipidentitytypeentity (identifier, descriptor, identitytype_id, owner_id) VALUES ((SELECT nextval('hibernate_sequence')), 'role', 'ec6dc8a6-9fb3-4270-871b-ecce3299d1a9', 'ad112467-0446-4b4a-ab26-e0880e59db11');
INSERT INTO relationshipidentitytypeentity (identifier, descriptor, identitytype_id, owner_id) VALUES ((SELECT nextval('hibernate_sequence')), 'assignee', 'f93fcf75-8cd0-4797-ae78-2ec416bf9807', 'ad112467-0446-4b4a-ab26-e0880e59db11');

-- User Role
INSERT INTO attributedtypeentity (id) VALUES ('34d7f14e-3f6d-4049-81e9-88c93731f9b5');

INSERT INTO relationshiptypeentity (typename, id) VALUES ('org.picketlink.idm.model.basic.Grant', '34d7f14e-3f6d-4049-81e9-88c93731f9b5');
INSERT INTO relationshipidentitytypeentity (identifier, descriptor, identitytype_id, owner_id) VALUES ((SELECT nextval('hibernate_sequence')), 'role', 'c6eb3154-ffc8-44a0-a521-202abaca1408', '34d7f14e-3f6d-4049-81e9-88c93731f9b5');
INSERT INTO relationshipidentitytypeentity (identifier, descriptor, identitytype_id, owner_id) VALUES ((SELECT nextval('hibernate_sequence')), 'assignee', 'f93fcf75-8cd0-4797-ae78-2ec416bf9807', '34d7f14e-3f6d-4049-81e9-88c93731f9b5');


-- User Role to Joe Blow
-- User Role
INSERT INTO attributedtypeentity (id) VALUES ('2477b11e-b71f-4372-8a97-0da2ddd6415d');

INSERT INTO relationshiptypeentity (typename, id) VALUES ('org.picketlink.idm.model.basic.Grant', '2477b11e-b71f-4372-8a97-0da2ddd6415d');
INSERT INTO relationshipidentitytypeentity (identifier, descriptor, identitytype_id, owner_id) VALUES ((SELECT nextval('hibernate_sequence')), 'role', 'c6eb3154-ffc8-44a0-a521-202abaca1408', '2477b11e-b71f-4372-8a97-0da2ddd6415d');
INSERT INTO relationshipidentitytypeentity (identifier, descriptor, identitytype_id, owner_id) VALUES ((SELECT nextval('hibernate_sequence')), 'assignee', '071d8f1c-0d3e-4fae-a1e4-45f1a706c7b6', '2477b11e-b71f-4372-8a97-0da2ddd6415d');

