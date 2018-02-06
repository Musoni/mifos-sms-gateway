create database if not exists `mifos-sms-gateway` default character set utf8 default collate utf8_general_ci;

use `mifos-sms-gateway`;

create table if not exists smsOutboundMessage (
 id bigint(20) primary key not null auto_increment,
 externalId varchar(100) null comment 'This is the sms message identifier provided by the sms gateway, e.g. infobip',
 internalId bigint(20) not null comment 'This is the sms message identifier in the mifostenant sms_messages_outbound table',
 mifosTenantIdentifier varchar(100) not null comment 'This is the mifos tenant identifier, e.g. tugende',
 createdOnDate date null comment 'This is the date the message was added to the mifostenant sms_message_outbound table',
 submittedOnDate date null comment 'This is the date the message was submitted to the sms gateway',
 addedOnDate date not null comment 'This is the date the message was added to this table',
 deliveredOnDate date null comment 'This is the date that an attempt was made by the sms gateway to deliver the message',
 deliveryStatus int(5) not null default 100,
 deliveryErrorMessage varchar(200) null,
 mobileNumber varchar(50) not null,
 sourceAddress varchar(50) not null comment 'Sender of the SMS message.',
 message varchar(254) not null,
 unique key externalId (externalId)
);

create table if not exists configuration (
name varchar(50) primary key,
value varchar(200) not null
);

insert into configuration(name, value)
values ("DEVELOPMENT_MODE", "true"),
("ENABLE_OUTBOUND_MESSSAGE_SCHEDULER", "false"),
("SMS_GATEWAY_HOSTNAME", "smpp2.infobip.com"),
("SMS_GATEWAY_PASSWORD", ""),
("SMS_GATEWAY_PORT", "8887"),
("SMS_GATEWAY_SYSTEM_ID", "");